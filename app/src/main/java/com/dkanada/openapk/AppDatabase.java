package com.dkanada.openapk;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dkanada.openapk.utils.UtilsApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AppDatabase extends SQLiteOpenHelper {
  private static final String TABLE_NAME = "apps";
  private static final String COLUMN_NAME_APK = "apk";
  private static final String COLUMN_NAME_NAME = "name";
  private static final String COLUMN_NAME_VERSION = "version";
  private static final String COLUMN_NAME_SOURCE = "source";
  private static final String COLUMN_NAME_DATA = "data";
  private static final String COLUMN_NAME_SYSTEM = "system";
  private static final String COLUMN_NAME_FAVORITE = "favorite";
  private static final String COLUMN_NAME_HIDDEN = "hidden";
  private static final String COLUMN_NAME_DISABLED = "disabled";

  private static final String SQL_CREATE_ENTRIES =
      "CREATE TABLE " + AppDatabase.TABLE_NAME + " (" +
          AppDatabase.COLUMN_NAME_APK + " TEXT PRIMARY KEY," +
          AppDatabase.COLUMN_NAME_NAME + " TEXT," +
          AppDatabase.COLUMN_NAME_VERSION + " TEXT," +
          AppDatabase.COLUMN_NAME_SOURCE + " TEXT," +
          AppDatabase.COLUMN_NAME_DATA + " TEXT," +
          AppDatabase.COLUMN_NAME_SYSTEM + " TEXT," +
          AppDatabase.COLUMN_NAME_FAVORITE + " TEXT," +
          AppDatabase.COLUMN_NAME_HIDDEN + " TEXT," +
          AppDatabase.COLUMN_NAME_DISABLED + " TEXT)";
  private static final String SQL_DELETE_ENTRIES =
      "DROP TABLE IF EXISTS " + AppDatabase.TABLE_NAME;

  public AppDatabase(Context context) {
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
    removeAppInfo(appInfo);
    ContentValues values = new ContentValues();
    SQLiteDatabase db = getWritableDatabase();
    values.put(COLUMN_NAME_APK, appInfo.getAPK());
    values.put(COLUMN_NAME_NAME, appInfo.getName());
    values.put(COLUMN_NAME_VERSION, appInfo.getVersion());
    values.put(COLUMN_NAME_SOURCE, appInfo.getSource());
    values.put(COLUMN_NAME_DATA, appInfo.getData());
    values.put(COLUMN_NAME_SYSTEM, appInfo.getSystem());
    values.put(COLUMN_NAME_FAVORITE, appInfo.getFavorite());
    values.put(COLUMN_NAME_HIDDEN, appInfo.getHidden());
    values.put(COLUMN_NAME_DISABLED, appInfo.getDisabled());
    db.insert(TABLE_NAME, null, values);
  }

  public void removeAppInfo(AppInfo appInfo) {
    SQLiteDatabase db = getWritableDatabase();
    db.delete(TABLE_NAME, COLUMN_NAME_APK + " = ?", new String[]{String.valueOf(appInfo.getAPK())});
    db.close();
  }

  public void updateAppInfo(AppInfo appInfo) {
    SQLiteDatabase db = getWritableDatabase();
    removeAppInfo(appInfo);
    addAppInfo(appInfo);
  }

  public void updateDatabase(Context context) {
    final PackageManager packageManager = context.getPackageManager();
    List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

    // installed and system apps
    for (PackageInfo packageInfo : packages) {
      if (!(packageManager.getApplicationLabel(packageInfo.applicationInfo).equals("") || packageInfo.packageName.equals(""))) {
        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
          try {
            // installed apps
            AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, packageManager.getApplicationIcon(packageInfo.applicationInfo), false);
            addAppInfo(tempApp);
            //UtilsApp.saveIconToCache(context, tempApp);
          } catch (OutOfMemoryError e) {
            //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
            AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, context.getResources().getDrawable(R.drawable.ic_android), false);
            addAppInfo(tempApp);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          try {
            // system apps
            AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, packageManager.getApplicationIcon(packageInfo.applicationInfo), true);
            addAppInfo(tempApp);
            //UtilsApp.saveIconToCache(context, tempApp);
          } catch (OutOfMemoryError e) {
            //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
            AppInfo tempApp = new AppInfo(packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(), packageInfo.packageName, packageInfo.versionName, packageInfo.applicationInfo.sourceDir, packageInfo.applicationInfo.dataDir, context.getResources().getDrawable(R.drawable.ic_android), false);
            addAppInfo(tempApp);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  public ArrayList<AppInfo> returnAppList(int data) {
    ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
    String query = "SELECT * FROM " + TABLE_NAME;
    SQLiteDatabase db = getWritableDatabase();
    Cursor cursor = db.rawQuery(query, null);
    if (cursor.moveToFirst()) {
      do {
        switch(data) {
          // installed
          default:
            if (cursor.getString(5).equals(false)) {
              appList.add(returnAppInfo(cursor));
            }
            break;
          // system
          case 1:
            if (cursor.getString(5).equals(true)) {
              appList.add(returnAppInfo(cursor));
            }
            break;
          // favorite
          case 2:
            if (cursor.getString(6).equals(true)) {
              appList.add(returnAppInfo(cursor));
            }
            break;
          // hidden
          case 3:
            if (cursor.getString(7).equals(true)) {
              appList.add(returnAppInfo(cursor));
            }
            break;
          // disabled
          case 4:
            if (cursor.getString(8).equals(true)) {
              appList.add(returnAppInfo(cursor));
            }
            break;
        }
      } while (cursor.moveToNext());
    }
    return appList;
  }

  public AppInfo returnAppInfo(Cursor cursor) {
    String name = cursor.getString(0);
    String apk = cursor.getString(1);
    String version = cursor.getString(2);
    String source = cursor.getString(3);
    String data = cursor.getString(4);
    Boolean system = Boolean.parseBoolean(cursor.getString(5));
    Boolean favorite = Boolean.parseBoolean(cursor.getString(6));
    Boolean hidden = Boolean.parseBoolean(cursor.getString(7));
    Boolean disabled = Boolean.parseBoolean(cursor.getString(8));
    AppInfo app = new AppInfo(name + "##" + apk + "##" + version + "##" + source + "##" + data + "##" + system + "##" + favorite + "##" + hidden + "##" + disabled);
    return app;
  }
}
