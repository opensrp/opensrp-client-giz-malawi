package org.smartregister.giz.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.maternity.configuration.MaternityOutcomeFormProcessingTask;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.maternity.utils.MaternityJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.gson;

public class GizMaternityOutcomeFormProcessing extends MaternityOutcomeFormProcessingTask {

    private JSONObject repeatingGrpChildObject;

    private JSONObject repeatingGrpStillBirthObject;

    @NonNull
    public String childRegistrationEvent() {
        return MaternityConstants.EventType.BIRTH_REGISTRATION;
    }

    @NonNull
    public String getChildFormName() {
        return "child_enrollment";
    }

    @NonNull
    public String childOpensrpId() {
        return MaternityConstants.JSON_FORM_KEY.ZEIR_ID;
    }

    @NonNull
    public HashMap<String, String> childFormKeyToKeyMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("first_name", "baby_first_name");
        hashMap.put("last_name", "baby_last_name");
        hashMap.put("Date_Birth", "baby_dob");
        hashMap.put("Sex", "baby_gender");
        hashMap.put("Birth_Height", "birth_height_entered");
        hashMap.put("Birth_Weight", "birth_weight_entered");
        return hashMap;
    }

    @NonNull
    public HashMap<String, String> childKeyToColumnMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mother_guardian_first_name", "first_name");
        hashMap.put("mother_guardian_last_name", "last_name");
        hashMap.put("mother_guardian_date_birth", "dob");
        hashMap.put("village", "village");
        hashMap.put("home_address", "home_address");
        return hashMap;
    }

    @Override
    public List<Event> processMaternityForm(@NonNull String jsonString, @Nullable Intent data) throws JSONException {
        List<Event> eventList = new ArrayList<>();

        JSONObject jsonFormObject = new JSONObject(jsonString);

        String baseEntityId = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID);

        jsonFormObject.put("entity_id", baseEntityId);

        JSONArray fieldsArray = MaternityUtils.generateFieldsFromJsonForm(jsonFormObject);

        String steps = jsonFormObject.optString(JsonFormConstants.COUNT);

        int numOfSteps = Integer.parseInt(steps);

        for (int j = 0; j < numOfSteps; j++) {

            JSONObject step = jsonFormObject.optJSONObject(JsonFormConstants.STEP.concat(String.valueOf(j + 1)));

            String title = step.optString(JsonFormConstants.STEP_TITLE);

            if (MaternityConstants.JSON_FORM_STEP_NAME.BABIES_BORN.equals(title)) {

                HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn = MaternityUtils.buildRepeatingGroupValues(step, MaternityConstants.JSON_FORM_KEY.BABIES_BORN);

                if (!buildRepeatingGroupBorn.isEmpty()) {
                    if (StringUtils.isNotBlank(baseEntityId)) {

                        String[] ids = MaternityUtils.generateNIds(buildRepeatingGroupBorn.size());
                        int count = 0;
                        for (Map.Entry<String, HashMap<String, String>> entrySet : buildRepeatingGroupBorn.entrySet()) {
                            entrySet.getValue().put(MaternityDbConstants.Column.MaternityChild.BASE_ENTITY_ID, ids[count]);
                            count++;
                        }

                        createChildClients(jsonFormObject, baseEntityId, buildRepeatingGroupBorn);
                    }
                    String strGroup = gson.toJson(buildRepeatingGroupBorn);
                    JSONObject repeatingGroupObj = new JSONObject();
                    repeatingGroupObj.put(JsonFormConstants.KEY, MaternityConstants.JSON_FORM_KEY.BABIES_BORN_MAP);
                    repeatingGroupObj.put(JsonFormConstants.VALUE, strGroup);
                    repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                    fieldsArray.put(repeatingGroupObj);
                    setRepeatingGrpChildObject(repeatingGroupObj);
                }
            } else if (MaternityConstants.JSON_FORM_STEP_NAME.STILL_BORN_BABIES.equals(title)) {

                HashMap<String, HashMap<String, String>> buildRepeatingGroupStillBorn = MaternityUtils.buildRepeatingGroupValues(step, MaternityConstants.JSON_FORM_KEY.BABIES_STILLBORN);

                if (!buildRepeatingGroupStillBorn.isEmpty()) {
                    String strGroup = gson.toJson(buildRepeatingGroupStillBorn);
                    JSONObject repeatingGroupObj = new JSONObject();
                    repeatingGroupObj.put(JsonFormConstants.KEY, MaternityConstants.JSON_FORM_KEY.BABIES_STILL_BORN_MAP);
                    repeatingGroupObj.put(JsonFormConstants.VALUE, strGroup);
                    repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                    fieldsArray.put(repeatingGroupObj);
                    setRepeatingGrpStillBirthObject(repeatingGroupObj);
                }
            }
        }

        FormTag formTag = MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences());

        Event maternityOutcomeEvent = MaternityJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, MaternityConstants.EventType.MATERNITY_OUTCOME, "");
        MaternityJsonFormUtils.tagSyncMetadata(maternityOutcomeEvent);
        eventList.add(maternityOutcomeEvent);

        Event closeMaternityEvent = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                formTag, baseEntityId, MaternityConstants.EventType.MATERNITY_CLOSE, "");
        MaternityJsonFormUtils.tagSyncMetadata(closeMaternityEvent);
        closeMaternityEvent.addDetails(MaternityConstants.JSON_FORM_KEY.VISIT_END_DATE, MaternityUtils.convertDate(new Date(), MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
        eventList.add(closeMaternityEvent);

        ///add code for Maternity - to PNC transfer
        createMaternityPncTransferEvent(jsonFormObject);
        return eventList;
    }

    private void createMaternityPncTransferEvent(JSONObject maternityForm) {
        try {
            maternityForm.optJSONObject(JsonFormConstants.STEP1).optJSONArray(JsonFormConstants.FIELDS)
                    .put(getRepeatingGrpChildObject()).put(getRepeatingGrpStillBirthObject());
            new GizMaternityPncTransferProcessor().startTransferProcessing(maternityForm);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public JSONObject getRepeatingGrpChildObject() {
        return repeatingGrpChildObject;
    }

    public void setRepeatingGrpChildObject(JSONObject repeatingGrpChildObject) {
        this.repeatingGrpChildObject = repeatingGrpChildObject;
    }

    public JSONObject getRepeatingGrpStillBirthObject() {
        return repeatingGrpStillBirthObject;
    }

    public void setRepeatingGrpStillBirthObject(JSONObject repeatingGrpStillBirthObject) {
        this.repeatingGrpStillBirthObject = repeatingGrpStillBirthObject;
    }
}
