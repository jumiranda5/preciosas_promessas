package com.alf.preciosaspromessas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.alf.preciosaspromessas.utils.ListHelper;
import com.alf.preciosaspromessas.utils.SharedPrefs;
import com.alf.preciosaspromessas.utils.Verse;

import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    // Constants
    public static final String SOUND_PREFS = "sound";

    // Ads
    private FrameLayout adContainerView;
    private AdView mAdView;

    // UI
    private View mColorBg;
    private ImageButton mPlay, mFavorite, mShare, mFolder, mToggleSound;
    private TextView mShakeInstruction, mVerse;
    private MotionLayout motion;

    // Audio
    private AudioManager aManager;
    private Boolean isSoundOn = true;

    // Verses
    private String[] mVerses;
    private String mRandomVerse;

    // Db
    ListHelper db = new ListHelper(this);

    // Sensor
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVerses = getResources().getStringArray(R.array.verses);
        aManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        isSoundOn = SharedPrefs.getSoundStatus(this, SOUND_PREFS);

        initViews();
        checkSoundPrefs();

        initAdViewContainer();
        loadBanner();

        initButtons();

        initSensor();

    }

    /* ----------------------------------
                  INIT VIEWS
    ------------------------------------- */

    private void initViews() {
        mColorBg = findViewById(R.id.color_animation_bg);
        mVerse = findViewById(R.id.textView_verse);
        mFavorite = findViewById(R.id.btn_favorite);
        mShare = findViewById(R.id.btn_share);
        mPlay = findViewById(R.id.btn_play);
        mFolder = findViewById(R.id.btn_folder);
        mToggleSound = findViewById(R.id.btn_sound);
        mShakeInstruction = findViewById(R.id.text_shake_instruction);
        adContainerView = findViewById(R.id.ad_view_container);
        motion = findViewById(R.id.main_motion_layout);
    }


    /* ----------------------------------
                  BUTTONS
    ------------------------------------- */

    private void initButtons() {

        mPlay.setOnClickListener(v -> getRandomVerse());

        mShare.setOnClickListener(v -> share());

        mFavorite.setOnClickListener(v -> toggleFavorite());

        mFolder.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FavoriteListActivity.class)));

        mToggleSound.setOnClickListener(v -> toggleSoundPrefs());

        mVerse.setOnLongClickListener(v -> {
            copy();
            return false;
        });
    }

    /* ----------------------------------
                    VERSE
    ------------------------------------- */

    private void getRandomVerse() {

        stopSensor();
        mPlay.setEnabled(false);

        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(mVerses.length);
        mRandomVerse = mVerses[randomNumber];

        mShakeInstruction.setVisibility(View.GONE);
        mVerse.setVisibility(View.VISIBLE);
        animateBackground();
        animateVerse();
        playSound();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            mPlay.setEnabled(true);
            restartSensor();
        }, 1600);
    }


    /* ----------------------------------
               SAVE TO FAVORITES
    ------------------------------------- */

    private void toggleFavorite() {
        if (mRandomVerse == null){
            Toast.makeText(getApplicationContext(), R.string.no_verse, Toast.LENGTH_LONG).show();
        }
        else {
            animateFavBtn();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Verse isSaved = db.getVerseByString(mRandomVerse);
                int id = db.getVersesCount();

                if (isSaved == null) {
                    db.addVerse(new Verse(id, mRandomVerse));
                    mFavorite.setImageResource(R.drawable.ic_star_24);
                    mFavorite.setContentDescription(getString(R.string.desc_remove_from_favorites));
                }
                else {
                    mFavorite.setImageResource(R.drawable.ic_star_border_24);
                    mFavorite.setContentDescription(getString(R.string.desc_save_favorite));
                    db.deleteVerse(isSaved);
                }
            }, 200);
        }
    }

    private void setFavorite() {
        Verse isSaved = db.getVerseByString(mRandomVerse);

        if (isSaved == null) {
            mFavorite.setImageResource(R.drawable.ic_star_border_24);
            mFavorite.setContentDescription(getString(R.string.desc_save_favorite));
        }
        else {
            mFavorite.setImageResource(R.drawable.ic_star_24);
            mFavorite.setContentDescription(getString(R.string.desc_remove_from_favorites));
        }
    }


    /* ----------------------------------
                    SHARE
    ------------------------------------- */

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mRandomVerse);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    /* ----------------------------------
                    SHARE
    ------------------------------------- */

    private void copy(){
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(null , mRandomVerse);
        clipboard.setPrimaryClip(clip);
        Toast toast = new Toast(getApplicationContext());
        toast.setText(R.string.text_copied);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
    }

    /* ----------------------------------
                    SOUND
    ------------------------------------- */

    private void checkSoundPrefs() {
        if (isSoundOn) {
            mToggleSound.setImageResource(R.drawable.ic_volume_up_24);
            mToggleSound.setContentDescription(getString(R.string.desc_mute));
            aManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
        else {
            mToggleSound.setImageResource(R.drawable.ic_volume_off_24);
            mToggleSound.setContentDescription(getString(R.string.desc_turn_on_sound));
            aManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }
    }

    private void toggleSoundPrefs() {
        if (isSoundOn) {
            isSoundOn = false;
            SharedPrefs.setSoundPrefs(this, SOUND_PREFS, false);
        }
        else {
            isSoundOn = true;
            SharedPrefs.setSoundPrefs(this, SOUND_PREFS, true);
        }
        checkSoundPrefs();
    }

    private void playSound() {
        // Sound
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.harp);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }


    /* ----------------------------------
                  ANIMATIONS
    ------------------------------------- */

    private void animateBackground() {
        mColorBg.setBackgroundResource(R.drawable.animation);
        AnimationDrawable frameAnimation = (AnimationDrawable) mColorBg.getBackground();
        if(frameAnimation.isRunning()) frameAnimation.stop();
        frameAnimation.start();
    }

    private void animateVerse() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            animateMenuOut();
            new Handler(Looper.getMainLooper()).postDelayed(this::animateMenuIn, 1150);
        }, 50);
    }

    private void animateFavBtn() {
        Animator animator = AnimatorInflater.loadAnimator(this, R.animator.animate_star);
        animator.setTarget(mFavorite);
        animator.start();
    }

    private void animateMenuOut() {
        motion.setTransition(R.id.start, R.id.middle);
        motion.transitionToEnd();
    }

    private void animateMenuIn() {
        mFavorite.setVisibility(View.VISIBLE);
        mVerse.setText(mRandomVerse);
        setFavorite();
        motion.setTransition(R.id.middle, R.id.end);
        motion.transitionToEnd();
    }

    @Override
    public void onAnimationStart(Animation animation) { }

    @Override
    public void onAnimationEnd(Animation animation) { }

    @Override
    public void onAnimationRepeat(Animation animation) { }

    /* ----------------------------------
                     ADS
    ------------------------------------- */

    private void initAdViewContainer() {
        mAdView = new AdView(this);
        mAdView.setAdUnitId(getString(R.string.ad_unit_main));
        adContainerView.addView(mAdView);
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        mAdView.setAdSize(adSize);
        mAdView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    /* -------------------------------------------------------
                              SENSOR
    --------------------------------------------------------- */

    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 12) {
                stopSensor();
                getRandomVerse();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void stopSensor() {
        mSensorManager.unregisterListener(mSensorListener);
    }

    private void restartSensor() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /* ----------------------------------
                  LIFECYCLE
    ------------------------------------- */
    @Override
    protected void onResume(){
        super.onResume();
        restartSensor();
        if(mAdView != null) mAdView.resume();
    }

    @Override
    protected void onPause(){
        stopSensor();
        if(mAdView != null) mAdView.pause();
        super.onPause();
    }

}