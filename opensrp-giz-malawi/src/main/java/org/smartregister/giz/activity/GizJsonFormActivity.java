package org.smartregister.giz.activity;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.giz.fragment.GizMalawiJsonFormFragment;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class GizJsonFormActivity extends JsonWizardFormActivity {

    @Override
    public void initializeFormFragment() {
        GizMalawiJsonFormFragment wellnessJsonFormFragment = GizMalawiJsonFormFragment
                .getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, wellnessJsonFormFragment).commit();
    }

}
