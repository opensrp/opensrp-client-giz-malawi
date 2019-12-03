package org.smartregister.giz.configuration;

import android.content.Context;
import android.support.annotation.NonNull;

import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.activity.ChildImmunizationActivity;
import org.smartregister.giz.util.AppExecutors;
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
        String registerType = client.getColumnmaps().get("register_type");
        if (registerType != null) {
            if (registerType.equalsIgnoreCase("anc")) {
                // Fetch the next contact of the ANC user & then add it to the details
                openAncProfilePage(client, context);
            } else if (registerType.equalsIgnoreCase("child")) {
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
                        final Map<String, String> ancUserDetails = AncLibrary.getInstance()
                                .getDetailsRepository()
                                .getAllDetailsForClient(commonPersonObjectClient.getCaseId());
                        ancUserDetails.putAll(commonPersonObjectClient.getColumnmaps());

                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                Utils.navigateToProfile(context, (HashMap<String, String>) ancUserDetails);
                            }
                        });

                    }
                });
    }

    @Override
    public boolean showRegisterSwitcher(@NonNull CommonPersonObjectClient client) {
        String registerType = client.getColumnmaps().get(OpdConstants.ColumnMapKey.REGISTER_TYPE);
        return !(registerType == null || registerType.equalsIgnoreCase(OpdConstants.RegisterType.OPD));
    }
}
