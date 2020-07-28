package org.smartregister.giz.presenter;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.giz.R;
import org.smartregister.giz.configuration.GizPncRegisterQueryProvider;
import org.smartregister.giz.interactor.MaternityRegisterActivityInteractor;
import org.smartregister.maternity.activity.BaseMaternityRegisterActivity;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.pojo.MaternityEventClient;
import org.smartregister.maternity.pojo.RegisterParams;
import org.smartregister.maternity.presenter.BaseMaternityRegisterActivityPresenter;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.listener.PncEventActionCallBack;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 31-03-2020.
 */
public class MaternityRegisterActivityPresenter extends BaseMaternityRegisterActivityPresenter {

    private String maternityBaseEntityId;

    public MaternityRegisterActivityPresenter(@NonNull MaternityRegisterActivityContract.View view, @NonNull MaternityRegisterActivityContract.Model model) {
        super(view, model);
    }

    @NonNull
    @Override
    public MaternityRegisterActivityContract.Interactor createInteractor() {
        return new MaternityRegisterActivityInteractor();
    }

    @Override
    public void saveForm(@NonNull String jsonString, @NonNull RegisterParams registerParams) {
        if (registerParams.getFormTag() == null) {
            registerParams.setFormTag(MaternityJsonFormUtils.formTag(MaternityUtils.getAllSharedPreferences()));
        }

        List<MaternityEventClient> maternityEventClientList = model.processRegistration(jsonString, registerParams.getFormTag());
        if (maternityEventClientList == null || maternityEventClientList.isEmpty()) {
            return;
        }

        registerParams.setEditMode(false);
        interactor.saveRegistration(maternityEventClientList, jsonString, registerParams, this);
    }

