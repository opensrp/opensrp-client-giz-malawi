package org.smartregister.giz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.giz.R;
import org.smartregister.giz.fragment.OpdRegisterFragment;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.view.NavDrawerActivity;
import org.smartregister.giz.view.NavigationMenu;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.activity.BaseOpdRegisterActivity;
import org.smartregister.opd.fragment.BaseOpdRegisterFragment;
import org.smartregister.opd.utils.Utils;
import org.smartregister.view.fragment.BaseRegisterFragment;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-17
 */

public class OpdRegisterActivity extends BaseOpdRegisterActivity implements NavDrawerActivity {

    private NavigationMenu navigationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new OpdRegisterFragment();
    }


    public void createDrawer() {
        navigationMenu = NavigationMenu.getInstance(this, null, null);
        navigationMenu.getNavigationAdapter().setSelectedView(GizConstants.DrawerMenu.ALL_CLIENTS);
        navigationMenu.runRegisterCount();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        createDrawer();
    }

    @Override
    public void finishActivity() {

    }

    @Override
    public void openDrawer() {
        if (navigationMenu != null) {
            navigationMenu.openDrawer();
        }
    }

    @Override
    public void closeDrawer() {
        if (navigationMenu != null) {
            navigationMenu.closeDrawer();
        }
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        super.onActivityResultExtended(requestCode, resultCode, data);
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            if (mBaseFragment instanceof BaseOpdRegisterFragment) {
                String locationId = Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                presenter().startForm(formName, entityId, metaData, locationId);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().getOpdFormActivity());
        if (jsonForm.has(GizConstants.KEY.ENCOUNTER_TYPE) && jsonForm.optString(GizConstants.KEY.ENCOUNTER_TYPE).equals(
                GizConstants.KEY.OPD_REGISTRATION)) {
//            JsonFormUtils.addChildRegLocHierarchyQuestions(jsonForm, GizConstants.KEY.REGISTRATION_HOME_ADDRESS, LocationHierarchy.ENTIRE_TREE);
        }
        intent.putExtra(Constants.INTENT_KEY.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
//        super.startFormActivity(jsonForm);
    }

    @Override
    public void startRegistration() {
        throw new IllegalArgumentException();

//        super.startRegistration();

    }
}
