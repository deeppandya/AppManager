package com.deeppandya.appmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.model.AppModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by d_pandya on 3/7/17.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private Context context;

    public AppAdapter(Context context) {
        this.context=context;
    }

    private List<AppModel> appList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView apkIcon;
        TextView txtTitle;
        LinearLayout rl;
        TextView txtDesc;
        ImageButton about;

        public ViewHolder(View view) {
            super(view);
            txtTitle = (TextView) view.findViewById(R.id.firstline);
            apkIcon = (ImageView) view.findViewById(R.id.apk_icon);
            rl = (LinearLayout) view.findViewById(R.id.second);
            txtDesc= (TextView) view.findViewById(R.id.date);
            about=(ImageButton)view.findViewById(R.id.properties);
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
        holder.apkIcon.setImageDrawable(appModel.getImageId());
        holder.txtTitle.setText(appModel.getTitle());
        holder.txtDesc.setText(appModel.getSize());
        holder.rl.setClickable(true);
        holder.rl.setOnClickListener(new View.OnClickListener() {

            public void onClick(View p1) {
                Intent i1 = context.getPackageManager().getLaunchIntentForPackage(appModel.getPermissions());
                if (i1 != null)
                    context.startActivity(i1);
                else
                    Toast.makeText(context, context.getResources().getString(R.string.not_allowed), Toast.LENGTH_LONG).show();
            }
        });
        if (holder.about != null) {
            holder.about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v,appModel);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return appList!=null? appList.size():0;
    }

    public void setAppList(List<AppModel> appList) {
        this.appList = appList;
    }

    void showPopup(View view, final AppModel appModel){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open:
                        Intent i1 = context.getPackageManager().getLaunchIntentForPackage(appModel.getPermissions());
                        if (i1!= null)
                            context.startActivity(i1);
                        else
                            Toast.makeText(context,context.getResources().getString(R.string.not_allowed), Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.share:

                        return true;
                    case R.id.unins:

                        return true;
                    case R.id.play:
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.setData(Uri.parse("market://details?id=" + appModel.getPermissions()));
                        context.startActivity(intent1);
                        return true;
                    case R.id.properties:

                        context.startActivity(new Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + appModel.getPermissions())));
                        return true;
                    case R.id.backup:
                        return true;
                }
                return false;
            }
        });

        popupMenu.inflate(R.menu.app_options);
        popupMenu.show();
    }

}
