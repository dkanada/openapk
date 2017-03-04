package com.dkanada.openapk.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.dkanada.openapk.OpenAPKApplication;
import com.dkanada.openapk.R;
import com.dkanada.openapk.activities.AboutActivity;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.UtilsApp;

import yuku.ambilwarna.widget.AmbilWarnaPreference;

public final class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Preference prefDeleteAll, prefCustomPath, prefNavigationColor, prefDefaultValues, prefDoubleTap, prefRootEnabled, prefVersion;
  private AmbilWarnaPreference prefPrimaryColor, prefFABColor;
  private ListPreference prefFilename, prefSortMode, prefTheme;
  AppPreferences preferences;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);
    preferences = OpenAPKApplication.getAppPreferences();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    prefs.registerOnSharedPreferenceChangeListener(this);

    prefDeleteAll = findPreference("prefDeleteAll");
    prefCustomPath = findPreference("prefCustomPath");
    prefFilename = (ListPreference) findPreference("prefFilename");
    prefSortMode = (ListPreference) findPreference("prefSortMode");

    prefTheme = (ListPreference) findPreference("prefTheme");
    prefPrimaryColor = (AmbilWarnaPreference) findPreference("prefPrimaryColor");
    prefFABColor = (AmbilWarnaPreference) findPreference("prefFABColor");
    prefNavigationColor = findPreference("prefNavigationColor");
    prefDefaultValues = findPreference("prefDefaultValues");

    prefDoubleTap = findPreference("prefDoubleTap");
    prefRootEnabled = findPreference("prefRootEnabled");
    prefVersion = findPreference("prefVersion");

    prefVersion.setTitle(getResources().getString(R.string.app_name) + " v" + UtilsApp.getAppVersionName(getActivity()));
    prefVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        startActivity(new Intent(getActivity(), AboutActivity.class));
        return false;
      }
    });

    // prefDeleteAll
    prefDeleteAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        prefDeleteAll.setSummary(R.string.deleting);
        prefDeleteAll.setEnabled(false);
        Boolean deleteAll = UtilsApp.deleteAppFiles();
        if (deleteAll) {
          prefDeleteAll.setSummary(R.string.deleting_done);
        } else {
          prefDeleteAll.setSummary(R.string.deleting_error);
        }
        prefDeleteAll.setEnabled(true);
        return true;
      }
    });

    // prefDefaultValues
    prefDefaultValues.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        preferences.setPrimaryColorPref(getResources().getColor(R.color.actionBar));
        preferences.setFABColorPref(getResources().getColor(R.color.fab));
        return true;
      }
    });

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      prefPrimaryColor.setEnabled(false);
      prefNavigationColor.setEnabled(false);
      prefNavigationColor.setDefaultValue(false);
    }

    setSortModeSummary();
    setThemeSummary();
    setCustomPathSummary();
    setFilenameSummary();
  }

  private void setCustomPathSummary() {
    String path = preferences.getCustomPath();
    if (path.equals(UtilsApp.getDefaultAppFolder().getPath())) {
      prefCustomPath.setSummary("Not implemented yet due to non-free dependencies.");
      //prefCustomPath.setSummary(UtilsApp.getDefaultAppFolder().getPath());
    } else {
      prefCustomPath.setSummary(path);
    }
  }

  private void setFilenameSummary() {
    int filenameValue = Integer.valueOf(preferences.getFilename());
    prefFilename.setSummary(getResources().getStringArray(R.array.filenameEntries)[filenameValue]);
  }

  private void setSortModeSummary() {
    int sortValue = Integer.valueOf(preferences.getSortMode());
    prefSortMode.setSummary(getResources().getStringArray(R.array.sortEntries)[sortValue]);
  }

  private void setThemeSummary(){
    int sortValue = Integer.valueOf(preferences.getTheme());
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