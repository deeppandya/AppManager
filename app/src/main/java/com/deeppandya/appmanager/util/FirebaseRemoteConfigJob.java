package com.deeppandya.appmanager.util;

import android.util.Log;

import com.deeppandya.appmanager.managers.FirebaseManager;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by d_pandya on 3/10/17.
 */

public class FirebaseRemoteConfigJob implements JobCreator {
    private static final String FIREBASEJOBTAG = "FirebaseJobCreator";

    @Override
    public Job create(String tag) {
        switch (tag) {
            case FirebaseManager.SyncRemoteConfig.TAG:
                return new FirebaseManager.SyncRemoteConfig();
            default:
                Log.w(FIREBASEJOBTAG, "Cannot find job for tag " + tag);
                return null;
        }
    }
}
