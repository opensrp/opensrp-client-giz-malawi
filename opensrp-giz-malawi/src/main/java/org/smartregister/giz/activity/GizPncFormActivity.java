package org.smartregister.giz.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.giz.R;
import org.smartregister.giz.fragment.GizPncFormFragment;
import org.smartregister.pnc.activity.BasePncFormActivity;
import org.smartregister.pnc.fragment.BasePncFormFragment;

public class GizPncFormActivity extends BasePncFormActivity {

    @Override
    protected void initializeFormFragmentCore() {
        BasePncFormFragment pncFormFragment = new GizPncFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, JsonFormConstants.FIRST_STEP_NAME);
        pncFormFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, pncFormFragment).commit();
    }
}
