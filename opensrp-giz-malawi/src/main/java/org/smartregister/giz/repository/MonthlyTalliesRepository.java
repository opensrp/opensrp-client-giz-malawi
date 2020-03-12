package org.smartregister.giz.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.MonthlyTally;
import org.smartregister.giz.domain.Tally;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.repository.BaseRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class MonthlyTalliesRepository extends BaseRepository {

    public static final SimpleDateFormat DF_YYYYMM = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
    public static final SimpleDateFormat DF_DDMMYY = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
    private static final String TABLE_NAME = "monthly_tallies";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PROVIDER_ID = "provider_id";
    private static final String COLUMN_INDICATOR_CODE = "indicator_code";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_EDITED = "edited";
    private static final String COLUMN_DATE_SENT = "date_sent";
    private static final String COLUMN_INDICATOR_GROUPING = "indicator_grouping";
    private static final String COLUMN_UPDATED_AT = "updated_at";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String[] TABLE_COLUMNS = {
            COLUMN_ID, COLUMN_INDICATOR_CODE, COLUMN_PROVIDER_ID,
            COLUMN_VALUE, COLUMN_MONTH, COLUMN_EDITED, COLUMN_DATE_SENT, COLUMN_INDICATOR_GROUPING, COLUMN_CREATED_AT, COLUMN_UPDATED_AT
    };

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            COLUMN_INDICATOR_CODE + " VARCHAR NOT NULL," +
            COLUMN_PROVIDER_ID + " VARCHAR NOT NULL," +
            COLUMN_VALUE + " VARCHAR NOT NULL," +
            COLUMN_MONTH + " VARCHAR NOT NULL," +
            COLUMN_EDITED + " INTEGER NOT NULL DEFAULT 0," +
            COLUMN_INDICATOR_GROUPING + " TEXT," +
            COLUMN_DATE_SENT + " DATETIME NULL," +
            COLUMN_CREATED_AT + " DATETIME NULL," +
            COLUMN_UPDATED_AT + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)";

    private static final String INDEX_PROVIDER_ID = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_PROVIDER_ID + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_PROVIDER_ID + " COLLATE NOCASE);";
    private static final String INDEX_INDICATOR_ID = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_INDICATOR_CODE + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_INDICATOR_CODE + " COLLATE NOCASE);";
    private static final String INDEX_UPDATED_AT = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_UPDATED_AT + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_UPDATED_AT + ");";
    private static final String INDEX_MONTH = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_MONTH + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_MONTH + ");";
    private static final String INDEX_EDITED = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_EDITED + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_EDITED + ");";
    private static final String INDEX_DATE_SENT = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_DATE_SENT + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_DATE_SENT + ");";

    public static final String INDEX_UNIQUE = "CREATE UNIQUE INDEX " + TABLE_NAME + "_" + COLUMN_INDICATOR_CODE + "_" + COLUMN_MONTH + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_INDICATOR_CODE + "," + COLUMN_MONTH + ");";


    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY);
        database.execSQL(INDEX_PROVIDER_ID);
        database.execSQL(INDEX_INDICATOR_ID);
        database.execSQL(INDEX_UPDATED_AT);
        database.execSQL(INDEX_MONTH);
        database.execSQL(INDEX_EDITED);
        database.execSQL(INDEX_DATE_SENT);
        database.execSQL(INDEX_UNIQUE);
    }


    /**
     * Returns a list of all months that have corresponding daily tallies by unsent monthly tallies
     *
     * @param startDate The earliest date for the draft reports' month. Set argument to null if you
     *                  don't want this enforced
     * @param endDate   The latest date for the draft reports' month. Set argument to null if you
     *                  don't want this enforced
     * @return List of months with unsent monthly tallies
     */
    public List<Date> findUneditedDraftMonths(Date startDate, Date endDate) {
        return findUneditedDraftMonths(startDate, endDate, null);
    }

    /**
     * Returns a list of all months that have corresponding daily tallies by unsent monthly tallies
     *
     * @param startDate The earliest date for the draft reports' month. Set argument to null if you
     *                  don't want this enforced
     * @param endDate   The latest date for the draft reports' month. Set argument to null if you
     *                  don't want this enforced
     * @return List of months with unsent monthly tallies
     */
    public List<Date> findUneditedDraftMonths(Date startDate, Date endDate, @Nullable String grouping) {
        List<String> allTallyMonths = GizMalawiApplication.getInstance().dailyTalliesRepository()
                .findAllDistinctMonths(DF_YYYYMM, startDate, endDate, grouping);
        Cursor cursor = null;
        try {
            if (allTallyMonths != null) {
                String monthsString = "";
                for (String curMonthString : allTallyMonths) {
                    if (!TextUtils.isEmpty(monthsString)) {
                        monthsString = monthsString + ", ";
                    }

                    monthsString = monthsString + "'" + curMonthString + "'";
                }

                String selection = COLUMN_MONTH + " IN(" + monthsString + ") AND " + getGroupingSelectionCondition(grouping);

                cursor = getReadableDatabase().query(
                        TABLE_NAME,
                        new String[]{COLUMN_MONTH}, selection,
                        null, null, null, null);

                Timber.d(monthsString + " === Select " + COLUMN_MONTH + " from " + TABLE_NAME + " where " + COLUMN_MONTH + " IN(" + monthsString + ")");

                if (cursor != null && cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String curMonth = cursor.getString(cursor.getColumnIndex(COLUMN_MONTH));
                        allTallyMonths.remove(curMonth);
                    }
                }

                List<Date> unsentMonths = new ArrayList<>();
                for (String curMonthString : allTallyMonths) {
                    Date curMonth = DF_YYYYMM.parse(curMonthString);
                    if ((startDate != null && curMonth.getTime() < startDate.getTime())
                            || (endDate != null && curMonth.getTime() > endDate.getTime())) {
                        continue;
                    }
                    unsentMonths.add(curMonth);
                }

                return unsentMonths;
            }
        } catch (SQLException | ParseException e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return new ArrayList<>();
    }

    /**
     * Returns a list of draft monthly tallies corresponding to the provided month
     *
     * @param month The month to get the draft tallies for
     * @return
     */
    @NonNull
    public List<MonthlyTally> findDrafts(@NonNull String month) {
        return findDrafts(month, null);
    }

    /**
     * Returns a list of draft monthly tallies corresponding to the provided month
     *
     * @param month The month to get the draft tallies for
     * @return
     */
    @NonNull
    public List<MonthlyTally> findDrafts(@NonNull String month, @Nullable String grouping) {
        // Check if there exists any sent tally in the database for the month provided
        Cursor cursor = null;
        List<MonthlyTally> monthlyTallies = new ArrayList<>();
        try {
            String selection = COLUMN_MONTH + " = '" + month +
                    "' AND " + COLUMN_DATE_SENT + " IS NULL AND " + getGroupingSelectionCondition(grouping);
            cursor = getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS,
                    selection,
                    null, null, null, null, null);
            monthlyTallies = readAllDataElements(cursor);

            if (monthlyTallies.size() == 0) { // No tallies generated yet
                Timber.w("Using daily tallies instead of monthly");

                Map<String, List<IndicatorTally>> dailyTallies = ReportingLibrary.getInstance().dailyIndicatorCountRepository().findTalliesInMonth(DF_YYYYMM.parse(month), grouping);
                for (List<IndicatorTally> curList : dailyTallies.values()) {
                    MonthlyTally curTally = addUpDailyTallies(curList);
                    if (curTally != null) {
                        monthlyTallies.add(curTally);
                    }
                }
            }
        } catch (SQLException | ParseException e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return monthlyTallies;
    }


    /**
     * Returns a list of all monthly tallies corresponding to the provided month
     *
     * @param month The month to get the draft tallies for
     * @return
     */
    @Nullable
    public List<MonthlyTally> find(String month) {
        return find(month, null);
    }

    /**
     * Returns a list of all monthly tallies corresponding to the provided month
     *
     * @param month The month to get the draft tallies for
     * @return
     */
    @Nullable
    public List<MonthlyTally> find(String month, @Nullable String grouping) {
        // Check if there exists any sent tally in the database for the month provided
        Cursor cursor = null;
        List<MonthlyTally> monthlyTallies = new ArrayList<>();
        try {
            cursor = getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS,
                    COLUMN_MONTH + " = '" + month + "' AND " + getGroupingSelectionCondition(grouping),
                    null, null, null, null, null);
            monthlyTallies = readAllDataElements(cursor);
        } catch (SQLException e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return monthlyTallies;
    }

    @Nullable
    private MonthlyTally addUpDailyTallies(@NonNull List<IndicatorTally> dailyTallies) {
        String userName = GizMalawiApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM();
        MonthlyTally monthlyTally = null;
        double value = 0d;

        if (dailyTallies.size() > 0) {
            monthlyTally = new MonthlyTally();
            IndicatorTally indicatorTally = dailyTallies.get(0);
            monthlyTally.setIndicator(indicatorTally.getIndicatorCode());
            monthlyTally.setGrouping(indicatorTally.getGrouping());
        }

        for (IndicatorTally dailyIndicatorTally: dailyTallies) {
            try {
                value += dailyIndicatorTally.getFloatCount();
            } catch (NumberFormatException e) {
                Timber.w(e);
            }
        }

        if (monthlyTally != null) {
            monthlyTally.setUpdatedAt(Calendar.getInstance().getTime());
            monthlyTally.setValue(String.valueOf(Math.round(value)));
            monthlyTally.setProviderId(userName);
        }

        return monthlyTally;
    }



    public HashMap<String, ArrayList<MonthlyTally>> findAllSent(@NonNull SimpleDateFormat dateFormat) {
        return findAllSent(dateFormat, null);
    }

    public HashMap<String, ArrayList<MonthlyTally>> findAllSent(@NonNull SimpleDateFormat dateFormat, @Nullable String grouping) {
        HashMap<String, ArrayList<MonthlyTally>> tallies = new HashMap<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase()
                    .query(TABLE_NAME, TABLE_COLUMNS,
                            COLUMN_DATE_SENT + " IS NOT NULL AND " + getGroupingSelectionCondition(grouping), null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    MonthlyTally curTally = extractMonthlyTally(cursor);
                    String yearMonthString = dateFormat.format(curTally.getMonth());

                    ArrayList<MonthlyTally> monthlyTallies = tallies.get(yearMonthString);
                    if (monthlyTallies == null) {
                        monthlyTallies = new ArrayList<>();
                        tallies.put(yearMonthString, monthlyTallies);
                    }

                    monthlyTallies.add(curTally);
                }
            }
        } catch (SQLException | ParseException e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    public boolean save(@Nullable MonthlyTally tally) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            if (tally != null) {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_INDICATOR_CODE, tally.getIndicator());
                cv.put(COLUMN_VALUE, tally.getValue());
                cv.put(COLUMN_PROVIDER_ID, tally.getProviderId());
                cv.put(COLUMN_MONTH, DF_YYYYMM.format(tally.getMonth()));
                cv.put(COLUMN_DATE_SENT,
                        tally.getDateSent() == null ? null : tally.getDateSent().getTime());
                cv.put(COLUMN_EDITED, tally.isEdited() ? 1 : 0);
                cv.put(COLUMN_INDICATOR_GROUPING, tally.getGrouping());
                cv.put(COLUMN_CREATED_AT, Calendar.getInstance().getTimeInMillis());
                database.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                database.setTransactionSuccessful();

                return true;
            }
        } catch (SQLException e) {
            Timber.e(e);
        } finally {
            try {
                database.endTransaction();
            } catch (IllegalStateException e) {
                Timber.e(e);
            }
        }

        return false;
    }

    /**
     * save data from the monthly draft form whereby in the map the key is indicator_id and value is the form value
     * assumption here is that the data is edited..probably find a way to confirm this
     *
     * @param draftFormValues
     * @param month
     * @return
     */
    public boolean save(@Nullable Map<String, String> draftFormValues, @Nullable Date month) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            if (draftFormValues != null && !draftFormValues.isEmpty() && month != null) {
                String userName = GizMalawiApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM();

                for (String key : draftFormValues.keySet()) {
                    String value = draftFormValues.get(key);
                    ContentValues cv = new ContentValues();
                    String indicatorCode;
                    String grouping = null;

                    if (key.contains(">")) {
                        String[] codeAndGrouping = key.split(">");
                        indicatorCode = codeAndGrouping[0];
                        if (codeAndGrouping.length > 1) {
                            grouping = codeAndGrouping[1];
                        }
                    } else {
                        indicatorCode = key;
                    }

                    cv.put(COLUMN_INDICATOR_CODE, indicatorCode);
                    cv.put(COLUMN_INDICATOR_GROUPING, grouping);

                    if(!(value == null || value.isEmpty()))
                        cv.put(COLUMN_VALUE, Integer.valueOf(value));
                    else
                        cv.put(COLUMN_VALUE, "");

                    cv.put(COLUMN_MONTH, DF_YYYYMM.format(month));
                    cv.put(COLUMN_EDITED, 1);
                    cv.put(COLUMN_PROVIDER_ID, userName);
                    cv.put(COLUMN_CREATED_AT, Calendar.getInstance().getTimeInMillis());

                    Timber.d(key + " & " + value + " & " + userName + " & " + month);

                    database.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                }

                database.setTransactionSuccessful();

                return true;
            }
        } catch (SQLException e) {
            Timber.e(e);
        } finally {
            try {
                database.endTransaction();
            } catch (IllegalStateException e) {
                Timber.e(e);
            }
        }

        return false;
    }

    @NonNull
    private List<MonthlyTally> readAllDataElements(Cursor cursor) {
        List<MonthlyTally> tallies = new ArrayList<>();

        try {
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    MonthlyTally curTally = extractMonthlyTally(cursor);
                    tallies.add(curTally);
                }
            }
        } catch (SQLException | ParseException e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    @NonNull
    private MonthlyTally extractMonthlyTally(@NonNull Cursor cursor) throws ParseException {
        String indicatorId = cursor.getString(cursor.getColumnIndex(COLUMN_INDICATOR_CODE));

        MonthlyTally curTally = new MonthlyTally();
        curTally.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
        curTally.setProviderId(
                cursor.getString(cursor.getColumnIndex(COLUMN_PROVIDER_ID)));

        curTally.setIndicator(indicatorId);
        curTally.setValue(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));

        curTally.setMonth(DF_YYYYMM.parse(cursor.getString(cursor.getColumnIndex(COLUMN_MONTH))));
        curTally.setEdited(
                cursor.getInt(cursor.getColumnIndex(COLUMN_EDITED)) != 0
        );
        curTally.setDateSent(
                cursor.getString(cursor.getColumnIndex(COLUMN_DATE_SENT)) == null ?
                        null : new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_SENT)))
        );
        curTally.setGrouping(cursor.getString(cursor.getColumnIndex(COLUMN_INDICATOR_GROUPING)));
        curTally.setUpdatedAt(
                new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED_AT)))
        );

        Tally indicatorTally = new Tally();
        indicatorTally.setId(curTally.getId());
        indicatorTally.setValue(curTally.getValue());
        indicatorTally.setIndicator(curTally.getIndicator());

        curTally.setIndicatorTally(indicatorTally);

        return curTally;
    }


    /**
     * Returns a list of dates for monthly reports that have been edited, but not sent
     *
     * @param startDate The earliest date for the monthly reports. Set argument to null if you
     *                  don't want this enforced
     * @param endDate   The latest date for the monthly reports. Set argument to null if you
     *                  don't want this enforced
     * @return The list of monthly reports that have been edited, but not sent
     */
    public List<MonthlyTally> findEditedDraftMonths(Date startDate, Date endDate) {
        return findEditedDraftMonths(startDate, endDate, null);
    }

    /**
     * Returns a list of dates for monthly reports that have been edited, but not sent
     *
     * @param startDate The earliest date for the monthly reports. Set argument to null if you
     *                  don't want this enforced
     * @param endDate   The latest date for the monthly reports. Set argument to null if you
     *                  don't want this enforced
     * @return The list of monthly reports that have been edited, but not sent
     */
    public List<MonthlyTally> findEditedDraftMonths(Date startDate, Date endDate, @NonNull String grouping) {
        Cursor cursor = null;
        List<MonthlyTally> tallies = new ArrayList<>();

        try {
            cursor = getReadableDatabase().query(
                    TABLE_NAME, new String[]{COLUMN_MONTH, COLUMN_CREATED_AT},
                    COLUMN_DATE_SENT + " IS NULL AND " + COLUMN_EDITED + " = 1 AND " + getGroupingSelectionCondition(grouping),
                    null, COLUMN_MONTH, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String curMonth = cursor.getString(cursor.getColumnIndex(COLUMN_MONTH));
                    Date month = DF_YYYYMM.parse(curMonth);

                    if ((startDate != null && month.getTime() < startDate.getTime())
                            || (endDate != null && month.getTime() > endDate.getTime())) {
                        continue;
                    }

                    long dateStarted = cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT));
                    MonthlyTally tally = new MonthlyTally();
                    tally.setMonth(month);
                    tally.setCreatedAt(new Date(dateStarted));
                    tallies.add(tally);
                }
            }
        } catch (SQLException| ParseException e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    @NonNull
    public String getGroupingSelectionCondition(@Nullable String grouping) {
        return  " " + COLUMN_INDICATOR_GROUPING + " " + (grouping == null ? "IS NULL" : "= '" + grouping + "'");
    }
}
