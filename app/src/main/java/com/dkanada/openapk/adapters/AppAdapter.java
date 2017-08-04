package com.dkanada.openapk.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;
import com.dkanada.openapk.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        btnOpen.setVisibility(View.INVISIBLE);
        btnShare.setVisibility(View.INVISIBLE);

        if (App.getAppPreferences().getTheme().equals("1")) {
            btnOpen.setBackgroundColor(context.getResources().getColor(R.color.white));
            btnShare.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            btnOpen.setBackgroundColor(context.getResources().getColor(R.color.grey_dark));
            btnShare.setBackgroundColor(context.getResources().getColor(R.color.grey_dark));
        }

        btnOpen.setText(context.getString(R.string.action_open));
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionUtils.open(context, packageInfo);
            }
        });

        Boolean extract = App.getAppPreferences().getExtractButton();
        if (extract) {
            btnShare.setText(context.getString(R.string.action_share));
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActionUtils.share(context, packageInfo);
                }
            });
        } else {
            btnShare.setText(context.getString(R.string.action_extract));
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActionUtils.extract(context, packageInfo);
                }
            });
        }

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
                final FilterResults oReturn = new FilterResults();
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
                    oReturn.values = results;
                    oReturn.count = results.size();
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count > 0) {
                    MainActivity.setResultsMessage(false);
                } else {
                    MainActivity.setResultsMessage(true);
                }
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

            vOpen = (Button) view.findViewById(R.id.btnOne);
            vShare = (Button) view.findViewById(R.id.btnTwo);
        }
    }
}
