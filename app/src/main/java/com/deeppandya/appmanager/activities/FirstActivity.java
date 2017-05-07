package com.deeppandya.appmanager.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.appbrain.AdService;
import com.appbrain.AppBrain;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.managers.AppMemoryManager;
import com.deeppandya.appmanager.managers.AppStorageManager;
import com.deeppandya.appmanager.managers.FirebaseManager;
import com.deeppandya.appmanager.managers.PersistanceManager;
import com.deeppandya.appmanager.managers.RuntimePermissionManager;
import com.deeppandya.appmanager.util.CommonFunctions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class FirstActivity extends AdsActivity implements OnChartValueSelectedListener {

    CardView uninstallCard, backupCard, permissionCard, packageCard;

    private FirebaseAnalytics mFirebaseAnalytics;

    private PieChart memoryChart, storageChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PersistanceManager.getUserFirstTime(FirstActivity.this)) {
            CommonFunctions.openIntro(FirstActivity.this,false);
        }

        setContentView(R.layout.activity_first);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        showMemoryPieChart();

        showStoragePieChart();

        sendAnalyticsEvent();

        uninstallCard = (CardView) findViewById(R.id.uninstall_layout);
        uninstallCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.UNINSTALL);
            }
        });

        backupCard = (CardView) findViewById(R.id.backup_layout);
        backupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RuntimePermissionManager.checkSelfPermission(FirstActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    RuntimePermissionManager.requestPermission(FirstActivity.this, findViewById(android.R.id.content), Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            RuntimePermissionManager.PERMISSIONS_ACCOUNT, RuntimePermissionManager.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE,
                            getResources().getString(R.string.storage_permission_permissions_needed));
                } else {
                    openActivity(AppCategory.BACKUP);
                }
            }
        });

        permissionCard = (CardView) findViewById(R.id.permission_layout);
        permissionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.PERMISSIONS);
            }
        });

        packageCard = (CardView) findViewById(R.id.package_layout);
        packageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.PACKAGE);
            }
        });

        if (FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.FIRST_SCREEN_BANNER))
            loadAdMobBannerAd();

        if (!PersistanceManager.getUserFirstTime(FirstActivity.this) && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.LAUNCH_INTERSTITIAL)) {
            loadAdMobInterstitialAd();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RuntimePermissionManager.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openActivity(AppCategory.BACKUP);
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
                            CommonFunctions.openAppProperties(FirstActivity.this,getPackageName());
                        }
                    });
                    snackbar.show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMemoryPieChart() {
        memoryChart = (PieChart) findViewById(R.id.memoryChart);
        setPieChart(memoryChart);

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        AppMemoryManager.MemoryState memoryState = AppMemoryManager.getMemoryState(FirstActivity.this);

        entries.add(new PieEntry(((float)(memoryState.getUsedSize() * 100) / memoryState.getTotalSize()), getResources().getString(R.string.used), null));
        entries.add(new PieEntry(((float)(memoryState.getFreeSize() * 100) / memoryState.getTotalSize()), getResources().getString(R.string.free), null));

        setMemoryData(entries, memoryChart);
    }

    private void showStoragePieChart() {
        storageChart = (PieChart) findViewById(R.id.storageChart);
        setPieChart(storageChart);

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        AppStorageManager.StorageState storageState = AppStorageManager.getStorageState();

        entries.add(new PieEntry(((float)(storageState.getUsedSize() * 100) / storageState.getTotalSize()), getResources().getString(R.string.used), null));
        entries.add(new PieEntry(((float)(storageState.getFreeSize() * 100) / storageState.getTotalSize()), getResources().getString(R.string.free), null));

        setMemoryData(entries, storageChart);

    }

    private void setPieChart(PieChart pieChart) {
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(22f);

        pieChart.setOnChartValueSelectedListener(this);

        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private void setMemoryData(ArrayList<PieEntry> entries, PieChart pieChart) {

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        int[] colors = {R.color.colorRed, R.color.colorBlue};

        dataSet.setColors(colors, FirstActivity.this);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);

        pieChart.highlightValues(null);

        pieChart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first_screen_menu, menu);
        AdService ads = AppBrain.getAds();
        MenuItem item = menu.findItem(R.id.action_amazing_apps);
        ads.setOfferWallMenuItemClickListener(this, item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rate:
                CommonFunctions.rateApp(FirstActivity.this);
                break;
            case R.id.action_help:
                CommonFunctions.openIntro(FirstActivity.this,true);
                break;
            case R.id.action_feedback:
                CommonFunctions.sendMessageToDev(FirstActivity.this,getResources().getString(R.string.mail_feedback_subject),getResources().getString(R.string.title_send_feedback));
                break;
            case R.id.action_feature_request:
                CommonFunctions.sendMessageToDev(FirstActivity.this,getResources().getString(R.string.mail_feature_request_subject),getResources().getString(R.string.title_send_feature_request));
                break;
            case R.id.action_bug_report:
                CommonFunctions.sendMessageToDev(FirstActivity.this,getResources().getString(R.string.mail_bug_report_subject),getResources().getString(R.string.title_send_bug_report));
                break;
            case R.id.action_privacy_policy:

                break;
            case R.id.action_share:
                CommonFunctions.shareApp(FirstActivity.this,getResources().getString(R.string.app_name),getPackageName());
                break;
            case R.id.action_exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendAnalyticsEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "deep");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void loadAdMobBannerAd() {
        showBanner(getString(R.string.appmanager_firstscreen_banner));
    }

    private void openActivity(AppCategory appCategory) {
        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
        intent.putExtra("category", appCategory);
        startActivity(intent);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {

    }
}
