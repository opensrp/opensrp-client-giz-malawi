package org.smartregister.giz.repository;

import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.giz.domain.Hia2Indicator;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class HIA2IndicatorsRepository extends BaseRepository {
    private static final String TAG = HIA2IndicatorsRepository.class.getCanonicalName();
    private static final String HIA2_INDICATORS_TABLE_NAME = "indicators";
    private static final String ID_COLUMN = "_id";
    private static final String INDICATOR_CODE = "indicator_code";
    private static final String DESCRIPTION = "description";

    private static final String[] HIA2_TABLE_COLUMNS = {ID_COLUMN, INDICATOR_CODE, DESCRIPTION};

    public HashMap<String, Hia2Indicator> findAll() {
        HashMap<String, Hia2Indicator> response = new HashMap<>();
        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().query(HIA2_INDICATORS_TABLE_NAME, HIA2_TABLE_COLUMNS, null, null, null, null, null, null);
            List<Hia2Indicator> hia2Indicators = readAllDataElements(cursor);
            for (Hia2Indicator curIndicator : hia2Indicators) {
                response.put(curIndicator.getIndicatorCode(), curIndicator);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return response;
    }

    public Hia2Indicator findByIndicatorCode(String indicatorCode) {
        Cursor cursor = null;
        Hia2Indicator hia2Indicator = null;

        try {
            cursor = getReadableDatabase().query(HIA2_INDICATORS_TABLE_NAME, HIA2_TABLE_COLUMNS, INDICATOR_CODE + " = ? COLLATE NOCASE ", new String[]{indicatorCode}, null, null, null, null);
            List<Hia2Indicator> hia2Indicators = readAllDataElements(cursor);
            if (hia2Indicators.size() == 1) {
                hia2Indicator = hia2Indicators.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hia2Indicator;
    }

    public Hia2Indicator findById(long id) {
        Cursor cursor = null;
        Hia2Indicator hia2Indicator = null;

        try {
            cursor = getReadableDatabase().query(HIA2_INDICATORS_TABLE_NAME, HIA2_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
            List<Hia2Indicator> hia2Indicators = readAllDataElements(cursor);
            if (hia2Indicators.size() == 1) {
                hia2Indicator = hia2Indicators.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hia2Indicator;
    }

    private List<Hia2Indicator> readAllDataElements(Cursor cursor) {
        List<Hia2Indicator> hia2Indicators = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Hia2Indicator hia2Indicator = new Hia2Indicator();
                    hia2Indicator.setId(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)));
                    hia2Indicator.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                    hia2Indicator.setIndicatorCode(cursor.getString(cursor.getColumnIndex(INDICATOR_CODE)));
                    hia2Indicators.add(hia2Indicator);

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        return hia2Indicators;

    }

    /**
     * order by id asc so that the indicators are ordered by category and indicator id
     *
     * @return
     */
    public List<Hia2Indicator> fetchAll() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(HIA2_INDICATORS_TABLE_NAME, HIA2_TABLE_COLUMNS, null, null, null, null, ID_COLUMN + " asc ");
        return readAllDataElements(cursor);
    }
}
