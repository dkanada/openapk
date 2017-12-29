package com.dkanada.openapk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileOperations {
    // copy files that do not require root access
    public static boolean cpExternalPartition(String input, String output) {
        createFolder(App.getAppPreferences().getCustomPath());
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

    public static void createFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void deleteFolder(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    // save app icon to cache folder
    public static boolean saveIconToCache(Context context, String packageName) {
        boolean result = false;
        try {
            FileOutputStream out = new FileOutputStream(new File(context.getCacheDir(), packageName + ".png"));
            Drawable icon = context.getPackageManager().getApplicationIcon(packageName);
            BitmapDrawable iconBitmap = (BitmapDrawable) icon;
            iconBitmap.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // delete app icon from cache folder
    public static boolean removeIconFromCache(Context context, String packageName) {
        File file = new File(context.getCacheDir(), packageName + ".png");
        return file.delete();
    }

    // get app icon from cache folder
    public static Drawable getIconFromCache(Context context, String packageName) {
        Drawable result;
        try {
            File file = new File(context.getCacheDir(), packageName + ".png");
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            result = new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            result = context.getResources().getDrawable(R.drawable.ic_android);
        }
        return result;
    }

    public static void writeToFile(Context context, String file, String content) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(content);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
