package com.dkanada.openapk.activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.interfaces.PackageStatsListener;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.OtherUtils;
import com.dkanada.openapk.utils.PackageStatsHelper;
import com.dkanada.openapk.views.InformationView;

public class StorageActivity extends ThemeActivity implements PackageStatsListener {
    private AppPreferences appPreferences;
    private Context context;
    private PackageInfo packageInfo;

    LinearLayout internal;
    LinearLayout external;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        context = this;

        getInitialConfiguration();
        setInitialConfiguration();
        setScreenElements();
    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(App.getPackageName(packageInfo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            toolbar.setBackgroundColor(appPreferences.getPrimaryColor());
            if (appPreferences.getStatusColor()) {
                getWindow().setStatusBarColor(OtherUtils.dark(appPreferences.getPrimaryColor(), 0.8));
            }
            if (appPreferences.getNavigationColor()) {
                getWindow().setNavigationBarColor(appPreferences.getPrimaryColor());
            }
        }
    }

    private void setScreenElements() {
        PackageStatsHelper.getPackageStats(context, packageInfo, this);

        internal = (LinearLayout) findViewById(R.id.internal);
        InformationView internalCode = new InformationView(context, getString(R.string.code), getString(R.string.layout_loading), true);
        InformationView internalData = new InformationView(context, getString(R.string.data), getString(R.string.layout_loading), false);
        InformationView internalCache = new InformationView(context, getString(R.string.cache), getString(R.string.layout_loading), true);
        internal.addView(internalCode);
        internal.addView(internalData);
        internal.addView(internalCache);

        external = (LinearLayout) findViewById(R.id.external);
        InformationView externalCode = new InformationView(context, getString(R.string.code), getString(R.string.layout_loading), true);
        InformationView externalData = new InformationView(context, getString(R.string.data), getString(R.string.layout_loading), false);
        InformationView externalCache = new InformationView(context, getString(R.string.cache), getString(R.string.layout_loading), true);
        InformationView externalObb = new InformationView(context, getString(R.string.obb), getString(R.string.layout_loading), false);
        InformationView externalMedia = new InformationView(context, getString(R.string.media), getString(R.string.layout_loading), true);
        external.addView(externalCode);
        external.addView(externalData);
        external.addView(externalCache);
        external.addView(externalObb);
        external.addView(externalMedia);
    }

    private void getInitialConfiguration() {
        String packageName = getIntent().getStringExtra("package");
        try {
            packageInfo = getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPackageStats(PackageStats packageStats) {
        internal.removeAllViews();
        InformationView internalCode = new InformationView(context, getString(R.string.code), OtherUtils.formatSize(packageStats.codeSize), true);
        InformationView internalData = new InformationView(context, getString(R.string.data), OtherUtils.formatSize(packageStats.dataSize), false);
        InformationView internalCache = new InformationView(context, getString(R.string.cache), OtherUtils.formatSize(packageStats.cacheSize), true);
        internal.addView(internalCode);
        internal.addView(internalData);
        internal.addView(internalCache);

        external.removeAllViews();
        external = (LinearLayout) findViewById(R.id.external);
        InformationView externalCode = new InformationView(context, getString(R.string.code), OtherUtils.formatSize(packageStats.externalCodeSize), true);
        InformationView externalData = new InformationView(context, getString(R.string.data), OtherUtils.formatSize(packageStats.externalDataSize), false);
        InformationView externalCache = new InformationView(context, getString(R.string.cache), OtherUtils.formatSize(packageStats.externalCacheSize), true);
        InformationView externalObb = new InformationView(context, getString(R.string.obb), OtherUtils.formatSize(packageStats.externalObbSize), false);
        InformationView externalMedia = new InformationView(context, getString(R.string.media), OtherUtils.formatSize(packageStats.externalMediaSize), true);
        external.addView(externalCode);
        external.addView(externalData);
        external.addView(externalCache);
        external.addView(externalObb);
        external.addView(externalMedia);
    }
}
