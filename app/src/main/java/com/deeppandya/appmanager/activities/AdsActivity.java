package com.deeppandya.appmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.deeppandya.appmanager.managers.FirebaseManager;
import com.deeppandya.appmanager.util.FirebaseUtil;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class AdsActivity extends AppCompatActivity implements InterstitialAdListener{

    private static final String TAG = AdsActivity.class.getName();
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginUser();

    }

    private void loginUser() {
        if (FirebaseManager.getFirebaseAuth().getCurrentUser() == null) {
            FirebaseUtil.anonymousUserLogin();
        }
    }

    protected void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this, "291507681300038_309703792813760");
        mInterstitialAd.setAdListener(this);
        mInterstitialAd.loadAd();
    }

    @Override
    public void onError(Ad ad, AdError error) {
        Log.e(TAG,error.getErrorMessage());
    }

    @Override
    public void onAdLoaded(Ad ad) {
        // Ad is loaded and ready to be displayed
        // You can now display the full screen add using this code:
        mInterstitialAd.show();
    }

    @Override
    public void onAdClicked(Ad ad) {

    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {

    }

    @Override
    public void onInterstitialDismissed(Ad ad) {

    }
}
