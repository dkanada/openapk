package com.dkanada.openapk.async;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.OtherUtils;
import com.dkanada.openapk.utils.SystemUtils;

public class CopyFileAsync extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private MaterialDialog dialog;
    private String source;
    private String destination;

    public CopyFileAsync(Context context, MaterialDialog dialog, String source, String destination) {
        this.context = context;
        this.dialog = dialog;
        this.source = source;
        this.destination = destination;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean status = false;
        if (OtherUtils.checkPermissions(context) && SystemUtils.isRoot()) {
            status = SystemUtils.cpDataPartition(source, destination);
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
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.error_generic));
        }
    }
}
