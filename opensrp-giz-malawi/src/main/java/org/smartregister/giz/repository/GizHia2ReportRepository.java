package org.smartregister.giz.repository;

import android.content.ContentValues;

import org.json.JSONObject;
import org.smartregister.domain.db.Column;
import org.smartregister.domain.db.ColumnAttribute;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Hia2ReportRepository;

import java.util.Date;

import timber.log.Timber;

public class GizHia2ReportRepository extends Hia2ReportRepository {

    public enum report_column implements Column {
        creator(ColumnAttribute.Type.text, false, false),
        dateCreated(ColumnAttribute.Type.date, false, true),
        editor(ColumnAttribute.Type.text, false, false),
        dateEdited(ColumnAttribute.Type.date, false, false),
        voided(ColumnAttribute.Type.bool, false, false),
        dateVoided(ColumnAttribute.Type.date, false, false),
        voider(ColumnAttribute.Type.text, false, false),
        voidReason(ColumnAttribute.Type.text, false, false),

        reportId(ColumnAttribute.Type.text, true, true),
        syncStatus(ColumnAttribute.Type.text, false, true),
        validationStatus(ColumnAttribute.Type.text, false, true),
        json(ColumnAttribute.Type.text, false, false),
        locationId(ColumnAttribute.Type.text, false, false),
        childLocationId(ColumnAttribute.Type.text, false, false),
        reportDate(ColumnAttribute.Type.date, false, true),
        reportType(ColumnAttribute.Type.text, false, true),
        formSubmissionId(ColumnAttribute.Type.text, false, false),
        providerId(ColumnAttribute.Type.text, false, false),
        entityType(ColumnAttribute.Type.text, false, false),
        version(ColumnAttribute.Type.text, false, false),
        updatedAt(ColumnAttribute.Type.date, false, true),
        serverVersion(ColumnAttribute.Type.longnum, false, true),
        grouping(ColumnAttribute.Type.text, false, false);

        private ColumnAttribute column;

        report_column(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        public ColumnAttribute column() {
            return column;
        }
    }

    @Override
    public void addReport(JSONObject jsonObject) {
        try {

            ContentValues values = new ContentValues();
            values.put(report_column.json.name(), jsonObject.toString());
            values.put(report_column.reportType.name(),
                    jsonObject.has(report_column.reportType.name()) ? jsonObject.getString(
                            report_column.reportType.name()) : "");
            values.put(report_column.updatedAt.name(), dateFormat.format(new Date()));
            values.put(report_column.reportDate.name(),
                    jsonObject.optString(report_column.reportDate.name()
                    ));
            values.put(report_column.providerId.name(),
                    jsonObject.optString(report_column.providerId.name()
                    ));
            values.put(report_column.dateCreated.name(),
                    jsonObject.optString(report_column.dateCreated.name()
                    ));
            values.put(report_column.grouping.name(),
                    jsonObject.optString(report_column.grouping.name()
                    ));

            values.put(report_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);
            //update existing event if eventid present
            if (jsonObject.has(report_column.formSubmissionId.name())
                    && jsonObject.getString(report_column.formSubmissionId.name()) != null) {
                //sanity check
                if (checkIfExistsByFormSubmissionId(Table.hia2_report,
                        jsonObject.getString(report_column
                                .formSubmissionId
                                .name()))) {
                    getWritableDatabase().update(Table.hia2_report.name(),
                            values,
                            report_column.formSubmissionId.name() + "=?",
                            new String[]{jsonObject.getString(
                                    report_column.formSubmissionId.name())});
                } else {
                    //that odd case
                    values.put(report_column.formSubmissionId.name(),
                            jsonObject.getString(report_column.formSubmissionId.name()));

                    getWritableDatabase().insert(Table.hia2_report.name(), null, values);

                }
            } else {
// a case here would be if an event comes from openmrs
                getWritableDatabase().insert(Table.hia2_report.name(), null, values);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
