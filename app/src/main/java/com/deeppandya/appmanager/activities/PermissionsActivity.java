package com.deeppandya.appmanager.activities;

import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.adapter.PermissionsAdapter;
import com.deeppandya.appmanager.managers.PermissionGroupsManager;
import com.deeppandya.appmanager.managers.PermissionsWithoutGroupsManager;
import com.deeppandya.appmanager.model.PermissionModel;
import com.deeppandya.appmanager.util.CommonFunctions;

import java.util.ArrayList;
import java.util.List;

public class PermissionsActivity extends AdsActivity {

    private RecyclerView recyclerView;
    private PermissionsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemissions);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new PermissionsAdapter(PermissionsActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Button btnGotoSettings = findViewById(R.id.btnGoToSettings);
        btnGotoSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonFunctions.openAppProperties(PermissionsActivity.this, getAppPackageName());
                loadInterstitialAd();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getAppName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setPermissionData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setPermissionData() {
        if (getIntent() != null && getIntent().getStringArrayExtra("permissions") != null) {

            String[] permissions = getIntent().getStringArrayExtra("permissions");

            List<PermissionModel> permissionModels = new ArrayList<>();

            for (String permission : permissions) {
                try {
                    PermissionInfo pinfo = getPackageManager().getPermissionInfo(permission, PackageManager.GET_META_DATA);

                    PermissionModel permissionModel = new PermissionModel();

                    if (pinfo.group != null && PermissionGroupsManager.getPermissionGroups().get(pinfo.group) != null) {
                        permissionModel.setGroupText(PermissionGroupsManager.getPermissionGroups().get(pinfo.group).first);
                        permissionModel.setPermissionText(pinfo.loadLabel(getPackageManager()).toString());
                        permissionModel.setPermissionIcon(PermissionGroupsManager.getPermissionGroups().get(pinfo.group).second);
                    } else if (PermissionsWithoutGroupsManager.getPermissionWithoutGroups().get(permission) != null) {
                        String[] group = permission.split("\\.");
                        String groupText = (group[group.length - 1]).replace("_", " ");

                        permissionModel.setGroupText(groupText);
                        permissionModel.setPermissionText(pinfo.loadLabel(getPackageManager()).toString());
                        permissionModel.setPermissionIcon(PermissionsWithoutGroupsManager.getPermissionWithoutGroups().get(permission));
                    } else {
                        String[] group = permission.split("\\.");
                        String groupText = (group[group.length - 1]).replace("_", " ");

                        permissionModel.setGroupText(groupText);
                        permissionModel.setPermissionText(pinfo.loadLabel(getPackageManager()).toString());
                        permissionModel.setPermissionIcon(PermissionGroupsManager.getPermissionGroups().get("SYSTEM").second);
                    }

                    permissionModels.add(permissionModel);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            mAdapter.setPermissionModels(permissionModels);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        }
    }

    public String getAppPackageName() {
        if (getIntent() != null && getIntent().getStringExtra("packageName") != null) {
            return getIntent().getStringExtra("packageName");
        }
        return "";
    }

    public String getAppName() {
        if (getIntent() != null && getIntent().getStringExtra("appName") != null) {
            return getIntent().getStringExtra("appName");
        }
        return "";
    }

}
