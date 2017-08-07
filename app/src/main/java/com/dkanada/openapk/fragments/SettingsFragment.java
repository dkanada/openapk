package com.dkanada.openapk.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppPreferences;

public final class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference prefCustomPath;
    private ListPreference prefCustomFile;
    private ListPreference prefSortMethod;
    private ListPreference prefTheme;

    AppPreferences appPreferences;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        appPreferences = App.getAppPreferences();
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        context = getActivity();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);

        prefCustomPath = (EditTextPreference) findPreference(getString(R.string.pref_custom_path));
        prefCustomFile = (ListPreference) findPreference(getString(R.string.pref_custom_file));
        prefSortMethod = (ListPreference) findPreference(getString(R.string.pref_sort_method));
        prefTheme = (ListPreference) findPreference(getString(R.string.pref_theme));

        // removes settings that wont work on lower versions
        Preference prefNavigationColor = findPreference(getString(R.string.pref_navigation_color));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            prefNavigationColor.setEnabled(false);
        }

        Preference prefReset = findPreference(getString(R.string.pref_reset));
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
        prefCustomPath.setSummary(appPreferences.getCustomPath());
    }

    private void setFilenameSummary() {
        int filenameValue = Integer.valueOf(appPreferences.getFilename());
        prefCustomFile.setSummary(getResources().getStringArray(R.array.filenameEntries)[filenameValue]);
    }

    private void setSortModeSummary() {
        int sortValue = Integer.valueOf(appPreferences.getSortMode());
        prefSortMethod.setSummary(getResources().getStringArray(R.array.sortEntries)[sortValue]);
    }

    private void setThemeSummary() {
        int themeValue = Integer.valueOf(appPreferences.getTheme());
        prefTheme.setSummary(getResources().getStringArray(R.array.themeEntries)[themeValue]);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (isAdded()) {
            Preference pref = findPreference(key);
            if (pref == prefCustomPath) {
                setCustomPathSummary();
            } else if (pref == prefCustomFile) {
                setFilenameSummary();
            } else if (pref == prefSortMethod) {
                setSortModeSummary();
            } else if (pref == prefTheme) {
                setThemeSummary();
            }
        }
    }
}