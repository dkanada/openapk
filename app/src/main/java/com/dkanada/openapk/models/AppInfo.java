package com.dkanada.openapk.models;

import android.content.pm.PackageInfo;

import com.dkanada.openapk.App;

public class AppInfo {
    private String packageLabel;
    private String packageName;
    private String versionName;
    private String versionCode;
    private String data;
    private String source;
    private String install;
    private String update;

    public AppInfo(PackageInfo packageInfo) {
        packageLabel = App.getPackageName(packageInfo);
        packageName = packageInfo.packageName;
        versionName = packageInfo.versionName;
        versionCode = Integer.toString(packageInfo.versionCode);
        data = packageInfo.applicationInfo.dataDir;
        source = packageInfo.applicationInfo.sourceDir;
        install = Long.toString(packageInfo.firstInstallTime);
        update = Long.toString(packageInfo.lastUpdateTime);
    }

    public AppInfo(String packageLabel, String packageName, String versionName, String versionCode, String data, String source, String install, String update) {
        this.packageLabel = packageLabel;
        this.packageName = packageName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.data = data;
        this.source = source;
        this.install = install;
        this.update = update;
    }

    public String getPackageLabel() {
        return packageLabel;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getData() {
        return data;
    }

    public String getSource() {
        return source;
    }

    public String getInstall() {
        return install;
    }

    public String getUpdate() {
        return update;
    }
}
