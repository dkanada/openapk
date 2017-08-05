package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PackageStatsHandler {
    private Method getPackageSize;
    private PackageStats packageStats;
    private Context context;

    public PackageStatsHandler(Context context) {
        this.context = context;
        try {
            getPackageSize = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public int getPackageSize(PackageInfo packageInfo) {
        PackageStats packageStats = getPackageStats(packageInfo);
        return (int) (packageStats.cacheSize + packageStats.codeSize + packageStats.dataSize
                + packageStats.externalCacheSize + packageStats.externalCodeSize + packageStats.externalDataSize
                + packageStats.externalObbSize + packageStats.externalMediaSize);
    }

    public PackageStats getPackageStats(PackageInfo packageInfo) {
        try {
            getPackageSize.invoke(context.getPackageManager(), packageInfo.applicationInfo.packageName, new IPackageStatsObserver.Stub() {
                @Override
                public void onGetStatsCompleted(final PackageStats pStats, final boolean succeeded) throws RemoteException {
                    packageStats = pStats;
                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return packageStats;
    }
}
