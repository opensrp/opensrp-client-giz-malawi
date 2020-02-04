package org.smartregister.giz.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.giz.util.GizConstants;
import org.smartregister.repository.BaseRepository;

import java.util.Date;
import java.util.List;


public class RegisterTypeRepository extends BaseRepository implements RegisterTypeDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + GizConstants.TABLE_NAME.REGISTER_TYPE + "("
            + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " VARCHAR NOT NULL,"
            + GizConstants.Columns.RegisterType.REGISTER_TYPE + " VARCHAR NOT NULL, "
            + GizConstants.Columns.RegisterType.DATE_CREATED + " INTEGER NOT NULL, "
            + GizConstants.Columns.RegisterType.DATE_REMOVED + " INTEGER NULL, "
            + "UNIQUE(" + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + ", " + GizConstants.Columns.RegisterType.REGISTER_TYPE + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + GizConstants.TABLE_NAME.REGISTER_TYPE
            + "_" + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + "_index ON " + GizConstants.TABLE_NAME.REGISTER_TYPE +
            "(" + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public List<RegisterType> findAll(String baseEntityId) {
        return null;
    }

    @Override
    public boolean remove(String registerType, String baseEntityId) {
        return false;
    }

    @Override
    public boolean removeAll(String baseEntityId) {
        return false;
    }

    @Override
    public boolean add(String registerType, String baseEntityId) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GizConstants.Columns.RegisterType.BASE_ENTITY_ID, baseEntityId);
        contentValues.put(GizConstants.Columns.RegisterType.REGISTER_TYPE, registerType);
        contentValues.put(GizConstants.Columns.RegisterType.DATE_CREATED, new Date().getTime());
        long result = database.insert(GizConstants.TABLE_NAME.REGISTER_TYPE, null, contentValues);
        return result != -1;
    }
}
