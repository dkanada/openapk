package com.dkanada.openapk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;

import com.dkanada.openapk.interfaces.PackageStatsListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PackageStatsHelper {
    public static void getPackageStats(Context context, PackageInfo packageInfo, final PackageStatsListener packageStatsListener) {
        try {
            final Activity activity = (Activity) context;
            Method getPackageSize = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            getPackageSize.invoke(context.getPackageManager(), packageInfo.applicationInfo.packageName, new IPackageStatsObserver.Stub() {
                @Override
                public void onGetStatsCompleted(final android.content.pm.PackageStats pStats, final boolean succeeded) throws RemoteException {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            packageStatsListener.onPackageStats(pStats);
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
}
