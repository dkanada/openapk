package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.dkanada.openapk.R;

public class AppPreferences {
    private SharedPreferences sharedPreferences;
    private Context context;

    public static final int CODE_PERMISSION = 10;
    public static final int CODE_UNINSTALL = 11;

    public AppPreferences(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static AppPreferences get(Context context) {
        return new AppPreferences(context);
    }

    public String getCustomPath() {
        return sharedPreferences.getString(context.getString(R.string.pref_custom_path), Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name));
    }

    public String getFilename() {
        return sharedPreferences.getString(context.getString(R.string.pref_custom_file), "0");
    }

    public String getSortMethod() {
        return sharedPreferences.getString(context.getString(R.string.pref_sort_method), "0");
    }

    public String getTheme() {
        return sharedPreferences.getString(context.getString(R.string.pref_theme), "1");
    }

    public int getPrimaryColor() {
        return sharedPreferences.getInt(context.getString(R.string.pref_primary_color), context.getResources().getColor(R.color.primary));
    }

    public int getAccentColor() {
        return sharedPreferences.getInt(context.getString(R.string.pref_accent_color), context.getResources().getColor(R.color.accent));
    }

    public Boolean getDoubleTap() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_double_tap), false);
    }

    public Boolean getRootEnabled() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_enable_root), false);
    }

    public boolean getInitialSetup() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_initial_setup), false);
    }

    public void setInitialSetup(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_initial_setup), value);
        editor.apply();
    }

    public int getRootStatus() {
        return sharedPreferences.getInt(context.getString(R.string.pref_root), 0);
    }

    public void setRootStatus(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_root), value);
        editor.apply();
    }
}
