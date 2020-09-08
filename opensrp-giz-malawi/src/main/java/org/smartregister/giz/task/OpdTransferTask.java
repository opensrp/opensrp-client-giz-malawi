package org.smartregister.giz.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.smartregister.giz.R;
import org.smartregister.giz.util.GizUtils;

import java.lang.ref.WeakReference;

public class OpdTransferTask extends AsyncTask<Void, Void, Void> {

    private String eventType;
    private String baseEntityId;
    private ProgressDialog progressDialog;
    private WeakReference<Activity> activityWeakReference;

    public OpdTransferTask(Activity activity, String eventType, String baseEntityId) {
        this.eventType = eventType;
        this.baseEntityId = baseEntityId;
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activityWeakReference.get());
        progressDialog.setMessage(activityWeakReference.get().getString(R.string.loading));
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        GizUtils.createOpdTransferEvent(eventType, baseEntityId);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        activityWeakReference.get().finish();
    }
}
