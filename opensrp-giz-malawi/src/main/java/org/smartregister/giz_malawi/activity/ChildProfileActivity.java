package org.smartregister.giz_malawi.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import org.smartregister.child.util.Utils;
import org.smartregister.view.activity.BaseProfileActivity;

public class ChildProfileActivity extends BaseProfileActivity {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = org.smartregister.giz_malawi.util.Utils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(org.smartregister.giz_malawi.util.Utils.setAppLocale(base, lang));
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Utils.showToast(this, "In the profile page!!");

    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {

    }
}

