package com.deeppandya.appmanager.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appbrain.AdService;
import com.appbrain.AppBrain;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.fragments.AppManagerFragment;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.util.CommonFunctions;

public class NavigationDrawerActivity extends AdsActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private LinearLayout imgRate,imgAppOfTheDay,imgOfferWall;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler=new Handler();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setNavigationHeader();

        if (savedInstanceState == null) {
            loadAppUninstallManagerFragment();
        }
    }

    private void loadAppUninstallManagerFragment() {
        Bundle bundle=new Bundle();
        bundle.putSerializable("category", AppCategory.UNINSTALL);

        Fragment fragment=new AppManagerFragment();
        fragment.setArguments(bundle);

        setToolbarTitle(AppCategory.UNINSTALL);

        loadFragment(fragment);
    }

    private void loadAppBackUpManagerFragment() {
        Bundle bundle=new Bundle();
        bundle.putSerializable("category", AppCategory.BACKUP);

        Fragment fragment=new AppManagerFragment();
        fragment.setArguments(bundle);

        setToolbarTitle(AppCategory.BACKUP);

        loadFragment(fragment);
    }

    private void loadAppPermissionsManagerFragment() {
        Bundle bundle=new Bundle();
        bundle.putSerializable("category", AppCategory.PERMISSIONS);

        Fragment fragment=new AppManagerFragment();
        fragment.setArguments(bundle);

        setToolbarTitle(AppCategory.PERMISSIONS);

        loadFragment(fragment);
    }

    private void loadAppPackageManagerFragment() {
        Bundle bundle=new Bundle();
        bundle.putSerializable("category", AppCategory.PACKAGE);

        Fragment fragment=new AppManagerFragment();
        fragment.setArguments(bundle);

        setToolbarTitle(AppCategory.PACKAGE);

        loadFragment(fragment);
    }

    private void setToolbarTitle(AppCategory appCategory) {
        if(appCategory==AppCategory.UNINSTALL){
            getSupportActionBar().setTitle(getResources().getString(R.string.uninstall_manager));
        }else if(appCategory==AppCategory.BACKUP){
            getSupportActionBar().setTitle(getResources().getString(R.string.backup_manager));
        }else if(appCategory==AppCategory.PERMISSIONS){
            getSupportActionBar().setTitle(getResources().getString(R.string.permission_manager));
        }else if(appCategory==AppCategory.PACKAGE){
            getSupportActionBar().setTitle(getResources().getString(R.string.package_manager));
        }
    }

    private void setNavigationHeader() {
        navHeader = navigationView.getHeaderView(0);
        imgRate=(LinearLayout) navHeader.findViewById(R.id.img_rate);
        imgRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NavigationDrawerActivity.this, getResources().getString(R.string.rate_us), Toast.LENGTH_SHORT).show();
                CommonFunctions.rateApp(NavigationDrawerActivity.this);
            }
        });

        imgAppOfTheDay=(LinearLayout) navHeader.findViewById(R.id.img_app_of_day);
        imgAppOfTheDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NavigationDrawerActivity.this, getResources().getString(R.string.app_of_the_day), Toast.LENGTH_SHORT).show();
                loadAdMobInterstitialAd();
            }
        });

        imgOfferWall=(LinearLayout) navHeader.findViewById(R.id.img_offer_wall);
        imgOfferWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NavigationDrawerActivity.this, getResources().getString(R.string.recommended_apps), Toast.LENGTH_SHORT).show();
                AdService ads = AppBrain.getAds();
                ads.setOfferWallClickListener(NavigationDrawerActivity.this,view);
            }
        });
    }

    private void loadFragment(final Fragment fragment) {

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, fragment.getTag());
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        AdService ads = AppBrain.getAds();
        MenuItem item = menu.findItem(R.id.action_amazing_apps);
        ads.setOfferWallMenuItemClickListener(this, item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_app_uninstall_manager) {
            loadAppUninstallManagerFragment();
        } else if (id == R.id.nav_app_backup_manager) {
            loadAppBackUpManagerFragment();
        } else if (id == R.id.nav_app_permission_manager) {
            loadAppPermissionsManagerFragment();
        } else if (id == R.id.nav_app_package_manager) {
            loadAppPackageManagerFragment();
        } else if (id == R.id.nav_share) {
            CommonFunctions.shareApp(NavigationDrawerActivity.this, getResources().getString(R.string.app_name), getPackageName());
        } else if (id == R.id.nav_feedback) {
            CommonFunctions.sendMessageToDev(NavigationDrawerActivity.this, getResources().getString(R.string.mail_feedback_subject), getResources().getString(R.string.title_send_feedback));
        } else if (id == R.id.nav_feature_request) {
            CommonFunctions.sendMessageToDev(NavigationDrawerActivity.this, getResources().getString(R.string.mail_feature_request_subject), getResources().getString(R.string.title_send_feature_request));
        } else if (id == R.id.nav_bug_report) {
            CommonFunctions.sendMessageToDev(NavigationDrawerActivity.this, getResources().getString(R.string.mail_bug_report_subject), getResources().getString(R.string.title_send_bug_report));
        } else if (id == R.id.nav_help) {
            CommonFunctions.openIntro(NavigationDrawerActivity.this);
        } else if (id == R.id.nav_privacy_policy) {
            CommonFunctions.openWebView(NavigationDrawerActivity.this,"https://firebasestorage.googleapis.com/v0/b/app-manager-7b1bf.appspot.com/o/PrivacyPolicy.pdf?alt=media&token=f80434dc-d18c-4976-bf58-890f111cf7ad");
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
