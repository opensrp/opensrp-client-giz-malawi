package org.smartregister.giz.application;

import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.giz.BuildConfig;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.anc.library.util.ConstantsUtils.PrefKeyUtils.POPULATION_CHARACTERISTICS;

public class GizMalawiSyncConfiguration extends SyncConfiguration {
    @Override
    public int getSyncMaxRetries() {
        return BuildConfig.MAX_SYNC_RETRIES;
    }

    @Override
    public SyncFilter getSyncFilterParam() {
        return SyncFilter.LOCATION;
    }

    @Override
    public String getSyncFilterValue() {
        AllSharedPreferences sharedPreferences = GizMalawiApplication.getInstance().context().userService()
                .getAllSharedPreferences();
        return sharedPreferences.fetchDefaultLocalityId(sharedPreferences.fetchRegisteredANM());
    }

    @Override
    public int getUniqueIdSource() {
        return BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;
    }

    @Override
    public int getUniqueIdBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    }

    @Override
    public int getUniqueIdInitialBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    }

    @Override
    public boolean isSyncSettings() {
        return BuildConfig.IS_SYNC_SETTINGS;
    }

    @Override
    public SyncFilter getEncryptionParam() {
        return SyncFilter.TEAM;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return false;
    }

    @Override
    public List<String> getSynchronizedLocationTags() {
        return new ArrayList<>();
    }

    @Override
    public String getTopAllowedLocationLevel() {
        return null;
    }

    @Override
    public String getSettingsSyncFilterValue() {
        AllSharedPreferences sharedPreferences = AncLibrary.getInstance().getContext().userService().getAllSharedPreferences();
        String providerId = sharedPreferences.fetchRegisteredANM();
        return sharedPreferences.fetchDefaultLocalityId(providerId);
    }

    @Override
    public SyncFilter getSettingsSyncFilterParam() {
        return SyncFilter.LOCATION_ID;
    }


    @Override
    public boolean resolveSettings() {
        return BuildConfig.RESOLVE_SETTINGS;
    }

    @Override
    public boolean hasExtraSettingsSync() {
        return BuildConfig.HAS_EXTRA_SETTINGS_SYNC_FILTER;
    }

    @Override
    public List<String> getExtraSettingsParameters() {
        AllSharedPreferences sharedPreferences = AncLibrary.getInstance().getContext().userService().getAllSharedPreferences();
        String providerId = sharedPreferences.fetchRegisteredANM();
        List<String> params = new ArrayList<>();
        params.add("locations_id" + "=" + sharedPreferences.fetchDefaultLocalityId(providerId));
        params.add("serverVersion=0");

        params.add("identifier" + "=" + POPULATION_CHARACTERISTICS);
        return params;
    }
}

