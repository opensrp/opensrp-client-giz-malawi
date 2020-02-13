package org.smartregister.giz.repository;

import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;

import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.DailyTally;
import org.smartregister.giz.domain.Hia2Indicator;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class DailyTalliesRepository extends BaseRepository {

    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final String TABLE_NAME = "indicator_daily_tally";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_INDICATOR_CODE = "indicator_code";
    private static final String COLUMN_INDICATOR_VALUE = "indicator_value";
    private static final String COLUMN_DAY = "day";
    private static final String[] TABLE_COLUMNS = {
            COLUMN_ID, COLUMN_INDICATOR_CODE, COLUMN_INDICATOR_VALUE, COLUMN_DAY
    };

    /**
     * Returns a list of dates for distinct months with daily tallies
     *
     * @param dateFormat The format to use to format the months' dates
     * @param startDate  The first date to consider. Set argument to null if you
     *                   don't want this enforced
     * @param endDate    The last date to consider. Set argument to null if you
     *                   don't want this enforced
     * @return A list of months that have daily tallies
     */
    public List<String> findAllDistinctMonths(SimpleDateFormat dateFormat, Date startDate, Date endDate) {
        Cursor cursor = null;
        List<String> months = new ArrayList<>();

        try {
            String selectionArgs = "";
            if (startDate != null) {
                selectionArgs = COLUMN_DAY + " >= '" + DAY_FORMAT.format(startDate) + "'";
            }

            if (endDate != null) {
                if (!TextUtils.isEmpty(selectionArgs)) {
                    selectionArgs = selectionArgs + " AND ";
                }

                selectionArgs = selectionArgs + COLUMN_DAY + " <= '" + DAY_FORMAT.format(endDate) +"'";
            }

            cursor = getReadableDatabase().query(true, TABLE_NAME,
                    new String[]{COLUMN_DAY},
                    selectionArgs, null, null, null, null, null);

            months = getUniqueMonths(dateFormat, cursor);
        } catch (SQLException | ParseException e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return months;
    }

    /**
     * Returns a list of unique months formatted in the provided {@link SimpleDateFormat}
     *
     * @param dateFormat The date format to format the months
     * @param cursor     Cursor to get the dates from
     * @return
     */
    private List<String> getUniqueMonths(SimpleDateFormat dateFormat, Cursor cursor) throws ParseException {
        List<String> months = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                //Date curMonth = new Date(cursor.getLong(0));
                Date curMonth = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse((cursor.getString(0)));
                String month = dateFormat.format(curMonth);
                if (!months.contains(month)) {
                    months.add(month);
                }
            }
        }

        return months;
    }

    private String getDayBetweenDatesSelection(Date startDate, Date endDate) {
        return COLUMN_DAY + " >= '" + DAY_FORMAT.format(startDate) +
                "' AND " + COLUMN_DAY + " <= '" + DAY_FORMAT.format(endDate) + "'";
    }

    public HashMap<String, ArrayList<DailyTally>> findAll(SimpleDateFormat dateFormat, Date minDate, Date maxDate) {
        HashMap<String, ArrayList<DailyTally>> tallies = new HashMap<>();
        Cursor cursor = null;
        try {
            HashMap<String, Hia2Indicator> indicatorMap = GizMalawiApplication.getInstance()
                    .hIA2IndicatorsRepository().findAll();
            cursor = getReadableDatabase()
                    .query(TABLE_NAME, TABLE_COLUMNS,
                            getDayBetweenDatesSelection(minDate, maxDate),
                            null, null, null, COLUMN_DAY + " DESC", null);
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    DailyTally curTally = extractDailyTally(indicatorMap, cursor);
                    if (curTally != null) {
                        final String dayString = dateFormat.format(curTally.getDay());
                        if (!TextUtils.isEmpty(dayString)) {
                            if (!tallies.containsKey(dayString) ||
                                    tallies.get(dayString) == null) {
                                tallies.put(dayString, new ArrayList<DailyTally>());
                            }

                            tallies.get(dayString).add(curTally);
                        } else {
                            Timber.w("There appears to be a daily tally with a null date");
                        }
                    }
                }
            }
        } catch (SQLException | ParseException | NullPointerException e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    private DailyTally extractDailyTally(HashMap<String, Hia2Indicator> indicatorMap, Cursor cursor) throws ParseException {
        String indicatorId = cursor.getString(cursor.getColumnIndex(COLUMN_INDICATOR_CODE));
        if (indicatorMap.containsKey(indicatorId)) {
            Hia2Indicator indicator = indicatorMap.get(indicatorId);
            DailyTally curTally = new DailyTally();
            curTally.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            curTally.setIndicator(indicator.getIndicatorCode());
            curTally.setValue(cursor.getString(cursor.getColumnIndex(COLUMN_INDICATOR_VALUE)));
            Date day = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse((cursor.getString(cursor.getColumnIndex(COLUMN_DAY))));
            curTally.setDay(day);
            return curTally;
        }

        return null;
    }
}