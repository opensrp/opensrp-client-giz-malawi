package org.smartregister.giz.util;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.domain.Setting;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GizOpdMultiSelectListRepositoryHelper {

    public List<MultiSelectItem> fetchHelperData(Setting setting) {
        try {
            JSONObject jsonValObject = setting != null ? new JSONObject(setting.getValue()) : null;
            if (jsonValObject != null) {
                JSONArray jsonOptionsArray = jsonValObject.optJSONArray(AllConstants.SETTINGS);
                if (jsonOptionsArray != null) {
                    JSONArray jsonValuesArray = jsonOptionsArray.optJSONObject(0)
                            .optJSONArray(JsonFormConstants.VALUES);
                    if (jsonValuesArray != null) {
                        return processOptionsJsonArray(jsonValuesArray);
                    }
                }
            }
            return null;
        } catch (JSONException e) {
            Timber.e(e, "settings are null");
            return null;
        }
    }

    public List<MultiSelectItem> processOptionsJsonArray(@NonNull JSONArray jsonArray) throws JSONException {
        List<MultiSelectItem> multiSelectItems = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonDataObject = jsonArray.getJSONObject(i);
            String entityId = jsonDataObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);

            if (StringUtils.isEmpty(entityId.trim()))
                entityId = jsonDataObject.getString(JsonFormConstants.KEY);


            multiSelectItems.add(
                    new MultiSelectItem(
                            jsonDataObject.getString(JsonFormConstants.KEY),
                            jsonDataObject.getString(JsonFormConstants.TEXT),
                            jsonDataObject.has(JsonFormConstants.MultiSelectUtils.PROPERTY) ? jsonDataObject.getJSONObject(JsonFormConstants.MultiSelectUtils.PROPERTY).toString() : null,
                            jsonDataObject.optString(JsonFormConstants.OPENMRS_ENTITY),
                            entityId,
                            jsonDataObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT)
                    )
            );
        }
        return multiSelectItems;
    }
}