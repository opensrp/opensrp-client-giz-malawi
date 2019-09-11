package org.smartregister.giz.activity;

import android.support.design.widget.BottomNavigationView;
import android.view.View;

import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.giz.fragment.AncRegisterFragment;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.view.NavigationMenu;
import org.smartregister.view.fragment.BaseRegisterFragment;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-09
 */

public class AncRegisterActivity extends BaseHomeRegisterActivity {

    private NavigationMenu navigationMenu;

    @Override
    protected void registerBottomNavigation() {
        // Do nothing because the bottom navigation was removed and overriden by someone
    }

    @Override
    public void setSelectedBottomBarMenuItem(int itemId) {
        // Do nothing here
    }

    public void createDrawer() {
        navigationMenu = NavigationMenu.getInstance(this, null, null);
        navigationMenu.getNavigationAdapter().setSelectedView(GizConstants.DrawerMenu.ANC_CLIENTS);
        navigationMenu.runRegisterCount();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        createDrawer();
    }

    @Override
    public BaseRegisterFragment getRegisterFragment() {
        return new AncRegisterFragment();
    }

    @Override
    public boolean isLibraryItemEnabled() {
        return false;
    }

    @Override
    public boolean isMeItemEnabled() {
        return false;
    }

    @Override
    public boolean isAdvancedSearchEnabled() {
        return false;
    }

    public void openDrawer() {
        if (navigationMenu != null) {
            navigationMenu.openDrawer();
        }
    }

    public void closeDrawer() {
        if (navigationMenu != null) {
            navigationMenu.closeDrawer();
        }
    }
}
