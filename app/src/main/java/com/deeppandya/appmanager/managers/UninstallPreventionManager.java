package com.deeppandya.appmanager.managers;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.receiver.UninstallReceiver;

/**
 * Manage Uninstall prevention interactions
 * Created by d_pandya on 3/8/17.
 */
public class UninstallPreventionManager {

    public static final String UNINSTALL_KEY = "uninstall_prevention_changed";

    private static UninstallPreventionManager mInstance;

    public static UninstallPreventionManager getInstance() {
        if (mInstance == null)
            mInstance = new UninstallPreventionManager();
        return mInstance;
    }

    private UninstallPreventionManager() {
    }

    /**
     * Determine if Hexlock is enabled or not as a device Administrator
     * @param context The current context
     * @return true if Hexlock is Device Admin
     */
    public boolean isDeviceAdminEnabled(Context context) {
        ComponentName deviceAdminComponentName = new ComponentName(context, UninstallReceiver.class);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return devicePolicyManager.isAdminActive(deviceAdminComponentName);
    }

    /**
     * Launch system activity to enable Hexlock as System Administrator
     * @param context The current activity
     * @param fragment The current fragment if you want to receive onActivityResult callback in a fragment
     * @param activate Whether you want to activate on deactivate Hexlock as System Administrator
     */
    public void enableDeviceAdmin(@NonNull Activity context, @Nullable Fragment fragment, boolean activate) {

        ComponentName deviceAdminComponentName = new ComponentName(context, UninstallReceiver.class);
        if (activate) {
            // Launch the activity to have the user enable our admin.
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, context.getString(R.string.add_admin_extra_app_text));

            if (fragment != null)
                fragment.startActivityForResult(intent, UninstallReceiver.REQUEST_CODE_ENABLE_ADMIN);
            else
                context.startActivityForResult(intent, UninstallReceiver.REQUEST_CODE_ENABLE_ADMIN);
        } else {
            DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            mDPM.removeActiveAdmin(deviceAdminComponentName);
        }
        // update result INTENT
        Intent intent = context.getIntent();
        // set to true to indicate there was a status change so we have to update views
        intent.putExtra(UNINSTALL_KEY, true);
    }

    public interface UninstallPreventionListener {
        void OnActivateDeviceAdministrator();
        void OnDeactivateDeviceAdministrator();
    }

}
