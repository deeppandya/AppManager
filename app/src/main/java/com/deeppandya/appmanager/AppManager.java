package com.deeppandya.appmanager;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.appbrain.AppBrain;
import com.deeppandya.appmanager.managers.FirebaseManager;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.util.FirebaseRemoteConfigJob;
import com.evernote.android.job.JobManager;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by d_pandya on 3/9/17.
 */

public class AppManager extends MultiDexApplication {
    public static List<AppModel> apps=new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        Fabric.with(this, new Crashlytics());

        FirebaseApp.initializeApp(this);
        // init job scheduler
        JobManager.create(this).addJobCreator(new FirebaseRemoteConfigJob());
        FirebaseManager.configure(this);
        Fabric.with(this, new Crashlytics());

        AppBrain.init(this);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5984394588344927~5614205493");

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
