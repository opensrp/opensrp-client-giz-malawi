package org.smartregister.giz.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.Constants;
import org.smartregister.giz.R;
import org.smartregister.giz.configuration.ChildStatusRepeatingGroupGenerator;
import org.smartregister.giz.contract.NavigationMenuContract;
import org.smartregister.giz.fragment.PncRegisterFragment;
import org.smartregister.giz.repository.GizChildRegisterQueryProvider;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.view.NavDrawerActivity;
import org.smartregister.giz.view.NavigationMenu;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.activity.BasePncRegisterActivity;
import org.smartregister.pnc.config.RepeatingGroupGenerator;
import org.smartregister.pnc.contract.PncRegisterActivityContract;
import org.smartregister.pnc.fragment.BasePncRegisterFragment;
import org.smartregister.pnc.model.PncRegisterActivityModel;
import org.smartregister.pnc.pojo.RegisterParams;
import org.smartregister.pnc.presenter.BasePncRegisterActivityPresenter;
import org.smartregister.pnc.presenter.PncRegisterActivityPresenter;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.pnc.utils.PncJsonFormUtils;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterActivity extends BasePncRegisterActivity implements NavDrawerActivity, NavigationMenuContract {

    private NavigationMenu navigationMenu;

    @Override
    protected BasePncRegisterActivityPresenter createPresenter(@NonNull PncRegisterActivityContract.View view, @NonNull PncRegisterActivityContract.Model model) {
        return new PncRegisterActivityPresenter(view, model);
    }

    @Override
    protected PncRegisterActivityContract.Model createActivityModel() {
        return new PncRegisterActivityModel();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new PncRegisterFragment();
    }

    public void createDrawer() {
        navigationMenu = NavigationMenu.getInstance(this, null, null);
        if (navigationMenu != null) {
            navigationMenu.getNavigationAdapter().setSelectedView(GizConstants.DrawerMenu.PNC_CLIENTS);
            navigationMenu.runRegisterCount();
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        createDrawer();
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        //TODO: Continue fixing pnc outcome form from here
        // After filling in the form, we need to process it, create event(s) and process the event(s) (probably)
        if (requestCode == PncJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(PncConstants.JsonFormExtraConstants.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(PncJsonFormUtils.ENCOUNTER_TYPE);
                if (PncUtils.metadata() != null && encounterType.equals(PncUtils.metadata().getRegisterEventType())) {
                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(PncJsonFormUtils.formTag(PncUtils.context().allSharedPreferences()));

                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveForm(jsonString, registerParam);
                } else if (encounterType.equals(PncConstants.EventTypeConstants.PNC_MEDIC_INFO) || encounterType.equals(PncConstants.EventTypeConstants.PNC_VISIT)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().savePncForm(encounterType, data);
                }

            } catch (JSONException e) {
                Timber.e(e);
            }

        }
    }

    @Override
    public void startFormActivity(@NonNull String formName, @Nullable String entityId, @Nullable String metaData) {
        if (mBaseFragment instanceof BasePncRegisterFragment) {
            String locationId = PncUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, null, PncDbConstants.KEY.TABLE);
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, PncLibrary.getInstance().getPncConfiguration().getPncMetadata().getPncFormActivity());
        intent.putExtra(PncConstants.JsonFormExtraConstants.JSON, jsonForm.toString());
        Form form = new Form();
        form.setWizard(true);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, PncJsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void startFormActivityFromFormJson(@NonNull String entityId, @NonNull JSONObject jsonForm, @Nullable HashMap<String, String> intentData) {
        PncUtils.processPreChecks(entityId, jsonForm, intentData);
        generateRepeatingGrpFields(jsonForm, entityId);
        Intent intent = PncUtils.buildFormActivityIntent(jsonForm, intentData, this);
        if (intent != null) {
            startActivityForResult(intent, PncJsonFormUtils.REQUEST_CODE_GET_JSON);
        } else {
            Timber.e(new Exception(), "FormActivityConstants cannot be started because PncMetadata is NULL");
        }
    }

    public void generateRepeatingGrpFields(JSONObject json, String entityId) {
        if (PncConstants.EventTypeConstants.PNC_MEDIC_INFO.equals(json.optString(PncConstants.JsonFormKeyConstants.ENCOUNTER_TYPE))) {
            try {
                RepeatingGroupGenerator repeatingGroupGenerator = new RepeatingGroupGenerator(json.optJSONObject("step4"), "step4",
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
                JSONObject stepJsonObject = json.optJSONObject("step3");
                ArrayList<HashMap<String, String>> storedPncBabyValues = storedPncBabyValues(entityId);

                if (storedPncBabyValues.isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JsonFormConstants.KEY, "notice");
                    jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.TOASTER_NOTES);
                    jsonObject.put(JsonFormConstants.TOASTER_TYPE, JsonFormConstants.TOASTER_WARNING);
                    jsonObject.put(JsonFormConstants.TEXT, "There was no child associated with the current record");
                    stepJsonObject.optJSONArray(JsonFormConstants.FIELDS).put(jsonObject);
                    FormUtils.getFieldJSONObject(stepJsonObject.optJSONArray(JsonFormConstants.FIELDS), "child_status").put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                } else {
                    new ChildStatusRepeatingGroupGenerator(json.optJSONObject("step3"), "step3",
                            "child_status",
                            visitColumnMap(),
                            PncDbConstants.KEY.BASE_ENTITY_ID,
                            storedPncBabyValues(entityId)).init();
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @NonNull
    public ArrayList<HashMap<String, String>> storedPncBabyValues(String entityId) {
        return ChildLibrary.
                getInstance()
                .context()
                .getEventClientRepository()
                .rawQuery(ChildLibrary.getInstance().getRepository().getReadableDatabase(),
                        "select pb.base_entity_id as base_entity_id, pb.baby_first_name as first_name, pb.baby_last_name as last_name, pb.dob as dob from pnc_baby pb " +
                                " inner join ec_client ec on pb.base_entity_id = ec.base_entity_id " +
                                " where pb" + "." + Constants.KEY.MOTHER_BASE_ENTITY_ID + " = '" + entityId + "' and ec.date_removed is null");
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
                                " where " + childRegisterQueryProvider.getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = '" + entityId + "' and ec_client.date_removed is null");
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

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, BasePncRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public NavigationMenu getNavigationMenu() {
        return navigationMenu;
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void openDrawer() {
        if (navigationMenu != null) {
            navigationMenu.openDrawer();
        }
    }

    @Override
    public void closeDrawer() {
        NavigationMenu.closeDrawer();
    }

    /*
    @Override
    public PncRegisterActivityContract.Presenter presenter() {
        return (PncRegisterActivityContract.Presenter) presenter;
    }


    @Override
    public void startRegistration() {
        //Do nothing
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }*/


}
