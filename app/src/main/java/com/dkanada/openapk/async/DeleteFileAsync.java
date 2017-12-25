package com.dkanada.openapk.async;

import android.content.Context;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dkanada.openapk.R;
import com.dkanada.openapk.utils.DialogUtils;
import com.dkanada.openapk.utils.OtherUtils;
import com.dkanada.openapk.utils.ShellCommands;

public class DeleteFileAsync extends AsyncTask<Void, String, Boolean> {
    private Context context;
    private MaterialDialog dialog;
    private String source;

    public DeleteFileAsync(Context context, MaterialDialog dialog, String source) {
        this.context = context;
        this.dialog = dialog;
        this.source = source;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean status = false;
        if (OtherUtils.checkPermissions(context) && ShellCommands.isRoot()) {
            status = ShellCommands.rmDataPartition(source);
        }
        return status;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        super.onPostExecute(status);
        dialog.dismiss();
        if (!ShellCommands.isRoot()) {
            DialogUtils.dialogMessage(context, context.getResources().getString(R.string.dialog_root_required), context.getResources().getString(R.string.dialog_root_required_description));
        } else if (!status) {
            DialogUtils.toastMessage(context, context.getResources().getString(R.string.error_generic));
        }
    }
}
