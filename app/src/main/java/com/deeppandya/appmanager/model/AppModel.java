package com.deeppandya.appmanager.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.deeppandya.appmanager.enums.AppType;
import com.deeppandya.appmanager.util.CommonFunctions;

import java.util.Calendar;

/**
 * Created by d_pandya on 3/7/17.
 */

public class AppModel {

    private static final String CURRENT_YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

    public AppModel(){

    }

    private Drawable appIcon;
    private String appName;
    private String appDesc;
    private CharSequence[] permissions;
    private String symlink;
    private String size;
    private long date = 0,longSize=0;
    private String formattedDate = "";
    private String packageName;
    private AppType appType;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon){this.appIcon = appIcon;}
    public String getAppDesc() {
        return appDesc.toString();
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName.toString();
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setLongSize(long longSize) {
        this.longSize = longSize;
    }

    public long getLongSize() {
        return longSize;
    }

    public void setDate(long date) {
        this.date = date;
        setFormattedDate(CommonFunctions.getdate(this.date, CURRENT_YEAR));
    }

    public long getDate() {
        return date;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setPermissions(CharSequence[] permissions) {
        this.permissions = permissions;
    }

    public CharSequence[] getPermissions() {
        return permissions;
    }

    public void setSymlink(String symlink) {
        this.symlink = symlink;
    }

    public String getSymlink() {
        return symlink;
    }

    public boolean hasSymlink() {
        if (getSymlink() != null && getSymlink().length() != 0) {
            return true;
        } else return false;
    }

    public AppType getAppType() {
        return appType;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    @Override
    public String toString() {
        return appName + "\n" + appDesc;
    }
}
