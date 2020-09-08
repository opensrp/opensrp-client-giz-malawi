package org.smartregister.giz.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.interactor.ChildRegisterInteractor;
import org.smartregister.child.util.Constants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.SyncStatus;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.giz.util.GizJsonFormUtils;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.configuration.MaternityOutcomeFormProcessingTask;
import org.smartregister.maternity.pojo.MaternityEventClient;
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

    private ChildRegisterInteractor interactor;

    private HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn;

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
        hashMap.put("village", "home_address");
        hashMap.put("home_address", "village");
        hashMap.put("mother_hiv_status", "mother_hiv_status");
        hashMap.put("mother_tdv_doses", "mother_tdv_doses");
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
        GizJsonFormUtils.tagEventSyncMetadata(maternityOutcomeEvent);
        eventList.add(maternityOutcomeEvent);

        Event closeMaternityEvent = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                formTag, baseEntityId, MaternityConstants.EventType.MATERNITY_CLOSE, "");
        GizJsonFormUtils.tagEventSyncMetadata(closeMaternityEvent);
        closeMaternityEvent.addDetails(MaternityConstants.JSON_FORM_KEY.VISIT_END_DATE, MaternityUtils.convertDate(new Date(), MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
        eventList.add(closeMaternityEvent);

        ///add code for Maternity - to PNC transfer
        createMaternityPncTransferEvent(jsonFormObject);
        return eventList;
    }

    @Override
    public void createChildClients(@NonNull JSONObject jsonFormObject, @NonNull String baseEntityId, @NonNull HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn) {
        setBuildRepeatingGroupBorn(buildRepeatingGroupBorn);
        super.createChildClients(jsonFormObject, baseEntityId, buildRepeatingGroupBorn);
    }

    @Override
    protected void saveMaternityChild(List<MaternityEventClient> maternityEventClients) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();
            for (MaternityEventClient eventClient : maternityEventClients) {
                try {
                    Client baseClient = eventClient.getClient();
                    Event baseEvent = eventClient.getEvent();
                    JSONObject clientJson = new JSONObject(MaternityJsonFormUtils.gson.toJson(baseClient));
                    MaternityLibrary.getInstance().getEcSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                    JSONObject eventJson = new JSONObject(MaternityJsonFormUtils.gson.toJson(baseEvent));
                    MaternityLibrary.getInstance().getEcSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
                    currentFormSubmissionIds.add(baseEvent.getFormSubmissionId());
                    createGrowthEvents(eventClient, clientJson);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
            long lastSyncTimeStamp = GizUtils.getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            MaternityLibrary.getInstance().getClientProcessorForJava().processClient(MaternityLibrary.getInstance().getEcSyncHelper().getEvents(currentFormSubmissionIds));
            GizUtils.getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void createGrowthEvents(@NonNull MaternityEventClient eventClient, @NonNull JSONObject clientJson) throws JSONException {
        Client client = eventClient.getClient();
        UpdateRegisterParams params = new UpdateRegisterParams();
        params.setStatus(SyncStatus.PENDING.value());
        JSONObject tempForm = new JSONObject();
        JSONObject tempStep = new JSONObject();
        tempForm.put(JsonFormConstants.STEP1, tempStep);
        String height = "";
        String weight = "";
        for (Map.Entry<String, HashMap<String, String>> entrySet : getBuildRepeatingGroupBorn().entrySet()) {
            HashMap<String, String> details = entrySet.getValue();
            if (client.getBaseEntityId().equals(details.get(MaternityDbConstants.Column.MaternityChild.BASE_ENTITY_ID))) {
                height = details.get("birth_height_entered");
                weight = details.get("birth_weight_entered");
                break;
            }
        }
        JSONArray jsonArray = new JSONArray();

        JSONObject heightObject = new JSONObject();
        heightObject.put(JsonFormConstants.KEY, Constants.KEY.BIRTH_HEIGHT);
        heightObject.put(JsonFormConstants.VALUE, height);
        jsonArray.put(heightObject);

        JSONObject weightObject = new JSONObject();
        weightObject.put(JsonFormConstants.KEY, Constants.KEY.BIRTH_WEIGHT);
        weightObject.put(JsonFormConstants.VALUE, weight);
        jsonArray.put(weightObject);

        tempStep.put(JsonFormConstants.FIELDS, jsonArray);
        interactor().processHeight(client.getIdentifiers(), tempForm.toString(), params, clientJson);
        interactor().processWeight(client.getIdentifiers(), tempForm.toString(), params, clientJson);
    }

    private ChildRegisterInteractor interactor() {
        if (interactor == null) {
            interactor = new ChildRegisterInteractor();
        }
        return interactor;
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

    public HashMap<String, HashMap<String, String>> getBuildRepeatingGroupBorn() {
        return buildRepeatingGroupBorn;
    }

    public void setBuildRepeatingGroupBorn(HashMap<String, HashMap<String, String>> buildRepeatingGroupBorn) {
        this.buildRepeatingGroupBorn = buildRepeatingGroupBorn;
    }

    @Nullable
    @Override
    public HashMap<String, String> motherDetails(@NonNull String baseEntityId) {
        ArrayList<HashMap<String, String>> hashMap = CoreLibrary.getInstance().context().getEventClientRepository().rawQuery(MaternityLibrary.getInstance().getRepository().getReadableDatabase(),
                "select ec_client.first_name,ec_client.last_name,ec_client.dob,ec_client.village,ec_client.home_address,ec_mother_details.mother_hiv_status,ec_mother_details.mother_tdv_doses from  ec_client" +
                        " left join ec_mother_details on ec_client.base_entity_id = ec_mother_details.base_entity_id where " + MaternityUtils.metadata().getTableName() + ".id = '" + baseEntityId + "' limit 1");
        if (!hashMap.isEmpty()) {
            return hashMap.get(0);
        }
        return null;
    }
}
