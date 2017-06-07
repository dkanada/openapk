package com.dkanada.openapk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.dkanada.openapk.App;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppUtils {
  private static final int MY_PERMISSIONS_REQUEST_WRITE_READ = 1;

  // extract file to app directory
  public static Boolean extractFile(AppInfo appInfo) {
    Boolean res = false;
    File input = new File(appInfo.getSource());
    File output = getOutputFilename(appInfo);
    createAppDir();
    try {
      FileUtils.copyFile(input, output);
      res = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return res;
  }

  // return default folder where apps will be saved
  public static File getDefaultAppFolder() {
    return new File(Environment.getExternalStorageDirectory() + "/OpenAPK");
  }

  // return custom folder where apps will be saved
  public static File getCustomAppFolder() {
    AppPreferences appPreferences = App.getAppPreferences();
    return new File(appPreferences.getCustomPath());
  }

  // create app directory
  public static void createAppDir() {
    File appDir = getCustomAppFolder();
    if (!appDir.exists()) {
      appDir.mkdir();
    }
  }

  // get the name of the extracted app
  public static String getAPKFilename(AppInfo appInfo) {
    AppPreferences appPreferences = App.getAppPreferences();
    switch (appPreferences.getFilename()) {
      case "1":
        return appInfo.getAPK() + "-" + appInfo.getVersion();
      case "2":
        return appInfo.getName() + "-" + appInfo.getVersion();
      case "3":
        return appInfo.getAPK();
      case "4":
        return appInfo.getName();
      default:
        return appInfo.getAPK();
    }
  }

  // get the name of the extracted app with the path
  public static File getOutputFilename(AppInfo appInfo) {
    return new File(getCustomAppFolder().getPath() + "/" + getAPKFilename(appInfo) + ".apk");
  }

  // delete all extracted apps from folder
  public static Boolean deleteAppFiles() {
    Boolean res = false;
    File f = getCustomAppFolder();
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
  public static void goToGooglePlay(Context context, String id) {
    try {
      context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + id)));
    } catch (ActivityNotFoundException e) {
      context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + id)));
    }
  }

  // save app name to clipboard
  public static void saveClipboard(Context context, AppInfo appInfo) {
    ClipData clipData;
    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    clipData = ClipData.newPlainText("text", appInfo.getAPK());
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
  public static Boolean saveIconToCache(Context context, AppInfo appInfo) {
    Boolean res = false;
    try {
      ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(appInfo.getAPK(), 0);
      File fileUri = new File(context.getCacheDir(), appInfo.getAPK());
      FileOutputStream out = new FileOutputStream(fileUri);
      Drawable icon = context.getPackageManager().getApplicationIcon(applicationInfo);
      BitmapDrawable iconBitmap = (BitmapDrawable) icon;
      iconBitmap.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
      res = true;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return res;
  }

  // delete app icon from cache folder
  public static Boolean removeIconFromCache(Context context, AppInfo appInfo) {
    File file = new File(context.getCacheDir(), appInfo.getAPK());
    return file.delete();
  }

  // get app icon from cache folder
  public static Drawable getIconFromCache(Context context, AppInfo appInfo) {
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
}