    @Override
    public void onNoUniqueId() {
        if (getView() != null) {
            getView().displayShortToast(R.string.no_unique_id);
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }

    @Override
    public void saveOutcomeForm(@NonNull String eventType, @Nullable Intent data) {
        maternityBaseEntityId = MaternityUtils.getIntentValue(data, MaternityConstants.IntentKey.BASE_ENTITY_ID);
        super.saveOutcomeForm(eventType, data);
    }

    @Override
    public void onEventSaved() {
        super.onEventSaved();
        processPncRegistration();
    }

    public void processPncRegistration() {
        String pncBaseEntityId = JsonFormUtils.generateRandomUUIDString();

        JSONObject jsonObject = PncUtils.getJsonFormToJsonObject("pnc_registration_template");
        try {
            if (jsonObject != null) {
                String jsonString = jsonObject.toString();

                String q1 = "SELECT * FROM ec_client WHERE base_entity_id='" + maternityBaseEntityId + "'";
                String q2 = "SELECT * FROM maternity_registration_details WHERE base_entity_id='" + maternityBaseEntityId + "'";

                String q3 = "SELECT * FROM maternity_outcome WHERE base_entity_id='" + maternityBaseEntityId + "'";
                HashMap<String, String> tempData = PncUtils.getMergedData(q3);

                Set<String> possibleJsonArrayKeys = new HashSet<>();
                possibleJsonArrayKeys.add("dob_unknown");
                possibleJsonArrayKeys.add("occupation");

                HashMap<String, String> data = PncUtils.getMergedData(q1, q2);
                data.put("mother_status", tempData.get("mother_status"));

                if ("alive".equalsIgnoreCase(data.get("mother_status"))) {

                    for (Map.Entry<String, String> entry : data.entrySet()) {

                        String value = entry.getValue();
                        value = value == null ? "" : value;

                        if (possibleJsonArrayKeys.contains(entry.getKey()) && isValidJSONArray(value)) {
                            jsonString = jsonString.replace("\"{" + entry.getKey() + "}\"", value);
                        }
                        else {
                            jsonString = jsonString.replace("{" + entry.getKey() + "}", value);
                        }
                    }

                    jsonString = jsonString.replace("{app_version_name}", com.vijay.jsonwizard.BuildConfig.VERSION_NAME);

                    jsonObject = new JSONObject(jsonString);
                    jsonObject.put(JsonFormUtils.ENTITY_ID, pncBaseEntityId);

                    Intent intent = new Intent();
                    intent.putExtra(PncConstants.JsonFormExtraConstants.JSON, jsonObject.toString());
                    intent.putExtra(JsonFormConstants.SKIP_VALIDATION, false);

                    processPncData(intent, () -> processPncOutcome(pncBaseEntityId));
                }
            }
        }
        catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    public void processPncOutcome(String pncBaseEntityId) {

        JSONObject jsonObject = PncUtils.getJsonFormToJsonObject("pnc_outcome_template");
        try {
            if (jsonObject != null) {
                String jsonString = jsonObject.toString();

                String q1 = "SELECT * FROM maternity_outcome WHERE base_entity_id='" + maternityBaseEntityId + "'";
                String q2 = "SELECT * FROM maternity_registration_details WHERE base_entity_id='" + maternityBaseEntityId + "'";

                Set<String> possibleJsonArrayKeys = new HashSet<>();
                possibleJsonArrayKeys.add("lmp_unknown");
                possibleJsonArrayKeys.add("previous_complications");
                possibleJsonArrayKeys.add("previous_delivery_mode");
                possibleJsonArrayKeys.add("previous_pregnancy_outcomes");
                possibleJsonArrayKeys.add("family_history");
                possibleJsonArrayKeys.add("obstetric_complications");
                possibleJsonArrayKeys.add("obstetric_care");

                HashMap<String, String> data = PncUtils.getMergedData(q1, q2);
                for (Map.Entry<String, String> entry : data.entrySet()) {

                    String value = entry.getValue();
                    value = value == null ? "" : value;

                    if (possibleJsonArrayKeys.contains(entry.getKey()) && isValidJSONArray(value)) {
                        jsonString = jsonString.replace("\"{" + entry.getKey() + "}\"", value);
                    }
                    else {
                        jsonString = jsonString.replace("{" + entry.getKey() + "}", value);
                    }
                }

                jsonString = jsonString.replace("{app_version_name}", com.vijay.jsonwizard.BuildConfig.VERSION_NAME);

                jsonObject = new JSONObject(jsonString);

                addAllChild(jsonObject);
                addStillBirthCondition(jsonObject);

                jsonObject.put(JsonFormUtils.ENTITY_ID, pncBaseEntityId);


                Intent intent = new Intent();
                intent.putExtra("base-entity-id", pncBaseEntityId);
                intent.putExtra(PncConstants.JsonFormExtraConstants.JSON, jsonObject.toString());
                intent.putExtra(JsonFormConstants.SKIP_VALIDATION, false);
                intent.putExtra("entity_table", "ec_client");

                processPncData(intent, () -> {
                    if (getView() != null) {
                        getView().hideProgressDialog();
                        showAlertDialog(getView(), pncBaseEntityId);
                    }
                });
            }
        }
        catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    private void addAllChild(JSONObject outcomeJsonObject) throws JSONException {

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(outcomeJsonObject.getJSONObject("step4").getJSONArray("fields").getJSONObject(0));

        Set<String> possibleJsonArrayKeys = new HashSet<>();
        possibleJsonArrayKeys.add("complications");
        possibleJsonArrayKeys.add("care_mgt");
        possibleJsonArrayKeys.add("child_hiv_status");

        String query = "SELECT * FROM maternity_child WHERE mother_base_entity_id='" + maternityBaseEntityId + "' AND stillbirth_condition IS NULL";
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
                    jsonString = jsonString.replace("\"" + key + "\"", "\"" + newKey + "\"");
                    jsonString = jsonString.replace("\"step4:" + key + "\"", "\"step4:" + newKey + "\"");
                }

                jsonObject = new JSONObject(jsonString);
                JSONArray fieldsArray = jsonObject.getJSONArray("fields");
                for (int i = 0; i < fieldsArray.length(); i++) {
                    outcomeJsonObject.getJSONObject("step4").getJSONArray("fields").put(fieldsArray.getJSONObject(i));
                }
            }
        }
    }

