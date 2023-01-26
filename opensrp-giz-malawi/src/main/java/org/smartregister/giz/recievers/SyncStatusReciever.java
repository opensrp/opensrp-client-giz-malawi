package org.smartregister.giz.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.domain.FetchStatus;
import org.smartregister.giz.job.GizReProcessJob;

import java.io.Serializable;

import timber.log.Timber;

import static org.smartregister.receiver.SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS;

public class SyncStatusReciever  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            Serializable fetchStatusSerializable = data.getSerializable(EXTRA_FETCH_STATUS);
            if (fetchStatusSerializable instanceof FetchStatus) {
                FetchStatus fetchStatus = (FetchStatus) fetchStatusSerializable;
                    if (fetchStatus.equals(FetchStatus.fetched)) {
                        GizReProcessJob.scheduleJobImmediately(GizReProcessJob.TAG);
                        Timber.d("Broadcast data fetched");
                    } else if (fetchStatus.equals(FetchStatus.nothingFetched)) {
                        Timber.d("Broadcast data not fetched");
                    } else if (fetchStatus.equals(FetchStatus.noConnection)) {
                        Timber.d("Broadcast data fetch failed");
                    }

                //}
            }
        }

        System.out.println("Broadcast Recieved");
    }
}
