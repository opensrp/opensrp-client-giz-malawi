package org.smartregister.giz.repository;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Event;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

public class GizEventRepository extends BaseRepository {

    public boolean hasEvent(@NonNull String baseEntityId, @NonNull String eventType) {
        boolean hasEvent = false;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("event", new String[]{"baseEntityId"},
                "baseEntityId = ? and eventType = ? ", new String[]{baseEntityId, eventType},
                null, null, null, "1");
        if (cursor != null) {
            hasEvent = cursor.getCount() > 0;
            cursor.close();
        }
        return hasEvent;
    }

    public HashSet<String> processSkippedClients() {
        String missingClientsFromClientRegisterQuery = "SELECT DISTINCT baseEntityId, formSubmissionId FROM event WHERE baseEntityId IN (SELECT baseEntityId FROM client WHERE baseEntityId NOT IN (SELECT DISTINCT base_entity_id FROM client_register_type)) AND eventType LIKE '%Registration%'";
        String missingClientsFromECClientQuery = "SELECT DISTINCT baseEntityId, formSubmissionId FROM event WHERE baseEntityId IN (SELECT baseEntityId FROM client WHERE baseEntityId NOT IN (SELECT DISTINCT base_entity_id FROM ec_client)) AND eventType LIKE '%Registration%'";
        HashSet<String> formSubmissionIDs = new HashSet<>();
        HashSet<String> baseEntityIds = new HashSet<>();
        addFormSubmissionIds(formSubmissionIDs, baseEntityIds, missingClientsFromClientRegisterQuery);
        addFormSubmissionIds(formSubmissionIDs, baseEntityIds, missingClientsFromECClientQuery);

        try {
            Timber.e("Found %d skipped clients", formSubmissionIDs.size());
            if (formSubmissionIDs.size() > 0) {
                GizUtils.initiateEventProcessing(new ArrayList<>(formSubmissionIDs));
            }

            Timber.d("Finished reprocessing events and clients");
        } catch (Exception e) {
            Timber.e(e);
        }

        return baseEntityIds;
    }

    protected void addFormSubmissionIds(HashSet<String> formSubmissionIds, HashSet<String> baseEntityIds, String query) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        int formSubmissionIdColIndex = cursor.getColumnIndex("formSubmissionId");
        int baseEntityIdColIndex = cursor.getColumnIndex("baseEntityId");

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                formSubmissionIds.add(cursor.getString(formSubmissionIdColIndex));
                baseEntityIds.add(cursor.getString(baseEntityIdColIndex));
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    public List<Event> getEventsByBaseEntityId(String baseEntityId) {
        ArrayList<Event> eventsList = new ArrayList<>();
        EventClientRepository eventClientRepository = GizMalawiApplication.getInstance().eventClientRepository();

        if (StringUtils.isBlank(baseEntityId)) {
            return eventsList;
        } else {
            Cursor cursor = null;

            try {
                cursor = this.getReadableDatabase().rawQuery("SELECT json FROM event WHERE "
                        + EventClientRepository.event_column.baseEntityId.name()
                        + " = ? ORDER BY eventDate ASC", new String[]{baseEntityId});

                while (cursor.moveToNext()) {
                    String jsonEventStr = cursor.getString(0);
                    jsonEventStr = jsonEventStr.replaceAll("'", "");
                    Event event = eventClientRepository.convert(jsonEventStr, Event.class);

                    eventsList.add(event);
                }

            } catch (Exception var10) {
                Timber.e(var10);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

            }

            return eventsList;
        }
    }
}
