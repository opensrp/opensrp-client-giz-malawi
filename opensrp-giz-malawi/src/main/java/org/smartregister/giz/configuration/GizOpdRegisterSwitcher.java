package org.smartregister.giz.configuration;

import android.content.Context;
import android.support.annotation.NonNull;

import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.activity.ChildImmunizationActivity;
import org.smartregister.giz.util.AppExecutors;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.opd.configuration.OpdRegisterSwitcher;
import org.smartregister.opd.utils.OpdConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-20
 */

public class GizOpdRegisterSwitcher implements OpdRegisterSwitcher {

    @Override
    public void switchFromOpdRegister(@NonNull CommonPersonObjectClient client, @NonNull Context context) {
        String registerType = client.getColumnmaps().get(OpdConstants.ColumnMapKey.REGISTER_TYPE);
        if (registerType != null) {
            if (registerType.equalsIgnoreCase(GizConstants.RegisterType.ANC)) {
                // Fetch the the ANC user details
                openAncProfilePage(client, context);
            } else if (registerType.equalsIgnoreCase(GizConstants.RegisterType.CHILD)) {
                ChildImmunizationActivity.launchActivity(context, client, new RegisterClickables());
            }
        }
    }

    private void openAncProfilePage(@NonNull final CommonPersonObjectClient commonPersonObjectClient, @NonNull final Context context) {
        final AppExecutors appExecutors = new AppExecutors();
        appExecutors.diskIO()
                .execute(new Runnable() {

                    @Override
                    public void run() {
                        final Map<String, String> ancUserDetails = PatientRepository.getWomanProfileDetails(commonPersonObjectClient.getCaseId());
                        ancUserDetails.putAll(commonPersonObjectClient.getColumnmaps());
                        appExecutors.mainThread().execute(() -> Utils.navigateToProfile(context, (HashMap<String, String>) ancUserDetails));

                    }
                });
    }

    @Override
    public boolean showRegisterSwitcher(@NonNull CommonPersonObjectClient client) {
        String registerType = client.getColumnmaps().get(OpdConstants.ColumnMapKey.REGISTER_TYPE);
        return !(registerType == null || registerType.equalsIgnoreCase(OpdConstants.RegisterType.OPD));
    }
}
