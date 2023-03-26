package org.smartregister.giz.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.evernote.android.job.Job;

import org.jetbrains.annotations.NotNull;
import org.smartregister.AllConstants;
import org.smartregister.giz.service.ReProcessSyncIntentService;
import org.smartregister.job.BaseJob;

public class GIZClientReprocessJob extends BaseJob {

    public static final String TAG = "GIZClientReprocessJob";

    @NonNull
    @NotNull
    @Override
    protected Result onRunJob(@NonNull @NotNull Job.Params params) {
        Intent intent = new Intent(getApplicationContext(), ReProcessSyncIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;

    }
}
