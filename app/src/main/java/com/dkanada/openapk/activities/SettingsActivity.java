package com.dkanada.openapk.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.fragments.SettingsFragment;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.OtherUtils;

public class SettingsActivity extends ThemeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setInitialConfiguration();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SettingsFragment fragment = new SettingsFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
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
        getSupportActionBar().setTitle(R.string.settings);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(OtherUtils.dark(AppPreferences.get(this).getPrimaryColor(), 0.8));
        getWindow().setNavigationBarColor(AppPreferences.get(this).getPrimaryColor());
    }
}