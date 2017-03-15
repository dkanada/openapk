package com.dkanada.openapk.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.activities.AboutActivity;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.AppUtils;

import yuku.ambilwarna.widget.AmbilWarnaPreference;

public final class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

  // change the summary on prefChanged
  private Preference prefCustomPath;
  private ListPreference prefFilename;
  private ListPreference prefSortMode;
  private ListPreference prefTheme;

  // disable if android version too low
  private AmbilWarnaPreference prefPrimaryColor;
  private Preference prefNavigationColor;

  // action on click
  private Preference prefReset;

  AppPreferences appPreferences;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    appPreferences = App.getAppPreferences();
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    prefs.registerOnSharedPreferenceChangeListener(this);

    prefCustomPath = findPreference("prefCustomPath");
    prefFilename = (ListPreference) findPreference("prefFilename");
    prefSortMode = (ListPreference) findPreference("prefSortMode");
    prefTheme = (ListPreference) findPreference("prefTheme");

    prefPrimaryColor = (AmbilWarnaPreference) findPreference("prefPrimaryColor");
    prefNavigationColor = findPreference("prefNavigationColor");

    prefReset = findPreference("prefReset");

    // removes settings that wont work on lower versions
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      prefPrimaryColor.setEnabled(false);
      prefNavigationColor.setEnabled(false);
    }

    prefReset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.edit().clear().apply();
        return true;
      }
    });

    setSortModeSummary();
    setThemeSummary();
    setCustomPathSummary();
    setFilenameSummary();
  }

  private void setCustomPathSummary() {
    String path = appPreferences.getCustomPath();
    if (path.equals(AppUtils.getDefaultAppFolder().getPath())) {
      prefCustomPath.setSummary("Not implemented yet due to non-free dependencies.");
      //prefCustomPath.setSummary(AppUtils.getDefaultAppFolder().getPath());
    } else {
      prefCustomPath.setSummary(path);
    }
  }

  private void setFilenameSummary() {
    int filenameValue = Integer.valueOf(appPreferences.getFilename());
    prefFilename.setSummary(getResources().getStringArray(R.array.filenameEntries)[filenameValue]);
  }

  private void setSortModeSummary() {
    int sortValue = Integer.valueOf(appPreferences.getSortMode());
    prefSortMode.setSummary(getResources().getStringArray(R.array.sortEntries)[sortValue]);
  }

  private void setThemeSummary(){
    int sortValue = Integer.valueOf(appPreferences.getTheme());
    prefTheme.setSummary(getResources().getStringArray(R.array.themeEntries)[sortValue]);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    // TODO why is the fragment not connected after the activity closes once
    if (isAdded()) {
      Preference pref = findPreference(key);
      if (pref == prefCustomPath) {
        setCustomPathSummary();
      } else if (pref == prefFilename) {
        setFilenameSummary();
      } else if (pref == prefSortMode) {
        setSortModeSummary();
      } else if (pref == prefTheme) {
        setThemeSummary();
      }
    }
  }
}