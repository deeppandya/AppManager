package com.deeppandya.appmanager.managers;

import android.support.v4.util.Pair;

import com.deeppandya.appmanager.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by deeppandya on 2017-05-22.
 */

public class PermissionGroupsManager {
    private static Map<String,Pair<String,Integer>> permissionGroups;


    public static Map<String,Pair<String,Integer>> getPermissionGroups() {
        if (permissionGroups == null) {
            permissionGroups = getPermissionGroupsData();
        }
        return permissionGroups;
    }

    private static Map<String, Pair<String, Integer>> getPermissionGroupsData() {
        Map<String,Pair<String,Integer>> permissionGroupsData=new HashMap<>();
        permissionGroupsData.put("android.permission-group.CALENDAR",new Pair<String, Integer>("Calender", R.drawable.ic_date_range_black_24dp));
        permissionGroupsData.put("android.permission-group.CAMERA",new Pair<String, Integer>("Camera", R.drawable.ic_photo_camera_black_24dp));
        permissionGroupsData.put("android.permission-group.CONTACTS",new Pair<String, Integer>("Contacts", R.drawable.ic_perm_contact_calendar_black_24dp));
        permissionGroupsData.put("android.permission-group.LOCATION",new Pair<String, Integer>("Location", R.drawable.ic_my_location_black_24dp));
        permissionGroupsData.put("android.permission-group.MICROPHONE",new Pair<String, Integer>("Microphone", R.drawable.ic_keyboard_voice_black_24dp));
        permissionGroupsData.put("android.permission-group.PHONE",new Pair<String, Integer>("Phone", R.drawable.ic_phone_black_24dp));
        permissionGroupsData.put("android.permission-group.SENSORS",new Pair<String, Integer>("Sensors", R.drawable.ic_settings_input_antenna_black_24dp));
        permissionGroupsData.put("android.permission-group.SMS",new Pair<String, Integer>("Sms", R.drawable.ic_sms_black_24dp));
        permissionGroupsData.put("android.permission-group.STORAGE",new Pair<String, Integer>("Storage",R.drawable.ic_storage_black_24dp));
        permissionGroupsData.put("SYSTEM",new Pair<String, Integer>("System", R.drawable.ic_settings_applications_black_24dp));

        return permissionGroupsData;
    }
}
