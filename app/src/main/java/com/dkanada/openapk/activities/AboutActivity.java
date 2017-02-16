package com.dkanada.openapk.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dkanada.openapk.OpenAPKApplication;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.UtilsApp;
import com.dkanada.openapk.utils.UtilsUI;

public class AboutActivity extends AppCompatActivity {
  // load settings
  AppPreferences appPreferences;

  // about variables
  private Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    this.appPreferences = OpenAPKApplication.getAppPreferences();
    if (appPreferences.getTheme().equals("1")) {
      setTheme(R.style.Light);
    } else {
      setTheme(R.style.Dark);
    }
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    this.context = this;

    setInitialConfiguration();
    setScreenElements();
  }

  private void setInitialConfiguration() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.action_about);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));
      toolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
      if (appPreferences.getNavigationColorPref()) {
        getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
      }
    }
  }

  private void setScreenElements() {
    TextView header = (TextView) findViewById(R.id.header);
    TextView appNameVersion = (TextView) findViewById(R.id.app_name);

    header.setBackgroundColor(appPreferences.getPrimaryColorPref());
    appNameVersion.setText(getResources().getString(R.string.app_name) + " " + UtilsApp.getAppVersionName(getApplicationContext()));
  }
}
