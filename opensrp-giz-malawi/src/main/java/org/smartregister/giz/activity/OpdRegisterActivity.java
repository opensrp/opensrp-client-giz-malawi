package org.smartregister.giz.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentPagerAdapter;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.anc.library.fragment.MeFragment;
import org.smartregister.giz.R;
import org.smartregister.giz.fragment.OpdRegisterFragment;
import org.smartregister.giz.presenter.OpdRegisterActivityPresenter;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.view.NavDrawerActivity;
import org.smartregister.giz.view.NavigationMenu;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.activity.BaseOpdRegisterActivity;
import org.smartregister.opd.fragment.BaseOpdRegisterFragment;
import org.smartregister.opd.model.OpdRegisterActivityModel;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-17
 */

public class OpdRegisterActivity extends BaseOpdRegisterActivity implements NavDrawerActivity {

    private NavigationMenu navigationMenu;

    @Override
    protected BaseOpdRegisterActivityPresenter createPresenter(@NonNull OpdRegisterActivityContract.View view, @NonNull OpdRegisterActivityContract.Model model) {
        return new OpdRegisterActivityPresenter(view, model);
    }

    @Override
    protected void initializePresenter() {
        presenter = new OpdRegisterActivityPresenter(this, new OpdRegisterActivityModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new OpdRegisterFragment();
    }


    public void createDrawer() {
        navigationMenu = NavigationMenu.getInstance(this, null, null);
        if (navigationMenu != null) {
            navigationMenu.getNavigationAdapter().setSelectedView(GizConstants.DrawerMenu.ALL_CLIENTS);
            navigationMenu.runRegisterCount();
        }
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
        NavigationMenu.closeDrawer();
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == OpdJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(OpdJsonFormUtils.ENCOUNTER_TYPE).equals(OpdUtils.metadata().getRegisterEventType())) {
                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));

                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveForm(jsonString, registerParam);
                }

            } catch (JSONException e) {
                Timber.e(e);
            }

        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        if (mBaseFragment instanceof BaseOpdRegisterFragment) {
            String locationId = OpdUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId);
        } else {

            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().getOpdFormActivity());
        if (jsonForm.has(GizConstants.KEY.ENCOUNTER_TYPE) && jsonForm.optString(GizConstants.KEY.ENCOUNTER_TYPE).equals(
                GizConstants.KEY.OPD_REGISTRATION)) {
//            OpdJsonFormUtils.addRegLocHierarchyQuestions(jsonForm, GizConstants.KEY.REGISTRATION_HOME_ADDRESS, LocationHierarchy.ENTIRE_TREE);
        }
        intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, OpdJsonFormUtils.REQUEST_CODE_GET_JSON);
    }


    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, OpdRegisterActivity.class);
        startActivity(intent);
        finish();

        //mPager.setCurrentItem(0);
    }

    @Override
    public OpdRegisterActivityContract.Presenter presenter() {
        return (OpdRegisterActivityContract.Presenter) presenter;
    }


    @Override
    public void startRegistration() {
        //Do nothing
    }


    @Override
    protected Fragment[] getOtherFragments() {
        ME_POSITION = 1;

        Fragment[] fragments = new Fragment[1];
        fragments[ME_POSITION - 1] = new MeFragment();

        return fragments;
    }


}
