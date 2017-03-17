package com.dkanada.openapk.async;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;
import com.dkanada.openapk.activities.MainActivity;
import com.dkanada.openapk.utils.AppUtils;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.RootUtils;

public class UninstallAsync extends AsyncTask<Void, String, Boolean> {
  private Context context;
  private Activity activity;
  private MaterialDialog dialog;
  private AppInfo appInfo;

  public UninstallAsync(Context context, MaterialDialog dialog, AppInfo appInfo) {
    this.context = context;
    this.activity = (Activity) context;
    this.dialog = dialog;
    this.appInfo = appInfo;
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    Boolean status = false;
    if (AppUtils.checkPermissions(activity) && RootUtils.isRooted()) {
      status = RootUtils.uninstallWithRootPermission(appInfo.getSource());
    }
    return status;
  }

  @Override
  protected void onPostExecute(Boolean status) {
    super.onPostExecute(status);
    dialog.dismiss();
    if (status && RootUtils.isRooted()) {
      MaterialDialog.Builder materialDialog = DialogUtils.showUninstalled(context, appInfo);
      materialDialog.callback(new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
          RootUtils.rebootSystem();
          dialog.dismiss();
        }
      });
      materialDialog.callback(new MaterialDialog.ButtonCallback() {
        @Override
        public void onNegative(MaterialDialog dialog) {
          dialog.dismiss();
          Intent intent = new Intent(context, MainActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          activity.finish();
          context.startActivity(intent);
        }
      });
      materialDialog.show();
    } else if (!RootUtils.isRooted()) {
      DialogUtils.showTitleContent(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
    } else {
      // TODO implement
      DialogUtils.showTitleContent(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
    }
  }
}