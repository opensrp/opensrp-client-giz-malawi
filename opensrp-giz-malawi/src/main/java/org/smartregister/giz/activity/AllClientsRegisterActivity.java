package org.smartregister.giz.activity;

import android.content.Intent;

import org.smartregister.giz.fragment.AllClientsRegisterFragment;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.view.NavigationMenu;
import org.smartregister.view.fragment.BaseRegisterFragment;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-17
 */

public class AllClientsRegisterActivity extends OpdRegisterActivity {
    private NavigationMenu navigationMenu;

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new AllClientsRegisterFragment();
    }

    public void createDrawer() {
        navigationMenu = NavigationMenu.getInstance(this, null, null);
        if (navigationMenu != null) {
            navigationMenu.getNavigationAdapter().setSelectedView(GizConstants.DrawerMenu.ALL_CLIENTS);
            navigationMenu.runRegisterCount();
        }
    }

    @Override
    public NavigationMenu getNavigationMenu() {
        return navigationMenu;
    }

    @Override
    public void openDrawer() {
        if (navigationMenu != null) {
            navigationMenu.openDrawer();
        }
    }


    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, AllClientsRegisterActivity.class);
        startActivity(intent);
        finish();
    }
}