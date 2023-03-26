package org.smartregister.giz.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.repository.GizEventRepository;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

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
        GizEventRepository gizEventRepository = GizMalawiApplication.getInstance().gizEventRepository();
        HashSet<String> clientBaseEntityIds = gizEventRepository.processSkippedClients();

        processSkippedClientEvents(gizEventRepository, clientBaseEntityIds);

        AllSharedPreferences allSharedPreferences = Context.getInstance().allSharedPreferences();
        boolean isProcessed = allSharedPreferences.getBooleanPreference(GizConstants.Pref.DEATH_AND_OPD_CLOSE_REPROCESSED);
        if (!isProcessed) {
            reprocessEventsOfType("OPD Close");
            reprocessEventsOfType("Death");

            allSharedPreferences.saveBooleanPreference(GizConstants.Pref.DEATH_AND_OPD_CLOSE_REPROCESSED, true);
        }
    }

    private void processSkippedClientEvents(GizEventRepository gizEventRepository, HashSet<String> clientBaseEntityIds) {
        Timber.d("Processing events for %d skipped clients", clientBaseEntityIds.size());
        EventClientRepository eventClientRepository = GizMalawiApplication.getInstance().eventClientRepository();

        for (String clientBaseEntityId: clientBaseEntityIds) {
            Client client = eventClientRepository.fetchClientByBaseEntityId(clientBaseEntityId);
            if (client.getDeathdate() != null) {
                continue;
            }

            List<Event> eventsList = gizEventRepository.getEventsByBaseEntityId(clientBaseEntityId);

            List<EventClient> eventClientList = new ArrayList<>();
            Timber.d("Processing %d events for client %s", eventsList.size(), clientBaseEntityId);

            for (Event event: eventsList) {
                eventClientList.add(new EventClient(event, client));
            }

            try {
                GizMalawiApplication.getInstance().getClientProcessor()
                        .processClient(eventClientList);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    protected void reprocessEventsOfType(String eventType) {
        EventClientRepository eventClientRepository = GizMalawiApplication.getInstance().eventClientRepository();
        ArrayList<String> eventTypes = new ArrayList<>();
        eventTypes.add(eventType);
        List<EventClient> opdCloseEvents = eventClientRepository.fetchEventClientsByEventTypes(eventTypes);

        try {
            GizMalawiApplication.getInstance().getClientProcessor().processClient(opdCloseEvents);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
