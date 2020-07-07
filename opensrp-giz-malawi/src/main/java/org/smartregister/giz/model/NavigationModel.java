package org.smartregister.giz.model;

import org.smartregister.giz.R;
import org.smartregister.giz.contract.NavigationContract;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class NavigationModel implements NavigationContract.Model {
    private static NavigationModel instance;
    private static List<NavigationOption> navigationOptions = new ArrayList<>();

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();

        return instance;
    }

    @Override
    public List<NavigationOption> getNavigationItems() {
        if (navigationOptions.size() == 0) {

            NavigationOption allClientsOption = new NavigationOption(R.mipmap.sidemenu_families
                    , R.mipmap.sidemenu_families_active, R.string.all_clients, GizConstants.DrawerMenu.ALL_CLIENTS, 0, true);

            if (allClientsOption.isEnabled()) {
                navigationOptions.add(allClientsOption);
            }

            NavigationOption opdNavigationOption = new NavigationOption(R.mipmap.sidemenu_families,
                    R.mipmap.sidemenu_families_active, R.string.menu_opd_clients, GizConstants.DrawerMenu.OPD_CLIENTS,
                    0, true);
            if (opdNavigationOption.isEnabled()) {
                navigationOptions.add(opdNavigationOption);
            }

            NavigationOption childNavigationOption = new NavigationOption(R.mipmap.sidemenu_children,
                    R.mipmap.sidemenu_children_active, R.string.menu_child_clients, GizConstants.DrawerMenu.CHILD_CLIENTS,
                    0, true);
            if (childNavigationOption.isEnabled()) {
                navigationOptions.add(childNavigationOption);
            }

            NavigationOption ancNavigationOption = new NavigationOption(R.mipmap.sidemenu_anc,
                    R.mipmap.sidemenu_anc_active, R.string.menu_anc_clients, GizConstants.DrawerMenu.ANC_CLIENTS,
                    0, true);
            if (ancNavigationOption.isEnabled()) {
                navigationOptions.add(ancNavigationOption);
            }

            // Maternity navigation
            NavigationOption maternityNavigationOption = new NavigationOption(R.mipmap.sidemenu_maternity,
                    R.mipmap.sidemenu_maternity_active, R.string.menu_maternity_clients, GizConstants.DrawerMenu.MATERNITY_CLIENTS,
                    0, true);
            if (maternityNavigationOption.isEnabled()) {
                navigationOptions.add(maternityNavigationOption);
            }

            // Pnc navigation
            NavigationOption pncNavigationOption = new NavigationOption(R.mipmap.sidemenu_families,
                    R.mipmap.sidemenu_families_active, R.string.menu_pnc_clients, GizConstants.DrawerMenu.PNC_CLIENTS,
                    0, true);
            if (pncNavigationOption.isEnabled()) {
                navigationOptions.add(pncNavigationOption);
            }
        }

        return navigationOptions;
    }

    @Override
    public String getCurrentUser() {
        String preferredName = "";
        try {
            preferredName = Utils.getPrefferedName().split(" ")[0];
        } catch (Exception e) {
            Timber.e(e);
        }

        return preferredName;
    }
}
