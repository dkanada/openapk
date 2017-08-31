package com.dkanada.openapk.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.OtherUtils;
import com.dkanada.openapk.utils.SystemUtils;

public class DeleteFileAsync extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private Activity activity;
    private MaterialDialog dialog;
    private String file;

    public DeleteFileAsync(Context context, MaterialDialog dialog, String file) {
        this.context = context;
        this.activity = (Activity) context;
        this.dialog = dialog;
        this.file = file;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean status = false;
        if (OtherUtils.checkPermissions(activity) && SystemUtils.isRoot()) {
            status = SystemUtils.rmDataPartition(file);
        }
        return status;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        dialog.dismiss();
        if (!SystemUtils.isRoot()) {
            DialogUtils.dialogMessage(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
        } else if (!status) {
            DialogUtils.toastMessage(activity, context.getResources().getString(R.string.error_generic));
        }
    }
}
