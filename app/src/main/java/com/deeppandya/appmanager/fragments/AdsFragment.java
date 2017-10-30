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

/**
 * A simple {@link Fragment} subclass.
 */
public class AdsFragment extends Fragment {

    private static final String TAG = AdsFragment.class.getName();
    private AdView facebookAdView;

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

    @Override
    public void onDestroy() {
        if (facebookAdView != null) facebookAdView.destroy();
        super.onDestroy();
    }
}
