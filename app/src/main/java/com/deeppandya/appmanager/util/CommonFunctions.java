package com.deeppandya.appmanager.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.Toast;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.asynctask.CopyFileAsynctask;
import com.deeppandya.appmanager.model.AppModel;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.IOException;
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

    public static void rateApp(Context context){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            FirebaseCrash.log(e.getMessage());
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void openAppProperties(Context context,AppModel appModel) {
        context.startActivity(new Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + appModel.getPackageName())));
    }

    public static void openAppInPlayStore(Context context,AppModel appModel) {
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        intent1.setData(Uri.parse("market://details?id=" + appModel.getPackageName()));
        context.startActivity(intent1);
    }

    public static void uninstallApp(Context context,AppModel appModel) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + appModel.getPackageName()));
        context.startActivity(intent);
    }

    public static void openApp(Context context,AppModel appModel) {
        Intent i1 = context.getPackageManager().getLaunchIntentForPackage(appModel.getPackageName());
        if (i1 != null)
            context.startActivity(i1);
        else
            Toast.makeText(context, context.getResources().getString(R.string.not_allowed), Toast.LENGTH_LONG).show();
    }

    public static void backupApp(Context context,View view, AppModel appModel) {
        File inputFile = new File(appModel.getAppDesc());
        File destDir = new File(CommonFunctions.getBackupDir());
        if (!destDir.exists() || !destDir.isDirectory()) destDir.mkdirs();

        File outFile = new File(destDir + File.separator + appModel.getAppName() + "_" + appModel.getSymlink() + ".apk");

        try {
            outFile.createNewFile();
            CopyFileAsynctask copyFilesAsynctask = new CopyFileAsynctask(view, context, inputFile, outFile, appModel.getAppName());
            copyFilesAsynctask.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
