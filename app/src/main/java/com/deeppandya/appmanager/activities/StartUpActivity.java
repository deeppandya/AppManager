package com.deeppandya.appmanager.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.deeppandya.appmanager.AppManager;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.asynctask.GetAppsAsyncTask;
import com.deeppandya.appmanager.listeners.GetAppsListener;
import com.deeppandya.appmanager.managers.PersistanceManager;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.util.CommonFunctions;

import java.util.List;

public class StartUpActivity extends AdsActivity {

    private GetAppsAsyncTask getAppsAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MobileAds.initialize(getApplicationContext(), FirebaseManager.getRemoteConfig().getString(FirebaseManager.ADMOMB_APP_ID));

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_startup);

        getApps();
    }

    private void getApps() {
        GetAppsListener getAppsListener = new GetAppsListener() {
            @Override
            public void beforeGetApps() {
                findViewById(R.id.mainProgress).setVisibility(View.VISIBLE);
            }

            @Override
            public void afterGetApps(List<AppModel> appModels) {
                AppManager.apps.clear();
                AppManager.apps = appModels;

                if (PersistanceManager.getUserFirstTime(StartUpActivity.this)) {
                    finish();
                    CommonFunctions.openIntro(StartUpActivity.this, false);
                } else {
                    Intent navigationIntent = new Intent(StartUpActivity.this, NavigationDrawerActivity.class);
                    finish();
                    startActivity(navigationIntent);
                    overridePendingTransition(0,0);
                }

            }

            @Override
            public void onError() {
                findViewById(R.id.mainProgress).setVisibility(View.INVISIBLE);
            }
        };

        CommonFunctions.setApps(StartUpActivity.this,getAppsListener);

    }

    @Override
    protected void onDestroy() {
        if (getAppsAsyncTask != null && (getAppsAsyncTask.getStatus() == AsyncTask.Status.RUNNING || getAppsAsyncTask.getStatus() == AsyncTask.Status.PENDING)) {
            getAppsAsyncTask.cancel(true);
        }
        super.onDestroy();
    }
}
