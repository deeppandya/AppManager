package com.deeppandya.appmanager.model;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.deeppandya.appmanager.util.CommonFunctions;

import java.util.Calendar;

/**
 * Created by d_pandya on 3/7/17.
 */

public class AppModel implements Parcelable {

    private static final String CURRENT_YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

    public AppModel(Parcel im) {
        title = im.readString();
        desc = im.readString();
        permissions = im.readString();
        symlink = im.readString();
        int j = im.readInt();
        date = im.readLong();
        int i = im.readInt();
        if (i == 0) {
            header = false;
        } else {
            header = true;
        } if (j == 0) {
            isDirectory = false;
        } else {
            isDirectory= true;
        }
        // don't save bitmaps in parcel, it might exceed the allowed transaction threshold
        //Bitmap bitmap = (Bitmap) im.readParcelable(getClass().getClassLoader());
        // Convert Bitmap to Drawable:
        //imageId = new BitmapDrawable(bitmap);
        date1 = im.readString();
        size = im.readString();
        longSize=im.readLong();
    }


    public int describeContents() {
        // TODO: Implement this method
        return 0;
    }

    public void writeToParcel(Parcel p1, int p2) {
        p1.writeString(title);
        p1.writeString(desc);
        p1.writeString(permissions);
        p1.writeString(symlink);
        p1.writeInt(isDirectory?1:0);
        p1.writeLong(date);
        p1.writeInt(header ? 1 : 0);
        //p1.writeParcelable(imageId.getBitmap(), p2);
        p1.writeString(date1);
        p1.writeString(size);
        p1.writeLong(longSize);
        // TODO: Implement this method
    }

    private BitmapDrawable imageId;
    private String title;
    private String desc;
    private String permissions;
    private String symlink;
    private String size;
    private boolean isDirectory;
    private long date = 0,longSize=0;
    private String date1 = "";
    private boolean header;
    //same as hfile.modes but different than openmode in Main.java
    //private OpenMode mode = OpenMode.FILE;

    public AppModel(BitmapDrawable imageId, String title, String desc, String permissions,
                          String symlink, String size, long longSize, boolean header, String date, boolean isDirectory) {
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
        this.permissions = permissions.trim();
        this.symlink = symlink.trim();
        this.size = size;
        this.header = header;
        this.longSize=longSize;
        this.isDirectory = isDirectory;
        if (!date.trim().equals("")) {
            this.date = Long.parseLong(date);
            this.date1 = CommonFunctions.getdate(this.date, CURRENT_YEAR);
        }
    }

    public static final Parcelable.Creator<AppModel> CREATOR =
            new Parcelable.Creator<AppModel>() {
                public AppModel createFromParcel(Parcel in) {
                    return new AppModel(in);
                }

                public AppModel[] newArray(int size) {
                    return new AppModel[size];
                }
            };

    public Drawable getImageId() {
        return imageId;
    }

    public void setImageId(BitmapDrawable imageId){this.imageId=imageId;}
    public String getDesc() {
        return desc.toString();
    }


    public String getTitle() {
        return title.toString();
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getSize() {
        return size;
    }

    public long getlongSize() {
        return longSize;
    }

    public String getDate() {
        return date1;
    }

    public long getDate1() {
        return date;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getSymlink() {
        return symlink;
    }

    public boolean hasSymlink() {
        if (getSymlink() != null && getSymlink().length() != 0) {
            return true;
        } else return false;
    }

    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}
