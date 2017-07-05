package com.deeppandya.appmanager.fragments;


import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.deeppandya.appmanager.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.mopub.mobileads.MoPubView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdsFragment extends Fragment implements InterstitialAdListener {

    private static final String TAG = AdsFragment.class.getName();
    private AdView facebookAdView;
    private InterstitialAd facebookInterstitialAd;
    private MoPubView mopubView;

    public AdsFragment() {
        // Required empty public constructor
    }

    public void showBanner(View rootView){
        facebookAdView = new AdView(getActivity(), "291507681300038_291507847966688", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) rootView.findViewById(R.id.bannerView);

        // Add the ad view to your activity layout
        adContainer.addView(facebookAdView);

        // Request an ad
        facebookAdView.loadAd();
    }

    public void showMopubBanner(View rootView){
//        mopubView = (MoPubView) rootView.findViewById(R.id.bannerView);
//        mopubView.setAdUnitId("493437314eae4f36a307db56502e9cb5"); // Enter your Ad Unit ID from www.mopub.com
//        mopubView.loadAd();
    }

    public void loadFacebookInterstitialAd() {
        facebookInterstitialAd = new InterstitialAd(getActivity(), "291507681300038_309703792813760");
        facebookInterstitialAd.setAdListener(this);
        facebookInterstitialAd.loadAd();
    }

    @Override
    public void onError(Ad ad, AdError error) {
        Log.e(TAG,error.getErrorMessage());
    }

    @Override
    public void onAdLoaded(Ad ad) {
        // Ad is loaded and ready to be displayed
        // You can now display the full screen add using this code:
        facebookInterstitialAd.show();
    }

    @Override
    public void onAdClicked(Ad ad) {

    }


    @Override
    public void onInterstitialDisplayed(Ad ad) {

    }

    @Override
    public void onInterstitialDismissed(Ad ad) {

    }

    @Override
    public void onDestroy() {
        if (facebookAdView != null) facebookAdView.destroy();
        if(mopubView!=null) mopubView.destroy();
        super.onDestroy();
    }
}
