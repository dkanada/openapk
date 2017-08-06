package com.dkanada.openapk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.RemoteException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PackageStatsHandler {
    public PackageStatsHandler(Context context, PackageInfo packageInfo) {
        try {
            final Activity activity = (Activity) context;
            Method getPackageSize = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            getPackageSize.invoke(context.getPackageManager(), packageInfo.applicationInfo.packageName, new IPackageStatsObserver.Stub() {
                @Override
                public void onGetStatsCompleted(final PackageStats pStats, final boolean succeeded) throws RemoteException {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PackageStats packageStats = pStats;
                        }
                    });
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public long getPackageStatsTotal(PackageStats packageStats) {
        return packageStats.cacheSize + packageStats.codeSize + packageStats.dataSize
                + packageStats.externalCacheSize + packageStats.externalCodeSize + packageStats.externalDataSize
                + packageStats.externalObbSize + packageStats.externalMediaSize;
    }
}
