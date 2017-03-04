package com.dkanada.openapk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dkanada.openapk.OpenAPKApplication;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppPreferences;

public abstract class ThemableActivity extends AppCompatActivity {

  private AppPreferences appPreferences;
  private String currentTheme;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    appPreferences = OpenAPKApplication.getAppPreferences();
    currentTheme = appPreferences.getTheme();
    if (appPreferences.getTheme().equals("1")) {
      setTheme(R.style.Light);
    } else {
      setTheme(R.style.Dark);
      setTheme(R.style.DrawerDark);
    }
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!appPreferences.getTheme().equals(currentTheme)) {
      restart();
    }
  }

  protected void restart() {
    Intent intent = new Intent(this, getClass());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    overridePendingTransition(0, 0);
    startActivity(intent);
  }
}