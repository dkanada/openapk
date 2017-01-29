package com.dkanada.openapk.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.AppInfo;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.UtilsApp;
import com.dkanada.openapk.utils.UtilsDialog;

public class ExtractFileInBackground extends AsyncTask<Void, String, Boolean> {
  private Context context;
  private Activity activity;
  private MaterialDialog dialog;
  private AppInfo appInfo;

  public ExtractFileInBackground(Context context, MaterialDialog dialog, AppInfo appInfo) {
    this.activity = (Activity) context;
    this.context = context;
    this.dialog = dialog;
    this.appInfo = appInfo;
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    Boolean status = false;
    if (UtilsApp.checkPermissions(activity)) {
      status = UtilsApp.copyFile(appInfo);
    }
    return status;
  }

  @Override
  protected void onPostExecute(Boolean status) {
    super.onPostExecute(status);
    dialog.dismiss();
    if (status) {
      UtilsDialog.showSnackbar(activity, String.format(context.getResources().getString(R.string.dialog_saved_description), appInfo.getName(), UtilsApp.getAPKFilename(appInfo)), context.getResources().getString(R.string.button_undo), UtilsApp.getOutputFilename(appInfo), 1).show();
    } else {
      UtilsDialog.showTitleContent(context, context.getResources().getString(R.string.dialog_extract_fail), context.getResources().getString(R.string.dialog_extract_fail_description));
    }
  }
}