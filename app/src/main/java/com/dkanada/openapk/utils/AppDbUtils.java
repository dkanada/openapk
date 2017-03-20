package com.dkanada.openapk.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;

import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;

import java.util.ArrayList;
import java.util.List;

public class AppDbUtils extends SQLiteOpenHelper {
  private static final String TABLE_NAME = "apps";
  private static final String COLUMN_NAME_NAME = "name";
  private static final String COLUMN_NAME_APK = "apk";
  private static final String COLUMN_NAME_VERSION = "version";
  private static final String COLUMN_NAME_SOURCE = "source";
  private static final String COLUMN_NAME_DATA = "data";
  private static final String COLUMN_NAME_SYSTEM = "system";
  private static final String COLUMN_NAME_FAVORITE = "favorite";
  private static final String COLUMN_NAME_HIDDEN = "hidden";
  private static final String COLUMN_NAME_DISABLED = "disabled";

  private static final String SQL_CREATE_ENTRIES =
      "CREATE TABLE " + AppDbUtils.TABLE_NAME + " (" +
          AppDbUtils.COLUMN_NAME_NAME + " TEXT," +
          AppDbUtils.COLUMN_NAME_APK + " TEXT PRIMARY KEY," +
          AppDbUtils.COLUMN_NAME_VERSION + " TEXT," +
          AppDbUtils.COLUMN_NAME_SOURCE + " TEXT," +
          AppDbUtils.COLUMN_NAME_DATA + " TEXT," +
          AppDbUtils.COLUMN_NAME_SYSTEM + " TEXT," +
          AppDbUtils.COLUMN_NAME_FAVORITE + " TEXT," +
          AppDbUtils.COLUMN_NAME_HIDDEN + " TEXT," +
          AppDbUtils.COLUMN_NAME_DISABLED + " TEXT)";
  private static final String SQL_DELETE_ENTRIES =
      "DROP TABLE IF EXISTS " + AppDbUtils.TABLE_NAME;
  private static final String QUERY = "SELECT * FROM " + TABLE_NAME;

  public AppDbUtils(Context context) {
    super(context, "apps.db", null, 1);
  }

