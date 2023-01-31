package org.smartregister.giz.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.smartregister.giz.application.GizMalawiApplication;

public class ReProcessSyncIntentService extends IntentService {

    public static final String TAG = "ReProcessSyncIntentService";

    public ReProcessSyncIntentService()
    {
        super(TAG);
    }

    public ReProcessSyncIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GizMalawiApplication.getInstance().gizEventRepository().processSkippedClients();
    }
}
