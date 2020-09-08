package org.smartregister.giz.util;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.Report;
import org.smartregister.giz.domain.ReportHia2Indicator;
import org.smartregister.opd.utils.OpdJsonFormUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-12-02
 */

public class GizReportUtils {

    public static void createReportAndSaveReport(@NonNull List<ReportHia2Indicator> hia2Indicators, @NonNull Date month,
                                                 @NonNull String reportType, @NonNull String grouping, DateTime dateSent) {
        try {
            String providerId = GizMalawiApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM();
            String locationId = GizMalawiApplication.getInstance().context().allSharedPreferences().getPreference(Constants.CURRENT_LOCATION_ID);
            Report report = new Report();
            report.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
            report.setHia2Indicators(hia2Indicators);
            report.setLocationId(locationId);
            report.setProviderId(providerId);
            report.setDateCreated(dateSent);
            report.setGrouping(grouping);
            // Get the second last day of the month
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(month);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 2);

            report.setReportDate(new DateTime(calendar.getTime()));
            report.setReportType(reportType);
            JSONObject reportJson = new JSONObject(JsonFormUtils.gson.toJson(report));
            GizMalawiApplication.getInstance().hia2ReportRepository().addReport(reportJson);

            createReportAndProcessEvent(reportJson);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void createReportAndProcessEvent(@NonNull JSONObject reportJson) throws Exception {
        FormTag formTag = GizJsonFormUtils.formTag(GizUtils.getAllSharedPreferences());
        Event baseEvent = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                formTag, "", GizConstants.EventType.REPORT_CREATION, "");
        baseEvent.addDetails("reportJson", reportJson.toString());
        baseEvent.setFormSubmissionId(reportJson.optString("formSubmissionId"));
        GizJsonFormUtils.tagEventSyncMetadata(baseEvent);

        JSONObject eventJson = new JSONObject(OpdJsonFormUtils.gson.toJson(baseEvent));

        GizMalawiApplication.getInstance().getEcSyncHelper().
                addEvent(baseEvent.getBaseEntityId(), eventJson);

        long lastSyncTimeStamp = Utils.getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        GizMalawiApplication.getInstance().getClientProcessor()
                .processClient(GizMalawiApplication.getInstance().getEcSyncHelper().getEvents(Arrays.asList(reportJson.optString("formSubmissionId"))));
        GizUtils.getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
    }

    @NonNull
    public static String getStringIdentifier(@NonNull String identifierCode) {
        return identifierCode
                .toLowerCase()
                .replace(" ", "_")
                .replace("/", "");
    }
}
