package com.dkanada.openapk.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dkanada.openapk.App;
import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.widgets.SnackBar;
import com.dkanada.openapk.R;
import com.mikepenz.materialize.color.Material;

import java.io.File;

public class DialogUtils {
    public static MaterialDialog dialogMessage(Context context, String title, String content) {
        return dialogFixBackground(context, new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .cancelable(true)
                .positiveText(context.getResources().getString(android.R.string.ok)));
    }

    public static MaterialDialog dialogProgress(Context context, String title, String content) {
        return dialogFixBackground(context, new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .cancelable(false)
                .progress(true, 0));
    }

    public static void dialogChooseDirectory(Context context) {
        dialogFixBackground(context, new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.settings_custom_path))
                .positiveText(context.getResources().getString(android.R.string.ok))
                .negativeText(context.getResources().getString(android.R.string.cancel))
                .input(null, App.getAppPreferences().getCustomPath(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        App.getAppPreferences().setCustomPath(input.toString());
                    }
                })
                .cancelable(true));
    }

    public static void dialogSystemAction(Context context) {
        dialogFixBackground(context, new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.dialog_system_action))
                .content(context.getResources().getString(R.string.dialog_system_action_description))
                .positiveText(context.getResources().getString(android.R.string.ok))
                .negativeText(context.getResources().getString(android.R.string.cancel))
                .cancelable(false));
    }

    public static void toastMessage(Activity activity, String text) {
        new SnackBar(activity, text, null, null).show();
    }

    public static void toastAction(Activity activity, String text, String buttonText, View.OnClickListener onClickListener) {
        new SnackBar(activity, text, buttonText, onClickListener).show();
    }

    public static MaterialDialog dialogFixBackground(Context context, MaterialDialog.Builder dialog) {
        if (App.getAppPreferences().getTheme().equals("0")) {
            dialog.backgroundColor(context.getResources().getColor(R.color.grey_dark));
        }
        return dialog.show();
    }
}
