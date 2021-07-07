package org.smartregister.giz.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.smartregister.giz.model.Visit;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class VisitRepository extends BaseRepository {

    public static final String VISIT_TABLE = "opd_client_visits";
    private static final String VISIT_ID = "visit_id";
    private static final String VISIT_TYPE = "visit_type";
    private static final String VISIT_GROUP = "visit_group";
    private static final String LOCATION_ID = "location_id";
    private static final String CHILD_LOCATION_ID = "child_location_id";
    private static final String BASE_ENTITY_ID = "base_entity_id";
    private static final String VISIT_DATE = "visit_date";
    private static final String FORM_SUBMISSION_ID = "form_submission_id";
    private static final String UPDATED_AT = "updated_at";
    private static final String CREATED_AT = "created_at";
    private static final String DELETED_AT = "deleted_at";
    private static final String CREATE_VISIT_TABLE =
            "CREATE TABLE " + VISIT_TABLE + "("
                    + VISIT_ID + " VARCHAR NULL, "
                    + VISIT_TYPE + " VARCHAR NULL, "
                    + LOCATION_ID + " VARCHAR NULL, "
                    + CHILD_LOCATION_ID + " VARCHAR NULL, "
                    + VISIT_GROUP + " VARCHAR NULL, "
                    + BASE_ENTITY_ID + " VARCHAR NULL, "
                    + VISIT_DATE + " VARCHAR NULL, "
                    + FORM_SUBMISSION_ID + " VARCHAR NULL, "
                    + UPDATED_AT + " DATETIME NULL, "
                    + CREATED_AT + " DATETIME NULL, "
                    + DELETED_AT + " DATETIME NULL)";
    private static final String BASE_ENTITY_ID_INDEX = "CREATE INDEX " + VISIT_TABLE + "_" + BASE_ENTITY_ID + "_index ON " + VISIT_TABLE
            + "("
            + BASE_ENTITY_ID + " COLLATE NOCASE , "
            + VISIT_TYPE + " COLLATE NOCASE , "
            + VISIT_DATE + " COLLATE NOCASE"
            + ");";
    private String[] VISIT_COLUMNS = {VISIT_ID, VISIT_TYPE, LOCATION_ID, CHILD_LOCATION_ID, VISIT_GROUP, LOCATION_ID, BASE_ENTITY_ID, VISIT_DATE, FORM_SUBMISSION_ID, UPDATED_AT, CREATED_AT, DELETED_AT};

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_VISIT_TABLE);
        database.execSQL(BASE_ENTITY_ID_INDEX);
    }

    private ContentValues createValues(Visit visit) {
        ContentValues values = new ContentValues();
        values.put(VISIT_ID, visit.getVisitId());
        values.put(VISIT_TYPE, visit.getVisitType());
        values.put(VISIT_GROUP, visit.getVisitGroup());
        values.put(LOCATION_ID, visit.getLocationId());
        values.put(CHILD_LOCATION_ID, visit.getChildLocationId());
        values.put(BASE_ENTITY_ID, visit.getBaseEntityId());
        values.put(VISIT_DATE, visit.getDate() != null ? visit.getDate().getTime() : null);
        values.put(FORM_SUBMISSION_ID, visit.getFormSubmissionId());
        values.put(CREATED_AT, visit.getCreatedAt().getTime());
        if (visit.getUpdatedAt() != null)
            values.put(UPDATED_AT, visit.getUpdatedAt().getTime());
        if (visit.getDeletedAt() != null)
            values.put(DELETED_AT, visit.getDeletedAt().getTime());
        return values;
    }

    public void addVisit(Visit visit) {
        addVisit(visit, getWritableDatabase());
    }

    public void addVisit(Visit visit, SQLiteDatabase database) {
        if (visit == null) {
            return;
        }
        // Handle updated home visit details
        database.insert(VISIT_TABLE, null, createValues(visit));
        EventBus.getDefault().post(visit);
    }

    public void deleteVisit(String visitID) {
        try {
            getWritableDatabase().delete(VISIT_TABLE, VISIT_ID + "= ?", new String[]{visitID});
            getWritableDatabase().delete(VisitDetailsRepository.VISIT_DETAILS_TABLE, VISIT_ID + "= ?", new String[]{visitID});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public List<Visit> readVisits(Cursor cursor) {
        List<Visit> visits = new ArrayList<>();

        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    Visit visit = new Visit();
                    visit.setVisitId(cursor.getString(cursor.getColumnIndex(VISIT_ID)));
                    visit.setVisitType(cursor.getString(cursor.getColumnIndex(VISIT_TYPE)));
                    visit.setVisitGroup(cursor.getString(cursor.getColumnIndex(VISIT_GROUP)));
                    visit.setLocationId(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));
                    visit.setChildLocationId(cursor.getString(cursor.getColumnIndex(CHILD_LOCATION_ID)));
                    visit.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
                    visit.setDate(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(VISIT_DATE)))));
                    visit.setFormSubmissionId(cursor.getString(cursor.getColumnIndex(FORM_SUBMISSION_ID)));
                    visit.setCreatedAt(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(CREATED_AT)))));

                    String updatedValue = cursor.getString(cursor.getColumnIndex(UPDATED_AT));
                    //if (updatedValue != null)
                        visit.setUpdatedAt(new Date(Long.parseLong(updatedValue)));

                    String deletedValue = cursor.getString(cursor.getColumnIndex(DELETED_AT));
                    if (deletedValue != null)
                        visit.setUpdatedAt(new Date(Long.parseLong(deletedValue)));

                    visits.add(visit);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return visits;
    }

    public List<Visit> getVisits(String baseEntityID, String visitType) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, BASE_ENTITY_ID + " = ? AND " + VISIT_TYPE + " = ? ", new String[]{baseEntityID, visitType}, null, null, CREATED_AT + " ASC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public List<Visit> getVisitsByGroup(String visitGroup) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, VISIT_GROUP + " = ? ", new String[]{visitGroup}, null, null, CREATED_AT + " ASC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public List<Visit> getUniqueDayLatestThreeVisits(String baseEntityID, String visitType) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "select STRFTIME('%Y%m%d', datetime((" + VISIT_DATE + ")/1000,'unixepoch')) as d,* from " + VISIT_TABLE + " where " + VISIT_TYPE + " = '" + visitType + "' AND " +
                    "" + BASE_ENTITY_ID + " = '" + baseEntityID + "'  group by d order by " + VISIT_DATE + " desc limit 3";
            cursor = getReadableDatabase().rawQuery(query, null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public List<Visit> getVisitsByVisitId(String visitID) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, VISIT_ID + " = ? ", new String[]{visitID}, null, null, VISIT_DATE + " DESC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits;
    }

    public Visit getVisitByVisitId(String visitID) {
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, VISIT_ID + " = ? ", new String[]{visitID}, null, null, VISIT_DATE + " DESC ", null);
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return visits.size() == 1 ? visits.get(0) : null;
    }

    public Visit getVisitByFormSubmissionID(String formSubmissionID) {
        if (StringUtils.isBlank(formSubmissionID))
            return null;

        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(VISIT_TABLE, VISIT_COLUMNS, FORM_SUBMISSION_ID + " = ? ", new String[]{formSubmissionID}, null, null, VISIT_DATE + " DESC ", "1");
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return (visits.size() > 0) ? visits.get(0) : null;
    }

    public Visit getLatestVisit(String baseEntityID, String visitType) {
        return getLatestVisit(baseEntityID, visitType, null);
    }

    public Visit getLatestVisit(String baseEntityID, String visitType, SQLiteDatabase sqLiteDatabase) {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = getReadableDatabase();
        }
        List<Visit> visits = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(VISIT_TABLE, VISIT_COLUMNS, BASE_ENTITY_ID + " = ? AND " + VISIT_TYPE + " = ? ", new String[]{baseEntityID, visitType}, null, null, VISIT_DATE + " DESC ", "1");
            visits = readVisits(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return (visits.size() > 0) ? visits.get(0) : null;
    }

    public String getLastInteractedWithAndVisitNotDone(String baseEntityID, String dateColumn) {
        SQLiteDatabase database = getReadableDatabase();
        net.sqlcipher.Cursor cursor = null;
        try {
            if (database == null) {
                return null;
            }

            cursor = database.query(GizConstants.TABLE_NAME.ALL_CLIENTS, new String[]{dateColumn}, BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE, new String[]{baseEntityID}, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                String date = cursor.getString(cursor.getColumnIndex(dateColumn));
                return date;
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;

    }
}
