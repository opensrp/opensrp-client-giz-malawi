package org.smartregister.giz_malawi.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.AllConstants;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.fragment.StatusEditDialogFragment;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.giz_malawi.R;
import org.smartregister.giz_malawi.fragment.ChildRegistrationDataFragment;
import org.smartregister.giz_malawi.util.GizUtils;
import org.smartregister.util.Utils;

import static org.smartregister.giz_malawi.util.GizUtils.setAppLocale;

/**
 * Created by ndegwamartin on 06/03/2019.
 */
public class ChildDetailTabbedActivity extends BaseChildDetailTabbedActivity {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = GizUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(setAppLocale(base, lang));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        detailsMap = getChildDetails().getColumnmaps();

        switch (item.getItemId()) {
            case R.id.registration_data:
                String populatedForm = JsonFormUtils.getMetadataForEditForm(this, detailsMap);
                startFormActivity(populatedForm);
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.immunization_data:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                Utils.startAsyncTask(new BaseChildDetailTabbedActivity.LoadAsyncTask(BaseChildDetailTabbedActivity.STATUS.EDIT_VACCINE), null);
                saveButton.setVisibility(View.VISIBLE);
                for (int i = 0; i < overflow.size(); i++) {
                    overflow.getItem(i).setVisible(false);
                }
                return true;

            case R.id.recurring_services_data:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                Utils.startAsyncTask(new BaseChildDetailTabbedActivity.LoadAsyncTask(BaseChildDetailTabbedActivity.STATUS.EDIT_SERVICE), null);
                saveButton.setVisibility(View.VISIBLE);
                for (int i = 0; i < overflow.size(); i++) {
                    overflow.getItem(i).setVisible(false);
                }
                return true;
            case R.id.weight_data:
                if (viewPager.getCurrentItem() != 1) {
                    viewPager.setCurrentItem(1);
                }
                Utils.startAsyncTask(new BaseChildDetailTabbedActivity.LoadAsyncTask(BaseChildDetailTabbedActivity.STATUS.EDIT_WEIGHT), null);
                saveButton.setVisibility(View.VISIBLE);
                for (int i = 0; i < overflow.size(); i++) {
                    overflow.getItem(i).setVisible(false);
                }
                return true;

            case R.id.report_deceased:
                String reportDeceasedMetadata = getReportDeceasedMetadata();
                startFormActivity(reportDeceasedMetadata);
                return true;
            case R.id.change_status:
                FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                android.app.Fragment prev = this.getFragmentManager().findFragmentByTag(DIALOG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                StatusEditDialogFragment.newInstance(detailsMap).show(ft, DIALOG_TAG);
                return true;
            case R.id.report_adverse_event:
                return launchAdverseEventForm();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        // Todo
    }

    @Override
    public void onNoUniqueId() {
        // Todo
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public ChildRegistrationDataFragment getChildRegistrationDataFragment() {
        return new ChildRegistrationDataFragment();
    }

    @Override
    protected void navigateToRegisterActivity() {

        Intent intent = new Intent(getApplicationContext(), ChildRegisterActivity.class);
        intent.putExtra(AllConstants.INTENT_KEY.IS_REMOTE_LOGIN, false);
        startActivity(intent);
        finish();
    }

    @Override
    protected void startFormActivity(String formData) {

        Intent intent = new Intent(getApplicationContext(), ChildFormActivity.class);

        Form formParam = new Form();
        formParam.setWizard(false);
        formParam.setHideSaveLabel(true);
        formParam.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParam);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, formData);

        startActivityForResult(intent, REQUEST_CODE_GET_JSON);


    }
}
