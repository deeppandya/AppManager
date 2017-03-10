package com.deeppandya.appmanager.util;

import com.deeppandya.appmanager.enums.SortOrder;
import com.deeppandya.appmanager.enums.AppSortType;
import com.deeppandya.appmanager.model.AppModel;
import java.util.Comparator;

public class FileListSorter implements Comparator<AppModel> {

    private SortOrder sortOrder;
    private AppSortType appSortType;

    public FileListSorter(AppSortType appSortType, SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        this.appSortType = appSortType;
    }

    /**
     * Compares two elements and return negative, zero and positive integer if first argument is
     * less than, equal to or greater than second
     *
     * @param file1
     * @param file2
     * @return
     */
    @Override
    public int compare(AppModel file1, AppModel file2) {

        if (appSortType == AppSortType.BYNAME) {

            // sort by name
            return sortOrder.getSortOrderValue() * file1.getAppName().compareToIgnoreCase(file2.getAppName());
        } else if (appSortType == AppSortType.BYDATE) {

            // sort by last modified
            return sortOrder.getSortOrderValue() * Long.valueOf(file1.getDate()).compareTo(Long.valueOf(file2.getDate()));
        } else if (appSortType == AppSortType.BYSIZE) {

            // sort by size
            return sortOrder.getSortOrderValue() * Long.valueOf(file1.getLongSize()).compareTo(Long.valueOf(file2.getLongSize()));

        }
        return 0;

    }

    static String getExtension(String a) {
        return a.substring(a.lastIndexOf(".") + 1).toLowerCase();
    }

}
