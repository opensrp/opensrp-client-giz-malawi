package org.smartregister.giz.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.giz.util.DbConstants;

public class GizMonthlyTalliesRepository extends MonthlyTalliesRepository {
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "(" +
            DbConstants.Table.MonthlyTalliesRepository.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            DbConstants.Table.MonthlyTalliesRepository.INDICATOR_CODE + " VARCHAR NOT NULL," +
            DbConstants.Table.MonthlyTalliesRepository.PROVIDER_ID + " VARCHAR NOT NULL," +
            DbConstants.Table.MonthlyTalliesRepository.VALUE + " VARCHAR NOT NULL," +
            DbConstants.Table.MonthlyTalliesRepository.MONTH + " VARCHAR NOT NULL," +
            DbConstants.Table.MonthlyTalliesRepository.EDITED + " INTEGER NOT NULL DEFAULT 0," +
            DbConstants.Table.MonthlyTalliesRepository.INDICATOR_GROUPING + " TEXT," +
            DbConstants.Table.MonthlyTalliesRepository.DATE_SENT + " DATETIME NULL," +
            DbConstants.Table.MonthlyTalliesRepository.CREATED_AT + " DATETIME NULL," +
            DbConstants.Table.MonthlyTalliesRepository.UPDATED_AT + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, UNIQUE (" + DbConstants.Table.MonthlyTalliesRepository.INDICATOR_CODE + "," + DbConstants.Table.MonthlyTalliesRepository.MONTH + ") ON CONFLICT REPLACE)";
    private static final String INDEX_PROVIDER_ID = "CREATE INDEX " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "_" + DbConstants.Table.MonthlyTalliesRepository.PROVIDER_ID + "_index" +
            " ON " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "(" + DbConstants.Table.MonthlyTalliesRepository.PROVIDER_ID + " COLLATE NOCASE);";
    private static final String INDEX_INDICATOR_ID = "CREATE INDEX " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "_" + DbConstants.Table.MonthlyTalliesRepository.INDICATOR_CODE + "_index" +
            " ON " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "(" + DbConstants.Table.MonthlyTalliesRepository.INDICATOR_CODE + " COLLATE NOCASE);";
    private static final String INDEX_UPDATED_AT = "CREATE INDEX " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "_" + DbConstants.Table.MonthlyTalliesRepository.UPDATED_AT + "_index" +
            " ON " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "(" + DbConstants.Table.MonthlyTalliesRepository.UPDATED_AT + ");";
    private static final String INDEX_MONTH = "CREATE INDEX " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "_" + DbConstants.Table.MonthlyTalliesRepository.MONTH + "_index" +
            " ON " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "(" + DbConstants.Table.MonthlyTalliesRepository.MONTH + ");";
    private static final String INDEX_EDITED = "CREATE INDEX " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "_" + DbConstants.Table.MonthlyTalliesRepository.EDITED + "_index" +
            " ON " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "(" + DbConstants.Table.MonthlyTalliesRepository.EDITED + ");";
    private static final String INDEX_DATE_SENT = "CREATE INDEX " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "_" + DbConstants.Table.MonthlyTalliesRepository.DATE_SENT + "_index" +
            " ON " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "(" + DbConstants.Table.MonthlyTalliesRepository.DATE_SENT + ");";

    public static final String INDEX_UNIQUE = "CREATE UNIQUE INDEX " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "_" + DbConstants.Table.MonthlyTalliesRepository.INDICATOR_CODE + "_" + DbConstants.Table.MonthlyTalliesRepository.MONTH + "_index" +
            " ON " + DbConstants.Table.MonthlyTalliesRepository.TABLE_NAME + "(" + DbConstants.Table.MonthlyTalliesRepository.INDICATOR_CODE + "," + DbConstants.Table.MonthlyTalliesRepository.MONTH + ");";

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
}
