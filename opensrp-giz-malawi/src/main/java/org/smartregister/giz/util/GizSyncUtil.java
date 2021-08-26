package org.smartregister.giz.util;

import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncSettingsServiceJob;

public class GizSyncUtil {

    public static void initiateProfileSync() {
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        SyncSettingsServiceJob.scheduleJobImmediately(SyncSettingsServiceJob.TAG);
    }
}
