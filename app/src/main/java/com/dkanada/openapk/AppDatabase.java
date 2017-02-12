package com.dkanada.openapk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase extends SQLiteOpenHelper {
  public static final String TABLE_NAME = "apps";
  public static final String COLUMN_NAME_APK = "apk";
  public static final String COLUMN_NAME_NAME = "name";
  public static final String COLUMN_NAME_VERSION = "version";
  public static final String COLUMN_NAME_SOURCE = "source";
  public static final String COLUMN_NAME_DATA = "data";
  public static final String COLUMN_NAME_SYSTEM = "system";
  public static final String COLUMN_NAME_FAVORITE = "favorite";
  public static final String COLUMN_NAME_HIDDEN = "hidden";
  public static final String COLUMN_NAME_DISABLED = "disabled";

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

  public void checkDatabase(AppInfo appInfo) {

  }

  public void addAppInfo(AppInfo appInfo) {

  }

  public void removeAppInfo(AppInfo appInfo) {

  }

  public void readAppInfo(AppInfo appInfo) {

  }

  public void updateAppInfo(AppInfo appInfo) {

  }
}
