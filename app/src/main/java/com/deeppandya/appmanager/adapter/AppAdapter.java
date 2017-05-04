package com.deeppandya.appmanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.enums.AppSortType;
import com.deeppandya.appmanager.enums.AppType;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.util.CommonFunctions;
import com.deeppandya.appmanager.managers.PersistanceManager;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by d_pandya on 3/7/17.
 */

public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View view;
    private Context context;
    private AppCategory appCategory;
    private AppSortType appSortType;
    private List<Object> selectedItems;
    private List<Object> appList;

    private static final int APP_VIEW_TYPE = 0;
    private static final int AD_VIEW_TYPE = 1;

    public AppAdapter(View view, Context context) {
        this.view = view;
        this.context = context;
        selectedItems = new ArrayList<>();
    }

    public void setAppCategory(AppCategory appCategory) {
        this.appCategory = appCategory;
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView txtAppName;
        LinearLayout appLayout;
        TextView txtAppDesc;
        ImageButton appProperties;
        Button btnUninstall, btnBackup, btnPermission, btnPackage;

        public AppViewHolder(View view) {
            super(view);
            txtAppName = (TextView) view.findViewById(R.id.app_name);
            appIcon = (ImageView) view.findViewById(R.id.app_icon);
            appLayout = (LinearLayout) view.findViewById(R.id.app_layout);
            txtAppDesc = (TextView) view.findViewById(R.id.app_desc);
            appProperties = (ImageButton) view.findViewById(R.id.app_properties);

            btnUninstall = (Button) view.findViewById(R.id.btnUninstall);
            btnBackup = (Button) view.findViewById(R.id.btnBackup);
            btnPermission = (Button) view.findViewById(R.id.btnPermission);

        }
    }

    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case APP_VIEW_TYPE:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.app_row_layout, parent, false);

                return new AppViewHolder(itemView);
            case AD_VIEW_TYPE:

            default:
                View nativeExpressLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.native_express_ad_container,
                        parent, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case APP_VIEW_TYPE:
                AppViewHolder appViewHolder = (AppViewHolder) holder;
                final AppModel appModel = (AppModel) appList.get(position);
                appViewHolder.appIcon.setImageDrawable(appModel.getAppIcon());
                appViewHolder.txtAppName.setText(appModel.getAppName());

                if (appSortType == AppSortType.BYDATE) {
                    appViewHolder.txtAppDesc.setText(appModel.getFormattedDate());
                } else {
                    appViewHolder.txtAppDesc.setText(appModel.getSize());
                }


                if (appViewHolder.appProperties != null) {
                    appViewHolder.appProperties.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopup(v, appModel);
                        }
                    });
                }

                if (appCategory == AppCategory.UNINSTALL) {
                    if (selectedItems.contains(appList.get(position))) {
                        appViewHolder.appLayout.setActivated(true);
                        appViewHolder.btnUninstall.setVisibility(View.GONE);
                    } else {
                        appViewHolder.appLayout.setActivated(false);
                        appViewHolder.btnUninstall.setVisibility(View.VISIBLE);
                        appViewHolder.btnUninstall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonFunctions.uninstallApp(context, appModel);
                            }
                        });
                    }
                } else if (appCategory == AppCategory.BACKUP) {
                    if (selectedItems.contains(appList.get(position))) {
                        appViewHolder.appLayout.setActivated(true);
                        appViewHolder.btnBackup.setVisibility(View.GONE);
                    } else {
                        appViewHolder.appLayout.setActivated(false);
                        appViewHolder.btnBackup.setVisibility(View.VISIBLE);
                        appViewHolder.btnBackup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                List<Object> appModelList = new ArrayList<Object>();
                                appModelList.add(appModel);

                                CommonFunctions.backupApp(context, view, appModelList);
                            }
                        });
                    }
                } else if (appCategory == AppCategory.PERMISSIONS) {
                    appViewHolder.btnPermission.setVisibility(View.VISIBLE);
                    if (appModel.getPermissions() != null && appModel.getPermissions().length > 0) {
                        appViewHolder.btnPermission.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPermissions(appModel);
                            }
                        });
                    } else {
                        appViewHolder.btnPermission.setText(context.getResources().getString(R.string.no_permission));
                    }
                } else if (appCategory == AppCategory.PACKAGE) {
                    appViewHolder.txtAppDesc.setText(appModel.getPackageName());
                }


