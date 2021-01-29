package org.smartregister.giz.configuration;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.pnc.config.RepeatingGroupGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildStatusRepeatingGroupGenerator extends RepeatingGroupGenerator {

    public ChildStatusRepeatingGroupGenerator(@NonNull JSONObject step, @NonNull String stepName, @NonNull String repeatingGroupKey, @NonNull Map<String, String> columnMap, @NonNull String uniqueKeyField, @NonNull List<HashMap<String, String>> storedValues) {
        super(step, stepName, repeatingGroupKey, columnMap, uniqueKeyField, storedValues);
    }

    @Override
    public void updateField(JSONObject repeatingGrpField, Map<String, String> entryMap) throws JSONException {
        super.updateField(repeatingGrpField, entryMap);
        String key = repeatingGrpField.optString(JsonFormConstants.KEY);
        if ("child_name".equals(key)) {
            repeatingGrpField.put(JsonFormConstants.TEXT, String.format("%s %s", entryMap.get("first_name"), entryMap.get("last_name")));
        }
    }
}
