package org.smartregister.giz.model;

import android.util.Log;

import org.smartregister.giz.R;
import org.smartregister.giz.contract.NavigationContract;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class NavigationModel implements NavigationContract.Model {
    private static NavigationModel instance;
    private static List<NavigationOption> navigationOptions = new ArrayList<>();
    private String TAG = NavigationModel.class.getCanonicalName();

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
            navigationOptions.addAll(asList(childNavigationOption));
        }

        return navigationOptions;
    }

    @Override
    public String getCurrentUser() {
        String res = "";
        try {
            res = Utils.getPrefferedName().split(" ")[0];
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return res;
    }
}
