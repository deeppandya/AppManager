package com.deeppandya.appmanager.enums;

/**
 * Created by d_pandya on 3/8/17.
 */

public enum AppSortType {
    BYNAME(0),BYDATE(1),BYSIZE(2);

    private int value;

    AppSortType(int i) {
        value=i;
    }

    public int getSortTypeValue(){
        return value;
    }

    public static AppSortType toSortType (String sortTypeString) {
        try {
            return valueOf(sortTypeString);
        } catch (Exception ex) {
            // For error cases
            return BYNAME;
        }
    }

    public static AppSortType getSortTypeByInt(int which) {
        if(which==0){
            return BYNAME;
        }else if(which==1){
            return BYDATE;
        }else{
            return BYSIZE;
        }
    }
}
