package org.smartregister.giz.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.giz.activity.GizJsonFormActivity;
import org.smartregister.giz.activity.HIA2ReportsActivity;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.MonthlyTally;
import org.smartregister.giz.repository.MonthlyTalliesRepository;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.util.FormUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-12-02
 */
public class StartDraftMonthlyFormTask extends AsyncTask<Void, Void, Intent> {
    private final HIA2ReportsActivity baseActivity;
    private final Date date;
    private final String formName;
    private final boolean firstTimeEdit;

    public StartDraftMonthlyFormTask(@NonNull HIA2ReportsActivity baseActivity,
                                     @NonNull Date date, @NonNull String formName, boolean firstTimeEdit) {
        this.baseActivity = baseActivity;
        this.date = date;
        this.formName = formName;
        this.firstTimeEdit = firstTimeEdit;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        baseActivity.showProgressDialog();
    }

    @Override
    protected Intent doInBackground(Void... params) {
        try {
            MonthlyTalliesRepository monthlyTalliesRepository = GizMalawiApplication.getInstance().monthlyTalliesRepository();
            List<MonthlyTally> monthlyTallies = monthlyTalliesRepository.findDrafts(MonthlyTalliesRepository.DF_YYYYMM.format(date));

            if (monthlyTallies.size() < 1) {
                return null;
            }

            JSONObject form;
            try {
                form = FormUtils.getInstance(baseActivity).getFormJson(formName);
            } catch (Exception e) {
                Timber.e(e);
                return null;
            }

            JSONObject step1 = form.getJSONObject("step1");
            String title = MonthlyTalliesRepository.DF_YYYYMM.format(date).concat(" Draft");
            step1.put(GizConstants.KEY.TITLE, title);

            JSONArray fieldsArray = step1.getJSONArray("fields");

            // Sort the indicators for better readability from the JSON FORM
            Collections.sort(monthlyTallies, new Comparator<MonthlyTally>() {
                @Override
                public int compare(MonthlyTally o1, MonthlyTally o2) {
                    return o1.getIndicator().compareTo(o2.getIndicator());
                }
            });

            // This map holds each category as key and all the fields for that category as the
            // value (jsonarray)
            for (MonthlyTally monthlyTally : monthlyTallies) {
                JSONObject jsonObject = new JSONObject();

                int resourceId = baseActivity.getResources().getIdentifier(monthlyTally.getIndicator().toLowerCase(), "string", baseActivity.getPackageName());
                String label = resourceId != 0 ? baseActivity.getResources().getString(resourceId) : monthlyTally.getIndicator();

                JSONObject vRequired = new JSONObject();
                vRequired.put(JsonFormConstants.VALUE, "true");
                vRequired.put(JsonFormConstants.ERR, "Specify: " + monthlyTally.getIndicator());
                JSONObject vNumeric = new JSONObject();
                vNumeric.put(JsonFormConstants.VALUE, "true");
                vNumeric.put(JsonFormConstants.ERR, "Value should be numeric");

                jsonObject.put(JsonFormConstants.KEY, monthlyTally.getIndicator());
                jsonObject.put(JsonFormConstants.TYPE, "edit_text");
                //jsonObject.put(JsonFormConstants.READ_ONLY, readOnlyList.contains(monthlyTally.getIndicatorCode()));
                jsonObject.put(JsonFormConstants.READ_ONLY, false);
                jsonObject.put(JsonFormConstants.HINT, label);
                jsonObject.put(JsonFormConstants.VALUE, monthlyTally.getValue());

                    /*if (DailyTalliesRepository.IGNORED_INDICATOR_CODES
                            .contains(monthlyTally.getIndicatorCode()) && firstTimeEdit) {
                        jsonObject.put(JsonFormConstants.VALUE, "");
                    }*/
                jsonObject.put(JsonFormConstants.V_REQUIRED, vRequired);
                jsonObject.put(JsonFormConstants.V_NUMERIC, vNumeric);
                jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "");
                jsonObject.put(JsonFormConstants.OPENMRS_ENTITY, "");
                jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, "");
                jsonObject.put(GizConstants.KEY.HIA_2_INDICATOR, monthlyTally.getIndicator());

                fieldsArray.put(jsonObject);
            }

            // Add the confirm button
            JSONObject buttonObject = new JSONObject();
            buttonObject.put(JsonFormConstants.KEY, HIA2ReportsActivity.FORM_KEY_CONFIRM);
            buttonObject.put(JsonFormConstants.VALUE, "false");
            buttonObject.put(JsonFormConstants.TYPE, "button");
            buttonObject.put(JsonFormConstants.HINT, "Confirm");
            buttonObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "");
            buttonObject.put(JsonFormConstants.OPENMRS_ENTITY, "");
            buttonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, "");
            JSONObject action = new JSONObject();
            action.put(JsonFormConstants.BEHAVIOUR, "finish_form");
            buttonObject.put(JsonFormConstants.ACTION, action);

            fieldsArray.put(buttonObject);

            form.put(JsonFormConstants.REPORT_MONTH, HIA2ReportsActivity.dfyymmdd.format(date));
            form.put("identifier", "HIA2ReportForm");

            Intent intent = new Intent(baseActivity, GizJsonFormActivity.class);
            intent.putExtra("json", form.toString());
            intent.putExtra(JsonFormConstants.SKIP_VALIDATION, false);

            return intent;
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Intent intent) {
        super.onPostExecute(intent);
        baseActivity.hideProgressDialog();

        if (intent != null) {
            baseActivity.startActivityForResult(intent, HIA2ReportsActivity.REQUEST_CODE_GET_JSON);
        }
    }
}
