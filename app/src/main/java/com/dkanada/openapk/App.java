package com.dkanada.openapk;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.utils.AppPreferences;
import com.dkanada.openapk.utils.FileOperations;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;

import java.util.ArrayList;

public class App extends Application {
    private static AppPreferences appPreferences;
    private static PackageManager packageManager;
    private static int currentAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        // set fields
        appPreferences = new AppPreferences(this);
        packageManager = getPackageManager();
        currentAdapter = 0;

        // register custom fonts
        Iconics.registerFont(new GoogleMaterial());
    }

    public static AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public static String getPackageName(PackageInfo packageInfo) {
        return packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
    }

    public static Drawable getPackageIcon(PackageInfo packageInfo) {
        return packageManager.getApplicationIcon(packageInfo.applicationInfo);
    }

    public static int getCurrentAdapter() {
        return currentAdapter;
    }

    public static void setCurrentAdapter(int value) {
        currentAdapter = value;
    }
}