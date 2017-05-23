package com.deeppandya.appmanager.model;

/**
 * Created by deeppandya on 2017-05-22.
 */

public class PermissionModel {
    private String groupText;
    private String permissionText;
    private int permissionIcon;

    public String getGroupText() {
        return groupText;
    }

    public void setGroupText(String groupText) {
        this.groupText = groupText;
    }

    public String getPermissionText() {
        return permissionText;
    }

    public void setPermissionText(String permissionText) {
        this.permissionText = permissionText;
    }

    public int getPermissionIcon() {
        return permissionIcon;
    }

    public void setPermissionIcon(int permissionIcon) {
        this.permissionIcon = permissionIcon;
    }
}
