package org.smartregister.giz.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.giz.job.GIZClientReprocessJob;

import timber.log.Timber;

import static org.smartregister.receiver.SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS;

public class GizReprocessSyncStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            boolean isComplete = data.getBoolean(EXTRA_COMPLETE_STATUS, false);
            if (isComplete) {
                GIZClientReprocessJob.scheduleJobImmediately(GIZClientReprocessJob.TAG);
                Timber.d("fetch completed");
            }
        }

        Timber.d("broadcast Recieved");
    }
}
