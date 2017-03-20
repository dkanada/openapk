package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dkanada.openapk.R;

public class AppPreferences {
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor editor;
  private Context context;

  private static final String KeyCustomPath = "prefCustomPath";
  private static final String KeyFilename = "prefFilename";
  private static final String KeySortMode = "prefSortMode";
  private static final String KeyTheme = "prefTheme";
  private static final String KeyPrimaryColor = "prefPrimaryColor";
  private static final String KeyFABColor = "prefFABColor";
  private static final String KeyNavigationColor = "prefNavigationColor";
  private static final String KeyDoubleTap = "prefDoubleTap";
  private static final String KeyRootEnabled = "prefRootEnabled";

  // internal preferences
  private static final String KeyInitialSetup = "prefInitialSetup";
  private static final String KeyIsRooted = "prefIsRooted";

  public AppPreferences(Context context) {
    this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    this.editor = sharedPreferences.edit();
    this.context = context;
  }

  public String getCustomPath() {
    return sharedPreferences.getString(KeyCustomPath, AppUtils.getDefaultAppFolder().getPath());
  }

  public void setCustomPath(String res) {
    editor.putString(KeyCustomPath, res);
    editor.commit();
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
    return sharedPreferences.getInt(KeyPrimaryColor, context.getResources().getColor(R.color.primary));
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