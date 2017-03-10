package com.deeppandya.appmanager.adapter;

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
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.util.CommonFunctions;
import com.deeppandya.appmanager.managers.PersistanceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d_pandya on 3/7/17.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private View view;
    private Context context;
    private AppCategory appCategory;
    private AppSortType appSortType;
    private List<AppModel> selectedItems;
    private List<AppModel> appList;

    public AppAdapter(View view, Context context) {
        this.view = view;
        this.context = context;
        selectedItems = new ArrayList<>();
    }

    public void setAppCategory(AppCategory appCategory) {
        this.appCategory = appCategory;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView txtAppName;
        LinearLayout appLayout;
        TextView txtAppDesc;
        ImageButton appProperties;
        Button btnUninstall, btnBackup, btnPermission, btnPackage;

        public ViewHolder(View view) {
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_row_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AppModel appModel = appList.get(position);
        holder.appIcon.setImageDrawable(appModel.getAppIcon());
        holder.txtAppName.setText(appModel.getAppName());

        if (appSortType == AppSortType.BYDATE) {
            holder.txtAppDesc.setText(appModel.getFormattedDate());
        } else {
            holder.txtAppDesc.setText(appModel.getSize());
        }


        if (holder.appProperties != null) {
            holder.appProperties.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v, appModel);
                }
            });
        }

        if (appCategory == AppCategory.UNINSTALL) {
            if(selectedItems.contains(appList.get(position))){
                holder.appLayout.setActivated(true);
                holder.btnUninstall.setVisibility(View.GONE);
            }else{
                holder.appLayout.setActivated(false);
                holder.btnUninstall.setVisibility(View.VISIBLE);
                holder.btnUninstall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonFunctions.uninstallApp(context,appModel);
                    }
                });
            }
        } else if (appCategory == AppCategory.BACKUP) {
            if(selectedItems.contains(appList.get(position))){
                holder.appLayout.setActivated(true);
                holder.btnBackup.setVisibility(View.GONE);
            }else{
                holder.appLayout.setActivated(false);
                holder.btnBackup.setVisibility(View.VISIBLE);
                holder.btnBackup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonFunctions.backupApp(context,view,appModel);
                    }
                });
            }
        } else if (appCategory == AppCategory.PERMISSIONS) {
            holder.btnPermission.setVisibility(View.VISIBLE);
            if (appModel.getPermissions() != null && appModel.getPermissions().length > 0) {
                holder.btnPermission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPermissions(appModel);
                    }
                });
            } else {
                holder.btnPermission.setText(context.getResources().getString(R.string.no_permission));
            }
        } else if (appCategory == AppCategory.PACKAGE) {
            holder.txtAppDesc.setText(appModel.getPackageName());
        }


//        if(appModel.getAppType()== AppType.SYSTEMAPP){
//            holder.appLayout.setBackgroundColor( context.getResources().getColor(android.R.color.holo_red_light));
//        }else{
//            holder.appLayout.setBackgroundColor( context.getResources().getColor(android.R.color.holo_blue_light));
//        }

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

    public List<AppModel> getSelectedItems() {
        return selectedItems;
    }

    public void setAppList(List<AppModel> appList) {
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
                        CommonFunctions.openApp(context,appModel);
                    case R.id.share:

                        return true;
                    case R.id.play:
                        CommonFunctions.openAppInPlayStore(context,appModel);
                        return true;
                    case R.id.app_properties:
                        CommonFunctions.openAppProperties(context,appModel.getPackageName());
                        return true;
                }
                return false;
            }
        });

        popupMenu.inflate(R.menu.app_options);
        popupMenu.show();
    }
}
