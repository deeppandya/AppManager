package com.deeppandya.appmanager.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.activities.IntroActivity;
import com.deeppandya.appmanager.asynctask.CopyFileAsynctask;
import com.deeppandya.appmanager.model.AppModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by d_pandya on 3/7/17.
 */

public class CommonFunctions {

    private static final SimpleDateFormat sSDF = new SimpleDateFormat("MMM dd, yyyy");

    private static int REQUEST_INVITE = 2;

    public static String getdate(long f, String year) {
        String date = sSDF.format(f);
        if (date.substring(date.length() - 4, date.length()).equals(year))
            date = date.substring(0, date.length() - 6);
        return date;
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));

        String[] formats = {"KB", "MB", "GB", "TB", "PB", "EB"};

        //String pre = ("KMGTPE").charAt(exp - 1) + ("");
        String pre = formats[exp - 1];
        return String.format("%.2f %s", bytes / Math.pow(unit, exp), pre);
    }

    public static String getBackupDir() {
        return Environment.getExternalStorageDirectory().getPath() + "/AppManager/apk_backup";
    }

    public static void openAppProperties(Context context, String appPackageName) {
        Intent intent = new Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + appPackageName));
        context.startActivity(intent);
    }

    public static void openAppInPlayStore(Context context, AppModel appModel) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + appModel.getPackageName()));
        context.startActivity(intent);
    }

    public static void uninstallApp(Context context, AppModel appModel) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + appModel.getPackageName()));
        context.startActivity(intent);
    }

    public static void openApp(Context context, AppModel appModel) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appModel.getPackageName());
        if (intent != null)
            context.startActivity(intent);
        else
            Toast.makeText(context, context.getResources().getString(R.string.not_allowed), Toast.LENGTH_LONG).show();
    }

    public static void backupApp(Context context, View view, List<AppModel> apps) {

        for (AppModel appModel : apps) {
            File inputFile = new File(appModel.getAppDesc());
            File destDir = new File(CommonFunctions.getBackupDir());
            if (!destDir.exists() || !destDir.isDirectory()) destDir.mkdirs();

            File outFile = new File(destDir + File.separator + appModel.getAppName() + "_" + appModel.getSymlink() + ".apk");

            try {
                outFile.createNewFile();
                CopyFileAsynctask copyFilesAsynctask = new CopyFileAsynctask(view, context, inputFile, outFile, appModel.getAppName());
                copyFilesAsynctask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void shareApp(Activity activity, String appName, String packageName) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + packageName + "&hl=en");
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    public static void openWebView(Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebasestorage.googleapis.com/v0/b/app-manager-7b1bf.appspot.com/o/PrivacyPolicy.pdf?alt=media&token=1cc45b77-9fbc-4587-a70b-b23c494f1709"));
        context.startActivity(browserIntent);
    }

    public static void openIntro(Activity activity, boolean isHelp) {
        Intent introIntent = new Intent(activity, IntroActivity.class);
        introIntent.putExtra("isHelp", isHelp);
        activity.startActivity(introIntent);
        activity.overridePendingTransition(0, 0);
    }
}
