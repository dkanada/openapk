package com.dkanada.openapk.utils;

import android.content.Context;

import com.dkanada.openapk.models.AppItem;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ParseJson {
    private Context context;

    public ParseJson(Context context) {
        this.context = context;
    }

    public List<AppItem> getHiddenList() {
        return getAppList(context.getCacheDir().toString() + "/hide.json");
    }

    public void setHiddenList(List<AppItem> appList) {
        setAppList(context.getCacheDir().toString() + "/hide.json", appList);
    }

    public List<AppItem> getAppList(String file) {
        List<AppItem> appList = new ArrayList<>();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file)));
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

    public void setAppList(String file, List<AppItem> appList) {
        Gson gson = new Gson();
        String content = gson.toJson(appList);
        FileOperations.writeToFile(context, file, content);
    }
}
