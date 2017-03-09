package com.dkanada.openapk.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppUtils;
import com.dkanada.openapk.utils.DialogUtils;

public class ExtractFileAsync extends AsyncTask<Void, String, Boolean> {
  private Context context;
  private Activity activity;
  private MaterialDialog dialog;
  private AppInfo appInfo;

  public ExtractFileAsync(Context context, MaterialDialog dialog, AppInfo appInfo) {
    this.context = context;
    this.activity = (Activity) context;
    this.dialog = dialog;
    this.appInfo = appInfo;
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    Boolean status = false;
    if (AppUtils.checkPermissions(activity)) {
      status = AppUtils.extractFile(appInfo);
    }
    return status;
  }

  @Override
  protected void onPostExecute(Boolean status) {
    super.onPostExecute(status);
    dialog.dismiss();
    if (status) {
      DialogUtils.showSnackBar(activity, String.format(context.getResources().getString(R.string.dialog_saved_description), appInfo.getName(), AppUtils.getAPKFilename(appInfo)), context.getResources().getString(R.string.button_undo), AppUtils.getOutputFilename(appInfo), 1).show();
    } else {
      DialogUtils.showTitleContent(context, context.getResources().getString(R.string.dialog_extract_fail), context.getResources().getString(R.string.dialog_extract_fail_description));
    }
  }
}