package org.smartregister.giz.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.giz.model.VisitDetail;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class VisitDetailsRepository extends BaseRepository {

    public static final String VISIT_DETAILS_TABLE = "opd_client_visit_details";
    private static final String VISIT_DETAILS_ID = "visit_details_id";
    private static final String VISIT_ID = "visit_id";
    private static final String VISIT_KEY = "visit_key";
    private static final String PARENT_CODE = "parent_code";
    private static final String DETAILS = "details";
    private static final String HUMAN_READABLE = "human_readable_details";
    private static final String UPDATED_AT = "updated_at";
    private static final String CREATED_AT = "created_at";

    private static final String CREATE_VISIT_TABLE =
            "CREATE TABLE " + VISIT_DETAILS_TABLE + "("
                    + VISIT_DETAILS_ID + " VARCHAR NULL, "
                    + VISIT_ID + " VARCHAR NULL, "
                    + VISIT_KEY + " VARCHAR NULL, "
                    + PARENT_CODE + " VARCHAR NULL, "
                    + DETAILS + " VARCHAR NULL, "
                    + HUMAN_READABLE + " VARCHAR NULL, "
                    + UPDATED_AT + " DATETIME NULL, "
                    + CREATED_AT + " DATETIME NULL)";

    private static final String VISIT_ID_INDEX = "CREATE INDEX " + VISIT_DETAILS_TABLE + "_" + VISIT_ID + "_index ON " + VISIT_DETAILS_TABLE
            + "("
            + VISIT_ID + " COLLATE NOCASE , "
            + VISIT_KEY + " COLLATE NOCASE "
            + ");";


    private String[] VISIT_DETAILS_COLUMNS = {VISIT_ID, VISIT_KEY, PARENT_CODE, VISIT_DETAILS_ID, HUMAN_READABLE, DETAILS, UPDATED_AT, CREATED_AT};

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_VISIT_TABLE);
        database.execSQL(VISIT_ID_INDEX);
    }

    private ContentValues createValues(VisitDetail visitDetail) {
        ContentValues values = new ContentValues();
        values.put(VISIT_DETAILS_ID, visitDetail.getVisitDetailsId());
        values.put(VISIT_ID, visitDetail.getVisitId());
        values.put(VISIT_KEY, visitDetail.getVisitKey());
        values.put(PARENT_CODE, visitDetail.getParentCode());
        values.put(DETAILS, visitDetail.getDetails());
        values.put(HUMAN_READABLE, visitDetail.getHumanReadable());
        values.put(CREATED_AT, visitDetail.getCreatedAt().getTime());
        if (visitDetail.getUpdatedAt() != null)
            values.put(UPDATED_AT, visitDetail.getUpdatedAt().getTime());
        return values;
    }

    public void addVisitDetails(VisitDetail visitDetail) {
        addVisitDetails(visitDetail, getWritableDatabase());
    }

    public void addVisitDetails(VisitDetail visitDetail, SQLiteDatabase database) {
        if (visitDetail == null) {
            return;
        }
        // Handle updated home visit details
        database.insert(VISIT_DETAILS_TABLE, null, createValues(visitDetail));
    }

    public List<VisitDetail> getVisits(String visitID) {
        List<VisitDetail> visitDetails = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_DETAILS_TABLE, VISIT_DETAILS_COLUMNS, VISIT_ID + " = ? ", new String[]{visitID}, null, null, null, null);
            visitDetails = readVisitDetails(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visitDetails;
    }

    public List<VisitDetail> readVisitDetails(Cursor cursor) {
        List<VisitDetail> visitDetailList = new ArrayList<>();

        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    VisitDetail visitDetail = new VisitDetail();
                    visitDetail.setVisitId(cursor.getString(cursor.getColumnIndex(VISIT_ID)));
                    visitDetail.setVisitDetailsId(cursor.getString(cursor.getColumnIndex(VISIT_DETAILS_ID)));
                    visitDetail.setVisitKey(cursor.getString(cursor.getColumnIndex(VISIT_KEY)));
                    visitDetail.setParentCode(cursor.getString(cursor.getColumnIndex(PARENT_CODE)));
                    visitDetail.setDetails(cursor.getString(cursor.getColumnIndex(DETAILS)));
                    visitDetail.setHumanReadable(cursor.getString(cursor.getColumnIndex(HUMAN_READABLE)));
                    visitDetail.setCreatedAt(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(CREATED_AT)))));

                    String updatedValue = cursor.getString(cursor.getColumnIndex(UPDATED_AT));
                    if (updatedValue != null)
                        visitDetail.setUpdatedAt(new Date(Long.parseLong(updatedValue)));

                    visitDetailList.add(visitDetail);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return visitDetailList;
    }

    public void deleteVisitDetails(String visitID) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(VISIT_DETAILS_TABLE, VISIT_ID + " = ? ", new String[]{visitID});
    }
}
