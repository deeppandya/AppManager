package com.deeppandya.appmanager.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.deeppandya.appmanager.MainActivity;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.enums.SortType;

import java.text.SimpleDateFormat;

/**
 * Created by d_pandya on 3/7/17.
 */

public class CommonFunctions {

    private static final SimpleDateFormat sSDF = new SimpleDateFormat("MMM dd, yyyy");

    public static String getdate(long f, String year) {
        String date = sSDF.format(f);
        if(date.substring(date.length()-4,date.length()).equals(year))
            date=date.substring(0,date.length()-6);
        return date;
    }

    public static StringBuilder parsePermissions(String[] permissions) {
        StringBuilder currentPermissions = new StringBuilder();

        if (permissions != null) {
            String delim = "";
            for (int i = 0; i < permissions.length; i++) {
                currentPermissions.append(delim).append(permissions[i]);
                delim = ",";
            }
        }

        return currentPermissions;

    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + ("");
        return String.format("%.1f%s", bytes / Math.pow(unit, exp), pre);
    }

    public static String getBackupDir() {
        return Environment.getExternalStorageDirectory().getPath() + "/AppManager/apk_backup";
    }

    public static Drawable tintMyDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

}
