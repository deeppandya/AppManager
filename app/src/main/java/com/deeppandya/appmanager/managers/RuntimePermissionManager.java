package com.deeppandya.appmanager.managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;

import com.deeppandya.appmanager.R;

/**
 * Created by d_pandya on 3/10/17.
 */

public class RuntimePermissionManager {
    public static String[] PERMISSIONS_ACCOUNT = {Manifest.permission.WRITE_EXTERNAL_STORAGE};


    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 101;

    public static boolean checkSelfPermission(Activity activity, String permission){
        return ActivityCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED;

    }

    public static void requestPermission(final Activity activity, View anchor, String permission, final String[] permissions,
                                         final int requestCode, String rationaleText){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            Snackbar snackbar = createSnackBar(anchor, rationaleText, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.allow, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(activity, permissions, requestCode);
                        }
                    });
            snackbar.show();
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    public static void requestPermission(final Fragment fragment, View anchor, String permission, final String[] permissions,
                                         final int requestCode, String rationaleText){
        if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), permission)) {
            Snackbar snackbar = createSnackBar(anchor, rationaleText, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fragment.requestPermissions(permissions, requestCode);
                        }
                    });
            snackbar.show();
        } else {
            fragment.requestPermissions(permissions, requestCode);
        }
    }

    public static Snackbar createSnackBar(View view, String text, int duration){
        Snackbar snackbar = Snackbar.make(view, text, duration);
        return snackbar;
    }
}
