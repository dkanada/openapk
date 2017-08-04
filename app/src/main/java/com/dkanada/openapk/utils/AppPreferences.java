package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.dkanada.openapk.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AppPreferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String KeyCustomPath = "prefCustomPath";
    private static final String KeyFilename = "prefFilename";
    private static final String KeySortMode = "prefSortMode";
    private static final String KeyExtractButton = "prefExtractButton";
    private static final String KeyTheme = "prefTheme";
    private static final String KeyPrimaryColor = "prefPrimaryColor";
    private static final String KeyAccentColor = "prefAccentColor";
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
        return sharedPreferences.getString(KeyCustomPath, Environment.getExternalStorageDirectory() + "/OpenAPK");
    }

    public void setCustomPath(String value) {
        editor.putString(KeyCustomPath, value);
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

    public int getPrimaryColor() {
        return sharedPreferences.getInt(KeyPrimaryColor, context.getResources().getColor(R.color.primary));
    }

    public int getAccentColor() {
        return sharedPreferences.getInt(KeyAccentColor, context.getResources().getColor(R.color.accent));
    }

    public Boolean getNavigationColor() {
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

    public void setInitialSetup(boolean value) {
        editor.putBoolean(KeyInitialSetup, value);
        editor.commit();
    }

    public int getRootStatus() {
        return sharedPreferences.getInt(KeyIsRooted, 0);
    }

    public void setRootStatus(int value) {
        editor.putInt(KeyIsRooted, value);
        editor.commit();
    }
}
