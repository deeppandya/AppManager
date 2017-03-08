package com.deeppandya.appmanager.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.asynctask.CopyFileAsynctask;
import com.deeppandya.appmanager.enums.AppCategory;
import com.deeppandya.appmanager.enums.SortType;
import com.deeppandya.appmanager.model.AppModel;
import com.deeppandya.appmanager.util.CommonFunctions;
import com.deeppandya.appmanager.util.PersistanceManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by d_pandya on 3/7/17.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private View view;
    private Context context;
    private AppCategory appCategory;
    private SortType sortType;

    public AppAdapter(View view,Context context) {
        this.view=view;
        this.context=context;
    }

    private List<AppModel> appList;

    public void setAppCategory(AppCategory appCategory) {
        this.appCategory=appCategory;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView txtAppName;
        LinearLayout appLayout;
        TextView txtAppDesc;
        ImageButton appProperties;
        Button btnUninstall,btnBackup,btnPermission,btnPackage;

        public ViewHolder(View view) {
            super(view);
            txtAppName = (TextView) view.findViewById(R.id.app_name);
            appIcon = (ImageView) view.findViewById(R.id.app_icon);
            appLayout = (LinearLayout) view.findViewById(R.id.app_layout);
            txtAppDesc = (TextView) view.findViewById(R.id.app_desc);
            appProperties =(ImageButton)view.findViewById(R.id.app_properties);

            btnUninstall=(Button)view.findViewById(R.id.btnUninstall);
            btnBackup=(Button)view.findViewById(R.id.btnBackup);
            btnPermission=(Button)view.findViewById(R.id.btnPermission);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_row_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppModel appModel = appList.get(position);
        holder.appIcon.setImageDrawable(appModel.getAppIcon());
        holder.txtAppName.setText(appModel.getAppName());

        if(sortType==SortType.BYDATE){
            holder.txtAppDesc.setText(appModel.getFormattedDate());
        }else{
            holder.txtAppDesc.setText(appModel.getSize());
        }


        if (holder.appProperties != null) {
            holder.appProperties.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v,appModel);
                }
            });
        }

        if(appCategory==AppCategory.UNINSTALL){
            holder.btnUninstall.setVisibility(View.VISIBLE);
            holder.btnUninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uninstallApp(appModel);
                }
            });
        }else if(appCategory==AppCategory.BACKUP){
            holder.btnBackup.setVisibility(View.VISIBLE);
            holder.btnBackup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backupApp(appModel);
                }
            });
        }else if(appCategory==AppCategory.PERMISSIONS){
            holder.btnPermission.setVisibility(View.VISIBLE);
            if(appModel.getPermissions()!=null && appModel.getPermissions().length>0){
                holder.btnPermission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPermissions(appModel);
                    }
                });
            }else{
                holder.btnPermission.setText(context.getResources().getString(R.string.no_permission));
            }
        }else if(appCategory==AppCategory.PACKAGE){
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
        builder.setTitle(appModel.getAppName()+" "+context.getResources().getString(R.string.permission));
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

        ListView listView=alert.getListView();
        listView.setDivider(new ColorDrawable(context.getResources().getColor(R.color.colorAccent))); // set color
        listView.setDividerHeight(2); // set height

        alert.show();
    }

    private void showPermissionDesc(String permission,String permissionDesc) {
        new MaterialDialog.Builder(context)
                .title(permission)
                .content(permissionDesc)
                .positiveText(R.string.ok)
                .show();

    }

    @Override
    public int getItemCount() {
        return appList!=null? appList.size():0;
    }

    public void setAppList(List<AppModel> appList) {
        this.sortType= PersistanceManager.getSortType(context);
        this.appList = appList;
    }

    void showPopup(View view, final AppModel appModel){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open:
                        openApp(appModel);
                    case R.id.share:

                        return true;
                    case R.id.play:
                        openAppInPlayStore(appModel);
                        return true;
                    case R.id.app_properties:
                        openAppProperties(appModel);
                        return true;
                }
                return false;
            }
        });

        popupMenu.inflate(R.menu.app_options);
        popupMenu.show();
    }

    private void backupApp(AppModel appModel) {
        File inputFile = new File(appModel.getAppDesc());
        File destDir = new File(CommonFunctions.getBackupDir());
        if(!destDir.exists() || !destDir.isDirectory())destDir.mkdirs();

        File outFile=new File(destDir+File.separator+ appModel.getAppName() + "_" + appModel.getSymlink() + ".apk");

        try {
            outFile.createNewFile();
            CopyFileAsynctask copyFilesAsynctask=new CopyFileAsynctask(view,context,inputFile,outFile,appModel.getAppName());
            copyFilesAsynctask.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAppProperties(AppModel appModel) {
        context.startActivity(new Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + appModel.getPackageName())));
    }

    private void openAppInPlayStore(AppModel appModel) {
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        intent1.setData(Uri.parse("market://details?id=" + appModel.getPackageName()));
        context.startActivity(intent1);
    }

    private void uninstallApp(AppModel appModel) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:"+appModel.getPackageName()));
        context.startActivity(intent);
    }

    private void openApp(AppModel appModel) {
        Intent i1 = context.getPackageManager().getLaunchIntentForPackage(appModel.getPackageName());
        if (i1!= null)
            context.startActivity(i1);
        else
            Toast.makeText(context,context.getResources().getString(R.string.not_allowed), Toast.LENGTH_LONG).show();
    }

}
