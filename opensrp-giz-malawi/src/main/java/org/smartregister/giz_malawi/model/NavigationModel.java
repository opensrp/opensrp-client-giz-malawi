package org.smartregister.giz_malawi.model;

import android.util.Log;

import org.smartregister.giz_malawi.contract.NavigationContract;
import org.smartregister.util.Utils;

public class NavigationModel implements NavigationContract.Model {
    private static NavigationModel instance;
    private String TAG = NavigationModel.class.getCanonicalName();

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();

        return instance;
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
