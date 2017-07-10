package com.dkanada.openapk.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.App;
import com.gc.materialdesign.widgets.SnackBar;
import com.dkanada.openapk.R;

import java.io.File;

public class DialogUtils {

    public static MaterialDialog showTitleContent(Context context, String title, String content) {
        MaterialDialog.Builder materialBuilder = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(context.getResources().getString(android.R.string.ok))
                .cancelable(true);
        return materialBuilder.show();
    }

    public static MaterialDialog showTitleContentWithProgress(Context context, String title, String content) {
        MaterialDialog.Builder materialBuilder = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .cancelable(false)
                .progress(true, 0);
        return materialBuilder.show();
    }

    public static MaterialDialog.Builder chooseDirectory(Context context) {
        return new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.settings_custom_path))
                .positiveText(context.getResources().getString(android.R.string.ok))
                .cancelable(true)
                .input("", App.getAppPreferences().getCustomPath(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        App.getAppPreferences().setCustomPath(input.toString());
                    }
                });
    }

    public static MaterialDialog.Builder systemAction(Context context) {
        return new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.dialog_system_action))
                .content(context.getResources().getString(R.string.dialog_system_action_description))
                .positiveText(context.getResources().getString(android.R.string.ok))
                .negativeText(context.getResources().getString(android.R.string.cancel))
                .cancelable(false);
    }

    public static SnackBar showSnackBar(Activity activity, String text, @Nullable String buttonText, @Nullable final File file, int style) {
        SnackBar snackBar;
        switch (style) {
            case 0:
                snackBar = new SnackBar(activity, text, null, null);
                break;
            case 1:
                snackBar = new SnackBar(activity, text, buttonText, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        file.delete();
                    }
                });
                break;
            case 2:
                snackBar = new SnackBar(activity, text, buttonText, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RootUtils.rebootSystem();
                    }
                });
                break;
            default:
                snackBar = new SnackBar(activity, text, null, null);
                break;
        }
        return snackBar;
    }
}
