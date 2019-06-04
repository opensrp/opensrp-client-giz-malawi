package org.smartregister.giz_malawi.interactor;

import org.smartregister.child.ChildLibrary;
import org.smartregister.giz_malawi.contract.NavigationContract;

import java.util.Date;

public class NavigationInteractor implements NavigationContract.Interactor {

    private static NavigationInteractor instance;

    private NavigationInteractor() {

    }

    public static NavigationInteractor getInstance() {
        if (instance == null)
            instance = new NavigationInteractor();

        return instance;
    }

    @Override
    public Date getLastSync() {
        return new Date(ChildLibrary.getInstance().getEcSyncHelper().getLastCheckTimeStamp());
    }

}
