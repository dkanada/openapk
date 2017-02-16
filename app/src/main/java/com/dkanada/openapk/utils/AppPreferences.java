package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dkanada.openapk.R;

import java.util.HashSet;
import java.util.Set;

public class AppPreferences {
  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor editor;
  private Context context;

  public static final String KeyCustomPath = "prefCustomPath";
  public static final String KeyCustomFilename = "prefCustomFilename";
  public static final String KeySortMode = "prefSortMode";
  public static final String KeyTheme = "prefTheme";
  public static final String KeyPrimaryColor = "prefPrimaryColor";
  public static final String KeyFABColor = "prefFABColor";
  public static final String KeyFABShow = "prefFABShow";
  public static final String KeyNavigationColor = "prefNavigationColor";
  public static final String KeyDoubleTap = "prefDoubleTap";
  public static final String KeyRootEnabled = "prefRootEnabled";

  // internal preferences
  public static final String KeyInstalledApps = "prefInstalledApps";
  public static final String KeySystemApps = "prefSystemApps";
  public static final String KeyFavoriteApps = "prefFavoriteApps";
  public static final String KeyHiddenApps = "prefHiddenApps";
  public static final String KeyDisabledApps = "prefDisabledApps";
  public static final String KeyIsRooted = "prefIsRooted";

  public AppPreferences(Context context) {
    this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    this.editor = sharedPreferences.edit();
    this.context = context;
  }

  public String getCustomPath() {
    return sharedPreferences.getString(KeyCustomPath, UtilsApp.getDefaultAppFolder().getPath());
  }

  public String getCustomFilename() {
    return sharedPreferences.getString(KeyCustomFilename, "1");
  }

  public String getSortMode() {
    return sharedPreferences.getString(KeySortMode, "1");
  }

  public String getTheme() {
    return sharedPreferences.getString(KeyTheme, "1");
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

  // every preference below here is for internal purposes only
  public Set<String> getFavoriteApps() {
    return sharedPreferences.getStringSet(KeyFavoriteApps, new HashSet<String>());
  }

  public void setFavoriteApps(Set<String> favoriteApps) {
    editor.remove(KeyFavoriteApps);
    editor.commit();
    editor.putStringSet(KeyFavoriteApps, favoriteApps);
    editor.commit();
  }

  public Set<String> getHiddenApps() {
    return sharedPreferences.getStringSet(KeyHiddenApps, new HashSet<String>());
  }

  public void setHiddenApps(Set<String> hiddenApps) {
    editor.remove(KeyHiddenApps);
    editor.commit();
    editor.putStringSet(KeyHiddenApps, hiddenApps);
    editor.commit();
  }

  public Set<String> getDisabledApps() {
    return sharedPreferences.getStringSet(KeyDisabledApps, new HashSet<String>());
  }

  public void setDisabledApps(Set<String> disabledApps) {
    editor.remove(KeyDisabledApps);
    editor.commit();
    editor.putStringSet(KeyDisabledApps, disabledApps);
    editor.commit();
  }

  public Set<String> getInstalledApps() {
    return sharedPreferences.getStringSet(KeyInstalledApps, new HashSet<String>());
  }

  public void setInstalledApps(Set<String> installedApps) {
    editor.remove(KeyInstalledApps);
    editor.commit();
    editor.putStringSet(KeyInstalledApps, installedApps);
    editor.commit();
  }

  public Set<String> getSystemApps() {
    return sharedPreferences.getStringSet(KeySystemApps, new HashSet<String>());
  }

  public void setSystemApps(Set<String> systemApps) {
    editor.remove(KeySystemApps);
    editor.commit();
    editor.putStringSet(KeySystemApps, systemApps);
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
