package com.deeppandya.appmanager.enums;

/**
 * Created by d_pandya on 3/8/17.
 */

public enum SortOrder {
    ASC(1),DESC(-1);

    private int value;

    SortOrder(int i) {
        value=i;
    }

    public int getSortOrderValue(){
        return value;
    }

    public static SortOrder toSortOrder (String sortTypeString) {
        try {
            return valueOf(sortTypeString);
        } catch (Exception ex) {
            // For error cases
            return ASC;
        }
    }
}
