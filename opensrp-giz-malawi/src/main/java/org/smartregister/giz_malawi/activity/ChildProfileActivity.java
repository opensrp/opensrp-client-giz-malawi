package org.smartregister.giz_malawi.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import org.smartregister.child.util.Utils;
import org.smartregister.giz_malawi.util.GizConstants;
import org.smartregister.giz_malawi.util.GizUtils;
import org.smartregister.giz_malawi.view.NavigationMenu;
import org.smartregister.view.activity.BaseProfileActivity;

public class ChildProfileActivity extends BaseProfileActivity {
    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = GizUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(GizUtils.setAppLocale(base, lang));
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Utils.showToast(this, "In the profile page!!");
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        openDrawer();
    }

    public void openDrawer() {
        NavigationMenu navigationMenu = NavigationMenu.getInstance(this, null, null);
        navigationMenu.getNavigationAdapter().setSelectedView(GizConstants.DrawerMenu.CHILD_CLIENTS);
        navigationMenu.runRegisterCount();
    }

    @Override
    protected void initializePresenter() {
        // Todo
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        // Todo
    }
}

