package com.deeppandya.appmanager.activities;

import android.app.SearchManager;
import android.media.ImageReader;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.adapter.AppAdapter;
import com.deeppandya.appmanager.asynctask.AppListLoader;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.enums.AppType;
import com.deeppandya.appmanager.enums.SortOrder;
import com.deeppandya.appmanager.enums.AppSortType;
import com.deeppandya.appmanager.managers.FirebaseManager;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.util.CommonFunctions;
import com.deeppandya.appmanager.util.DividerItemDecoration;
import com.deeppandya.appmanager.util.FileListSorter;
import com.deeppandya.appmanager.managers.PersistanceManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BannerActivity implements LoaderManager.LoaderCallbacks<List<AppModel>>, SearchView.OnQueryTextListener,RecyclerView.OnItemTouchListener,
        View.OnClickListener,
        ActionMode.Callback {

    private RecyclerView recyclerView;
    private AppAdapter mAdapter;

    GestureDetectorCompat gestureDetector;
    ActionMode actionMode;

    public static final int ID_LOADER_APP_LIST = 0;

    private boolean isUserApps=true;
    private boolean isSystemApps=false;

    private String appNameQuery;

    private List<AppModel> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apps=new ArrayList<>();

        appNameQuery="";

        setToolbarTitle(getAppcategory());

        setHintLayout();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new AppAdapter(findViewById(android.R.id.content), MainActivity.this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if(getAppcategory()==AppCategory.UNINSTALL || getAppcategory()==AppCategory.BACKUP) {
            recyclerView.addOnItemTouchListener(this);
            gestureDetector = new GestureDetectorCompat(this, new RecyclerViewDemoOnGestureListener());
        }

        mAdapter.setAppCategory(getAppcategory());

        recyclerView.setAdapter(mAdapter);

        loadAdMobBannerAd(getAppcategory());

        getSupportLoaderManager().initLoader(ID_LOADER_APP_LIST,null,this);

    }

    private void setHintLayout() {
        TextView txtHint=(TextView)findViewById(R.id.txtHint);
        final CardView hintLayout=(CardView)findViewById(R.id.hint_layout);
        if(getAppcategory()==AppCategory.UNINSTALL){
            txtHint.setText(getResources().getString(R.string.long_press_hint));
        }else if(getAppcategory()==AppCategory.BACKUP){
            txtHint.setText(String.format(getResources().getString(R.string.backup_can_be_found),CommonFunctions.getBackupDir()));
        }else{
            hintLayout.setVisibility(View.GONE);
        }

        ImageView btnClose=(ImageView)findViewById(R.id.btnHintCancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintLayout.setVisibility(View.GONE);
            }
        });

    }

    private void loadAdMobBannerAd(AppCategory appCategory) {

        String adUnitId="";

        if(appCategory==AppCategory.UNINSTALL && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.UNINSTALL_BANNER)){
            adUnitId=getResources().getString(R.string.appmanager_app_uninstall_banner);
            showBanner(adUnitId);
        }else if(appCategory==AppCategory.BACKUP && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.BACKUP_BANNER)){
            adUnitId=getResources().getString(R.string.appmanager_app_backup_banner);
            showBanner(adUnitId);
        }else if(appCategory==AppCategory.PERMISSIONS && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.PERMISSION_BANNER)){
            adUnitId=getResources().getString(R.string.appmanager_app_permissions_banner);
            showBanner(adUnitId);
        }else if(appCategory==AppCategory.PACKAGE && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.PACKAGE_BANNER)){
            adUnitId=getResources().getString(R.string.appmanager_app_package_banner);
            showBanner(adUnitId);
        }
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

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

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
            case R.id.action_user_apps:

                isUserApps=true;
                isSystemApps=false;

                setAppAdapter(isUserApps,isSystemApps,appNameQuery);
                break;
            case R.id.action_system_apps:
                isUserApps=false;
                isSystemApps=true;

                setAppAdapter(isUserApps,isSystemApps,appNameQuery);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAppAdapter(boolean isUserApps, boolean isSystemApps,String query) {

        if(apps!=null && apps.size()>0){

            List<AppModel> tempApps=new ArrayList<>();

            for(AppModel appModel:apps){

                if(appModel.getAppName().toLowerCase().contains(query.toLowerCase())){
                    if(isUserApps && appModel.getAppType()== AppType.USERAPP){
                        tempApps.add(appModel);
                    }else if(isSystemApps && appModel.getAppType()==AppType.SYSTEMAPP){
                        tempApps.add(appModel);
                    }else if(!isUserApps && !isSystemApps){
                        tempApps.add(appModel);
                    }
                }
            }

            Collections.sort(tempApps, new FileListSorter(PersistanceManager.getSortType(MainActivity.this), PersistanceManager.getSortOrder(MainActivity.this)));

            if(mAdapter!=null){
                mAdapter.setAppList(tempApps);
                mAdapter.notifyDataSetChanged();
            }

        }
    }


    @Override
    public Loader<List<AppModel>> onCreateLoader(int id, Bundle args) {
        recyclerView.setVisibility(View.GONE);
        findViewById(R.id.mainProgress).setVisibility(View.VISIBLE);
        return new AppListLoader(MainActivity.this);
    }

    @Override
    public void onLoadFinished(Loader<List<AppModel>> loader, List<AppModel> data) {
// set new data to adapter

        apps.clear();
        apps=data;

        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.mainProgress).setVisibility(View.GONE);

        setAppAdapter(isUserApps,isSystemApps,appNameQuery);
    }

    @Override
    public void onLoaderReset(Loader<List<AppModel>> loader) {
        mAdapter.setAppList(null);
    }

    public void showSortDialog() {
        String[] sort = getResources().getStringArray(R.array.sortbyApps);
        AppSortType current = PersistanceManager.getSortType(MainActivity.this);
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
                PersistanceManager.setSortType(MainActivity.this, AppSortType.getSortTypeByInt(which));

                //getSupportLoaderManager().restartLoader(MainActivity.ID_LOADER_APP_LIST, null, MainActivity.this);
                setAppAdapter(isUserApps,isSystemApps,appNameQuery);
                dialog.dismiss();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                int which = dialog.getSelectedIndex();

                PersistanceManager.setSortOrder(MainActivity.this,SortOrder.DESC);
                PersistanceManager.setSortType(MainActivity.this, AppSortType.getSortTypeByInt(which));

                //getSupportLoaderManager().restartLoader(MainActivity.ID_LOADER_APP_LIST, null, MainActivity.this);
                setAppAdapter(isUserApps,isSystemApps,appNameQuery);
                dialog.dismiss();
            }
        });
        materialDialogBuilder.title(R.string.sortby);
        materialDialogBuilder.build().show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        appNameQuery=query;
        setAppAdapter(isUserApps,isSystemApps,appNameQuery);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        appNameQuery=newText;
        setAppAdapter(isUserApps,isSystemApps,appNameQuery);
        return true;
    }

    private void myToggleSelection(int idx) {
        mAdapter.toggleSelection(idx);
        String title = getString(R.string.selected_count, mAdapter.getSelectedItemCount());
        actionMode.setTitle(title);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        if(getAppcategory()==AppCategory.UNINSTALL)
            inflater.inflate(R.menu.menu_uninstall_action_mode, menu);
        else if(getAppcategory()==AppCategory.BACKUP)
            inflater.inflate(R.menu.menu_backup_action_mode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_uninstall:
                List<AppModel> selectedItemPositionsForUninstall = mAdapter.getSelectedItems();
                if(selectedItemPositionsForUninstall.size()>0){
                    for (int i = selectedItemPositionsForUninstall.size() - 1; i >= 0; i--) {
                        CommonFunctions.uninstallApp(MainActivity.this,selectedItemPositionsForUninstall.get(i));
                    }
                }else{
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content),getResources().getString(R.string.please_select_atleast), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                actionMode.finish();
                return true;
            case R.id.action_backup:
                List<AppModel> selectedItemPositionsforBackup = mAdapter.getSelectedItems();
                if(selectedItemPositionsforBackup.size()>0){
                    CommonFunctions.backupApp(MainActivity.this, findViewById(android.R.id.content), selectedItemPositionsforBackup);
                }else{
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content),getResources().getString(R.string.please_select_atleast), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                actionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.actionMode = null;
        mAdapter.clearSelections();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.app_layout) {
            // item click
            int idx = recyclerView.getChildPosition(view);
            if (actionMode != null) {
                myToggleSelection(idx);
                return;
            }
        }
    }

    private class RecyclerViewDemoOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            onClick(view);
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (actionMode != null) {
                return;
            }
            // Start the CAB using the ActionMode.Callback defined above
            actionMode = startActionMode(MainActivity.this);
            int idx = recyclerView.getChildPosition(view);
            myToggleSelection(idx);
            super.onLongPress(e);
        }
    }

}
