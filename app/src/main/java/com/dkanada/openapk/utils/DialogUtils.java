package com.dkanada.openapk.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.App;
import com.gc.materialdesign.widgets.SnackBar;
import com.dkanada.openapk.R;

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

    public static void dialogSystemAction(Context context) {
        dialogFixBackground(context, new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.dialog_system_action))
                .content(context.getResources().getString(R.string.dialog_system_action_description))
                .positiveText(context.getResources().getString(android.R.string.ok))
                .negativeText(context.getResources().getString(android.R.string.cancel))
                .cancelable(false));
    }

    public static void toastMessage(Context context, String text) {
        new SnackBar((Activity) context, text, null, null).show();
    }

    public static void toastAction(Context context, String text, String buttonText, View.OnClickListener onClickListener) {
        new SnackBar((Activity) context, text, buttonText, onClickListener).show();
    }

    public static MaterialDialog dialogFixBackground(Context context, MaterialDialog.Builder dialog) {
        if (App.getAppPreferences().getTheme().equals("1")) {
            dialog.backgroundColor(context.getResources().getColor(R.color.grey_three));
        }
        return dialog.show();
    }
}
