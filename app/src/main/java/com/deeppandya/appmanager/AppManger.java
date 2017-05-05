package com.deeppandya.appmanager;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.appbrain.AppBrain;
import com.deeppandya.appmanager.managers.FirebaseManager;
import com.deeppandya.appmanager.util.FirebaseRemoteConfigJob;
import com.evernote.android.job.JobManager;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.orm.SugarApp;
import com.orm.SugarContext;
import io.fabric.sdk.android.Fabric;

/**
 * Created by d_pandya on 3/9/17.
 */

public class AppManger extends MultiDexApplication {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        SugarContext.init(this);
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6103213878258636~9820290101");
        // init job scheduler
        JobManager.create(this).addJobCreator(new FirebaseRemoteConfigJob());
        FirebaseManager.configure(this);
        Fabric.with(this, new Crashlytics());

        AppBrain.init(this);

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

    public static synchronized Context getAppContext() {
        return mContext;
    }

}
