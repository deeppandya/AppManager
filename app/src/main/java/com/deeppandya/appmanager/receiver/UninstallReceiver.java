package com.deeppandya.appmanager.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.deeppandya.appmanager.R;

/**
 * Purpose is to prevent uninstall of the app by registering as a device administrator
 * Created by d_pandya on 3/8/17.
 */
public class UninstallReceiver extends DeviceAdminReceiver {

    public static final int REQUEST_CODE_ENABLE_ADMIN = 77;

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.beware_unregistering_device_admin);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }
}
