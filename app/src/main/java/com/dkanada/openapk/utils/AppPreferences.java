package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.dkanada.openapk.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppPreferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public AppPreferences(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
        this.context = context;
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

    public Boolean getStatusColor() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_status_color), false);
    }

    public Boolean getNavigationColor() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_navigation_color), false);
    }

    public Boolean getDoubleTap() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_double_tap), false);
    }

    public Boolean getRootEnabled() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_enable_root), false);
    }

    // internal preferences
    public boolean getInitialSetup() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_initial_setup), false);
    }

    public void setInitialSetup(boolean value) {
        editor.putBoolean(context.getString(R.string.pref_initial_setup), value);
        editor.commit();
    }

    public int getRootStatus() {
        return sharedPreferences.getInt(context.getString(R.string.pref_root), 0);
    }

    public void setRootStatus(int value) {
        editor.putInt(context.getString(R.string.pref_root), value);
        editor.commit();
    }

    public List<String> getHiddenList() {
        String list = sharedPreferences.getString(context.getString(R.string.pref_hidden_list), "");
        if (list.equals("")) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(Arrays.asList(list.split(",")));
        }
    }

    public void setHiddenList(List<String> list) {
        String stringList = "";
        for (String string : list) {
            stringList += string + ",";
        }
        editor.putString(context.getString(R.string.pref_hidden_list), stringList);
        editor.commit();
    }

    public List<String> getFavoriteList() {
        String list = sharedPreferences.getString(context.getString(R.string.pref_favorite_list), "");
        if (list.equals("")) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(Arrays.asList(list.split(",")));
        }
    }

    public void setFavoriteList(List<String> list) {
        String stringList = "";
        for (String string : list) {
            stringList += string + ",";
        }
        editor.putString(context.getString(R.string.pref_favorite_list), stringList);
        editor.commit();
    }
}
