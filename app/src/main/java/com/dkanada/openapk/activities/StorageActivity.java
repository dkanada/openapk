package com.dkanada.openapk.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dkanada.openapk.R;
import com.dkanada.openapk.interfaces.PackageStatsListener;
import com.dkanada.openapk.models.AppItem;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.OtherUtils;
import com.dkanada.openapk.utils.PackageStatsHelper;
import com.dkanada.openapk.views.InformationView;

public class StorageActivity extends ThemeActivity implements PackageStatsListener {
    private AppItem appItem;
    private LinearLayout internal;
    private LinearLayout external;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        AppItem appItem = getIntent().getParcelableExtra("package");

        setInitialConfiguration();
        setScreenElements();
    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setBackgroundColor(AppPreferences.get(this).getPrimaryColor());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.storage);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(OtherUtils.dark(AppPreferences.get(this).getPrimaryColor(), 0.8));
        getWindow().setNavigationBarColor(AppPreferences.get(this).getPrimaryColor());
    }

    private void setScreenElements() {
        PackageInfo packageInfo = new PackageInfo();
        try {
            packageInfo = getPackageManager().getPackageInfo(appItem.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
        }
        PackageStatsHelper.getPackageStats(this, packageInfo, this);

        internal = (LinearLayout) findViewById(R.id.internal);
        InformationView internalCode = new InformationView(this, getString(R.string.code), getString(R.string.layout_loading), true);
        InformationView internalData = new InformationView(this, getString(R.string.data), getString(R.string.layout_loading), false);
        InformationView internalCache = new InformationView(this, getString(R.string.cache), getString(R.string.layout_loading), true);
        internal.addView(internalCode);
        internal.addView(internalData);
        internal.addView(internalCache);

        external = (LinearLayout) findViewById(R.id.external);
        InformationView externalCode = new InformationView(this, getString(R.string.code), getString(R.string.layout_loading), true);
        InformationView externalData = new InformationView(this, getString(R.string.data), getString(R.string.layout_loading), false);
        InformationView externalCache = new InformationView(this, getString(R.string.cache), getString(R.string.layout_loading), true);
        InformationView externalObb = new InformationView(this, getString(R.string.obb), getString(R.string.layout_loading), false);
        InformationView externalMedia = new InformationView(this, getString(R.string.media), getString(R.string.layout_loading), true);
        external.addView(externalCode);
        external.addView(externalData);
        external.addView(externalCache);
        external.addView(externalObb);
        external.addView(externalMedia);
    }

    @Override
    public void onPackageStats(PackageStats packageStats) {
        internal.removeAllViews();
        InformationView internalCode = new InformationView(this, getString(R.string.code), OtherUtils.formatSize(packageStats.codeSize), true);
        InformationView internalData = new InformationView(this, getString(R.string.data), OtherUtils.formatSize(packageStats.dataSize), false);
        InformationView internalCache = new InformationView(this, getString(R.string.cache), OtherUtils.formatSize(packageStats.cacheSize), true);
        internal.addView(internalCode);
        internal.addView(internalData);
        internal.addView(internalCache);

        external.removeAllViews();
        external = (LinearLayout) findViewById(R.id.external);
        InformationView externalCode = new InformationView(this, getString(R.string.code), OtherUtils.formatSize(packageStats.externalCodeSize), true);
        InformationView externalData = new InformationView(this, getString(R.string.data), OtherUtils.formatSize(packageStats.externalDataSize), false);
        InformationView externalCache = new InformationView(this, getString(R.string.cache), OtherUtils.formatSize(packageStats.externalCacheSize), true);
        InformationView externalObb = new InformationView(this, getString(R.string.obb), OtherUtils.formatSize(packageStats.externalObbSize), false);
        InformationView externalMedia = new InformationView(this, getString(R.string.media), OtherUtils.formatSize(packageStats.externalMediaSize), true);
        external.addView(externalCode);
        external.addView(externalData);
        external.addView(externalCache);
        external.addView(externalObb);
        external.addView(externalMedia);
    }
}
