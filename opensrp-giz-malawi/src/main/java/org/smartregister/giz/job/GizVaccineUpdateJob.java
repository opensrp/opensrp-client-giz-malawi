package org.smartregister.giz.job;

import android.support.annotation.NonNull;

import org.smartregister.immunization.job.VaccineSchedulesUpdateJob;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-02-13
 */

public class GizVaccineUpdateJob extends VaccineSchedulesUpdateJob {

    @NonNull
    @Override
    protected String getClientTableName() {
        return "ec_client";
    }
}
