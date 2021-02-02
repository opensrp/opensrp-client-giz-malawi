package org.smartregister.giz.repository;

import android.content.ContentValues;
import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.giz.domain.ClientRegisterType;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.repository.BaseRepository;

import java.util.Date;
import java.util.List;


public class ClientRegisterTypeRepository extends BaseRepository implements ClientRegisterTypeDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + GizConstants.TABLE_NAME.REGISTER_TYPE + "("
            + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " VARCHAR NOT NULL,"
            + GizConstants.Columns.RegisterType.REGISTER_TYPE + " VARCHAR NOT NULL, "
            + GizConstants.Columns.RegisterType.DATE_CREATED + " INTEGER NOT NULL, "
            + GizConstants.Columns.RegisterType.DATE_REMOVED + " VARCHAR NULL, "
            + "UNIQUE(" + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + ", " + GizConstants.Columns.RegisterType.REGISTER_TYPE + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + GizConstants.TABLE_NAME.REGISTER_TYPE
            + "_" + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + "_index ON " + GizConstants.TABLE_NAME.REGISTER_TYPE +
            "(" + GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public List<ClientRegisterType> findAll(String baseEntityId) {
        return null;
    }

    @Override
    public boolean remove(String registerType, String baseEntityId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rowsAffected = sqLiteDatabase.delete(GizConstants.TABLE_NAME.REGISTER_TYPE,
                GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " = ? and " + GizConstants.Columns.RegisterType.REGISTER_TYPE + " = ?", new String[]{baseEntityId, registerType});

        return rowsAffected > 0;
    }

    @Override
    public boolean softDelete(String registerType, String baseEntityId, String dateRemoved) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GizConstants.Columns.RegisterType.DATE_REMOVED, dateRemoved);
        int rowsAffected = sqLiteDatabase.update(GizConstants.TABLE_NAME.REGISTER_TYPE, contentValues,
                GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " = ? and " + GizConstants.Columns.RegisterType.REGISTER_TYPE + " = ?", new String[]{baseEntityId, registerType});
        return rowsAffected > 0;
    }

    @Override
    public boolean removeAll(String baseEntityId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rowsAffected = sqLiteDatabase.delete(GizConstants.TABLE_NAME.REGISTER_TYPE,
                GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " = ? ", new String[]{baseEntityId});
        return rowsAffected > 0;
    }


    public boolean addUnique(String registerType, String baseEntityId) {
        if (!hasRegisterType(baseEntityId)) {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(GizConstants.Columns.RegisterType.BASE_ENTITY_ID, baseEntityId);
            contentValues.put(GizConstants.Columns.RegisterType.REGISTER_TYPE, registerType);
            contentValues.put(GizConstants.Columns.RegisterType.DATE_CREATED, new Date().getTime());
            long result = database.insert(GizConstants.TABLE_NAME.REGISTER_TYPE, null, contentValues);
            return result != -1;
        }
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

    @Override
    public boolean findByRegisterType(String baseEntityId, String registerType) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(GizConstants.TABLE_NAME.REGISTER_TYPE, new String[]{GizConstants.Columns.RegisterType.BASE_ENTITY_ID},
                GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " = ? and " + GizConstants.Columns.RegisterType.REGISTER_TYPE + " = ?",
                new String[]{baseEntityId, registerType}, null, null, null);
        if (cursor != null) {
            boolean isType = cursor.getCount() > 0;
            cursor.close();
            return isType;
        }
        return false;
    }


    public boolean hasRegisterType(String baseEntityId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(GizConstants.TABLE_NAME.REGISTER_TYPE, new String[]{GizConstants.Columns.RegisterType.BASE_ENTITY_ID},
                GizConstants.Columns.RegisterType.BASE_ENTITY_ID + " = ? ",
                new String[]{baseEntityId}, null, null, null);
        if (cursor != null) {
            boolean hasType = cursor.getCount() > 0;
            cursor.close();
            return hasType;
        }
        return false;
    }
}
