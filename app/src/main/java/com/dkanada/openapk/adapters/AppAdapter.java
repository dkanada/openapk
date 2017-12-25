package com.dkanada.openapk.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkanada.openapk.App;
import com.dkanada.openapk.utils.ActionUtils;
import com.dkanada.openapk.activities.AppActivity;
import com.dkanada.openapk.R;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> implements Filterable {
    private List<PackageInfo> appList;
    private List<PackageInfo> appListSearch;
    private Context context;

    public AppAdapter(Context context, List<PackageInfo> appList) {
        this.appList = appList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    @Override
    public void onBindViewHolder(AppViewHolder appViewHolder, int i) {
        PackageInfo packageInfo = appList.get(i);
        appViewHolder.vName.setText(App.getPackageName(packageInfo));
        appViewHolder.vPackage.setText(packageInfo.packageName);
        appViewHolder.vIcon.setImageDrawable(App.getPackageIcon(packageInfo));
        setButtonEvents(appViewHolder, packageInfo);
    }

    private void setButtonEvents(AppViewHolder appViewHolder, final PackageInfo packageInfo) {
        Button btnOpen = appViewHolder.vOpen;
        Button btnShare = appViewHolder.vShare;
        final ImageView appIcon = appViewHolder.vIcon;
        final CardView cardView = appViewHolder.vCard;

        btnOpen.setTextColor(App.getAppPreferences().getAccentColor());
        btnShare.setTextColor(App.getAppPreferences().getAccentColor());

        if (App.getAppPreferences().getTheme().equals("0")) {
            btnOpen.setBackgroundColor(context.getResources().getColor(R.color.white));
            btnShare.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            btnOpen.setBackgroundColor(context.getResources().getColor(R.color.grey_three));
            btnShare.setBackgroundColor(context.getResources().getColor(R.color.grey_three));
        }

        btnOpen.setText(context.getString(R.string.action_open));
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.open(context, packageInfo);
            }
        });
        btnShare.setText(context.getString(R.string.action_share));
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.share(context, packageInfo);
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;
                Intent intent = new Intent(context, AppActivity.class);
                intent.putExtra("package", packageInfo.packageName);

                // the icon will smoothly transition to its new location if version is above lollipop
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String transitionName = context.getResources().getString(R.string.transition);
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, appIcon, transitionName);
                    context.startActivity(intent, transitionActivityOptions.toBundle());
                } else {
                    context.startActivity(intent);
                }
            }
        });
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults filterResults = new FilterResults();
                final List<PackageInfo> results = new ArrayList<>();
                if (appListSearch == null) {
                    appListSearch = appList;
                }
                if (charSequence != null) {
                    if (appListSearch != null && appListSearch.size() > 0) {
                        for (final PackageInfo packageInfo : appListSearch) {
                            if (App.getPackageName(packageInfo).toLowerCase().contains(charSequence.toString())) {
                                results.add(packageInfo);
                            }
                        }
                    }
                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                appList = (ArrayList<PackageInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View appAdapterView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_app, viewGroup, false);
        return new AppViewHolder(appAdapterView);
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        private CardView vCard;
        private TextView vName;
        private TextView vPackage;
        private ImageView vIcon;

        private Button vOpen;
        private Button vShare;

        public AppViewHolder(View view) {
            super(view);
            vCard = (CardView) view.findViewById(R.id.app_card);
            vName = (TextView) view.findViewById(R.id.txtName);
            vPackage = (TextView) view.findViewById(R.id.txtApk);
            vIcon = (ImageView) view.findViewById(R.id.imgIcon);

            vOpen = (Button) view.findViewById(R.id.btnOpen);
            vShare = (Button) view.findViewById(R.id.btnShare);
        }
    }
}
