package com.dkanada.openapk.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dkanada.openapk.OpenAPKApplication;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.UtilsApp;
import com.dkanada.openapk.utils.UtilsUI;

import yuku.ambilwarna.widget.AmbilWarnaPreference;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

  // load settings
  private AppPreferences appPreferences;
  private Toolbar toolbar;
  private Context context;

  private Preference prefVersion, prefLicense, prefDeleteAll, prefDefaultValues, prefNavigationBlack, prefCustomPath;
  private AmbilWarnaPreference prefPrimaryColor, prefFABColor;
  private ListPreference prefCustomFilename, prefSortMode, prefTheme;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    this.appPreferences = OpenAPKApplication.getAppPreferences();
    if (appPreferences.getTheme().equals("1")) {
      setTheme(R.style.Light);
    } else {
      setTheme(R.style.Dark);
    }
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.settings);
    this.context = this;

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    prefs.registerOnSharedPreferenceChangeListener(this);

    prefDeleteAll = findPreference("prefDeleteAll");
    prefCustomPath = findPreference("prefCustomPath");
    prefCustomFilename = (ListPreference) findPreference("prefCustomFilename");
    prefSortMode = (ListPreference) findPreference("prefSortMode");

    prefTheme = (ListPreference) findPreference("prefTheme");
    prefPrimaryColor = (AmbilWarnaPreference) findPreference("prefPrimaryColor");
    prefFABColor = (AmbilWarnaPreference) findPreference("prefFABColor");
    prefNavigationBlack = findPreference("prefNavigationBlack");
    prefDefaultValues = findPreference("prefDefaultValues");

    prefVersion = findPreference("prefVersion");
    prefLicense = findPreference("prefLicense");

    setInitialConfiguration();

    prefVersion.setTitle(getResources().getString(R.string.app_name) + " v" + UtilsApp.getAppVersionName(context));
    prefVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        startActivity(new Intent(context, AboutActivity.class));
        return false;
      }
    });

    prefLicense.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        startActivity(new Intent(context, LicenseActivity.class));
        return false;
      }
    });

    // set a few preference summaries
    setCustomFilenameSummary();
    setSortModeSummary();
    setCustomPathSummary();
    setThemeSummary();

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
        appPreferences.setPrimaryColorPref(getResources().getColor(R.color.actionBar));
        appPreferences.setFABColorPref(getResources().getColor(R.color.fab));
        return true;
      }
    });
  }

  @Override
  public void setContentView(int layoutResID) {
    ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_settings, new LinearLayout(this), false);
    toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);
    //TODO Toolbar should load the default style in XML (white title and back arrow), but doesn't happen
    toolbar.setTitleTextColor(getResources().getColor(R.color.textWhite));
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    });

    ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
    LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);
    getWindow().setContentView(contentView);
  }

  private void setInitialConfiguration() {
    toolbar.setTitle(getResources().getString(R.string.action_settings));

    // 5.0+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(UtilsUI.darker(appPreferences.getPrimaryColorPref(), 0.8));
      toolbar.setBackgroundColor(appPreferences.getPrimaryColorPref());
      if (!appPreferences.getNavigationBlackPref()) {
        getWindow().setNavigationBarColor(appPreferences.getPrimaryColorPref());
      }
    }

    // 5.0-
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      prefPrimaryColor.setEnabled(false);
      prefNavigationBlack.setEnabled(false);
      prefNavigationBlack.setDefaultValue(true);
    }
  }

  private void setCustomPathSummary() {
    String path = appPreferences.getCustomPath();
    if (path.equals(UtilsApp.getDefaultAppFolder().getPath())) {
      prefCustomPath.setSummary("Not implemented yet due to non-free dependencies.");
      //prefCustomPath.setSummary(UtilsApp.getDefaultAppFolder().getPath());
    } else {
      prefCustomPath.setSummary(path);
    }
  }

  private void setCustomFilenameSummary() {
    int filenameValue = Integer.valueOf(appPreferences.getCustomFilename()) - 1;
    prefCustomFilename.setSummary(getResources().getStringArray(R.array.filenameEntries)[filenameValue]);
  }

  private void setSortModeSummary() {
    int sortValue = Integer.valueOf(appPreferences.getSortMode()) - 1;
    prefSortMode.setSummary(getResources().getStringArray(R.array.sortEntries)[sortValue]);
  }

  private void setThemeSummary(){
    int sortValue = Integer.valueOf(appPreferences.getTheme()) - 1;
    prefTheme.setSummary(getResources().getStringArray(R.array.themeEntries)[sortValue]);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference pref = findPreference(key);
    if (pref == prefCustomPath) {
      setCustomPathSummary();
    } else if (pref == prefCustomFilename) {
      setCustomFilenameSummary();
    } else if (pref == prefSortMode) {
      setSortModeSummary();
    } else if (pref == prefTheme) {
      setThemeSummary();
    }
  }
}
