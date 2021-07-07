package org.smartregister.giz.util;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.db.EventClient;
import org.smartregister.giz.R;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.model.Visit;
import org.smartregister.giz.model.VisitDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

import static org.smartregister.opd.utils.OpdUtils.context;

public class VisitUtils {
    private static String[] default_obs = {"start", "end", "deviceid", "subscriberid", "simserial", "phonenumber"};
    private static final SimpleDateFormat sdf = new SimpleDateFormat(GizConstants.DateTimeFormat.yyyy_MM_dd, Locale.US);


    public static Visit eventToVisit(org.smartregister.domain.Event event) {
        List<String> exceptions = Arrays.asList(default_obs);

        Visit visit = new Visit();
        visit.setVisitId(event.getFormSubmissionId());
        visit.setBaseEntityId(event.getBaseEntityId());
        visit.setDate(event.getEventDate().toDate());
        visit.setVisitType(event.getEventType());
        visit.setEventId(event.getEventId());
        visit.setFormSubmissionId(event.getFormSubmissionId());
        visit.setChildLocationId(event.getChildLocationId());
        visit.setLocationId(event.getLocationId());
        visit.setCreatedAt(event.getDateCreated() != null ? event.getDateCreated().toDate() : new Date());
        visit.setUpdatedAt(event.getDateEdited() != null ? event.getDateEdited().toDate() : visit.getCreatedAt());
        visit.setVisitGroup(sdf.format(event.getEventDate().toDate()));

        Map<String, List<VisitDetail>> details = new HashMap<>();
        if (event.getObs() != null) {
            for (org.smartregister.domain.Obs obs : event.getObs()) {
                if (!exceptions.contains(obs.getFormSubmissionField())) {
                    VisitDetail detail = new VisitDetail();
                    detail.setVisitDetailsId(UUID.randomUUID().toString());
                    detail.setVisitId(visit.getVisitId());
                    detail.setVisitKey(obs.getFormSubmissionField());
                    detail.setParentCode(obs.getParentCode());
                    detail.setDetails(getDetailsValue(detail, obs.getValues().toString()));
                    detail.setHumanReadable(getDetailsValue(detail, obs.getHumanReadableValues().toString()));
                    detail.setCreatedAt(event.getDateCreated() != null ? event.getDateCreated().toDate() : new Date());
                    detail.setUpdatedAt(event.getDateEdited() != null ? event.getDateEdited().toDate() : detail.getCreatedAt());

                    List<VisitDetail> currentList = details.get(detail.getVisitKey());
                    if (currentList == null)
                        currentList = new ArrayList<>();

                    currentList.add(detail);
                    details.put(detail.getVisitKey(), currentList);
                }
            }
        }

        visit.setVisitDetails(details);
        return visit;
    }

    public static void processVisit(EventClient baseEvent) {
        try {
            // delete existing events from the database
            GizMalawiApplication.getInstance().visitRepository().deleteVisit(baseEvent.getEvent().getFormSubmissionId());

            Visit visit = eventToVisit(baseEvent.getEvent());

            GizMalawiApplication.getInstance().visitRepository().addVisit(visit);
            if (visit.getVisitDetails() != null) {
                for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
                    if (entry.getValue() != null) {
                        for (VisitDetail detail : entry.getValue()) {
                            GizMalawiApplication.getInstance().visitDetailsRepository().addVisitDetails(detail);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static String getDetailsValue(VisitDetail detail, String val) {
        String clean_val = cleanString(val);
        if (detail.getVisitKey().contains("date")) {
            return getFormattedDate(getSourceDateFormat(), getSaveDateFormat(), clean_val);
        }

        return clean_val;
    }

    public static String getFormattedDate(SimpleDateFormat source_sdf, SimpleDateFormat dest_sdf, String value) {
        try {
            Date date = source_sdf.parse(value);
            return dest_sdf.format(date);
        } catch (Exception e) {
            try {
                // fallback for long datetypes
                Date date = new Date(Long.parseLong(value));
                return dest_sdf.format(date);
            } catch (NumberFormatException | NullPointerException nfe) {
                Timber.e(e);
            }
            Timber.e(e);
        }
        return value;
    }

    public static SimpleDateFormat getSourceDateFormat() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    }

    public static SimpleDateFormat getSaveDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    public static String cleanString(String dirtyString) {
        if (StringUtils.isBlank(dirtyString))
            return "";

        return dirtyString.substring(1, dirtyString.length() - 1);
    }

    public static String getTranslatedVisitTypeName(String name) {
        switch (name) {
            case GizConstants.OpdModuleEvents.OPD_CHECK_IN:
                return context().getStringResource(R.string.opd_check_in);
            case GizConstants.OpdModuleEvents.OPD_VITAL_DANGER_SIGNS_CHECK:
                return context().getStringResource(R.string.vital_danger_signs);
            case GizConstants.OpdModuleEvents.OPD_DIAGNOSIS:
                return context().getStringResource(R.string.opd_diagnosis);
            case GizConstants.OpdModuleEvents.OPD_TREATMENT:
                return context().getStringResource(R.string.opd_treatment);
            case GizConstants.OpdModuleEvents.OPD_LABORATORY:
                return context().getStringResource(R.string.lab_reports);
            case GizConstants.OpdModuleEvents.OPD_PHARMACY:
                return context().getStringResource(R.string.pharmacy);
            case GizConstants.OpdModuleEvents.OPD_FINAL_OUTCOME:
                return context().getStringResource(R.string.final_outcome);
            case GizConstants.OpdModuleEvents.OPD_SERVICE_CHARGE:
                return context().getStringResource(R.string.service_fee);
            default:
                throw new IllegalArgumentException("Unknown VisitType");
        }
    }

}
