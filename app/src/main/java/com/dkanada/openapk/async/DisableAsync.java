package com.dkanada.openapk.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.utils.AppDbUtils;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppUtils;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.InterfaceUtils;
import com.dkanada.openapk.utils.RootUtils;

public class DisableAsync extends AsyncTask<Void, String, Boolean> {
  private Context context;
  private Activity activity;
  private MaterialDialog dialog;
  private AppInfo appInfo;

  public DisableAsync(Context context, MaterialDialog dialog, AppInfo appInfo) {
    this.context = context;
    this.activity = (Activity) context;
    this.dialog = dialog;
    this.appInfo = appInfo;
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    Boolean status = false;
    if (AppUtils.checkPermissions(activity) && RootUtils.isRooted()) {
      AppDbUtils appDbUtils = new AppDbUtils(context);
      if (!appDbUtils.checkAppInfo(appInfo, 4)) {
        status = RootUtils.disableWithRootPermission(appInfo.getAPK(), appDbUtils.checkAppInfo(appInfo, 4));
        appInfo.setDisabled(true);
        appDbUtils.updateAppInfo(appInfo, 4);
      } else {
        status = RootUtils.disableWithRootPermission(appInfo.getAPK(), appDbUtils.checkAppInfo(appInfo, 4));
        appInfo.setDisabled(false);
        appDbUtils.updateAppInfo(appInfo, 4);
      }
    }
    return status;
  }

  @Override
  protected void onPostExecute(Boolean status) {
    super.onPostExecute(status);
    dialog.dismiss();
    if (status && RootUtils.isRooted()) {
      DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_reboot), context.getResources().getString(R.string.button_reboot), null, 3).show();
    } else if (!RootUtils.isRooted()) {
      DialogUtils.showTitleContent(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
    } else {
      DialogUtils.showSnackBar((Activity) context, context.getResources().getString(R.string.error_layout), null, null, 2);
    }
  }
}