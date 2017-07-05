package com.deeppandya.appmanager.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.enums.AppType;
import com.deeppandya.appmanager.fragments.AppFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deeppandya on 2017-05-06.
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private int mNumOfTabs;
    private AppCategory appCategory;

    // tab titles
    private List<String> tabTitles;

    public TabPagerAdapter(Context context, FragmentManager fm, int NumOfTabs, AppCategory appCategory) {
        super(fm);
        this.context=context;
        tabTitles=new ArrayList<>();
        tabTitles.add(context.getResources().getString(R.string.user_apps));
        tabTitles.add(context.getResources().getString(R.string.system_apps));
        this.mNumOfTabs = NumOfTabs;
        this.appCategory=appCategory;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AppFragment appFragment =new AppFragment();
                Bundle userAppBundle=new Bundle();
                userAppBundle.putSerializable("category", appCategory);
                userAppBundle.putString("type", AppType.USERAPP.toString());
                appFragment.setArguments(userAppBundle);
                return appFragment;
            case 1:
                AppFragment systemAppFragment=new AppFragment();
                Bundle systemAppBundle=new Bundle();
                systemAppBundle.putSerializable("category", appCategory);
                systemAppBundle.putString("type", AppType.SYSTEMAPP.toString());
                systemAppFragment.setArguments(systemAppBundle);
                return systemAppFragment;

            case 2:
                AppFragment backedUpAppFragment=new AppFragment();
                Bundle backedUpAppBundle=new Bundle();
                backedUpAppBundle.putSerializable("category", appCategory);
                backedUpAppBundle.putString("type", AppType.BACKEDUPAPP.toString());
                backedUpAppFragment.setArguments(backedUpAppBundle);
                return backedUpAppFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
