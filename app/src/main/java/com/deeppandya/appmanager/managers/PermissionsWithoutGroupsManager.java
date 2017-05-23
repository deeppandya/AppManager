package com.deeppandya.appmanager.managers;

import android.support.v4.util.Pair;

import com.deeppandya.appmanager.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by deeppandya on 2017-05-22.
 */

public class PermissionsWithoutGroupsManager {
    private static Map<String,Integer> permissionWithoutGroups;


    public static Map<String,Integer> getPermissionWithoutGroups() {
        if (permissionWithoutGroups == null) {
            permissionWithoutGroups = getPermissionWithoutGroupsData();
        }
        return permissionWithoutGroups;
    }

    private static Map<String, Integer> getPermissionWithoutGroupsData() {
        Map<String,Integer> permissionWithoutGroupsData=new HashMap<>();
        permissionWithoutGroupsData.put("android.permission.ACCESS_WIFI_STATE", R.drawable.ic_network_wifi_black_24dp);
        permissionWithoutGroupsData.put("android.permission.ACCESS_NETWORK_STATE", R.drawable.ic_network_cell_black_24dp);
        permissionWithoutGroupsData.put("android.permission.ACCESS_NOTIFICATION_POLICY", R.drawable.ic_notifications_black_24dp);
        permissionWithoutGroupsData.put("android.permission.BATTERY_STATS", R.drawable.ic_battery_50_black_24dp);
        permissionWithoutGroupsData.put("android.permission.BIND_DEVICE_ADMIN", R.drawable.ic_perm_device_information_black_24dp);
        permissionWithoutGroupsData.put("android.permission.BLUETOOTH", R.drawable.ic_bluetooth_black_24dp);
        permissionWithoutGroupsData.put("android.permission.CHANGE_WIFI_STATE", R.drawable.ic_network_wifi_black_24dp);
        permissionWithoutGroupsData.put("android.permission.GET_TASKS", R.drawable.ic_bug_report_black_24dp);
        permissionWithoutGroupsData.put("android.permission.NFC", R.drawable.ic_nfc_black_24dp);
        permissionWithoutGroupsData.put("android.permission.VIBRATE", R.drawable.ic_vibration_black_24dp);
        permissionWithoutGroupsData.put("android.permission.INTERNET", R.drawable.ic_network_wifi_black_24dp);
        permissionWithoutGroupsData.put("android.permission.WAKE_LOCK", R.drawable.ic_phonelink_lock_black_24dp);

        return permissionWithoutGroupsData;
    }
}
