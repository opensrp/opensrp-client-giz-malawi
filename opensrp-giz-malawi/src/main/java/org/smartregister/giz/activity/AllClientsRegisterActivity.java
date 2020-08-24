package org.smartregister.giz.activity;

import android.content.Intent;

import org.smartregister.giz.fragment.AllClientsRegisterFragment;
import org.smartregister.view.fragment.BaseRegisterFragment;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-17
 */

public class AllClientsRegisterActivity extends OpdRegisterActivity {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new AllClientsRegisterFragment();
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, AllClientsRegisterActivity.class);
        startActivity(intent);
        finish();
    }
}