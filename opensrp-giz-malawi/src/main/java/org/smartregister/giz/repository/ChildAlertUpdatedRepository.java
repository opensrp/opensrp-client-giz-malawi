package org.smartregister.giz.repository;

import android.content.ContentValues;
import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.giz.util.GizConstants;
import org.smartregister.repository.BaseRepository;

import java.util.Date;

public class ChildAlertUpdatedRepository extends BaseRepository {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + GizConstants.TABLE_NAME.CHILD_UPDATED_ALERTS + "("
            + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " VARCHAR NOT NULL,"
            + GizConstants.Columns.RegisterType.DATE_CREATED + " INTEGER NOT NULL, "
            + "UNIQUE(" + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + GizConstants.TABLE_NAME.CHILD_UPDATED_ALERTS
            + "_" + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + "_index ON " + GizConstants.TABLE_NAME.CHILD_UPDATED_ALERTS +
            "(" + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }


    public boolean findOne(String baseEntityId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(GizConstants.TABLE_NAME.CHILD_UPDATED_ALERTS, new String[]{GizConstants.Columns.RegisterType.BASE_ENTITY_ID},
                GizConstants.Columns.RegisterType.BASE_ENTITY_ID + "=?",
                new String[]{baseEntityId}, null, null, null, "1");
        return cursor != null && cursor.getCount() > 0;

    }

    public boolean saveOrUpdate(@NonNull String baseEntityId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GizConstants.Columns.RegisterType.BASE_ENTITY_ID, baseEntityId);
        contentValues.put(GizConstants.Columns.RegisterType.DATE_CREATED, new Date().getTime());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(GizConstants.TABLE_NAME.CHILD_UPDATED_ALERTS, null, contentValues);
        return rows != -1;
    }

    public boolean deleteAll() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(GizConstants.TABLE_NAME.CHILD_UPDATED_ALERTS
                , null
                , null);
        return rows > 0;
    }
}
