package com.deeppandya.appmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.deeppandya.appmanager.adapter.AppAdapter;
import com.deeppandya.appmanager.asynctask.AppListLoader;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.enums.SortOrder;
import com.deeppandya.appmanager.enums.SortType;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.util.CommonFunctions;
import com.deeppandya.appmanager.util.DividerItemDecoration;
import com.deeppandya.appmanager.util.PersistanceManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<AppModel>> {

    private RecyclerView recyclerView;
    private AppAdapter mAdapter;

    public static final int ID_LOADER_APP_LIST = 0;
    private RelativeLayout adViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarTitle(getAppcategory());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adViewLayout = (RelativeLayout) findViewById(R.id.adView);

        mAdapter = new AppAdapter(findViewById(android.R.id.content), MainActivity.this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setAppCategory(getAppcategory());

        recyclerView.setAdapter(mAdapter);

        loadAdMobBannerAd(getAppcategory());

        getSupportLoaderManager().initLoader(ID_LOADER_APP_LIST,null,this);

    }

    private void loadAdMobBannerAd(AppCategory appCategory) {

        String adUnitId="";

        if(appCategory==AppCategory.UNINSTALL){
            adUnitId=getResources().getString(R.string.appmanager_app_uninstall_banner);
        }else if(appCategory==AppCategory.BACKUP){
            adUnitId=getResources().getString(R.string.appmanager_app_backup_banner);
        }else if(appCategory==AppCategory.PERMISSIONS){
            adUnitId=getResources().getString(R.string.appmanager_app_permissions_banner);
        }else if(appCategory==AppCategory.PACKAGE){
            adUnitId=getResources().getString(R.string.appmanager_app_package_banner);
        }

        AdView mAdView = new AdView(this);
        mAdView.setAdUnitId(adUnitId);
        mAdView.setAdSize(AdSize.BANNER);

        adViewLayout.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("DD88CB4BC53A57945289D53A627F700A")
                .build();
        mAdView.loadAd(adRequest);
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

    public AppCategory getAppcategory(){
        if(getIntent()!=null && getIntent().getSerializableExtra("category")!=null){
            return (AppCategory)getIntent().getSerializableExtra("category");
        }
        return AppCategory.UNINSTALL;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sortby:
                showSortDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<List<AppModel>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(MainActivity.this);
    }

    @Override
    public void onLoadFinished(Loader<List<AppModel>> loader, List<AppModel> data) {
// set new data to adapter

        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.mainProgress).setVisibility(View.GONE);

        mAdapter.setAppList(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<AppModel>> loader) {
        mAdapter.setAppList(null);
    }

    public void showSortDialog() {
        String[] sort = getResources().getStringArray(R.array.sortbyApps);
        SortType current = PersistanceManager.getSortType(MainActivity.this);
        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(MainActivity.this);
        materialDialogBuilder.items(sort).itemsCallbackSingleChoice(current.getSortTypeValue(), new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                return true;
            }
        });
        materialDialogBuilder.positiveText(R.string.ascending).positiveColor(getResources().getColor(R.color.colorAccent));
        materialDialogBuilder.negativeText(R.string.descending).negativeColor(getResources().getColor(R.color.colorAccent));
        materialDialogBuilder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                int which = dialog.getSelectedIndex();

                PersistanceManager.setSortOrder(MainActivity.this,SortOrder.ASC);
                PersistanceManager.setSortType(MainActivity.this,SortType.getSortTypeByInt(which));

                getSupportLoaderManager().restartLoader(MainActivity.ID_LOADER_APP_LIST, null, MainActivity.this);
                dialog.dismiss();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                int which = dialog.getSelectedIndex();

                PersistanceManager.setSortOrder(MainActivity.this,SortOrder.DESC);
                PersistanceManager.setSortType(MainActivity.this,SortType.getSortTypeByInt(which));

                getSupportLoaderManager().restartLoader(MainActivity.ID_LOADER_APP_LIST, null, MainActivity.this);
                dialog.dismiss();
            }
        });
        materialDialogBuilder.title(R.string.sortby);
        materialDialogBuilder.build().show();
    }

}
