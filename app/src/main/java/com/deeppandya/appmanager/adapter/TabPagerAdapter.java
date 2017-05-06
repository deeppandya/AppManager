package com.deeppandya.appmanager.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.fragments.SystemAppFragment;
import com.deeppandya.appmanager.fragments.UserAppFragment;

/**
 * Created by deeppandya on 2017-05-06.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private int mNumOfTabs;
    private AppCategory appCategory;

    // tab titles
    private String[] tabTitles;

    public TabPagerAdapter(Context context, FragmentManager fm, int NumOfTabs, AppCategory appCategory) {
        super(fm);
        this.context=context;
        tabTitles= new String[]{context.getResources().getString(R.string.user_apps),context.getResources().getString(R.string.system_apps)};
        this.mNumOfTabs = NumOfTabs;
        this.appCategory=appCategory;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                UserAppFragment userAppFragment=new UserAppFragment();
                Bundle userAppBundle=new Bundle();
                userAppBundle.putSerializable("category", appCategory);
                userAppFragment.setArguments(userAppBundle);
                return userAppFragment;
            case 1:
                SystemAppFragment systemAppFragment=new SystemAppFragment();
                Bundle systemAppBundle=new Bundle();
                systemAppBundle.putSerializable("category", appCategory);
                systemAppFragment.setArguments(systemAppBundle);
                return systemAppFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
