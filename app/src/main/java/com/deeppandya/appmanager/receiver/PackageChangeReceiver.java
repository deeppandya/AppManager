package com.deeppandya.appmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.deeppandya.appmanager.listeners.OnPackageChanged;

public class PackageChangeReceiver extends BroadcastReceiver {

    private OnPackageChanged onPackageChanged;

    public PackageChangeReceiver() {
    }

    public PackageChangeReceiver(OnPackageChanged onPackageChanged) {
        this.onPackageChanged = onPackageChanged;
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        onPackageChanged.registerReceiver(this,filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onPackageChanged.onPackageChanged();
    }
}
