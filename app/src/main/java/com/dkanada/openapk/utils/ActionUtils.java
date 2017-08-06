package com.dkanada.openapk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;

import java.io.File;

public class ActionUtils {
    public static boolean open(Context context, PackageInfo packageInfo) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            DialogUtils.toastMessage((Activity) context, context.getResources().getString(R.string.no_activity));
        }
        return true;
    }

    public static boolean extract(Context context, final PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        Boolean status = FileOperations.cpExternalPartition(packageInfo.applicationInfo.sourceDir, App.getAppPreferences().getCustomPath() + "/" + OtherUtils.getAPKFilename(packageInfo));
        if (!OtherUtils.checkPermissions(activity) || !status) {
            DialogUtils.dialogMessage(context, context.getResources().getString(R.string.dialog_error), context.getResources().getString(R.string.dialog_error_description));
            return false;
        }
        DialogUtils.toastAction(activity, String.format(context.getResources().getString(R.string.extract_success), packageInfo.packageName, OtherUtils.getAPKFilename(packageInfo)), context.getResources().getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new File(App.getAppPreferences().getCustomPath() + OtherUtils.getAPKFilename(packageInfo)).delete();
            }
        });
        return true;
    }

    public static boolean uninstall(Context context, PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        Boolean status = SystemUtils.rmSystemPartition(packageInfo.applicationInfo.sourceDir);
        if (!OtherUtils.checkPermissions(activity) || !SystemUtils.isRoot() || !status) {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.dialog_error_description));
            return false;
        }
        DialogUtils.toastAction(activity, context.getResources().getString(R.string.reboot_query), context.getResources().getString(R.string.reboot), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.rebootSystem();
            }
        });
        return true;
    }

    public static boolean share(Context context, PackageInfo packageInfo) {
        FileOperations.cpExternalPartition(packageInfo.applicationInfo.sourceDir, App.getAppPreferences().getCustomPath() + "/" + App.getAppPreferences().getFilename());
        Intent shareIntent = OtherUtils.getShareIntent(new File(context.getFilesDir() + OtherUtils.getAPKFilename(packageInfo)));
        context.startActivity(Intent.createChooser(shareIntent, String.format(context.getResources().getString(R.string.send), packageInfo.packageName)));
        return true;
    }

    public static boolean settings(Context context, PackageInfo packageInfo) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageInfo.packageName));
        context.startActivity(intent);
        return true;
    }

    public static boolean hide(Context context, PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        boolean status = SystemUtils.hide(packageInfo);
        if (!OtherUtils.checkPermissions(activity) || !SystemUtils.isRoot() || !status) {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.dialog_error_description));
            return false;
        }
        DialogUtils.toastAction(activity, context.getResources().getString(R.string.reboot_query), context.getResources().getString(R.string.reboot), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.rebootSystem();
            }
        });
        return true;
    }

    public static boolean disable(Context context, PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        Boolean status = SystemUtils.disable(packageInfo);
        if (!OtherUtils.checkPermissions(activity) || !SystemUtils.isRoot() || !status) {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.dialog_error_description));
            return false;
        }
        DialogUtils.toastAction(activity, context.getResources().getString(R.string.reboot_query), context.getResources().getString(R.string.reboot), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.rebootSystem();
            }
        });
        return true;
    }

    public static boolean favorite(Context context, PackageInfo packageInfo) {
        return true;
    }
}
