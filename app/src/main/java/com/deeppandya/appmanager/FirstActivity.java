package com.deeppandya.appmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.util.UninstallPreventionManager;

public class FirstActivity extends AppCompatActivity implements UninstallPreventionManager.UninstallPreventionListener{

    CardView uninstallCard,backupCard,permissionCard,packageCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        uninstallCard=(CardView)findViewById(R.id.uninstall_layout);
        uninstallCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.UNINSTALL);
                //UninstallPreventionManager.getInstance().enableDeviceAdmin(FirstActivity.this, null, true);
            }
        });

        backupCard=(CardView)findViewById(R.id.backup_layout);
        backupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.BACKUP);
            }
        });

        permissionCard=(CardView)findViewById(R.id.permission_layout);
        permissionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.PERMISSIONS);
            }
        });

        packageCard=(CardView)findViewById(R.id.package_layout);
        packageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(AppCategory.PACKAGE);
            }
        });

    }

    private void openActivity(AppCategory appCategory) {
        Intent intent=new Intent(FirstActivity.this,MainActivity.class);
        intent.putExtra("category",appCategory);
        startActivity(intent);
    }

    @Override
    public void OnActivateDeviceAdministrator() {
        UninstallPreventionManager.getInstance().enableDeviceAdmin(this, null, true);
    }

    @Override
    public void OnDeactivateDeviceAdministrator() {
        UninstallPreventionManager.getInstance().enableDeviceAdmin(this, null, false);
    }
}
