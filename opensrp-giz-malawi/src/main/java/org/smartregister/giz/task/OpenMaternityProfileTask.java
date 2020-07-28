package org.smartregister.giz.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.giz.activity.GizAncProfileActivity;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class OpenMaternityProfileTask extends AsyncTask<Void, Void, CommonPersonObjectClient> {

    private WeakReference<Activity> activityWeakReference;
    private String baseEntityId;
    private ProgressDialog progressDialog;

    public OpenMaternityProfileTask(Activity activity, String baseEntityId) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.baseEntityId = baseEntityId;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activityWeakReference.get());
        progressDialog.setMessage(activityWeakReference.get().getString(R.string.loading));
        progressDialog.show();
    }

    @Override
    protected CommonPersonObjectClient doInBackground(Void... voids) {
        HashMap<String, String> map = MaternityUtils.getMaternityClient(baseEntityId);
        if (map != null) {
            map.put(GizConstants.REGISTER_TYPE, GizConstants.RegisterType.MATERNITY);
            map.put("mmi_base_entity_id","dummy");
            CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(baseEntityId, map, "name");
            commonPersonObjectClient.setColumnmaps(map);
            return commonPersonObjectClient;
        }
        return null;
    }

    @Override
    protected void onPostExecute(CommonPersonObjectClient commonPersonObjectClient) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        MaternityMetadata maternityMetadata = MaternityLibrary.getInstance().getMaternityConfiguration().getMaternityMetadata();
        if (commonPersonObjectClient != null && maternityMetadata != null) {
            Activity activity = activityWeakReference.get();
            Intent intent = new Intent(activity, maternityMetadata.getProfileActivity());
            intent.putExtra(MaternityConstants.IntentKey.CLIENT_OBJECT, commonPersonObjectClient);
            activity.startActivity(intent);
            if (activity instanceof GizAncProfileActivity) {
                activity.finish();
            }
        }
    }
}
