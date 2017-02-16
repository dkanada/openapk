package com.dkanada.openapk;

import android.app.Application;

import com.dkanada.openapk.utils.AppPreferences;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;

public class OpenAPKApplication extends Application {
  private static AppPreferences appPreferences;
  private static int currentAdapter;

  @Override
  public void onCreate() {
    super.onCreate();

    // set fields
    appPreferences = new AppPreferences(this);
    currentAdapter = 0;

    // register custom fonts
    Iconics.registerFont(new GoogleMaterial());
  }

  public static AppPreferences getAppPreferences() {
    return appPreferences;
  }

  public static int getCurrentAdapter() {
    return currentAdapter;
  }

  public static void setCurrentAdapter(int value) {
    currentAdapter = value;
  }
}