//        if(appModel.getAppType()== AppType.SYSTEMAPP){
//            holder.appLayout.setBackgroundColor( context.getResources().getColor(android.R.color.holo_red_light));
//        }else{
//            holder.appLayout.setBackgroundColor( context.getResources().getColor(android.R.color.holo_blue_light));
//        }

                break;
            case AD_VIEW_TYPE:

            default:
                NativeExpressAdViewHolder nativeExpressHolder =
                        (NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView =
                        (NativeExpressAdView) appList.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;
                // The NativeExpressAdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // NativeExpressAdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled NativeExpressAdViewHolder.
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the Native Express ad to the native express ad view.
                adCardView.addView(adView);
        }
    }

    private void showPermissions(final AppModel appModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(appModel.getAppName() + " " + context.getResources().getString(R.string.permission));
        builder.setItems(appModel.getPermissions(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                try {
                    PermissionInfo pinfo = context.getPackageManager().getPermissionInfo(appModel.getPermissions()[item].toString(), PackageManager.GET_META_DATA);
                    showPermissionDesc(appModel.getPermissions()[item].toString(), pinfo.loadLabel(context.getPackageManager()).toString());
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        AlertDialog alert = builder.create();

        ListView listView = alert.getListView();
        listView.setDivider(new ColorDrawable(context.getResources().getColor(R.color.colorAccent))); // set color
        listView.setDividerHeight(2); // set height

        alert.show();
    }

    private void showPermissionDesc(String permission, String permissionDesc) {
        new MaterialDialog.Builder(context)
                .title(permission)
                .content(permissionDesc)
                .positiveText(R.string.ok)
                .show();

    }

    @Override
    public int getItemCount() {
        return appList != null ? appList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 6 == 0) ? AD_VIEW_TYPE : APP_VIEW_TYPE;
    }

    public void toggleSelection(int pos) {
        if (selectedItems.contains(appList.get(pos))) {
            selectedItems.remove(appList.get(pos));
        } else {
            selectedItems.add(appList.get(pos));
        }

        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Object> getSelectedItems() {
        return selectedItems;
    }

    public void setAppList(List<Object> appList) {
        this.appSortType = PersistanceManager.getSortType(context);
        this.appList = appList;
    }

    void showPopup(View view, final AppModel appModel) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open:
                        CommonFunctions.openApp(context, appModel);
                        return true;
                    case R.id.share:
                        CommonFunctions.shareApp(((Activity) context), appModel.getAppName(), appModel.getPackageName());
                        return true;
                    case R.id.play:
                        CommonFunctions.openAppInPlayStore(context, appModel);
                        return true;
                    case R.id.app_properties:
                        CommonFunctions.openAppProperties(context, appModel.getPackageName());
                        return true;
                }
                return false;
            }
        });

        popupMenu.inflate(R.menu.app_options);
        if (appModel.getAppType() == AppType.USERAPP) {
            popupMenu.getMenu().findItem(R.id.share).setVisible(true);
            popupMenu.getMenu().findItem(R.id.play).setVisible(true);
            popupMenu.getMenu().findItem(R.id.open).setVisible(true);
        } else {
            popupMenu.getMenu().findItem(R.id.share).setVisible(false);
            popupMenu.getMenu().findItem(R.id.play).setVisible(false);
            popupMenu.getMenu().findItem(R.id.open).setVisible(false);
        }
        popupMenu.show();
    }
}
