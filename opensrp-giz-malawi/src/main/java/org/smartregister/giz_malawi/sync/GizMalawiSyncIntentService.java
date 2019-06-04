package org.smartregister.giz_malawi.sync;

import org.smartregister.giz_malawi.application.GizMalawiApplication;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.intent.SyncIntentService;

public class GizMalawiSyncIntentService extends SyncIntentService {

    @Override
    protected ClientProcessorForJava getClientProcessor() {
        return GizMalawiApplication.getInstance().getClientProcessor();
    }

}
