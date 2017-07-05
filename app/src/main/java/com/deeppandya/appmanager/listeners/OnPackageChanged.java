package com.deeppandya.appmanager.listeners;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by deeppandya on 2017-05-23.
 */

public interface OnPackageChanged {
    void onPackageChanged();

    void registerReceiver(BroadcastReceiver receiver, IntentFilter filter);
}
