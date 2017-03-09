package com.deeppandya.appmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.util.PersistanceManager;
import com.deeppandya.appmanager.util.UninstallPreventionManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FirstActivity extends AppCompatActivity implements UninstallPreventionManager.UninstallPreventionListener{

    CardView uninstallCard,backupCard,permissionCard,packageCard;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(PersistanceManager.getUserFirstTime(FirstActivity.this)){
            openIntro();
        }

        setContentView(R.layout.activity_first);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sendAnalyticsEvent();

        uninstallCard=(CardView)findViewById(R.id.uninstall_layout);
        uninstallCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.UNINSTALL);
                //UninstallPreventionManager.getInstance().enableDeviceAdmin(FirstActivity.this, null, true);
            }
        });

        backupCard=(CardView)findViewById(R.id.backup_layout);
        backupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.BACKUP);
            }
        });

        permissionCard=(CardView)findViewById(R.id.permission_layout);
        permissionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.PERMISSIONS);
            }
        });

        packageCard=(CardView)findViewById(R.id.package_layout);
        packageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.PACKAGE);
            }
        });

        loadAdMobBannerAd();

        //loadDFPBannerAd();

    }

    private void openIntro() {
        Intent introIntent = new Intent(FirstActivity.this, PagerActivity.class);
        startActivity(introIntent);
    }


    private void sendAnalyticsEvent() {
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "deep");
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void loadAdMobBannerAd() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("DD88CB4BC53A57945289D53A627F700A")
                .build();
        mAdView.loadAd(adRequest);
    }

    private void loadDFPBannerAd() {
//        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
//        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
//        mPublisherAdView.loadAd(adRequest);

    }

    private void openActivity(AppCategory appCategory) {
        Intent intent=new Intent(FirstActivity.this,MainActivity.class);
        intent.putExtra("category",appCategory);
        startActivity(intent);
    }

    @Override
    public void OnActivateDeviceAdministrator() {
        UninstallPreventionManager.getInstance().enableDeviceAdmin(this, null, true);
    }

    @Override
    public void OnDeactivateDeviceAdministrator() {
        UninstallPreventionManager.getInstance().enableDeviceAdmin(this, null, false);
    }
}
