package com.alf.preciosaspromessas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.alf.preciosaspromessas.utils.ListAdapter;
import com.alf.preciosaspromessas.utils.ListHelper;
import com.alf.preciosaspromessas.utils.Verse;

import java.util.Collections;
import java.util.List;

public class FavoriteListActivity extends AppCompatActivity {

    protected List<Verse> mFavList;

    private RecyclerView mRecyclerView;

    // Ads
    private FrameLayout adContainerView;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        ListHelper db = new ListHelper(FavoriteListActivity.this);
        mFavList = db.getAllVerses();

        initViews();
        initRecyclerView();

        initAdViewContainer();
        loadBanner();

    }

    private void initViews() {
        adContainerView = findViewById(R.id.ad_view_container_list);
        mRecyclerView = findViewById(R.id.recyclerView_favorites);
    }

    private void initRecyclerView() {
        LinearLayoutManager listLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setLayoutManager(listLayoutManager);

        ListAdapter adapter = new ListAdapter(this, mFavList);
        mRecyclerView.setAdapter(adapter);

        Collections.reverse(mFavList);

        adapter.notifyDataSetChanged();
    }

    /* ----------------------------------
                     ADS
    ------------------------------------- */

    private void initAdViewContainer() {
        mAdView = new AdView(this);
        mAdView.setAdUnitId(getString(R.string.ad_units_list));
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

}