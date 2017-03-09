package com.dkanada.openapk.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.AppDatabase;
import com.dkanada.openapk.AppInfo;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.UtilsApp;
import com.dkanada.openapk.utils.UtilsDialog;
import com.dkanada.openapk.utils.UtilsRoot;

public class DisableInBackground extends AsyncTask<Void, String, Boolean> {
  private Context context;
  private Activity activity;
  private MaterialDialog dialog;
  private AppInfo appInfo;

  public DisableInBackground(Context context, MaterialDialog dialog, AppInfo appInfo) {
    this.context = context;
    this.activity = (Activity) context;
    this.dialog = dialog;
    this.appInfo = appInfo;
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    Boolean status = false;
    if (UtilsApp.checkPermissions(activity)) {
      AppDatabase appDatabase = new AppDatabase(context);
      if (!appDatabase.checkAppInfo(appInfo, 4)) {
        status = UtilsRoot.disableWithRootPermission(appInfo.getAPK(), appDatabase.checkAppInfo(appInfo, 4));
        appInfo.setDisabled(true);
        appDatabase.updateAppInfo(appInfo, 4);
      } else {
        status = UtilsRoot.disableWithRootPermission(appInfo.getAPK(), appDatabase.checkAppInfo(appInfo, 4));
        appInfo.setDisabled(false);
        appDatabase.updateAppInfo(appInfo, 4);
        UtilsDialog.showSnackBar(activity, context.getResources().getString(R.string.dialog_reboot), context.getResources().getString(R.string.button_reboot), null, 3).show();
      }
    }
    return status;
  }

  @Override
  protected void onPostExecute(Boolean status) {
    super.onPostExecute(status);
    dialog.dismiss();
    if (status) {
      UtilsDialog.showSnackBar(activity, "temp", null, null, 2).show();
    } else {
      UtilsDialog.showTitleContent(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
    }
  }
}