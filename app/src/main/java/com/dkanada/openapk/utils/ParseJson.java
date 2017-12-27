package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.dkanada.openapk.models.AppItem;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ParseJson {
    private Context context;
    private List<AppItem> appList;
    private String file;

    public ParseJson(Context context, String file) {
        this.context = context;
        this.file = file;
    }

    public boolean checkAppList(PackageInfo packageInfo) {
        appList = getAppList();
        for (AppItem appItem : appList) {
            if (appItem.getPackageName().equals(packageInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    public List<AppItem> getAppList() {
        List<AppItem> appList = new ArrayList<>();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(context.openFileInput(file)));
            reader.beginArray();
            while (reader.hasNext()) {
                Gson gson = new Gson();
                appList.add((AppItem) gson.fromJson(reader, AppItem.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appList;
    }

    public void setAppList(List<AppItem> appList) {
        Gson gson = new Gson();
        String content = gson.toJson(appList);
        FileOperations.writeToFile(context, file, content);
    }
}
