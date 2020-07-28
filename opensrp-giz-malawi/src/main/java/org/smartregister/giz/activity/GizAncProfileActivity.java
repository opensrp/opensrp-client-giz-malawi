package org.smartregister.giz.activity;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.library.event.PatientRemovedEvent;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.giz.util.GizUtils;

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
                GizUtils.showAncMaternityNavigationDialog(event, GizAncProfileActivity.this);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

}
