package com.deeppandya.appmanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.deeppandya.appmanager.BuildConfig;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.enums.AppSortType;
import com.deeppandya.appmanager.enums.SortOrder;
import com.deeppandya.appmanager.listeners.AppSortListener;
import com.deeppandya.appmanager.managers.PersistanceManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdsFragment extends Fragment {

    private RelativeLayout mBannerView;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private MaterialDialog.Builder materialDialogBuilder;

    public AdsFragment() {
        // Required empty public constructor
    }

    public void showBanner(View rootView, String unitId) {
        if (!BuildConfig.DEBUG) {
            mBannerView = (RelativeLayout) rootView.findViewById(R.id.bannerView);
            mAdView = new AdView(getActivity());
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
            mInterstitialAd = new InterstitialAd(getActivity());
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

    public void showSortDialog(final AppSortListener appSortListener) {
        String[] sort = getResources().getStringArray(R.array.sortbyApps);
        AppSortType current = PersistanceManager.getSortType(getActivity());
        materialDialogBuilder = new MaterialDialog.Builder(getActivity());
        materialDialogBuilder.items(sort).itemsCallbackSingleChoice(current.getSortTypeValue(), new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                return true;
            }
        });
        materialDialogBuilder.positiveText(R.string.ascending).positiveColor(getResources().getColor(R.color.colorAccent));
        materialDialogBuilder.negativeText(R.string.descending).negativeColor(getResources().getColor(R.color.colorAccent));
        materialDialogBuilder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                int which = dialog.getSelectedIndex();

                appSortListener.onPositiveClick(which);
                dialog.dismiss();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                int which = dialog.getSelectedIndex();

                appSortListener.onNegativeClick(which);
                dialog.dismiss();
            }
        });
        materialDialogBuilder.title(R.string.sortby);
        materialDialogBuilder.build().show();
    }

    @Override
    public void onPause() {
        if (mAdView != null) mAdView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) mAdView.resume();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) mAdView.destroy();
        super.onDestroy();
    }
}
