package org.smartregister.giz.activity;

import org.smartregister.anc.library.activity.MainContactActivity;
import org.smartregister.anc.library.domain.Contact;

public class GizAncContactActivity extends MainContactActivity {

    @Override
    public void loadGlobals(Contact contact) {
//        try {
//            String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
//            Facts entries = AncLibrary.getInstance().getPreviousContactRepository().getPreviousContactFacts(baseEntityId, String.valueOf(1), false);
//            String contact_date = entries.get("contact_date");
//            contact.getGlobals().put("first_contact_date", contact_date);
//        }catch (Exception e){
//            Timber.e(e);
//        }
        super.loadGlobals(contact);
    }
}
