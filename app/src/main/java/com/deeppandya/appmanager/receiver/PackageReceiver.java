package com.deeppandya.appmanager.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.deeppandya.appmanager.asynctask.AppListLoader;

/**
 * Created by d_pandya on 3/7/17.
 */

public class PackageReceiver extends BroadcastReceiver {

    private AppListLoader listLoader;
    private Activity activity;

    public PackageReceiver() {
    }

    public PackageReceiver(AppListLoader listLoader) {

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        listLoader.getContext().registerReceiver(this, filter);

        // Register for events related to SD card installation
        IntentFilter sdcardFilter = new IntentFilter(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdcardFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        listLoader.getContext().registerReceiver(this, sdcardFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        listLoader.onContentChanged();
    }

//    public PackageReceiver(Activity activity) {
//
//        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
//        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
//        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
//        filter.addDataScheme("package");
//        activity.registerReceiver(this, filter);
//
//        // Register for events related to SD card installation
//        IntentFilter sdcardFilter = new IntentFilter(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
//        sdcardFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
//        activity.registerReceiver(this, sdcardFilter);
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        ((MainActivity)activity).addNewApp();
//    }

}
