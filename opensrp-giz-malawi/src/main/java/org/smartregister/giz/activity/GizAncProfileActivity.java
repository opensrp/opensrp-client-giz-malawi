package org.smartregister.giz.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.library.event.PatientRemovedEvent;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityUtils;

import java.util.HashMap;

import timber.log.Timber;

public class GizAncProfileActivity extends ProfileActivity {

    @Override
    public void startFormForEdit(ClientDetailsFetchedEvent event) {
        if (event != null && event.isEditMode()) {
            String formMetadata = ANCJsonFormUtils.getAutoPopulatedJsonEditRegisterFormString(this, event.getWomanClient());
            try {
                JSONObject form = new JSONObject(formMetadata);
                JSONObject jsonObject = ANCJsonFormUtils.getFieldJSONObject(FormUtils.getMultiStepFormFields(form), ConstantsUtils.JsonFormKeyUtils.PREVIOUS_VISITS);
                if (jsonObject != null) {
                    jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                }
                ANCJsonFormUtils.startFormForEdit(this, ANCJsonFormUtils.REQUEST_CODE_GET_JSON, form.toString());
            } catch (Exception e) {
                Timber.e(e, " --> startFormForEdit");
            }
        }
    }


    @Override
    public void removePatient(PatientRemovedEvent event) {
        if (event != null) {
            try {
                Utils.removeStickyEvent(event);
                hideProgressDialog();
                if (event.getClosedNature() != null && ConstantsUtils.ClosedNature.TRANSFERRED.equals(event.getClosedNature())) {
                    AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppThemeAlertDialog)
                            .setCancelable(true)
                            .setMessage(R.string.anc_migration_to_maternity_text)
                            .setNegativeButton(R.string.yes, (dialog1, which) -> {
                                MaternityMetadata maternityMetadata = MaternityLibrary.getInstance().getMaternityConfiguration().getMaternityMetadata();
                                String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
                                HashMap<String, String> map = MaternityUtils.getMaternityClient(baseEntityId);
                                CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(baseEntityId, map, "name");
                                commonPersonObjectClient.setColumnmaps(map);
                                if (maternityMetadata != null) {
                                    Intent intent = new Intent(GizAncProfileActivity.this, maternityMetadata.getProfileActivity());
                                    intent.putExtra(MaternityConstants.IntentKey.CLIENT_OBJECT, commonPersonObjectClient);
                                    startActivity(intent);
                                    finish();
                                }

                            }).setPositiveButton(R.string.no, (dialog12, which) -> dialog12.dismiss()).create();

                    dialog.show();
                } else {
                    finish();
                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
