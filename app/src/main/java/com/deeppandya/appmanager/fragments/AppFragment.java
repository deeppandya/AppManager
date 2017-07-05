package com.deeppandya.appmanager.fragments;


import android.Manifest;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.deeppandya.appmanager.AppManager;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.adapter.AppAdapter;
import com.deeppandya.appmanager.asynctask.GetAppsAsyncTask;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.enums.AppSortType;
import com.deeppandya.appmanager.enums.AppType;
import com.deeppandya.appmanager.enums.SortOrder;
import com.deeppandya.appmanager.listeners.AppSortListener;
import com.deeppandya.appmanager.listeners.GetAppsListener;
import com.deeppandya.appmanager.listeners.GetAppsView;
import com.deeppandya.appmanager.listeners.OnPackageChanged;
import com.deeppandya.appmanager.managers.PersistanceManager;
import com.deeppandya.appmanager.managers.RuntimePermissionManager;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.receiver.PackageChangeReceiver;
import com.deeppandya.appmanager.util.CommonFunctions;
import com.deeppandya.appmanager.util.FileListSorter;
import com.mopub.nativeads.MoPubNativeAdLoadedListener;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppFragment extends AdsFragment implements GetAppsView, SearchView.OnQueryTextListener, AppSortListener, OnPackageChanged {

    private static final String TAG = AppFragment.class.getName();
    private RecyclerView recyclerView;
    private AppAdapter mAdapter;

    private String appNameQuery;

    private View rootView;
    private AppCategory appCategory;
    private GetAppsAsyncTask getAppsAsyncTask;
    private PackageChangeReceiver packageChangeReceiver;
    private String appType;

    private MaterialDialog.Builder materialDialogBuilder;

    public AppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_app, container, false);

        packageChangeReceiver = new PackageChangeReceiver(this);

        setHasOptionsMenu(true);

        appNameQuery = "";

        if (getArguments() != null && getArguments().get("category") != null) {
            appCategory = (AppCategory) getArguments().get("category");
        }

        if (getArguments() != null && getArguments().get("type") != null) {
            appType = getArguments().getString("type");
        }

        setHintLayout();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mAdapter = new AppAdapter(getActivity().findViewById(android.R.id.content), getActivity(), this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setAppCategory(appCategory);

        recyclerView.setAdapter(mAdapter);

        getApps();

        loadAdMobBannerAd();

        return rootView;
    }

    @Override
    public void onDestroy() {
        if (getAppsAsyncTask != null && (getAppsAsyncTask.getStatus() == AsyncTask.Status.RUNNING || getAppsAsyncTask.getStatus() == AsyncTask.Status.PENDING)) {
            getAppsAsyncTask.cancel(true);
        }

        if (packageChangeReceiver != null) {
            getActivity().unregisterReceiver(packageChangeReceiver);
        }

        if (materialDialogBuilder != null) {
            materialDialogBuilder = null;
        }

        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.apps_menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_sortby:
                showSortDialog(this);
                break;
            default:
                break;
        }

        return false;
    }

    private void getApps() {
        recyclerView.setVisibility(View.GONE);
        rootView.findViewById(R.id.mainProgress).setVisibility(View.VISIBLE);

        recyclerView.setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.mainProgress).setVisibility(View.GONE);

        setAppAdapter(isUserApp(), isSystemApp(), appNameQuery);

        //setAdAdapter();

        rootView.findViewById(R.id.mainProgress).setVisibility(View.GONE);

    }

    private void setAdAdapter() {
        MoPubRecyclerAdapter myMoPubAdapter = new MoPubRecyclerAdapter(getActivity(), mAdapter,new MoPubNativeAdPositioning.MoPubServerPositioning());
        // Create an ad renderer and view binder that describe your native ad layout.
        ViewBinder myViewBinder = new ViewBinder.Builder(R.layout.app_ad)
                .titleId(R.id.native_ad_title)
                .textId(R.id.native_ad_body)
                .iconImageId(R.id.native_ad_icon)
                .callToActionId(R.id.native_ad_icon)
                .build();

        MoPubStaticNativeAdRenderer myRenderer = new MoPubStaticNativeAdRenderer(myViewBinder);

        myMoPubAdapter.registerAdRenderer(myRenderer);
        myMoPubAdapter.loadAds("493437314eae4f36a307db56502e9cb5");
        myMoPubAdapter.setAdLoadedListener(new MoPubNativeAdLoadedListener() {
            @Override
            public void onAdLoaded(int position) {
                Log.e(TAG,"AdPosition");
            }

            @Override
            public void onAdRemoved(int position) {

            }
        });
    }

    private boolean isUserApp() {
        return (appType != null && appType.equals(AppType.USERAPP.toString())) ? true : false;
    }

    private boolean isSystemApp() {
        return (appType != null && appType.equals(AppType.SYSTEMAPP.toString())) ? true : false;
    }

    private void setHintLayout() {
        TextView txtHint = (TextView) rootView.findViewById(R.id.txtHint);
        final CardView hintLayout = (CardView) rootView.findViewById(R.id.hint_layout);
//        if (appCategory == AppCategory.UNINSTALL) {
//            txtHint.setText(getResources().getString(R.string.long_press_hint));
//        }
        if (appCategory == AppCategory.BACKUP && PersistanceManager.getBackupHint(getActivity())) {
            txtHint.setText(String.format(getResources().getString(R.string.backup_can_be_found), CommonFunctions.getBackupDir()));
        } else {
            hintLayout.setVisibility(View.GONE);
        }

        ImageView btnClose = (ImageView) rootView.findViewById(R.id.btnHintCancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersistanceManager.setBackupHint(getActivity(), false);
                hintLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setAppAdapter(boolean isUserApps, boolean isSystemApps, String query) {

        if (AppManager.apps != null && AppManager.apps.size() > 0) {

            List<AppModel> tempApps = new ArrayList<>();

            for (AppModel appModel : AppManager.apps) {

                if (appModel.getAppName().toLowerCase().contains(query.toLowerCase())) {
                    if (isUserApps && appModel.getAppType() == AppType.USERAPP) {
                        tempApps.add(appModel);
                    } else if (isSystemApps && appModel.getAppType() == AppType.SYSTEMAPP) {
                        tempApps.add(appModel);
                    } else if (!isUserApps && !isSystemApps) {
                        tempApps.add(appModel);
                    }
                }
            }

            Collections.sort(tempApps, new FileListSorter(PersistanceManager.getSortType(getActivity()), PersistanceManager.getSortOrder(getActivity())));

            if (mAdapter != null) {
                mAdapter.setAppList(tempApps);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        appNameQuery = query;
        setAppAdapter(isUserApp(), isSystemApp(), appNameQuery);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        appNameQuery = newText;
        setAppAdapter(isUserApp(), isSystemApp(), appNameQuery);
        return true;
    }

    private void loadAdMobBannerAd() {

//        String adUnitId = "";
//
//        if (appCategory == AppCategory.UNINSTALL && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.UNINSTALL_BANNER)) {
//            adUnitId = getResources().getString(R.string.appmanager_app_uninstall_banner);
//            showBanner(rootView, adUnitId);
//        } else if (appCategory == AppCategory.BACKUP && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.BACKUP_BANNER)) {
//            adUnitId = getResources().getString(R.string.appmanager_app_backup_banner);
//            showBanner(rootView, adUnitId);
//        } else if (appCategory == AppCategory.PERMISSIONS && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.PERMISSION_BANNER)) {
//            adUnitId = getResources().getString(R.string.appmanager_app_permissions_banner);
//            showBanner(rootView, adUnitId);
//        } else if (appCategory == AppCategory.PACKAGE && FirebaseManager.getRemoteConfig().getBoolean(FirebaseManager.PACKAGE_BANNER)) {
//            adUnitId = getResources().getString(R.string.appmanager_app_package_banner);
//            showBanner(rootView, adUnitId);
//        }

        showBanner(rootView);
    }

    @Override
    public void createAppBackup() {
        if (RuntimePermissionManager.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            RuntimePermissionManager.requestPermission(getActivity(), getActivity().findViewById(android.R.id.content), Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    RuntimePermissionManager.PERMISSIONS_ACCOUNT, RuntimePermissionManager.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE,
                    getResources().getString(R.string.storage_permission_permissions_needed));
        } else {
            if (mAdapter != null)
                CommonFunctions.backupApp(getActivity(), getActivity().findViewById(android.R.id.content), mAdapter.getAppModelBackUpList());
        }
    }

    @Override
    public void onPositiveClick(int which) {
        PersistanceManager.setSortOrder(getActivity(), SortOrder.ASC);
        PersistanceManager.setSortType(getActivity(), AppSortType.getSortTypeByInt(which));

        setAppAdapter(isUserApp(), isSystemApp(), appNameQuery);
    }

    @Override
    public void onNegativeClick(int which) {
        PersistanceManager.setSortOrder(getActivity(), SortOrder.DESC);
        PersistanceManager.setSortType(getActivity(), AppSortType.getSortTypeByInt(which));

        setAppAdapter(isUserApp(), isSystemApp(), appNameQuery);
    }

    @Override
    public void onPackageChanged() {
        GetAppsListener getAppsListener = new GetAppsListener() {
            @Override
            public void beforeGetApps() {
                recyclerView.setVisibility(View.GONE);
                rootView.findViewById(R.id.mainProgress).setVisibility(View.VISIBLE);
            }

            @Override
            public void afterGetApps(List<AppModel> appModels) {
                AppManager.apps.clear();
                AppManager.apps = appModels;

                getApps();

            }

            @Override
            public void onError() {
                rootView.findViewById(R.id.mainProgress).setVisibility(View.INVISIBLE);
            }
        };

        CommonFunctions.setApps(getActivity(),getAppsListener);
    }

    @Override
    public void registerReceiver(BroadcastReceiver packageReceiver, IntentFilter filter) {
        getActivity().registerReceiver(packageReceiver, filter);
    }

    private void showSortDialog(final AppSortListener appSortListener) {
        String[] sort = getResources().getStringArray(R.array.sortbyApps);
        AppSortType current = PersistanceManager.getSortType(getActivity());
        materialDialogBuilder = new MaterialDialog.Builder(getActivity());
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

                appSortListener.onPositiveClick(which);
                dialog.dismiss();
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                int which = dialog.getSelectedIndex();

                appSortListener.onNegativeClick(which);
                dialog.dismiss();
            }
        });
        materialDialogBuilder.title(R.string.sortby);
        materialDialogBuilder.build().show();
    }
}
