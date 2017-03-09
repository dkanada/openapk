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
import com.dkanada.openapk.utils.InterfaceUtils;

public class SettingsActivity extends ThemeActivity {
  private AppPreferences appPreferences;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    appPreferences = App.getAppPreferences();
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
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.action_settings);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(InterfaceUtils.darker(appPreferences.getPrimaryColorPref(), 0.8));
      toolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
      if (appPreferences.getNavigationColorPref()) {
        getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
      }
    }
  }
}