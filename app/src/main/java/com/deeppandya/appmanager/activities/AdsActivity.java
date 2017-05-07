package com.deeppandya.appmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.deeppandya.appmanager.BuildConfig;
import com.deeppandya.appmanager.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdsActivity extends AppCompatActivity {

    private RelativeLayout mBannerView;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showBanner(String unitId) {
        if (!BuildConfig.DEBUG) {
            mBannerView = (RelativeLayout) findViewById(R.id.bannerView);
            mAdView = new AdView(this);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(unitId);
            //mAdView.setAdUnitId("/6499/example/banner"); // Test Id
            mBannerView.addView(mAdView);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {

                    Log.e("onAdFailedToLoad", i + "");

                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }
            });

            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice("DD88CB4BC53A57945289D53A627F700A")
                    .build();
            mAdView.loadAd(adRequest);
        }
    }

    public void loadAdMobInterstitialAd() {
        if (!BuildConfig.DEBUG) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.appmanager_interstitial_unit_id));

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    //requestNewInterstitial();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.e("onerror", i + "");
                }
            });

            requestNewInterstitial();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("DD88CB4BC53A57945289D53A627F700A")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onPause() {
        if (mAdView != null) mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) mAdView.resume();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) mAdView.destroy();
        super.onDestroy();
    }
}