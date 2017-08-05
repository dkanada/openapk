package com.dkanada.openapk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dkanada.openapk.App;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class OtherUtils {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;

    // extract file to specified directory
    public static Boolean extractFile(PackageInfo packageInfo, String directory) {
        Boolean res = false;
        File input = new File(packageInfo.applicationInfo.sourceDir);
        File output = new File(directory + getAPKFilename(packageInfo));
        createAppDir();
        try {
            FileUtils.copyFile(input, output);
            res = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    // create app directory
    public static void createAppDir() {
        File appDir = new File(App.getAppPreferences().getCustomPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
    }

    // get the name of the extracted app
    public static String getAPKFilename(PackageInfo packageInfo) {
        AppPreferences appPreferences = App.getAppPreferences();
        switch (appPreferences.getFilename()) {
            case "0":
                return packageInfo.packageName + "-" + packageInfo.versionName + ".apk";
            case "1":
                return App.getPackageName(packageInfo) + "-" + packageInfo.versionCode + ".apk";
            case "2":
                return packageInfo.packageName + ".apk";
            case "3":
                return App.getPackageName(packageInfo) + ".apk";
            default:
                return packageInfo.packageName + ".apk";
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

    // open google play if installed otherwise open browser
    public static void goToGooglePlay(Context context, PackageInfo packageInfo) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageInfo.packageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageInfo.packageName)));
        }
    }

    // save app name to clipboard
    public static void saveClipboard(Context context, PackageInfo packageInfo) {
        ClipData clipData;
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipData = ClipData.newPlainText("text", packageInfo.packageName);
        clipboardManager.setPrimaryClip(clipData);
    }

    // get version number for this app
    public static String getAppVersionName(Context context) {
        String res = "0.0.0.0";
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // get version code for this app
    public static int getAppVersionCode(Context context) {
        int res = 0;
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // get intent to share app
    public static Intent getShareIntent(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
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
    public static Boolean removeIconFromCache(Context context, AppInfo appInfo) {
        // TODO
        File file = new File(context.getCacheDir(), appInfo.getAPK());
        return file.delete();
    }

    // get app icon from cache folder
    public static Drawable getIconFromCache(Context context, AppInfo appInfo) {
        // TODO
        Drawable res;
        try {
            File fileUri = new File(context.getCacheDir(), appInfo.getAPK());
            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            res = new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            res = context.getResources().getDrawable(R.drawable.ic_android);
        }
        return res;
    }

    // check app permissions
    public static Boolean checkPermissions(Activity activity) {
        Boolean res = false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_READ);
        } else {
            res = true;
        }
        return res;
    }

    public static int dark(int color, double factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, Math.max((int) (r * factor), 0), Math.max((int) (g * factor), 0), Math.max((int) (b * factor), 0));
    }

    // set the toolbar title with any string
    public static void setToolbarTitle(Activity activity, String title) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    // update the state of the favorite icon
    public static void updateAppFavoriteIcon(Context context, MenuItem menuItem, Boolean isFavorite) {
        if (isFavorite) {
            menuItem.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_star));
        } else {
            menuItem.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_star_border));
        }
    }
}
