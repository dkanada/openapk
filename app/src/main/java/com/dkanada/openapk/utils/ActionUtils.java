package com.dkanada.openapk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.models.AppInfo;

import java.io.File;

public class ActionUtils {
    public static boolean open(Context context, PackageInfo packageInfo) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            DialogUtils.showSnackBar((Activity) context, context.getResources().getString(R.string.dialog_no_activity), null, null, 0);
        }
        return true;
    }

    public static boolean extract(Context context, PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        Boolean status = AppUtils.extractFile(packageInfo, Environment.getExternalStorageDirectory() + "/OpenAPK/");
        if (!AppUtils.checkPermissions(activity) || !status) {
            DialogUtils.showTitleContent(context, context.getResources().getString(R.string.dialog_extract_fail), context.getResources().getString(R.string.dialog_extract_fail_description));
            return false;
        }
        DialogUtils.showSnackBar(activity, String.format(context.getResources().getString(R.string.dialog_extract_success_description), packageInfo.packageName, AppUtils.getAPKFilename(packageInfo)), context.getResources().getString(R.string.button_undo), new File(App.getAppPreferences().getCustomPath() + AppUtils.getAPKFilename(packageInfo)), 1).show();
        return true;
    }

    public static boolean uninstall(Context context, PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        Boolean status = RootUtils.uninstallRoot(packageInfo.applicationInfo.sourceDir);
        if (!AppUtils.checkPermissions(activity) || !RootUtils.isRoot() || !status) {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_error), null, null, 0);
            return false;
        }
        DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_reboot), context.getResources().getString(R.string.button_reboot), null, 2).show();
        return true;
    }

    public static boolean share(Context context, PackageInfo packageInfo) {
        AppUtils.extractFile(packageInfo, context.getFilesDir().toString());
        Intent shareIntent = AppUtils.getShareIntent(new File(context.getFilesDir() + AppUtils.getAPKFilename(packageInfo)));
        context.startActivity(Intent.createChooser(shareIntent, String.format(context.getResources().getString(R.string.send_to), packageInfo.packageName)));
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
        boolean status = RootUtils.disable(packageInfo.packageName, packageInfo.applicationInfo.enabled);
        if (!AppUtils.checkPermissions(activity) || !RootUtils.isRoot() || !status) {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_error), null, null, 0);
            return false;
        }
        DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_reboot), context.getResources().getString(R.string.button_reboot), null, 2).show();
        return true;
    }

    public static boolean disable(Context context, PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        Boolean status = RootUtils.disable(packageInfo.packageName, !packageInfo.applicationInfo.enabled);
        if (!AppUtils.checkPermissions(activity) || !RootUtils.isRoot() || !status) {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_error), null, null, 0);
            return false;
        }
        DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_reboot), context.getResources().getString(R.string.button_reboot), null, 2).show();
        return true;
    }

    public static boolean favorite(Context context, PackageInfo packageInfo) {
        return true;
    }
}
