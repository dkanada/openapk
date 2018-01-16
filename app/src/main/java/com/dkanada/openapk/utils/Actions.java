package com.dkanada.openapk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.models.AppItem;

import java.io.File;
import java.util.List;

public class Actions {
    public static void open(Context context, AppItem appItem) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(appItem.getPackageName());
        if (intent != null) {
            context.startActivity(intent);
        } else {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.error_open));
        }
    }

    public static void extract(Context context, final AppItem appItem) {
        boolean status = FileOperations.cpExternalPartition(appItem.getSource(), App.getAppPreferences().getCustomPath() + "/" + OtherUtils.getAPKFilename(appItem));
        if (!status && !OtherUtils.checkPermissions(context)) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.dialog_permissions));
        } else if (!status) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.error_generic));
        } else {
            DialogUtils.toastAction(context, String.format(context.getResources().getString(R.string.success_extract), appItem.getPackageName(), OtherUtils.getAPKFilename(appItem)), context.getResources().getString(R.string.undo), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new File(App.getAppPreferences().getCustomPath() + OtherUtils.getAPKFilename(appItem)).delete();
                }
            });
        }
    }

    public static void uninstall(Context context, AppItem appItem) {
        boolean status = ShellCommands.rmSystemPartition(appItem.getSource());
        if (!status && !ShellCommands.isRoot()) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.dialog_root_required_description));
        } else if (!status) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.error_generic));
        } else {
            DialogUtils.toastAction(context, context.getResources().getString(R.string.reboot_query), context.getResources().getString(R.string.reboot), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShellCommands.rebootSystem();
                }
            });
        }
    }

    public static void share(Context context, AppItem appItem) {
        boolean status = FileOperations.cpExternalPartition(appItem.getSource(), App.getAppPreferences().getCustomPath() + "/" + OtherUtils.getAPKFilename(appItem));
        if (!status && !OtherUtils.checkPermissions(context)) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.dialog_permissions_description));
        } else if (!status) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.error_generic));
        } else {
            Intent shareIntent = OtherUtils.getShareIntent(new File(App.getAppPreferences().getCustomPath() + "/" + OtherUtils.getAPKFilename(appItem)));
            context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.send)));
        }
    }

    public static void settings(Context context, AppItem appItem) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + appItem.getPackageName()));
        context.startActivity(intent);
    }

    public static void disable(Context context, AppItem appItem) {
        boolean status;
        if (!appItem.disable) {
            status = ShellCommands.disable(appItem);
        } else {
            status = ShellCommands.enable(appItem);
        }
        if (!status && !ShellCommands.isRoot()) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.dialog_root_required_description));
        } else if (!status) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.error_generic));
        } else {
            DialogUtils.toastAction(context, context.getResources().getString(R.string.reboot_query), context.getResources().getString(R.string.reboot), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShellCommands.rebootSystem();
                }
            });
        }
    }

    public static void hide(Context context, AppItem appItem) {
        boolean status;
        PackageList packageList = new PackageList(context);
        List<AppItem> appList = packageList.getHiddenList();
        if (!appItem.hide) {
            status = ShellCommands.hide(appItem);
            appList.add(appItem);
        } else {
            status = ShellCommands.unhide(appItem);
            appList.remove(appItem);
        }
        if (!status && !ShellCommands.isRoot()) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.dialog_root_required_description));
        } else if (!status) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.error_generic));
        } else {
            packageList.setHiddenList(appList);
            DialogUtils.toastAction(context, context.getResources().getString(R.string.reboot_query), context.getResources().getString(R.string.reboot), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShellCommands.rebootSystem();
                }
            });
        }
    }

    public static void favorite(PackageInfo packageInfo) {
        /*if (App.getAppPreferences().getFavoriteList().contains(packageInfo.packageName)) {
            List<String> list = App.getAppPreferences().getFavoriteList();
            list.remove(packageInfo.packageName);
            App.getAppPreferences().setFavoriteList(list);
        } else {
            List<String> list = App.getAppPreferences().getFavoriteList();
            list.add(packageInfo.packageName);
            App.getAppPreferences().setFavoriteList(list);
        }*/
    }
}
