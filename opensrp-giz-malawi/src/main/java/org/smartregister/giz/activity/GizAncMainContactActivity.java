package org.smartregister.giz.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.activity.MainContactActivity;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.util.GizUtils;

import java.util.Set;

public class GizAncMainContactActivity extends MainContactActivity {

    @Override
    public void startFormActivity(JSONObject form, Contact contact) {
        Set<String> hiddenFields = null;
        Set<String> disabledFields = null;
        try {
            JSONObject formConfig = GizUtils.getFormConfig(contact.getFormName(), GizConstants.FORM_CONFIG_LOCATION, getApplicationContext());
            if (formConfig != null) {
                hiddenFields = GizUtils.convertStringJsonArrayToSet(formConfig.optJSONArray(GizConstants.KEY.HIDDEN_FIELDS));
                disabledFields = GizUtils.convertStringJsonArrayToSet(formConfig.optJSONArray(GizConstants.KEY.DISABLED_FIELDS));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        contact.setHiddenFields(hiddenFields);
        contact.setDisabledFields(disabledFields);
        super.startFormActivity(form, contact);
    }
}
