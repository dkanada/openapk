package com.dkanada.openapk.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.OtherUtils;

public class AboutActivity extends ThemeActivity {
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setInitialConfiguration();
        setScreenElements();
    }

    private void setInitialConfiguration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.about);
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
        TextView header = (TextView) findViewById(R.id.header);
        TextView appNameVersion = (TextView) findViewById(R.id.app_name);
        ImageView icon = (ImageView) findViewById(R.id.about_icon);

        header.setBackgroundColor(appPreferences.getPrimaryColor());
        appNameVersion.setText(getResources().getString(R.string.app_name) + " " + OtherUtils.getAppVersionName(getApplicationContext()));
        if (appPreferences.getTheme().equals("0")) {
            icon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey));
        }
    }
}
