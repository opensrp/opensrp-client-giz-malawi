package org.smartregister.giz.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.giz.fragment.GizPncFormFragment;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.BaseRepository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class GizPncFormActivity extends BasePncFormActivity {

    GizPncFormFragment pncFormFragment;

    @Override
    protected void initializeFormFragmentCore() {
        pncFormFragment = new GizPncFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, JsonFormConstants.FIRST_STEP_NAME);
        pncFormFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, pncFormFragment).commit();
    }


    @Override
    public void init(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            if (PncConstants.EventTypeConstants.PNC_OUTCOME.equals(jsonObject.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
                addRepeatingGroupFields(jsonObject.getJSONObject("step4"));PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE.equals(jsonObject.optString(PncConstants.EventTypeConstants.PNC_OUTCOME));
            }
            super.init(jsonObject.toString());
        }
        catch (Exception ex) {
            Timber.e(ex);
            super.init(json);
        }
    }

    private void addRepeatingGroupFields(JSONObject step) {

        try {
            String motherBaseEntityId = getIntent().getStringExtra(PncDbConstants.KEY.BASE_ENTITY_ID);

            Set<String> possibleJsonArrayKeys = new HashSet<>();
            possibleJsonArrayKeys.add("first_name");
            possibleJsonArrayKeys.add("last_name");
            possibleJsonArrayKeys.add("gender");
            possibleJsonArrayKeys.add("dob");

            String query = "SELECT * FROM ec_client AS ec_client " +
                    "LEFT JOIN ec_child_details AS ec_child ON ec_child.relational_id = '" + motherBaseEntityId + "' " +
                    "WHERE ec_client.base_entity_id = ec_child.base_entity_id";

            ArrayList<HashMap<String, String>> childData = getData(query);

            JSONObject repeatingGroup = step.getJSONArray("fields").getJSONObject(1);
            int count = 0;

            for (HashMap<String, String> childMap : childData) {

                long dobInMills = getDate(childMap.get("dob")).getTime();
                long currentTimeMillis = System.currentTimeMillis();
                long diffInMillis = Math.abs(currentTimeMillis - dobInMills);
                long diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

                if (diff <= GizConstants.HOW_BABY_OLD_IN_DAYS) {
                    JSONObject jsonObject = PncUtils.getJsonFormToJsonObject("pnc_outcome_baby_born_template");
                    if (jsonObject != null) {
                        String jsonString = jsonObject.toString();

                        for (Map.Entry<String, String> entry : childMap.entrySet()) {

                            String value = entry.getValue();
                            value = value == null ? "" : value;

                            if (possibleJsonArrayKeys.contains(entry.getKey())) {
                                if (entry.getKey().equals("dob")) {
                                    Date dobDate = getDate(value);
                                    value = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dobDate);
                                }
                                jsonString = jsonString.replace("{" + entry.getKey() + "}", value);
                            }
                        }

                        jsonObject = new JSONObject(jsonString);
                        String randomBaseEntityId = JsonFormUtils.generateRandomUUIDString().replace("-","");
                        JSONArray fields = jsonObject.getJSONArray("fields");

                        for (int i = 0; i < fields.length(); i++) {
                            JSONObject field = fields.getJSONObject(i);
                            String key = field.getString("key");
                            String newKey = key + randomBaseEntityId;
                            jsonString = jsonString.replace("\"" + key + "\"", "\"" + newKey + "\"");
                            jsonString = jsonString.replace("\"step4:" + key + "\"", "\"step4:" + newKey + "\"");
                        }

                        jsonObject = new JSONObject(jsonString);
                        JSONArray fieldsArray = jsonObject.getJSONArray("fields");
                        processWithMandatoryChecks(fieldsArray, randomBaseEntityId);
                        for (int i = 0; i < fieldsArray.length(); i++) {
                            if (!fieldsArray.getJSONObject(i).getString("key").equals("child_registered_" + randomBaseEntityId)) {
                                step.getJSONArray("fields").put(fieldsArray.getJSONObject(i));
                            }
                        }
                    }
                    count += 1;
                }
                repeatingGroup.put("value", String.valueOf(count));
            }
        }
        catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    private void processWithMandatoryChecks(JSONArray fields, String baseEntityId) throws JSONException {

        // remove relevance
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);

            if (!field.getString("key").equals("baby_complications_other_" + baseEntityId)
                    && !field.getString("key").equals("baby_care_mgt_" + baseEntityId)
                    && !field.getString("key").equals("baby_care_mgt_specify_" + baseEntityId)
                    && !field.getString("key").equals("baby_referral_location_" + baseEntityId)
                    && !field.getString("key").equals("bf_first_hour_" + baseEntityId)
                    && !field.getString("key").equals("child_hiv_status_" + baseEntityId)
                    && !field.getString("key").equals("nvp_administration_" + baseEntityId)) {
                field.remove("relevance");
            }

            if (field.getString("key").equals("baby_gender_" + baseEntityId)) {
                field.put("type", "edit_text");
                field.put("hint", field.optString("label"));
                field.remove("options");
                field.remove("label");
                field.remove("label_text_style");
            }

            if (!field.getString("key").equals("baby_first_name_" + baseEntityId)
                    && !field.getString("key").equals("baby_last_name_" + baseEntityId)
                    && !field.getString("key").equals("baby_gender_" + baseEntityId)
                    && !field.getString("key").equals("baby_dob_" + baseEntityId)) {
                field.remove("value");
            }

            // make read only
            if (field.getString("key").equals("baby_first_name_" + baseEntityId)
                    || field.getString("key").equals("baby_last_name_" + baseEntityId)
                    || field.getString("key").equals("baby_gender_" + baseEntityId)
                    || field.getString("key").equals("baby_dob_" + baseEntityId)) {
                field.put("read_only","true");
            }
        }
    }

    private ArrayList<HashMap<String, String>> getData(String query) {
        BaseRepository repo = new BaseRepository();
        return repo.rawQuery(repo.getReadableDatabase(), query);
    }

    private boolean isValidJSONArray(String jsonString) {
        try {
            new JSONArray(jsonString);
            return true;
        }
        catch (JSONException ex) {
            return false;
        }
    }

    private Date getDate(@Nullable String dob) {
        Date date = null;
        if (StringUtils.isNotBlank(dob)) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
                date = dateFormat.parse(dob);
            } catch (ParseException e) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    date = dateFormat.parse(dob);
                } catch (ParseException pe) {
                    try {
                        date = DateUtil.parseDate(dob);
                    } catch (ParseException pee) {
                        Timber.e(e);
                    }
                }
            }
        }
        return date;
    }
}
