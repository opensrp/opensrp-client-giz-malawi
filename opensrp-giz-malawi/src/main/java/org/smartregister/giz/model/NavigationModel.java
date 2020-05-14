package org.smartregister.giz.model;

import org.smartregister.giz.R;
import org.smartregister.giz.contract.NavigationContract;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static java.util.Arrays.asList;

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

            NavigationOption allClientsOption = new NavigationOption(R.mipmap.sidemenu_children
                    , R.mipmap.sidemenu_children_active, R.string.all_clients, GizConstants.DrawerMenu.ALL_CLIENTS, 0, true);

            if (allClientsOption.isEnabled()) {
                navigationOptions.add(allClientsOption);
            }

            NavigationOption childNavigationOption = new NavigationOption(R.mipmap.sidemenu_children,
                    R.mipmap.sidemenu_children_active, R.string.menu_child_clients, GizConstants.DrawerMenu.CHILD_CLIENTS,
                    0, true);
            if (childNavigationOption.isEnabled()) {
                navigationOptions.add(childNavigationOption);
            }

            NavigationOption ancNavigationOption = new NavigationOption(R.mipmap.sidemenu_children,
                    R.mipmap.sidemenu_children_active, R.string.menu_anc_clients, GizConstants.DrawerMenu.ANC_CLIENTS,
                    0, true);
            if (ancNavigationOption.isEnabled()) {
                navigationOptions.add(ancNavigationOption);
            }

            // Maternity navigation
            NavigationOption maternityNavigationOption = new NavigationOption(R.mipmap.sidemenu_children,
                    R.mipmap.sidemenu_children_active, R.string.menu_maternity_clients, GizConstants.DrawerMenu.MATERNITY_CLIENTS,
                    0, true);
            if (maternityNavigationOption.isEnabled()) {
                navigationOptions.add(maternityNavigationOption);
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
