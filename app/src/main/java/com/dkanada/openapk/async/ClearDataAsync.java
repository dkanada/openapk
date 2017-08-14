package com.dkanada.openapk.async;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.OtherUtils;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.SystemUtils;

public class ClearDataAsync extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private Activity activity;
    private MaterialDialog dialog;
    private PackageInfo packageInfo;

    public ClearDataAsync(Context context, MaterialDialog dialog, PackageInfo packageInfo) {
        this.context = context;
        this.activity = (Activity) context;
        this.dialog = dialog;
        this.packageInfo = packageInfo;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean status = false;
        if (OtherUtils.checkPermissions(activity) && SystemUtils.isRoot()) {
            status = SystemUtils.rmDataPartition(packageInfo.applicationInfo.dataDir);
        }
        return status;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        dialog.dismiss();
        if (status && SystemUtils.isRoot()) {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.success_clear_data, packageInfo.packageName));
        } else if (!SystemUtils.isRoot()) {
            DialogUtils.dialogMessage(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
        } else {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.error_generic));
        }
    }
}