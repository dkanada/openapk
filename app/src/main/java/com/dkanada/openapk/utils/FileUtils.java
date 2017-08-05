package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    // copy files that do not require root access
    public static Boolean cpExternalPartition(String input, String output) {
        createAppDir();
        File inputFile = new File(input);
        File outputFile = new File(output);
        try {
            org.apache.commons.io.FileUtils.copyFile(inputFile, outputFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // create app directory
    public static void createAppDir() {
        File appDir = new File(App.getAppPreferences().getCustomPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
    }

    // delete all extracted apps from folder
    public static Boolean deleteAppFiles() {
        Boolean res = false;
        File f = new File(App.getAppPreferences().getCustomPath());
        if (f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            for (File file : files) {
                file.delete();
            }
            if (f.listFiles().length == 0) {
                res = true;
            }
        }
        return res;
    }

    // save app icon to cache folder
    public static Boolean saveIconToCache(Context context, PackageInfo packageInfo) {
        Boolean res = false;
        try {
            File fileUri = new File(context.getCacheDir(), packageInfo.packageName);
            FileOutputStream out = new FileOutputStream(fileUri);
            Drawable icon = context.getPackageManager().getApplicationIcon(packageInfo.applicationInfo);
            BitmapDrawable iconBitmap = (BitmapDrawable) icon;
            iconBitmap.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    // delete app icon from cache folder
    public static Boolean removeIconFromCache(Context context, PackageInfo packageInfo) {
        File file = new File(context.getCacheDir(), packageInfo.packageName);
        return file.delete();
    }

    // get app icon from cache folder
    public static Drawable getIconFromCache(Context context, PackageInfo packageInfo) {
        Drawable res;
        try {
            File fileUri = new File(context.getCacheDir(), packageInfo.packageName);
            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            res = new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            res = context.getResources().getDrawable(R.drawable.ic_android);
        }
        return res;
    }

    public static long getFolderSize(String directory) {
        File dir = new File(directory);
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for(int i = 0; i < fileList.length; i++) {
                if(fileList[i].isDirectory()) {
                    result += getFolderSize(fileList[i].toString());
                } else {
                    result += fileList[i].length();
                }
            }
            return result;
        }
        return 0;
    }
}
