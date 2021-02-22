package org.smartregister.giz.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.giz.model.ReasonForDefaultingModel;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;

import timber.log.Timber;

public class ReasonForDefaultingRepository extends BaseRepository {
    public static final String TABLE_NAME = "reason_for_defaulting";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            GizConstants.JsonAssets.ID + "  VARCHAR NOT NULL PRIMARY KEY, " +
            GizConstants.JsonAssets.OUTREACH_DATE + "  VARCHAR, " +
            GizConstants.JsonAssets.FOLLOWUP_DATE + "  VARCHAR, " +
            GizConstants.JsonAssets.OUTREACH_DEFAULTING_REASON + "  VARCHAR, " +
            GizConstants.JsonAssets.ADDITIONAL_DEFAULTING_NOTES + "  VARCHAR, " +
            GizConstants.JsonAssets.BASE_ENTITY_ID + "  VARCHAR, " +
            GizConstants.JsonAssets.EVENT_DATE + "  VARCHAR, " +
            GizConstants.JsonAssets.OTHER_DEFAULTING_REASON + "  VARCHAR )";
    public static final String BASE_ID_INDEX = "CREATE UNIQUE INDEX " + TABLE_NAME + "_" + GizConstants.JsonAssets.ID + "_index ON " + TABLE_NAME + "(" + GizConstants.JsonAssets.ID + " COLLATE NOCASE " + ")";


    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(BASE_ID_INDEX);
    }

    public void addOrUpdate(ReasonForDefaultingModel reasonForDefaultingModel) {
        addOrUpdateReasonForDefaulting(reasonForDefaultingModel, getWritableDatabase());
    }

    public void addOrUpdateReasonForDefaulting(ReasonForDefaultingModel reasonForDefaultingModel, SQLiteDatabase database) {
        if (reasonForDefaultingModel == null) {
            return;
        }
        database.replace(TABLE_NAME, null, createValues(reasonForDefaultingModel));

    }

    private ContentValues createValues(ReasonForDefaultingModel reasonForDefaultingModel) {
        ContentValues values = new ContentValues();
        values.put(GizConstants.JsonAssets.ID, reasonForDefaultingModel.getId());
        values.put(GizConstants.JsonAssets.OUTREACH_DATE, reasonForDefaultingModel.getOutreachDate());
        values.put(GizConstants.JsonAssets.FOLLOWUP_DATE, reasonForDefaultingModel.getFollowupDate());
        values.put(GizConstants.JsonAssets.OUTREACH_DEFAULTING_REASON, reasonForDefaultingModel.getOutreachDefaultingReason());
        values.put(GizConstants.JsonAssets.ADDITIONAL_DEFAULTING_NOTES, reasonForDefaultingModel.getAdditionalDefaultingNotes());
        values.put(GizConstants.JsonAssets.BASE_ENTITY_ID, reasonForDefaultingModel.getBaseEntityId());
        values.put(GizConstants.JsonAssets.EVENT_DATE, reasonForDefaultingModel.getDateCreated());
        values.put(GizConstants.JsonAssets.OTHER_DEFAULTING_REASON, reasonForDefaultingModel.getOtherOutreachDefaultingReason());
        return values;
    }

    public ArrayList<ReasonForDefaultingModel> readAllReasonsForDefaulting() {
        String[] columns = {GizConstants.JsonAssets.ID, GizConstants.JsonAssets.OUTREACH_DATE,
                GizConstants.JsonAssets.FOLLOWUP_DATE, GizConstants.JsonAssets.OUTREACH_DEFAULTING_REASON, GizConstants.JsonAssets.ADDITIONAL_DEFAULTING_NOTES,
                GizConstants.JsonAssets.BASE_ENTITY_ID, GizConstants.JsonAssets.EVENT_DATE, GizConstants.JsonAssets.OTHER_DEFAULTING_REASON};
        Cursor cursor = getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, null, null);
        ArrayList<ReasonForDefaultingModel> reasonForDefaultingModels = new ArrayList<>();

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    reasonForDefaultingModels.add(new ReasonForDefaultingModel(
                            cursor.getString(cursor.getColumnIndex(GizConstants.JsonAssets.ID)),
                            cursor.getString(cursor.getColumnIndex(GizConstants.JsonAssets.OUTREACH_DATE)),
                            cursor.getString(cursor.getColumnIndex(GizConstants.JsonAssets.FOLLOWUP_DATE)),
                            cursor.getString(cursor.getColumnIndex(GizConstants.JsonAssets.OUTREACH_DEFAULTING_REASON)),
                            cursor.getString(cursor.getColumnIndex(GizConstants.JsonAssets.ADDITIONAL_DEFAULTING_NOTES)),
                            cursor.getString(cursor.getColumnIndex(GizConstants.JsonAssets.BASE_ENTITY_ID)),
                            cursor.getString(cursor.getColumnIndex(GizConstants.JsonAssets.EVENT_DATE)),
                            cursor.getString(cursor.getColumnIndex(GizConstants.JsonAssets.OTHER_DEFAULTING_REASON))));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Timber.e(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reasonForDefaultingModels;
    }

    public void purgeReasonForDefaulting(String baseEntityID) {
        try {
            getWritableDatabase().execSQL("DELETE FROM reason_for_defaulting WHERE id ='" + baseEntityID + "'");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public long getReasonForDefaultingCount() {
        SQLiteDatabase db = getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }

}
