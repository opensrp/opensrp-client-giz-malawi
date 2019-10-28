package org.smartregister.giz.activity;


import android.support.v4.app.Fragment;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.giz.fragment.OpdFormFragment;
import org.smartregister.opd.activity.BaseOpdFormActivity;

public class OpdFormActivity extends BaseOpdFormActivity {

    private OpdFormFragment formFragment;

    @Override
    public void initializeFormFragment() {
        OpdFormFragment opdFormFragment = (OpdFormFragment) OpdFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, opdFormFragment).commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof OpdFormFragment) {
            formFragment = (OpdFormFragment) fragment;
        }
    }
}
