package org.smartregister.giz.activity;

import org.json.JSONObject;
import org.smartregister.anc.library.activity.MainContactActivity;
import org.smartregister.anc.library.domain.Contact;

public class GizAncMainContactActivity extends MainContactActivity {

    @Override
    public void startFormActivity(JSONObject form, Contact contact) {
        super.startFormActivity(form, contact);
    }
}
