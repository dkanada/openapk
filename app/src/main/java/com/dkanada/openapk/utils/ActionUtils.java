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
import java.util.List;

public class ActionUtils {
    public static void open(Context context, PackageInfo packageInfo) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.error_open));
        }
    }

    public static void extract(Context context, final PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        Boolean status = FileOperations.cpExternalPartition(packageInfo.applicationInfo.sourceDir, App.getAppPreferences().getCustomPath() + "/" + OtherUtils.getAPKFilename(packageInfo));
        if (!OtherUtils.checkPermissions(activity) || !status) {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.error_generic));
        }
        DialogUtils.toastAction(activity, String.format(context.getResources().getString(R.string.success_extract), packageInfo.packageName, OtherUtils.getAPKFilename(packageInfo)), context.getResources().getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new File(App.getAppPreferences().getCustomPath() + OtherUtils.getAPKFilename(packageInfo)).delete();
            }
        });
    }

    public static void uninstall(Context context, PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        Boolean status = ShellCommands.rmSystemPartition(packageInfo.applicationInfo.sourceDir);
        if (!OtherUtils.checkPermissions(activity) || !ShellCommands.isRoot() || !status) {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.error_generic));
        }
        DialogUtils.toastAction(activity, context.getResources().getString(R.string.reboot_query), context.getResources().getString(R.string.reboot), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShellCommands.rebootSystem();
            }
        });
    }

    public static void share(Context context, PackageInfo packageInfo) {
        FileOperations.cpExternalPartition(packageInfo.applicationInfo.sourceDir, App.getAppPreferences().getCustomPath() + "/" + OtherUtils.getAPKFilename(packageInfo));
        Intent shareIntent = OtherUtils.getShareIntent(new File(App.getAppPreferences().getCustomPath() + "/" + OtherUtils.getAPKFilename(packageInfo)));
        context.startActivity(Intent.createChooser(shareIntent, String.format(context.getResources().getString(R.string.send), packageInfo.packageName)));
    }

    public static void settings(Context context, PackageInfo packageInfo) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageInfo.packageName));
        context.startActivity(intent);
    }

    public static void disable(Context context, PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        Boolean status;
        if (packageInfo.applicationInfo.enabled) {
            status = ShellCommands.disable(packageInfo);
        } else {
            status = ShellCommands.enable(packageInfo);
        }
        if (!OtherUtils.checkPermissions(activity) || !ShellCommands.isRoot() || !status) {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.error_generic));
        }
        DialogUtils.toastAction(activity, context.getResources().getString(R.string.reboot_query), context.getResources().getString(R.string.reboot), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShellCommands.rebootSystem();
            }
        });
    }

    public static void hide(Context context, PackageInfo packageInfo) {
        Activity activity = (Activity) context;
        boolean status;
        if (ShellCommands.checkHidden(context, packageInfo.packageName) != null) {
            status = ShellCommands.unhide(packageInfo);
        } else {
            status = ShellCommands.hide(packageInfo);
        }
        if (!OtherUtils.checkPermissions(activity) || !ShellCommands.isRoot() || !status) {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.error_generic));
        }
        DialogUtils.toastAction(activity, context.getResources().getString(R.string.reboot_query), context.getResources().getString(R.string.reboot), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShellCommands.rebootSystem();
            }
        });
    }

    public static void favorite(PackageInfo packageInfo) {
        if (App.getAppPreferences().getFavoriteList().contains(packageInfo.packageName)) {
            List<String> list = App.getAppPreferences().getFavoriteList();
            list.remove(packageInfo.packageName);
            App.getAppPreferences().setFavoriteList(list);
        } else {
            List<String> list = App.getAppPreferences().getFavoriteList();
            list.add(packageInfo.packageName);
            App.getAppPreferences().setFavoriteList(list);
        }
    }
}
