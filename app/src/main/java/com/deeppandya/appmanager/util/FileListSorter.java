package com.deeppandya.appmanager.util;

import com.deeppandya.appmanager.model.AppModel;
import java.util.Comparator;

public class FileListSorter implements Comparator<AppModel> {

    private int dirsOnTop = 0;

    private int asc = 1;
    int sort = 0;
    boolean rootMode;

    public FileListSorter(int dir, int sort, int asc, boolean rootMode) {
        this.dirsOnTop = dir;
        this.asc = asc;
        this.sort = sort;
        this.rootMode = rootMode;
    }

    boolean isDirectory(AppModel path) {
        return path.isDirectory();
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

        /*File f1;

        if(!file1.hasSymlink()) {

            f1=new File(file1.getDesc());
        } else {
            f1=new File(file1.getSymlink());
        }

        File f2;

        if(!file2.hasSymlink()) {

            f2=new File(file2.getDesc());
        } else {
            f2=new File(file1.getSymlink());
        }*/

        if (dirsOnTop == 0) {
            if (isDirectory(file1) && !isDirectory(file2)) {
                return -1;


            } else if (isDirectory(file2) && !isDirectory(file1)) {
                return 1;
            }
        } else if (dirsOnTop == 1) {
            if (isDirectory(file1) && !isDirectory(file2)) {

                return 1;
            } else if (isDirectory(file2) && !isDirectory(file1)) {
                return -1;
            }
        }

        if (sort == 0) {

            // sort by name
            return asc * file1.getTitle().compareToIgnoreCase(file2.getTitle());
        } else if (sort == 1) {

            // sort by last modified
            return asc * Long.valueOf(file1.getDate1()).compareTo(Long.valueOf(file2.getDate1()));
        } else if (sort == 2) {

            // sort by size
            if (!file1.isDirectory() && !file2.isDirectory()) {

                return asc * Long.valueOf(file1.getlongSize()).compareTo(Long.valueOf(file2.getlongSize()));
            } else {

                return file1.getTitle().compareToIgnoreCase(file2.getTitle());
            }

        } else if (sort == 3) {

            // sort by type
            if (!file1.isDirectory() && !file2.isDirectory()) {

                final String ext_a = getExtension(file1.getTitle());
                final String ext_b = getExtension(file2.getTitle());


                final int res = asc * ext_a.compareTo(ext_b);
                if (res == 0) {
                    return asc * file1.getTitle().compareToIgnoreCase(file2.getTitle());
                }
                return res;
            } else {
                return file1.getTitle().compareToIgnoreCase(file2.getTitle());
            }
        }
        return 0;

    }

    static String getExtension(String a) {
        return a.substring(a.lastIndexOf(".") + 1).toLowerCase();
    }

}
