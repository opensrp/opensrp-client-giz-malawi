package org.smartregister.giz.repository;

import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.repository.BaseRepository;

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
}
