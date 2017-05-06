package com.deeppandya.appmanager.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.adapter.TabPagerAdapter;
import com.deeppandya.appmanager.enums.AppCategory;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppManagerFragment extends Fragment {

    private AppCategory appCategory;

    public AppManagerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_uninstall_manager, container, false);

        if(getArguments()!=null && getArguments().get("category")!=null){
            appCategory=(AppCategory)getArguments().get("category");
        }

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.user_apps)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.system_apps)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabPagerAdapter(getActivity(),getFragmentManager(), tabLayout.getTabCount(),appCategory));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }
}
