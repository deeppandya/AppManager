package com.deeppandya.appmanager.managers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.deeppandya.appmanager.BuildConfig;
import com.deeppandya.appmanager.R;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * Created by d_pandya on 3/10/17.
 */

public class FirebaseManager {

    // Remote config fields
    public static final String FIRST_SCREEN_BANNER = "first_screen_banner";
    public static final String UNINSTALL_BANNER = "uninstall_banner";
    public static final String BACKUP_BANNER = "backup_banner";
    public static final String PERMISSION_BANNER = "permission_banner";
    public static final String PACKAGE_BANNER = "package_banner";
    public static final String LAUNCH_INTERSTITIAL = "launch_interstitial";

    private static FirebaseRemoteConfig firebaseRemoteConfig;
    private static FirebaseAnalytics firebaseAnalytics;
    private static Context sContext;

    private FirebaseManager() {
    }

    public static void configure(Context context) {
        sContext = context;
        configureSyncJob();
    }

    public static FirebaseRemoteConfig getRemoteConfig() {
        if (firebaseRemoteConfig == null) {
            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            firebaseRemoteConfig.setConfigSettings(configSettings);
            firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        }
        return firebaseRemoteConfig;
    }

    public static FirebaseAnalytics getAnalytics() {
        if (sContext == null) {
            throw new IllegalStateException("FirebaseUtils needs to be configured before use.");
        }
        if (firebaseAnalytics == null) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(sContext);
        }
        return firebaseAnalytics;
    }

    private static void configureSyncJob() {
        new JobRequest.Builder(SyncRemoteConfig.TAG)
                .setPeriodic(21600000)
                .setUpdateCurrent(true)
                .setPersisted(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

    public static class SyncRemoteConfig extends Job {

        public static final String TAG = "sync_remote_config_job";

        @NonNull
        @Override
        protected Result onRunJob(Params params) {

            FirebaseManager.getRemoteConfig().fetch()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseManager.getRemoteConfig().activateFetched();
                        }
                    });

            return Result.SUCCESS;
        }
    }
}
