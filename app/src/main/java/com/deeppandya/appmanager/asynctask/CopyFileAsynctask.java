package com.deeppandya.appmanager.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.util.CommonFunctions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by d_pandya on 3/8/17.
 */

public class CopyFileAsynctask extends AsyncTask<Void, Integer, Boolean> {
    ProgressDialog progress;

    private View view;
    private Context context;
    private File input;
    private File output;
    private String appName;

    public CopyFileAsynctask(View view, Context context, File input, File output, String appName) {
        this.view = view;
        this.context = context;
        this.input = input;
        this.output = output;
        this.appName = appName;
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
        progress.setMessage(String.format(context.getResources().getString(R.string.copying_apk), output.getPath()));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setMax((int) input.length());
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected Boolean doInBackground(Void... Void) {
        try {
            copyFiles(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        progress.dismiss();
        showSnackBar(view);
    }

    private void showSnackBar(View view) {
        if (view != null) {

            boolean isFileExplorer=false;

            Uri selectedUri = Uri.parse(CommonFunctions.getBackupDir());
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");

            if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
                isFileExplorer=true;
            } else {
                isFileExplorer=false;
            }


            Snackbar snackbar = Snackbar
                    .make(view, "APK file of "+appName+" has been copied to "+output.getPath(), Snackbar.LENGTH_LONG);

            if(isFileExplorer){
                snackbar.setAction("Open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(intent);
                    }
                });
            }
            snackbar.show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progress.setProgress(values[0]);
        progress.setProgressNumberFormat(CommonFunctions.humanReadableByteCount(values[0]) + " / " + CommonFunctions.humanReadableByteCount(input.length()));


    }

    public void copyFiles(File src, File dst) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(src);
        FileOutputStream fileOutputStream = new FileOutputStream(dst);
        byte[] var4 = new byte[1024];

        int total = 0;

        int var5;
        while ((var5 = fileInputStream.read(var4)) > 0) {
            fileOutputStream.write(var4, 0, var5);
            total += var5;
            publishProgress(total);
        }

        fileInputStream.close();
        fileOutputStream.close();
    }

}
