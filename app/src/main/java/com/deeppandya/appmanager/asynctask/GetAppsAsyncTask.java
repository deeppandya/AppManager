package com.deeppandya.appmanager.asynctask;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.format.Formatter;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.enums.AppType;
import com.deeppandya.appmanager.listeners.GetAppsListener;
import com.deeppandya.appmanager.model.AppModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by deeppandya on 2017-05-06.
 */

public class GetAppsAsyncTask extends AsyncTask<Void, Integer, List<AppModel>> {
    private final PackageManager packageManager;
    private Context context;
    private GetAppsListener getAppsListener;
    private List<AppModel> mApps;

    public GetAppsAsyncTask(Context context,GetAppsListener getAppsListener) {
        this.context = context;
        this.getAppsListener=getAppsListener;
        packageManager = context.getPackageManager();
    }

    @Override
    protected void onPreExecute() {
        getAppsListener.beforeGetApps();
    }

    @Override
    protected List<AppModel> doInBackground(Void... Void) {
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        if (apps == null)
            apps = new ArrayList<>();

        mApps = new ArrayList<>(apps.size());

        for (ApplicationInfo object : apps) {
            File sourceDir = new File(object.sourceDir);

            String label = object.loadLabel(packageManager).toString();

            AppModel appModel = new AppModel();
            appModel.setPackageName(object.packageName);
            appModel.setAppIcon(object.loadIcon(packageManager) != null ? object.loadIcon(packageManager) : new BitmapDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)));
            appModel.setAppName(label == null ? object.packageName : label);
            appModel.setAppDesc(object.sourceDir);
            appModel.setPermissions(getAppPermissions(packageManager, object.packageName));
            appModel.setSymlink(object.flags + "");
            appModel.setSize(Formatter.formatFileSize(context, sourceDir.length()));
            appModel.setLongSize(sourceDir.length());
            appModel.setDate(sourceDir.lastModified());

            if ((object.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // System app
                appModel.setAppType(AppType.SYSTEMAPP);
            } else {
                // User installed app
                appModel.setAppType(AppType.USERAPP);
            }

            if (!appModel.getPackageName().equals(context.getPackageName()))
                mApps.add(appModel);

        }

        return mApps;
    }

    @Override
    protected void onPostExecute(List<AppModel> appModels) {
        if(appModels!=null && appModels.size()>0){
            getAppsListener.afterGetApps(appModels);
        }else{
            getAppsListener.onError();
        }
    }

    private CharSequence[] getAppPermissions(PackageManager packageManager, String packageName) {

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            return packageInfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException ex) {
            ex.getMessage();
        }

        return new CharSequence[0];
    }

}
