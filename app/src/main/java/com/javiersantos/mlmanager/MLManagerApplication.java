package com.javiersantos.mlmanager;

import android.app.Application;

import com.javiersantos.mlmanager.utils.AppPreferences;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;

public class MLManagerApplication extends Application {
  private static AppPreferences sAppPreferences;

  @Override
  public void onCreate() {
    super.onCreate();

    // Load Shared Preference
    sAppPreferences = new AppPreferences(this);

    // Register custom fonts like this (or also provide a font definition file)
    Iconics.registerFont(new GoogleMaterial());
  }

  public static AppPreferences getAppPreferences() {
    return sAppPreferences;
  }
}