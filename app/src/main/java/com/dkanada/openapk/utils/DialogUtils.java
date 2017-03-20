package com.dkanada.openapk.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.dkanada.openapk.App;
import com.dkanada.openapk.activities.SettingsActivity;
import com.dkanada.openapk.fragments.SettingsFragment;
import com.gc.materialdesign.widgets.SnackBar;
import com.dkanada.openapk.models.AppInfo;
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

  public static MaterialDialog.Builder uninstallSystemApp(Context context) {
    return new MaterialDialog.Builder(context)
        .title(context.getResources().getString(R.string.dialog_uninstall_root))
        .content(context.getResources().getString(R.string.dialog_uninstall_root_description))
        .positiveText(context.getResources().getString(R.string.action_uninstall))
        .negativeText(context.getResources().getString(android.R.string.cancel))
        .cancelable(false);
  }

  public static SnackBar showSnackBar(Activity activity, String text, @Nullable String buttonText, @Nullable final File file, Integer style) {
    SnackBar snackBar;
    switch (style) {
      case 1:
        snackBar = new SnackBar(activity, text, buttonText, new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            file.delete();
          }
        });
        break;
      case 2:
        snackBar = new SnackBar(activity, text, null, null);
        break;
      case 3:
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