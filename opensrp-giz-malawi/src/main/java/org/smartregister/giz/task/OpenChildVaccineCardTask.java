package org.smartregister.giz.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.util.ChildDbUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.giz.activity.ChildImmunizationActivity;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class OpenChildVaccineCardTask extends AsyncTask<Void, Void, HashMap<String, String>> {

    private String baseEntityId;
    private ProgressDialog progressDialog;
    private WeakReference<Activity> activity;

    public OpenChildVaccineCardTask(String baseEntityId, Activity activity) {
        this.baseEntityId = baseEntityId;
        this.activity = new WeakReference<>(activity);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getApplicationContext().getString(R.string.loading));
        progressDialog.setMessage("Opening Child Vaccine Card");
        progressDialog.show();
    }

    @Override
    protected HashMap<String, String> doInBackground(Void... voids) {
        return ChildDbUtils.fetchChildDetails(baseEntityId);
    }

    @Override
    protected void onPostExecute(HashMap<String, String> map) {
        progressDialog.dismiss();
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(baseEntityId, map, "child");
        ChildImmunizationActivity.launchActivity(activity.get(), commonPersonObjectClient, new RegisterClickables());
    }
}