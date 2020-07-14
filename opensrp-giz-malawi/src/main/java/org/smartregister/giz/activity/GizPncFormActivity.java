package org.smartregister.giz.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.Constants;
import org.smartregister.giz.R;
import org.smartregister.giz.fragment.GizPncFormFragment;
import org.smartregister.giz.repository.GizChildRegisterQueryProvider;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.config.RepeatingGroupGenerator;
import org.smartregister.pnc.fragment.BasePncFormFragment;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import timber.log.Timber;

public class GizPncFormActivity extends BasePncFormActivity {

    BasePncFormFragment pncFormFragment;

    @Override
    protected void initializeFormFragmentCore() {
        pncFormFragment = new GizPncFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, JsonFormConstants.FIRST_STEP_NAME);
        pncFormFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, pncFormFragment).commit();
    }

    @Override
    protected void initiateFormUpdate(JSONObject json) {
        super.initiateFormUpdate(json);
        generateRepeatingGrpFields(json);
    }

    @Override
    public void generateRepeatingGrpFields(JSONObject json) {
        if (PncConstants.EventTypeConstants.PNC_OUTCOME.equals(json.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
            try {
                RepeatingGroupGenerator repeatingGroupGenerator = new RepeatingGroupGenerator(json.optJSONObject("step4"),
                        "baby_alive_group",
                        OutcomeColumnMap(),
                        PncDbConstants.KEY.BASE_ENTITY_ID,
                        storedValues());
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
                new RepeatingGroupGenerator(json.optJSONObject("step3"),
                        "child_status",
                        VisitColumnMap(),
                        PncDbConstants.KEY.BASE_ENTITY_ID,
                        storedValues()).init();
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    public ArrayList<HashMap<String, String>> storedValues() {
        String motherBaseEntityId = getIntent().getStringExtra(PncDbConstants.KEY.BASE_ENTITY_ID);
        GizChildRegisterQueryProvider childRegisterQueryProvider = new GizChildRegisterQueryProvider();
        return ChildLibrary.
                getInstance()
                .context()
                .getEventClientRepository()
                .rawQuery(ChildLibrary.getInstance().getRepository().getReadableDatabase(),
                        childRegisterQueryProvider.mainRegisterQuery() +
                                " where " + childRegisterQueryProvider.getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = '" + motherBaseEntityId + "'");
    }

    public Map<String, String> OutcomeColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("baby_first_name", "first_name");
        map.put("baby_last_name", "last_name");
        map.put("baby_dob", "dob");
        map.put("baby_gender", "gender");
        return map;
    }

    public Map<String, String> VisitColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("child_name", "first_name");
        map.put("open_vaccine_card", "base_entity_id");
        return map;
    }
}
