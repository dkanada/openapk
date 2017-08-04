package com.dkanada.openapk.async;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.models.AppInfo;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.AppUtils;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.RootUtils;

public class RemoveCacheAsync extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private Activity activity;
    private MaterialDialog dialog;
    private PackageInfo packageInfo;

    public RemoveCacheAsync(Context context, MaterialDialog dialog, PackageInfo packageInfo) {
        this.context = context;
        this.activity = (Activity) context;
        this.dialog = dialog;
        this.packageInfo = packageInfo;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean status = false;
        if (AppUtils.checkPermissions(activity) && RootUtils.isRoot()) {
            status = RootUtils.removeCache(packageInfo.applicationInfo.dataDir + "/cache/**");
        }
        return status;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        dialog.dismiss();
        if (status && RootUtils.isRoot()) {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_cache_success_description, packageInfo.packageName), null, null, 0).show();
        } else if (!RootUtils.isRoot()) {
            DialogUtils.showTitleContent(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
        } else {
            DialogUtils.showSnackBar(activity, context.getResources().getString(R.string.dialog_error), null, null, 0);
        }
    }
}