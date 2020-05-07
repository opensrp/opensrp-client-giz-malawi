package org.smartregister.giz.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.giz.R;
import org.smartregister.giz.contract.NavigationMenuContract;
import org.smartregister.giz.fragment.GizMeFragment;
import org.smartregister.giz.fragment.MaternityRegisterFragment;
import org.smartregister.giz.presenter.MaternityRegisterActivityPresenter;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.view.NavDrawerActivity;
import org.smartregister.giz.view.NavigationMenu;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.activity.BaseMaternityRegisterActivity;
import org.smartregister.maternity.contract.MaternityRegisterActivityContract;
import org.smartregister.maternity.fragment.BaseMaternityRegisterFragment;
import org.smartregister.maternity.model.MaternityRegisterActivityModel;
import org.smartregister.maternity.presenter.BaseMaternityRegisterActivityPresenter;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityJsonFormUtils;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 2020-03-31
 */

public class MaternityRegisterActivity extends BaseMaternityRegisterActivity implements NavDrawerActivity, NavigationMenuContract {

    private NavigationMenu navigationMenu;


    @Override
    protected BaseMaternityRegisterActivityPresenter createPresenter(@NonNull MaternityRegisterActivityContract.View view, @NonNull MaternityRegisterActivityContract.Model model) {
        return new MaternityRegisterActivityPresenter(view, model);
    }

    @Override
    protected MaternityRegisterActivityContract.Model createActivityModel() {
        return new MaternityRegisterActivityModel();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new MaternityRegisterFragment();
    }

    public void createDrawer() {
        navigationMenu = NavigationMenu.getInstance(this, null, null);
        if (navigationMenu != null) {
            navigationMenu.getNavigationAdapter().setSelectedView(GizConstants.DrawerMenu.MATERNITY_CLIENTS);
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

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        //TODO: Continue fixing maternity outcome form from here
        // After filling in the form, we need to process it, create event(s) and process the event(s) (probably)
        if (requestCode == MaternityJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(MaternityConstants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(MaternityJsonFormUtils.ENCOUNTER_TYPE);
                if (MaternityUtils.metadata() != null && encounterType.equals(MaternityUtils.metadata().getRegisterEventType())) {
                    org.smartregister.maternity.pojos.RegisterParams registerParam = new org.smartregister.maternity.pojos.RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(MaternityJsonFormUtils.formTag(MaternityUtils.context().allSharedPreferences()));

                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveForm(jsonString, registerParam);
                } else if (encounterType.equals(MaternityConstants.EventType.MATERNITY_OUTCOME)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveOutcomeForm(encounterType, data);
                }

            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    //
    @Override
    public void startFormActivity(@NonNull String formName, @Nullable String entityId, @Nullable String metaData) {
        if (mBaseFragment instanceof BaseMaternityRegisterFragment) {
            String locationId = MaternityUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, null, "ec_client");
        } else {
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, MaternityLibrary.getInstance().getMaternityConfiguration().getMaternityMetadata().getMaternityFormActivity());
        intent.putExtra(MaternityConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(true);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, MaternityJsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, BaseMaternityRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public MaternityRegisterActivityContract.Presenter presenter() {
        return (MaternityRegisterActivityContract.Presenter) presenter;
    }

    @Override
    protected Fragment[] getOtherFragments() {
        ME_POSITION = 1;

        Fragment[] fragments = new Fragment[1];
        fragments[ME_POSITION - 1] = new GizMeFragment();

        return fragments;
    }

    @Override
    public NavigationMenu getNavigationMenu() {
        return navigationMenu;
    }
}