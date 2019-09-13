package org.smartregister.opd.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.fragment.OpdRegisterFragment;
import org.smartregister.opd.model.OpdRegisterActivityModel;
import org.smartregister.opd.presenter.OpdRegisterActivityPresenter;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterActivity extends BaseRegisterActivity implements OpdRegisterActivityContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected void registerBottomNavigation() {
        // Do nothing
    }

    @Override
    protected void initializePresenter() {
        presenter = new OpdRegisterActivityPresenter(this, new OpdRegisterActivityModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new OpdRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected void onResumption() {
        super.onResumption();
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        /*try {
            if (mBaseFragment instanceof OpdRegisterFragment) {
                String locationId = OpdLibrary.getInstance().context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                presenter().startForm(formName, entityId, metaData, locationId, "");
            }
        } catch (Exception e) {
            Timber.e(e);
            displayToast(getString(R.string.error_unable_to_start_form));
        }*/
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        /*Intent intent = new Intent(this, Utils.metadata().familyFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setName(getString(R.string.add_fam));
        form.setActionBarBackground(R.color.family_actionbar);
        form.setNavigationBackground(R.color.family_navigation);
        form.setHomeAsUpIndicator(R.mipmap.ic_cross_white);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);*/
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.registerEventType)
                        || form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(CoreConstants.EventType.CHILD_REGISTRATION)
                ) {
                    presenter().saveForm(jsonString, false);
                }
            } catch (Exception e) {
                Timber.e(e);
            }

        }*/
    }

    @Override
    public List<String> getViewIdentifiers() {
        //return Arrays.asList(Utils.metadata().familyRegister.config);
        return null;
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, OpdRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public OpdRegisterActivityContract.Presenter presenter() {
        return (OpdRegisterActivityContract.Presenter) presenter;
    }

    @Override
    public void startRegistration() {
        //startFormActivity(Utils.metadata().familyRegister.formName, null, null);
    }
}