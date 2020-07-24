package org.smartregister.giz.configuration;


import android.support.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.configuration.AncMaternityTransferProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GizAncMaternityTransferProcessor extends AncMaternityTransferProcessor {

    @Override
    public String transferForm() {
        return "anc_maternity_transfer";
    }

    @Override
    public Map<String, String> columnMap() {
        HashMap<String, String> columnMap = new HashMap<>();
        columnMap.put("gravida", "gravidity");
        columnMap.put("miscarriages_abortions", "abortion_number");
        columnMap.put("gest_age", "ga_calculated");//? weeks ? days
        return columnMap;
    }

    @NotNull
    protected String[] previousContactKeys() {
        return new String[]{"gravida", "parity", "prev_preg_comps", "prev_preg_comps_other",
                "miscarriages_abortions", "occupation", "occupation_other", "religion_other",
                "religion", "marital_status", "educ_level","gest_age"};
    }

    @Override
    protected void updateValue(@NonNull JSONObject object, @NonNull String value) {
        try {
            Object newValue = value;
            String key = object.optString(JsonFormConstants.KEY);
            String type = object.optString(JsonFormConstants.TYPE);

            if (key.equals("gest_age")) {
                newValue = value + " weeks";
            }

            if (JsonFormConstants.CHECK_BOX.equals(type) && !value.isEmpty()) {
                JSONArray jsonArray = new JSONArray(value);
                object.put(JsonFormConstants.VALUE, jsonArray);
            } else {
                object.put(JsonFormConstants.VALUE, newValue.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected List<String> getCloseFormFieldsList() {
        return new ArrayList<>(Arrays.asList("onset_labour_date", "onset_labour_time"));
    }
}
