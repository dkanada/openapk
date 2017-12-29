package com.dkanada.openapk.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.OtherUtils;

public class AboutActivity extends ThemeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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
        getSupportActionBar().setTitle(R.string.about);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(OtherUtils.dark(AppPreferences.get(this).getPrimaryColor(), 0.8));
        getWindow().setNavigationBarColor(AppPreferences.get(this).getPrimaryColor());
    }

    private void setScreenElements() {
        TextView header = (TextView) findViewById(R.id.header);
        TextView text = (TextView) findViewById(R.id.app_name);
        ImageView icon = (ImageView) findViewById(R.id.about_icon);

        header.setBackgroundColor(AppPreferences.get(this).getPrimaryColor());
        text.setText(String.format("%s %s", getResources().getString(R.string.app_name), OtherUtils.getAppVersionName(getApplicationContext())));
        if (AppPreferences.get(this).getTheme().equals("0")) {
            icon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.grey_two));
        }
    }
}
