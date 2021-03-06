package com.deeppandya.appmanager.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appbrain.AdService;
import com.appbrain.AppBrain;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.fragments.AppBackedUpFragment;
import com.deeppandya.appmanager.fragments.AppManagerFragment;
import com.deeppandya.appmanager.managers.FirebaseManager;
import com.deeppandya.appmanager.managers.PersistanceManager;
import com.deeppandya.appmanager.managers.RuntimePermissionManager;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.util.CommonFunctions;
import com.deeppandya.appmanager.util.FirebaseUtil;

public class NavigationDrawerActivity extends AdsActivity
        implements NavigationView.OnNavigationItemSelectedListener, AppBackedUpFragment.OnListFragmentInteractionListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Handler mHandler;
    private AdService appBrainAds;

    float mainRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        appBrainAds = AppBrain.getAds();

        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setNavigationHeader();

        if (RuntimePermissionManager.checkSelfPermission(NavigationDrawerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            askForStoragePermission();
        }

        if (savedInstanceState == null) {
            loadAppUninstallManagerFragment();
        }

        if (!PersistanceManager.getUserFirstTime(NavigationDrawerActivity.this) && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.LAUNCH_INTERSTITIAL)) {
            loadInterstitialAd();
        }

        setRating(false);
    }

    private void askForStoragePermission() {
        new AlertDialog.Builder(NavigationDrawerActivity.this)
                .setMessage(R.string.allow_application_manager)
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RuntimePermissionManager.requestPermission(NavigationDrawerActivity.this, findViewById(android.R.id.content), Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                RuntimePermissionManager.PERMISSIONS_ACCOUNT, RuntimePermissionManager.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE,
                                getResources().getString(R.string.storage_permission_permissions_needed));
                    }
                })
                .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar snackbar = RuntimePermissionManager.createSnackBar(findViewById(android.R.id.content),
                                getResources().getString(R.string.storage_permission_go_to_settings), Snackbar.LENGTH_LONG);

                        snackbar.setAction(R.string.action_settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CommonFunctions.openAppProperties(NavigationDrawerActivity.this, getPackageName());
                            }
                        });
                        snackbar.show();
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RuntimePermissionManager.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadAppBackUpManagerFragment();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    RuntimePermissionManager.createSnackBar(findViewById(android.R.id.content),
                            getResources().getString(R.string.storage_permission_permissions_needed), Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar snackbar = RuntimePermissionManager.createSnackBar(findViewById(android.R.id.content),
                            getResources().getString(R.string.storage_permission_go_to_settings), Snackbar.LENGTH_LONG);

                    snackbar.setAction(R.string.action_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommonFunctions.openAppProperties(NavigationDrawerActivity.this, getPackageName());
                        }
                    });
                    snackbar.show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void loadAppUninstallManagerFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", AppCategory.UNINSTALL);

        Fragment fragment = new AppManagerFragment();
        fragment.setArguments(bundle);

        setToolbarTitle(AppCategory.UNINSTALL);

        loadFragment(fragment);
    }

    private void loadAppBackUpManagerFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", AppCategory.BACKUP);

        Fragment fragment = new AppManagerFragment();
        fragment.setArguments(bundle);

        setToolbarTitle(AppCategory.BACKUP);

        loadFragment(fragment);
    }

    private void loadAppPermissionsManagerFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", AppCategory.PERMISSIONS);

        Fragment fragment = new AppManagerFragment();
        fragment.setArguments(bundle);

        setToolbarTitle(AppCategory.PERMISSIONS);

        loadFragment(fragment);
    }

    private void loadAppPackageManagerFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", AppCategory.PACKAGE);

        Fragment fragment = new AppManagerFragment();
        fragment.setArguments(bundle);

        setToolbarTitle(AppCategory.PACKAGE);

        loadFragment(fragment);
    }

    private void setToolbarTitle(AppCategory appCategory) {
        if (appCategory == AppCategory.UNINSTALL) {
            if(getSupportActionBar()!=null) getSupportActionBar().setTitle(getResources().getString(R.string.uninstall_manager));
        } else if (appCategory == AppCategory.BACKUP) {
            if(getSupportActionBar()!=null) getSupportActionBar().setTitle(getResources().getString(R.string.backup_manager));
        } else if (appCategory == AppCategory.PERMISSIONS) {
            if(getSupportActionBar()!=null) getSupportActionBar().setTitle(getResources().getString(R.string.permission_manager));
        } else if (appCategory == AppCategory.PACKAGE) {
            if(getSupportActionBar()!=null) getSupportActionBar().setTitle(getResources().getString(R.string.package_manager));
        } else if (appCategory == AppCategory.BACKEDUP) {
            if(getSupportActionBar()!=null) getSupportActionBar().setTitle(getResources().getString(R.string.backed_up_apps));
        }
    }

    private void setNavigationHeader() {
        View navHeader = navigationView.getHeaderView(0);
        LinearLayout imgRate = navHeader.findViewById(R.id.img_rate);
        imgRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NavigationDrawerActivity.this, getResources().getString(R.string.rate_us), Toast.LENGTH_SHORT).show();
                //CommonFunctions.rateApp(NavigationDrawerActivity.this);
                setRating(true);
            }
        });

        LinearLayout imgAppOfTheDay = navHeader.findViewById(R.id.img_app_of_day);
        imgAppOfTheDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NavigationDrawerActivity.this, getResources().getString(R.string.app_of_the_day), Toast.LENGTH_SHORT).show();
                loadInterstitialAd();
            }
        });

        LinearLayout imgOfferWall = navHeader.findViewById(R.id.img_offer_wall);
        imgOfferWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NavigationDrawerActivity.this, getResources().getString(R.string.recommended_apps), Toast.LENGTH_SHORT).show();
            }
        });

        appBrainAds.setOfferWallClickListener(NavigationDrawerActivity.this, imgOfferWall);
    }

    private void loadFragment(final Fragment fragment) {
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

        mHandler.post(mPendingRunnable);

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_app_uninstall_manager) {
            loadAppUninstallManagerFragment();
        } else if (id == R.id.nav_app_backup_manager) {
            if (RuntimePermissionManager.checkSelfPermission(NavigationDrawerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                RuntimePermissionManager.requestPermission(NavigationDrawerActivity.this, findViewById(android.R.id.content), Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        RuntimePermissionManager.PERMISSIONS_ACCOUNT, RuntimePermissionManager.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE,
                        getResources().getString(R.string.storage_permission_permissions_needed));
            } else {
                loadAppBackUpManagerFragment();
            }
        } else if (id == R.id.nav_app_permission_manager) {
            loadAppPermissionsManagerFragment();
        } else if (id == R.id.nav_app_package_manager) {
            loadAppPackageManagerFragment();
        }
// else if (id == R.id.nav_app_backedup_manager) {
//            loadAppBackedUpManagerFragment();
//        }
        else if (id == R.id.nav_share) {
            CommonFunctions.shareApp(NavigationDrawerActivity.this, getResources().getString(R.string.app_name), getPackageName());
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(NavigationDrawerActivity.this, IssueReporterActivity.class);
            intent.putExtra("action", IssueReporterActivity.FEEDBACK);
            startActivity(intent);
        } else if (id == R.id.nav_feature_request) {
            Intent intent = new Intent(NavigationDrawerActivity.this, IssueReporterActivity.class);
            intent.putExtra("action", IssueReporterActivity.FEATUREREQUEST);
            startActivity(intent);
        } else if (id == R.id.nav_bug_report) {
            Intent intent = new Intent(NavigationDrawerActivity.this, IssueReporterActivity.class);
            intent.putExtra("action", IssueReporterActivity.BUGREPORT);
            startActivity(intent);
        } else if (id == R.id.nav_help) {
            CommonFunctions.openIntro(NavigationDrawerActivity.this, true);
        } else if (id == R.id.nav_privacy_policy) {
            CommonFunctions.openWebView(NavigationDrawerActivity.this);
        } else if (id == R.id.nav_about) {
            openAboutDialog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    private void loadAppBackedUpManagerFragment() {
//
//        Fragment fragment = new AppBackedUpFragment();
//        setToolbarTitle(AppCategory.BACKUP);
//
//        loadFragment(fragment);
//    }

    private void openAboutDialog() {

        PackageInfo pInfo;
        String version = null;

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                NavigationDrawerActivity.this);

        // set txtGroupText
        alertDialogBuilder.setTitle(getResources().getString(R.string.about));

        // set dialog message
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.app_name) + " version : " + version)
                .setCancelable(false)
                .setNeutralButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onListFragmentInteraction(AppModel appModel) {
        Log.e("item Clicked", appModel.getAppName());
    }

    private void setRating(boolean isFromMenu) {

        final RatingDialog.Builder ratingDialogBuilder = new RatingDialog.Builder(this);

        ratingDialogBuilder.icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .title(getResources().getString(R.string.how_was_your_exp))
                .titleTextColor(R.color.black)
                .positiveButtonText(getResources().getString(R.string.not_now))
                .negativeButtonText(getResources().getString(R.string.rating_dialog_never))
                .positiveButtonTextColor(R.color.white)
                .negativeButtonTextColor(R.color.grey_500)
                .formTitle(getResources().getString(R.string.title_send_feedback))
                .formHint(getResources().getString(R.string.tell_us_what_went_wrong))
                .formSubmitText(getResources().getString(R.string.rating_dialog_submit))
                .formCancelText(getResources().getString(R.string.rating_dialog_cancel))
                .ratingBarColor(R.color.colorAccent)
                .threshold(4)
                .positiveButtonBackgroundColor(R.drawable.button_selector_positive)
                .negativeButtonBackgroundColor(R.drawable.button_selector_negative)
                .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {
                        mainRating = rating;
                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        FirebaseUtil.saveUserFeedbackToFirebase(mainRating, feedback);
                    }
                });

        if (!isFromMenu) {
            ratingDialogBuilder.session(3);
        }

        RatingDialog ratingDialog = ratingDialogBuilder.build();
        ratingDialog.show();
    }
}
