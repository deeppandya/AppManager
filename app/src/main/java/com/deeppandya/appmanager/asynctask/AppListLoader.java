package com.deeppandya.appmanager.asynctask;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.AsyncTaskLoader;
import android.text.format.Formatter;

import com.deeppandya.appmanager.enums.AppType;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.enums.SortOrder;
import com.deeppandya.appmanager.enums.AppSortType;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.receiver.PackageReceiver;
import com.deeppandya.appmanager.util.ConfigChange;
import com.deeppandya.appmanager.managers.PersistanceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by d_pandya on 3/7/17.
 */

public class AppListLoader extends AsyncTaskLoader<List<AppModel>> {

    private PackageManager packageManager;
    private PackageReceiver packageReceiver;
    private Context context;
    private List<AppModel> mApps;
    private SortOrder sortOrder;
    private AppSortType appSortType;

    public AppListLoader(Context context) {
        super(context);

        this.context = context;
        this.appSortType = PersistanceManager.getSortType(context);
        this.sortOrder = PersistanceManager.getSortOrder(context);

        /**
         * using global context because of the fact that loaders are supposed to be used
         * across fragments and activities
         */
        packageManager = getContext().getPackageManager();
    }

    @Override
    public List<AppModel> loadInBackground() {

        List<ApplicationInfo> apps = packageManager.getInstalledApplications(
                PackageManager.MATCH_UNINSTALLED_PACKAGES |
                        PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS);

        if (apps == null)
            apps = new ArrayList<>();

        mApps = new ArrayList<>(apps.size());

        for (ApplicationInfo object : apps) {
            File sourceDir = new File(object.sourceDir);

            String label = object.loadLabel(packageManager).toString();

            AppModel appModel=new AppModel();
            appModel.setPackageName(object.packageName);
            appModel.setAppIcon(object.loadIcon(packageManager) !=null ? object.loadIcon(packageManager): new BitmapDrawable(context.getResources(),BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)));
            appModel.setAppName(label == null ? object.packageName : label);
            appModel.setAppDesc(object.sourceDir);
            appModel.setPermissions(getAppPermissions(packageManager,object.packageName));
            appModel.setSymlink(object.flags+"");
            appModel.setSize(Formatter.formatFileSize(getContext(), sourceDir.length()));
            appModel.setLongSize(sourceDir.length());
            appModel.setDate(sourceDir.lastModified());

            if ((object.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // System app
                appModel.setAppType(AppType.SYSTEMAPP);
            } else {
                // User installed app
                appModel.setAppType(AppType.USERAPP);
            }

            mApps.add(appModel);

        }

        //Collections.sort(mApps, new FileListSorter(appSortType, sortOrder));

        return mApps;
    }

    @Override
    public void deliverResult(List<AppModel> data) {
        if (isReset()) {

            if (data != null)
                onReleaseResources(data);
        }

        // preserving old data for it to be closed
        List<AppModel> oldData = mApps;
        mApps = data;
        if (isStarted()) {
            // loader has been started, if we have data, return immediately
            super.deliverResult(mApps);
        }

        // releasing older resources as we don't need them now
        if (oldData != null) {
            onReleaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {

        if (mApps != null) {
            // we already have the results, load immediately
            deliverResult(mApps);
        }

        if (packageReceiver != null) {
            packageReceiver = new PackageReceiver(this);
        }

        boolean didConfigChange = ConfigChange.isConfigChanged(getContext().getResources());

        if (takeContentChanged() || mApps == null || didConfigChange) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<AppModel> data) {
        super.onCanceled(data);

        onReleaseResources(data);
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        // we're free to clear resources
        if (mApps != null) {
            onReleaseResources(mApps);
            mApps = null;
        }

        if (packageReceiver != null) {
            getContext().unregisterReceiver(packageReceiver);

            packageReceiver = null;
        }

        ConfigChange.recycle();

    }

    /**
     * We would want to release resources here
     * List is nothing we would want to close
     * @param layoutelementsList
     */
    private void onReleaseResources(List<AppModel> layoutelementsList) {

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
