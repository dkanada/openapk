package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.dkanada.openapk.models.AppInfo;

import java.util.List;

public class ParseJson {
    private Context context;
    private List<AppInfo> appList;
    private String file;

    public ParseJson(Context context, String file) {
        this.context = context;
        this.file = file;
    }

    public boolean checkAppList(PackageInfo packageInfo) {
        appList = FileOperations.readConfigFile(context, file);
        for (AppInfo appInfo : appList) {
            if (appInfo.getPackageName().equals(packageInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    public List<AppInfo> getAppList() {
        appList = FileOperations.readConfigFile(context, file);
        return appList;
    }

    public void setAppList(List<AppInfo> appList) {
        FileOperations.writeConfigFile(context, appList, file);
    }
}
