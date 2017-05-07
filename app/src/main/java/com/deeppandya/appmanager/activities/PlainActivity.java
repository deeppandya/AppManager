package com.deeppandya.appmanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.managers.PersistanceManager;
import com.deeppandya.appmanager.util.CommonFunctions;

public class PlainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PersistanceManager.getUserFirstTime(PlainActivity.this)) {
            finish();
            CommonFunctions.openIntro(PlainActivity.this, false);
        } else {
            Intent navigationIntent = new Intent(PlainActivity.this, NavigationDrawerActivity.class);
            finish();
            startActivity(navigationIntent);
            overridePendingTransition(0,0);
        }
    }
}
