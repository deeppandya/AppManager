package com.deeppandya.appmanager.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deeppandya.appmanager.R;
import com.deeppandya.appmanager.fragments.AppBackedUpFragment.OnListFragmentInteractionListener;
import com.deeppandya.appmanager.model.AppModel;

import java.util.List;

public class AppBackedUpAdapter extends RecyclerView.Adapter<AppBackedUpAdapter.ViewHolder> {

    private final List<AppModel> appModels;
    private final OnListFragmentInteractionListener mListener;

    public AppBackedUpAdapter(List<AppModel> appModels, OnListFragmentInteractionListener listener) {
        this.appModels = appModels;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.appIcon.setImageDrawable(appModels.get(position).getAppIcon());
        holder.txtAppName.setText(appModels.get(position).getAppName());

        holder.appProperties.setVisibility(View.GONE);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(appModels.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView txtAppName;
        LinearLayout appLayout;
        TextView txtAppDesc;
        ImageButton appProperties;
        Button btnUninstall, btnBackup, btnPermission, btnPackage;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view=view;
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
}
