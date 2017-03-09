package com.deeppandya.appmanager;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;

/**
 * Created by d_pandya on 3/9/17.
 */

public class AppManger extends MultiDexApplication {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6103213878258636~9820290101");

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
