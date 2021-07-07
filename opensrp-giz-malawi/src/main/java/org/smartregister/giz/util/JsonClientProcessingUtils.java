package org.smartregister.giz.util;

import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;

public class JsonClientProcessingUtils {

    public static FormTag formTag(AllSharedPreferences allSharedPreferences) {
        FormTag formTag = new FormTag();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = GizMalawiApplication.getInstance().getApplicationVersion();
        formTag.databaseVersion = GizMalawiApplication.getInstance().getDatabaseVersion();
        return formTag;
    }

    public static Event tagSyncMetadata(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(getCurrentLocationID(allSharedPreferences));
        event.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        event.setClientDatabaseVersion(GizMalawiApplication.getInstance().getDatabaseVersion());
        event.setClientApplicationVersion(GizMalawiApplication.getInstance().getApplicationVersion());
        return event;
    }

    public static String getCurrentLocationID(AllSharedPreferences allSharedPreferences) {
        return OpdJsonFormUtils.locationId(allSharedPreferences);
    }
}
