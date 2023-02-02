package org.smartregister.giz.repository;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.giz.util.GizUtils;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashSet;

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

    public void processSkippedClients() {
        String missingClientRegister = "SELECT DISTINCT baseEntityId, formSubmissionId AS formSubmissionId FROM event WHERE baseEntityId IN (SELECT baseEntityId FROM client WHERE baseEntityId NOT IN (SELECT DISTINCT base_entity_id FROM client_register_type)) AND eventType LIKE '%Registration%'";
        String missingECClient = "SELECT DISTINCT baseEntityId, formSubmissionId AS formSubmissionId FROM event WHERE baseEntityId IN (SELECT baseEntityId FROM client WHERE baseEntityId NOT IN (SELECT DISTINCT base_entity_id FROM ec_client)) AND eventType LIKE '%Registration%'";
        HashSet<String> formSubmissionIDs = new HashSet<>();
        addFormSubmissionIds(formSubmissionIDs, missingClientRegister);
        addFormSubmissionIds(formSubmissionIDs, missingECClient);

        try {
            if (formSubmissionIDs.size() > 0)
                GizUtils.initiateEventProcessing(new ArrayList<>(formSubmissionIDs));
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    public void addFormSubmissionIds(HashSet<String> ids, String query) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        int columnIndex = cursor.getColumnIndex("formSubmissionId");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ids.add(cursor.getString(columnIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
