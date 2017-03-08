package com.deeppandya.appmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.deeppandya.appmanager.enums.SortOrder;
import com.deeppandya.appmanager.enums.SortType;

/**
 * Created by d_pandya on 3/8/17.
 */

public class PersistanceManager {

    public static void setSortType(Context context, SortType sortType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sortType", sortType.toString());
        editor.commit();
    }

    public static SortType getSortType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sortType = sharedPreferences.getString("sortType", SortType.BYNAME.toString());
        return SortType.toSortType(sortType);
    }

    public static void setSortOrder(Context context, SortOrder sortOrder) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sortOrder", sortOrder.toString());
        editor.commit();
    }

    public static SortOrder getSortOrder(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = sharedPreferences.getString("sortOrder", SortOrder.ASC.toString());
        return SortOrder.toSortOrder(sortOrder);
    }

}
