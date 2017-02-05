package com.dkanada.openapk;

import android.app.Application;

import com.dkanada.openapk.utils.AppPreferences;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;

public class OpenAPKApplication extends Application {
  private static AppPreferences appPreferences;

  @Override
  public void onCreate() {
    super.onCreate();

    // load shared preference
    appPreferences = new AppPreferences(this);

    // register custom fonts
    Iconics.registerFont(new GoogleMaterial());
  }

  public static AppPreferences getAppPreferences() {
    return appPreferences;
  }
}