  public void onCreate(SQLiteDatabase db) {
    db.execSQL(SQL_CREATE_ENTRIES);
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // discard the data and start over
    db.execSQL(SQL_DELETE_ENTRIES);
    onCreate(db);
  }

  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    onUpgrade(db, oldVersion, newVersion);
  }

  public void addAppInfo(AppInfo appInfo) {
    if (checkAppInfo(appInfo, 0)) {
      // check for updates
    } else {
      ContentValues values = new ContentValues();
      SQLiteDatabase db = getWritableDatabase();
      values.put(COLUMN_NAME_NAME, appInfo.getName());
      values.put(COLUMN_NAME_APK, appInfo.getAPK());
      values.put(COLUMN_NAME_VERSION, appInfo.getVersion());
      values.put(COLUMN_NAME_SOURCE, appInfo.getSource());
      values.put(COLUMN_NAME_DATA, appInfo.getData());
      values.put(COLUMN_NAME_SYSTEM, appInfo.getSystem().toString());
      values.put(COLUMN_NAME_FAVORITE, appInfo.getFavorite().toString());
      values.put(COLUMN_NAME_HIDDEN, appInfo.getHidden().toString());
      values.put(COLUMN_NAME_DISABLED, appInfo.getDisabled().toString());
      db.insert(TABLE_NAME, null, values);
    }
  }

  public void removeAppInfo(AppInfo appInfo) {
    SQLiteDatabase db = getWritableDatabase();
    db.delete(TABLE_NAME, COLUMN_NAME_APK + " = ?", new String[]{String.valueOf(appInfo.getAPK())});
    db.close();
  }

  public Boolean checkAppInfo(AppInfo appInfo, int data) {
    SQLiteDatabase db = getWritableDatabase();
    String QUERY_EXIST = QUERY + " WHERE " + COLUMN_NAME_APK + " = '" + appInfo.getAPK() + "'";
    Cursor cursor = db.rawQuery(QUERY_EXIST, null);
    if (cursor.getCount() != 0) {
      cursor.moveToFirst();
      switch (data) {
        default:
          return true;
        case 1:
          return Boolean.parseBoolean(cursor.getString(5));
        case 2:
          return Boolean.parseBoolean(cursor.getString(6));
        case 3:
          return Boolean.parseBoolean(cursor.getString(7));
        case 4:
          return Boolean.parseBoolean(cursor.getString(8));
      }
    }
    return false;
  }

  public void updateAppInfo(AppInfo appInfo, int data) {
    SQLiteDatabase db = getWritableDatabase();
    ContentValues values = new ContentValues();
    String SELECTION = COLUMN_NAME_APK + " = '" + appInfo.getAPK() + "'";
    switch (data) {
      // everything
      default:
        removeAppInfo(appInfo);
        addAppInfo(appInfo);
        break;
      // system
      case 1:
        values.put(COLUMN_NAME_SYSTEM, appInfo.getSystem().toString());
        db.update(TABLE_NAME, values, SELECTION, null);
        break;
      // favorite
      case 2:
        values.put(COLUMN_NAME_FAVORITE, appInfo.getFavorite().toString());
        db.update(TABLE_NAME, values, SELECTION, null);
        break;
      // hidden
      case 3:
        values.put(COLUMN_NAME_HIDDEN, appInfo.getHidden().toString());
        db.update(TABLE_NAME, values, SELECTION, null);
        break;
      // disabled
      case 4:
        values.put(COLUMN_NAME_DISABLED, appInfo.getDisabled().toString());
        db.update(TABLE_NAME, values, SELECTION, null);
        break;
    }
  }

  public void updateDatabase(Context context) {
    final PackageManager packageManager = context.getPackageManager();
    List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
    // installed and system apps
    for (PackageInfo packageInfo : packages) {
      if (!(packageManager.getApplicationLabel(packageInfo.applicationInfo).equals("") || packageInfo.packageName.equals("")) && (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
        try {
          // installed apps
          AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, false, false, false, !packageInfo.applicationInfo.enabled, packageManager.getApplicationIcon(packageInfo.applicationInfo));
          addAppInfo(tempApp);
          AppUtils.saveIconToCache(context, tempApp);
        } catch (OutOfMemoryError e) {
          // TODO this is a workaround to avoid crashing on some devices (OutOfMemoryError) the drawable should be cached before
          AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, false, false, false, !packageInfo.applicationInfo.enabled, context.getResources().getDrawable(R.drawable.ic_android));
          addAppInfo(tempApp);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        try {
          // system apps
          AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, true, false, false, !packageInfo.applicationInfo.enabled, packageManager.getApplicationIcon(packageInfo.applicationInfo));
          addAppInfo(tempApp);
          AppUtils.saveIconToCache(context, tempApp);
        } catch (OutOfMemoryError e) {
          // TODO this is a workaround to avoid crashing on some devices (OutOfMemoryError) the drawable should be cached before
          AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, true, false, false, !packageInfo.applicationInfo.enabled, context.getResources().getDrawable(R.drawable.ic_android));
          addAppInfo(tempApp);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    SQLiteDatabase db = getWritableDatabase();
    String QUERY_EXIST = QUERY;
    Cursor cursor = db.rawQuery(QUERY_EXIST, null);
    cursor.moveToFirst();
    do {
      try {
        ApplicationInfo tmp = packageManager.getPackageInfo(cursor.getString(1), 0).applicationInfo;
      } catch (Exception e) {
        if (!checkAppInfo(getAppInfo(context, cursor), 3)) {
          removeAppInfo(getAppInfo(context, cursor));
          AppUtils.removeIconFromCache(context, getAppInfo(context, cursor));
          e.printStackTrace();
        }
      }
    } while (cursor.moveToNext());
  }

  public ArrayList<AppInfo> getAppList(Context context, int data) {
    ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
    SQLiteDatabase db = getWritableDatabase();
    Cursor cursor = db.rawQuery(QUERY, null);
    if (cursor.moveToFirst()) {
      do {
        switch (data) {
          // installed
          default:
            if (cursor.getString(5).equals("false") && cursor.getString(7).equals("false") && cursor.getString(8).equals("false")) {
              appList.add(getAppInfo(context, cursor));
            }
            break;
          // system
          case 1:
            if (cursor.getString(5).equals("true") && cursor.getString(7).equals("false") && cursor.getString(8).equals("false")) {
              appList.add(getAppInfo(context, cursor));
            }
            break;
          // favorite
          case 2:
            if (cursor.getString(6).equals("true")) {
              appList.add(getAppInfo(context, cursor));
            }
            break;
          // hidden
          case 3:
            if (cursor.getString(7).equals("true")) {
              appList.add(getAppInfo(context, cursor));
            }
            break;
          // disabled
          case 4:
            if (cursor.getString(8).equals("true")) {
              appList.add(getAppInfo(context, cursor));
            }
            break;
        }
      } while (cursor.moveToNext());
    }
    return appList;
  }

  public AppInfo getAppInfo(Context context, Cursor cursor) {
    String name = cursor.getString(0);
    String apk = cursor.getString(1);
    String version = cursor.getString(2);
    String source = cursor.getString(3);
    String data = cursor.getString(4);
    Boolean system = Boolean.parseBoolean(cursor.getString(5));
    Boolean favorite = Boolean.parseBoolean(cursor.getString(6));
    Boolean hidden = Boolean.parseBoolean(cursor.getString(7));
    Boolean disabled = Boolean.parseBoolean(cursor.getString(8));
    AppInfo tempApp = new AppInfo(name + "##" + apk + "##" + version + "##" + source + "##" + data + "##" + system + "##" + favorite + "##" + hidden + "##" + disabled);
    Drawable tempAppIcon = AppUtils.getIconFromCache(context, tempApp);
    tempApp.setIcon(tempAppIcon);
    return tempApp;
  }
}