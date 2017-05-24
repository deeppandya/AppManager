package com.deeppandya.appmanager.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.deeppandya.appmanager.asynctask.AppListLoader;
import com.deeppandya.appmanager.listeners.OnPackageChanged;

/**
 * Created by d_pandya on 3/7/17.
 */

public class PackageReceiver extends BroadcastReceiver {

    private AppListLoader listLoader;
    private Activity activity;
    private OnPackageChanged onPackageChanged;

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

    public PackageReceiver(OnPackageChanged onPackageChanged) {

        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        onPackageChanged.registerReceiver(this,filter);
        this.onPackageChanged=onPackageChanged;

        // Register for events related to SD card installation
        IntentFilter sdcardFilter = new IntentFilter(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdcardFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        onPackageChanged.onPackageChanged();
    }
}
