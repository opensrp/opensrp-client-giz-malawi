package org.smartregister.giz.activity;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.Constants;
import org.smartregister.giz.configuration.ChildStatusRepeatingGroupGenerator;
import org.smartregister.giz.repository.GizChildRegisterQueryProvider;
import org.smartregister.pnc.activity.BasePncProfileActivity;
import org.smartregister.pnc.config.RepeatingGroupGenerator;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import timber.log.Timber;

public class GizPncProfileActivity extends BasePncProfileActivity {

    @Override
    public void startFormActivity(String entityId, @NonNull JSONObject form, @NonNull HashMap<String, String> intentData) {
        generateRepeatingGrpFields(form, entityId);
        super.startFormActivity(entityId, form, intentData);
    }

    public void generateRepeatingGrpFields(JSONObject json, String entityId) {
        if (PncConstants.EventTypeConstants.PNC_MEDIC_INFO.equals(json.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
            try {
                RepeatingGroupGenerator repeatingGroupGenerator = new RepeatingGroupGenerator(json.optJSONObject("step4"),
                        "baby_alive_group",
                        outcomeColumnMap(),
                        PncDbConstants.KEY.BASE_ENTITY_ID,
                        storedValues(entityId));
                repeatingGroupGenerator
                        .setFieldsWithoutSpecialViewValidation
                                (new HashSet<>(
                                        Arrays.asList("birth_weight_entered", "birth_height_entered", "birth_record_date", "baby_gender", "baby_first_name", "baby_last_name", "baby_dob")));
                repeatingGroupGenerator.init();
            } catch (JSONException e) {
                Timber.e(e);
            }
        } else if (PncConstants.EventTypeConstants.PNC_VISIT.equals(json.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
            try {
                new ChildStatusRepeatingGroupGenerator(json.optJSONObject("step3"),
                        "child_status",
                        visitColumnMap(),
                        PncDbConstants.KEY.BASE_ENTITY_ID,
                        storedValues(entityId)).init();
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @NonNull
    public ArrayList<HashMap<String, String>> storedValues(String entityId) {
        GizChildRegisterQueryProvider childRegisterQueryProvider = new GizChildRegisterQueryProvider();
        return ChildLibrary.
                getInstance()
                .context()
                .getEventClientRepository()
                .rawQuery(ChildLibrary.getInstance().getRepository().getReadableDatabase(),
                        childRegisterQueryProvider.mainRegisterQuery() +
                                " where " + childRegisterQueryProvider.getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = '" + entityId + "'");
    }

    @NonNull
    public Map<String, String> outcomeColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("baby_first_name", "first_name");
        map.put("baby_last_name", "last_name");
        map.put("baby_dob", "dob");
        map.put("baby_gender", "gender");
        return map;
    }

    @NonNull
    public Map<String, String> visitColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("child_name", "first_name");
        map.put("open_vaccine_card", "base_entity_id");
        return map;
    }
}
