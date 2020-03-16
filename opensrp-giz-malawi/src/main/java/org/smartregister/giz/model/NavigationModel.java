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
            NavigationOption childNavigationOption = new NavigationOption(R.mipmap.sidemenu_children,
                    R.mipmap.sidemenu_children_active, R.string.menu_child_clients, GizConstants.DrawerMenu.CHILD_CLIENTS,
                    0);

            /*
            -- THIS CODE WILL BE UNCOMMENTED WHEN THE MODULES ARE ENABLED IN PRODUCTION --
            */

            NavigationOption ancNavigationOption = new NavigationOption(R.mipmap.sidemenu_children,
                    R.mipmap.sidemenu_children_active, R.string.menu_anc_clients, GizConstants.DrawerMenu.ANC_CLIENTS,
                    0);

            NavigationOption allClientsOption = new NavigationOption(R.mipmap.sidemenu_children
                    , R.mipmap.sidemenu_children_active, R.string.all_clients, GizConstants.DrawerMenu.ALL_CLIENTS, 0);

//            navigationOptions.addAll(asList(allClientsOption, childNavigationOption, ancNavigationOption));


            navigationOptions.addAll(asList(childNavigationOption, ancNavigationOption));
        }

        return navigationOptions;
    }

    @Override
    public String getCurrentUser() {
        String prefferedName = "";
        try {
            prefferedName = Utils.getPrefferedName().split(" ")[0];
        } catch (Exception e) {
            Timber.e(e, "NavigationModel --> getCurrentUser");
        }

        return prefferedName;
    }
}
