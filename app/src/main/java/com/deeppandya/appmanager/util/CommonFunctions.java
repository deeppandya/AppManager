package com.deeppandya.appmanager.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.deeppandya.appmanager.MainActivity;
import com.deeppandya.appmanager.R;

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

    public static void showSortDialog(final MainActivity activity) {
        String[] sort = activity.getResources().getStringArray(R.array.sortbyApps);
        int current = Integer.parseInt(activity.Sp.getString("sortbyApps", "0"));
        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(activity);
        materialDialogBuilder.items(sort).itemsCallbackSingleChoice(current > 2 ? current - 3 : current, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                return true;
            }
        });
        materialDialogBuilder.positiveText(R.string.ascending).positiveColor(activity.getResources().getColor(R.color.colorAccent));
        materialDialogBuilder.negativeText(R.string.descending).negativeColor(activity.getResources().getColor(R.color.colorAccent));
        materialDialogBuilder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                int which = dialog.getSelectedIndex();
                activity.Sp.edit().putString("sortbyApps", "" + which).commit();
                activity.getSortModes();
                activity.getSupportLoaderManager().restartLoader(MainActivity.ID_LOADER_APP_LIST, null, activity);
                dialog.dismiss();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                int which = dialog.getSelectedIndex() + 3;
                activity.Sp.edit().putString("sortbyApps", "" + which).commit();
                activity.getSortModes();
                activity.getSupportLoaderManager().restartLoader(MainActivity.ID_LOADER_APP_LIST, null, activity);
                dialog.dismiss();
            }
        });
        materialDialogBuilder.title(R.string.sortby);
        materialDialogBuilder.build().show();
    }
}
