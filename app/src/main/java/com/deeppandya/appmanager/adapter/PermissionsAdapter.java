package com.deeppandya.appmanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.model.PermissionModel;

import java.util.List;

/**
 * Created by deeppandya on 2017-05-22.
 */

public class PermissionsAdapter extends RecyclerView.Adapter<PermissionsAdapter.ViewHolder> {

    private final Context context;
    private List<PermissionModel> permissionModels;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtGroupText, txtPermissionText;
        public ImageView permissionIcon;

        public ViewHolder(View view) {
            super(view);
            txtGroupText = (TextView) view.findViewById(R.id.txtGroupText);
            txtPermissionText = (TextView) view.findViewById(R.id.txtPermissionText);
            permissionIcon = (ImageView) view.findViewById(R.id.permissionIcon);
        }
    }

    public PermissionsAdapter(Context context) {
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.permission_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PermissionModel permissionModel = permissionModels.get(position);
        String groupText=permissionModel.getGroupText().substring(0, 1).toUpperCase() + permissionModel.getGroupText().substring(1);
        holder.txtGroupText.setText(groupText);
        holder.txtPermissionText.setText(permissionModel.getPermissionText());
        holder.permissionIcon.setImageResource(permissionModel.getPermissionIcon());
    }

    @Override
    public int getItemCount() {
        return permissionModels.size();
    }

    public void setPermissionModels(List<PermissionModel> permissionModels) {
        this.permissionModels = permissionModels;
    }
}
