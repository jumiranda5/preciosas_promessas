package com.alf.preciosaspromessas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import com.alf.preciosaspromessas.utils.SharedPrefs;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "Splash";

    private ConsentInformation consentInformation;
    private ConsentForm consentForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new android.os.Handler().postDelayed(
                () -> {
                    getConsentStatus();
                },
                300);


    }

    private void startMainActivity() {
        Log.d(TAG, "Open Main Activity.");
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void initAdMob() {
        Log.d(TAG, "Initializing Admob...");
        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "Admob ready...");
            startMainActivity();
        });
    }


    /* ------------------------------------------------------
                          ADS CONSENT
    ------------------------------------------------------ */

    private void getConsentStatus() {

        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("699F572A89809921B6145108DA4819F4")
            .build();

        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .setConsentDebugSettings(debugSettings)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        Log.d(TAG, "Consent Status: " + consentInformation.getConsentStatus());

        int NOT_REQUIRED = 1;
        int OBTAINED = 3;

        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        // The consent information state was updated.
                        // You are now ready to check if a form is available.
                        if (consentInformation.getConsentStatus() == NOT_REQUIRED ||
                                consentInformation.getConsentStatus() == OBTAINED) {
                            initAdMob();
                        }
                        else if (consentInformation.isConsentFormAvailable()) {
                            loadForm();
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {
                        // Handle the error.
                        Log.e(TAG, "Load form error... " + formError);
                        initAdMob(); // Todo => handle error...
                    }
                });
    }

    private void loadForm() {
        UserMessagingPlatform.loadConsentForm(
                this,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {

                        SplashActivity.this.consentForm = consentForm;

                        if(consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            consentForm.show(
                                    SplashActivity.this,
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(@Nullable FormError formError) {

                                            Log.d(TAG, "New consent status: " + consentInformation.getConsentStatus());

                                            initAdMob();

                                            // Handle dismissal by reloading form.
                                            loadForm();

                                        }
                                    });
                        }
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        Log.e(TAG, "Load form loading error... " + formError);
                        // Todo => handle form load failure.
                    }
                }
        );
    }
}