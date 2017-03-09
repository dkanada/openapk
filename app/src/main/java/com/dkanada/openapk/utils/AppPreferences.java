package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dkanada.openapk.R;

public class AppPreferences {
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor editor;
  private Context context;

  public static final String KeyCustomPath = "prefCustomPath";
  public static final String KeyFilename = "prefFilename";
  public static final String KeySortMode = "prefSortMode";
  public static final String KeyTheme = "prefTheme";
  public static final String KeyPrimaryColor = "prefPrimaryColor";
  public static final String KeyFABColor = "prefFABColor";
  public static final String KeyFABShow = "prefFABShow";
  public static final String KeyNavigationColor = "prefNavigationColor";
  public static final String KeyDoubleTap = "prefDoubleTap";
  public static final String KeyRootEnabled = "prefRootEnabled";

  // internal preferences
  public static final String KeyInitialSetup = "prefInitialSetup";
  public static final String KeyIsRooted = "prefIsRooted";

  public AppPreferences(Context context) {
    this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    this.editor = sharedPreferences.edit();
    this.context = context;
  }

  public String getCustomPath() {
    return sharedPreferences.getString(KeyCustomPath, AppUtils.getDefaultAppFolder().getPath());
  }

  public String getFilename() {
    return sharedPreferences.getString(KeyFilename, "0");
  }

  public String getSortMode() {
    return sharedPreferences.getString(KeySortMode, "0");
  }

  public String getTheme() {
    return sharedPreferences.getString(KeyTheme, "0");
  }

  public void setTheme(String res) {
    editor.putString(KeyTheme, res);
    editor.commit();
  }

  public int getPrimaryColorPref() {
    return sharedPreferences.getInt(KeyPrimaryColor, context.getResources().getColor(R.color.actionBar));
  }

  public void setPrimaryColorPref(Integer res) {
    editor.putInt(KeyPrimaryColor, res);
    editor.commit();
  }

  public int getFABColorPref() {
    return sharedPreferences.getInt(KeyFABColor, context.getResources().getColor(R.color.fab));
  }

  public void setFABColorPref(Integer res) {
    editor.putInt(KeyFABColor, res);
    editor.commit();
  }

  public Boolean getFABShowPref() {
    return sharedPreferences.getBoolean(KeyFABShow, false);
  }

  public Boolean getNavigationColorPref() {
    return sharedPreferences.getBoolean(KeyNavigationColor, false);
  }

  public Boolean getDoubleTap() {
    return sharedPreferences.getBoolean(KeyDoubleTap, false);
  }

  public Boolean getRootEnabled() {
    return sharedPreferences.getBoolean(KeyRootEnabled, false);
  }

  // internal preferences
  public boolean getInitialSetup() {
    return sharedPreferences.getBoolean(KeyInitialSetup, false);
  }

  public void setInitialSetup(boolean setup) {
    editor.putBoolean(KeyInitialSetup, setup);
    editor.commit();
  }

  public int getRootStatus() {
    return sharedPreferences.getInt(KeyIsRooted, 0);
  }

  public void setRootStatus(int rootStatus) {
    editor.putInt(KeyIsRooted, rootStatus);
    editor.commit();
  }
}