    private void addStillBirthCondition(JSONObject outcomeJsonObject) throws JSONException {


        String query = "SELECT * FROM maternity_child WHERE mother_base_entity_id='" + maternityBaseEntityId + "' AND stillbirth_condition IS NOT NULL";
        ArrayList<HashMap<String, String>> childData = getData(query);

        for (HashMap<String, String> childMap : childData) {
            JSONObject jsonObject = PncUtils.getJsonFormToJsonObject("pnc_outcome_still_born_template");
            if (jsonObject != null) {
                String jsonString = jsonObject.toString();

                for (Map.Entry<String, String> entry : childMap.entrySet()) {

                    String value = entry.getValue();
                    value = value == null ? "" : value;

                    jsonString = jsonString.replace("{" + entry.getKey() + "}", value);
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
                    outcomeJsonObject.getJSONObject("step5").getJSONArray("fields").put(fieldsArray.getJSONObject(i));
                }
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

    private void processPncData(Intent data, PncEventActionCallBack callBack) {
        try {
            String jsonString = data.getStringExtra(PncConstants.JsonFormExtraConstants.JSON);

            JSONObject form = new JSONObject(jsonString);
            String encounterType = form.getString(PncJsonFormUtils.ENCOUNTER_TYPE);
            if (PncUtils.metadata() != null && encounterType.equals(PncUtils.metadata().getRegisterEventType())) {
                org.smartregister.pnc.pojo.RegisterParams registerParam = new org.smartregister.pnc.pojo.RegisterParams();
                registerParam.setEditMode(false);
                registerParam.setFormTag(PncJsonFormUtils.formTag(PncUtils.context().allSharedPreferences()));

                if (getView() != null) {
                    getView().showProgressDialog(R.string.saving_dialog_title);
                }
                PncUtils.saveRegistrationFormSilent(jsonString, registerParam, callBack);
            } else if (encounterType.equals(PncConstants.EventTypeConstants.PNC_OUTCOME)) {
                PncUtils.saveOutcomeAndVisitFormSilent(encounterType, data, callBack);
            }

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void showAlertDialog(MaternityRegisterActivityContract.View view, String pncBaseEntityId) {
        if (view instanceof BaseMaternityRegisterActivity) {
            BaseMaternityRegisterActivity activity = (BaseMaternityRegisterActivity) view;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("You will now be redirected to record the Postnatal Care for the woman");
            builder.setCancelable(false);
            builder.setPositiveButton("GO TO PROFILE", (dialogInterface, i) -> {

                PncMetadata pncMetadata = PncLibrary.getInstance().getPncConfiguration().getPncMetadata();

                GizPncRegisterQueryProvider pncRegisterQueryProvider = new GizPncRegisterQueryProvider();
                String query = pncRegisterQueryProvider.mainSelectWhereIDsIn().replace("%s", "'" + pncBaseEntityId + "'");
                Cursor cursor = PncLibrary.getInstance().getRepository().getReadableDatabase().rawQuery(query, null);
                cursor.moveToFirst();

                CommonPersonObject personinlist = PncLibrary.getInstance().context().commonrepository("ec_client").readAllcommonforCursorAdapter(cursor);
                CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(),
                        personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
                pClient.setColumnmaps(personinlist.getColumnmaps());

                if (pncMetadata != null) {
                    Intent intent = new Intent(activity, pncMetadata.getProfileActivity());
                    intent.putExtra(PncConstants.IntentKey.CLIENT_OBJECT, pClient);
                    activity.startActivity(intent);
                }
            });
            builder.create().show();
        }
    }
}
