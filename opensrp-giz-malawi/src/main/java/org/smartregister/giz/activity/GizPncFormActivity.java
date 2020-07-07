package org.smartregister.giz.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.giz.fragment.GizPncFormFragment;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.HashMap;

public class GizPncFormActivity extends BasePncFormActivity {

    @Override
    protected void initializeFormFragmentCore() {
        GizPncFormFragment pncFormFragment = new GizPncFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, JsonFormConstants.FIRST_STEP_NAME);
        pncFormFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, pncFormFragment).commit();
    }

    public void init() {
        /*if ("step4".equals(stepName)) {

            JSONObject step = super.getStep(stepName);

            try {
                String motherBaseEntityId = getActivity().getIntent().getStringExtra(PncDbConstants.KEY.BASE_ENTITY_ID);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(step.getJSONArray("fields").getJSONObject(1));

                Set<String> possibleJsonArrayKeys = new HashSet<>();
                possibleJsonArrayKeys.add("complications");
                possibleJsonArrayKeys.add("care_mgt");
                possibleJsonArrayKeys.add("child_hiv_status");

                String query = "SELECT * FROM ec_client AS ec_client " +
                        "LEFT JOIN ec_child_details AS ec_child ON ec_child.relational_id = '" + motherBaseEntityId + "' " +
                        "WHERE ec_client.base_entity_id = ec_child.base_entity_id";

                ArrayList<HashMap<String, String>> childData = getData(query);

                for (HashMap<String, String> childMap : childData) {
                    JSONObject jsonObject = PncUtils.getJsonFormToJsonObject("pnc_outcome_baby_born_template");
                    if (jsonObject != null) {
                        String jsonString = jsonObject.toString();

                        for (Map.Entry<String, String> entry : childMap.entrySet()) {

                            String value = entry.getValue();
                            value = value == null ? "" : value;

                            if (possibleJsonArrayKeys.contains(entry.getKey()) && isValidJSONArray(value)) {
                                jsonString = jsonString.replace("\"{" + entry.getKey() + "}\"", value);
                            }
                            else {
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
                            jsonString = jsonString.replace(key, newKey);
                        }

                        jsonObject = new JSONObject(jsonString);
                        JSONArray fieldsArray = jsonObject.getJSONArray("fields");
                        for (int i = 0; i < fieldsArray.length(); i++) {
                            step.getJSONArray("fields").put(fieldsArray.getJSONObject(i));
                        }
                    }
                }

                return step;
            }
            catch (JSONException ex) {
                Timber.e(ex);
                return step;
            }
        }
        else {
            return super.getStep(stepName);
        }*/
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
}
