package com.deeppandya.appmanager.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.deeppandya.appmanager.enums.SortOrder;
import com.deeppandya.appmanager.enums.AppSortType;

/**
 * Created by d_pandya on 3/8/17.
 */

public class PersistanceManager {

    public static final String PREF_USER_FIRST_TIME = "user_first_time";

    public static final String SORT_TYPE = "sort_type";

    public static final String SORT_ORDER = "sort_order";
    public static final String BACKUP_HINT = "backup_hint";

    public static void setSortType(Context context, AppSortType appSortType) {
        if(context!=null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SORT_TYPE, appSortType.toString());
            editor.apply();
        }
    }

    public static AppSortType getSortType(Context context) {
        if(context!=null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String sortType = sharedPreferences.getString(SORT_TYPE, AppSortType.BYNAME.toString());
            return AppSortType.toSortType(sortType);
        }else{
            return AppSortType.BYNAME;
        }
    }

    public static void setSortOrder(Context context, SortOrder sortOrder) {
        if(context!=null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SORT_ORDER, sortOrder.toString());
            editor.apply();
        }
    }

    public static SortOrder getSortOrder(Context context) {
        if(context!=null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String sortOrder = sharedPreferences.getString(SORT_ORDER, SortOrder.ASC.toString());
            return SortOrder.toSortOrder(sortOrder);
        }else{
            return SortOrder.ASC;
        }
    }

    public static boolean getUserFirstTime(Context context) {
        if(context!=null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getBoolean(PREF_USER_FIRST_TIME, true);
        }else{
            return true;
        }
    }

    public static void setUserFirstTime(Context context, boolean settingValue) {
        if(context!=null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_USER_FIRST_TIME, settingValue);
            editor.apply();
        }
    }

    public static boolean getBackupHint(Context context) {
        if(context!=null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getBoolean(BACKUP_HINT, true);
        }else {
            return true;
        }
    }

    public static void setBackupHint(Context context, boolean settingValue) {
        if(context!=null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(BACKUP_HINT, settingValue);
            editor.apply();
        }
    }
}
