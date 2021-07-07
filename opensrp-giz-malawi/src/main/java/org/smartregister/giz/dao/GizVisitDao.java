package org.smartregister.giz.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.dao.AbstractDao;
import org.smartregister.giz.R;
import org.smartregister.giz.domain.ProfileAction;
import org.smartregister.giz.domain.ProfileHistory;
import org.smartregister.giz.util.GizConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.smartregister.opd.utils.OpdUtils.context;

public class GizVisitDao extends AbstractDao {

    /***
     * Returns a map of all the visits completed today.
     *
     * @return
     */
    public static Map<String, List<ProfileAction.ProfileActionVisit>> getVisitsToday(String baseEntityId) {
        String todayDate = new SimpleDateFormat(GizConstants.DateTimeFormat.yyyy_MM_dd, Locale.US).format(new Date());

        String sql = "SELECT * FROM opd_client_visits where visit_group = '"
                + todayDate +
                "' and base_entity_id = '" + baseEntityId + "' order by created_at desc , updated_at desc";
        Map<String, List<ProfileAction.ProfileActionVisit>> visitMap = new HashMap<>();

        SimpleDateFormat sdfTime = new SimpleDateFormat(GizConstants.DateTimeFormat.hh_mm, Locale.US);

        DataMap<Void> dataMap = cursor -> {
            String visitType = getCursorValue(cursor, "visit_type");
            List<ProfileAction.ProfileActionVisit> visits = visitMap.get(visitType);
            if (visits == null) visits = new ArrayList<>();

            ProfileAction.ProfileActionVisit actionVisit = new ProfileAction.ProfileActionVisit();
            actionVisit.setVisitID(getCursorValue(cursor, "visit_id"));

            Date visitCreateDate = new Date(Long.parseLong(getCursorValue(cursor, "created_at")));
            actionVisit.setVisitTime(sdfTime.format(visitCreateDate));

            visits.add(actionVisit);
            visitMap.put(visitType, visits);

            return null;
        };

        readData(sql, dataMap);

        return visitMap;
    }


    public static Boolean getSeenToday(String baseEntityId) {
        String todayDate = new SimpleDateFormat(GizConstants.DateTimeFormat.yyyy_MM_dd, Locale.US).format(new Date());

        String sql = "SELECT * FROM opd_client_visits where visit_group = '"
                + todayDate +
                "' and base_entity_id = '" + baseEntityId + "' order by created_at desc , updated_at desc";
        Map<String, List<ProfileAction.ProfileActionVisit>> visitMap = new HashMap<>();

        SimpleDateFormat sdfTime = new SimpleDateFormat(GizConstants.DateTimeFormat.hh_mm, Locale.US);

        DataMap<Void> dataMap = cursor -> {
            String visitType = getCursorValue(cursor, "visit_type");
            List<ProfileAction.ProfileActionVisit> visits = visitMap.get(visitType);
            if (visits == null) visits = new ArrayList<>();
            visitMap.put(visitType, visits);

            return null;
        };

        readData(sql, dataMap);

        return visitMap.size() > 0;
    }

    public static JSONObject fetchEventAsJson(String formSubmissionId) throws JSONException {
        String sql = "SELECT json FROM Event where formSubmissionId = '" + formSubmissionId + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "json");
        return new JSONObject(readSingleValue(sql, dataMap));
    }

    public static String fetchEventByFormSubmissionId(String formSubmissionId) {
        String sql = "SELECT json FROM Event where formSubmissionId = '" + formSubmissionId + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "json");
        return readSingleValue(sql, dataMap);
    }

    public static List<ProfileHistory> getVisitHistory(String baseEntityId) {

        SimpleDateFormat sdfDate = new SimpleDateFormat(GizConstants.DateTimeFormat.dd_MMM_yyyy, Locale.US);
        String todayDate = sdfDate.format(new Date());
        SimpleDateFormat sdfTime = new SimpleDateFormat(GizConstants.DateTimeFormat.hh_mm, Locale.US);

        String sql = "SELECT * FROM opd_client_visits where base_entity_id = '" + baseEntityId + "' order by created_at desc , updated_at desc";

        DataMap<ProfileHistory> dataMap = cursor -> {

            ProfileHistory history = new ProfileHistory();

            Date visitCreateDate = new Date(Long.parseLong(getCursorValue(cursor, "created_at")));
            history.setID(getCursorValue(cursor, "form_submission_id"));
            String date = sdfDate.format(visitCreateDate);
            history.setEventDate(date.equals(todayDate) ? context().getStringResource(R.string.today) : date);
            history.setEventTime(sdfTime.format(visitCreateDate));
            history.setEventType(getCursorValue(cursor, "visit_type"));

            return history;
        };

        return readData(sql, dataMap);
    }
}
