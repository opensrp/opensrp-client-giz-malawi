package org.smartregister.giz.activity;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.giz.fragment.OpdFormFragment;
import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.utils.OpdConstants;

import java.util.HashMap;
import java.util.Set;

public class OpdFormActivity extends BaseOpdFormActivity {

    private HashMap<String, String> parcelableData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (getIntent() != null && extras != null) {
            Set<String> keySet = extras.keySet();

            for (String key: keySet) {
                if (!key.equals(OpdConstants.JSON_FORM_EXTRA.JSON)) {
                    Object objectValue = extras.get(key);

                    if (objectValue instanceof String) {
                        String value = (String) objectValue;
                        parcelableData.put(key, value);
                    }
                }
            }
        }
    }

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        OpdFormFragment opdFormFragment = OpdFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, opdFormFragment).commit();
    }

    @NonNull
    public HashMap<String, String> getParcelableData() {
        return parcelableData;
    }
}
