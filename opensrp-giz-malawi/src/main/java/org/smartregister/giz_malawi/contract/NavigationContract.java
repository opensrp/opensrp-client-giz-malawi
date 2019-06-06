package org.smartregister.giz_malawi.contract;

import android.app.Activity;

import java.util.Date;

public interface NavigationContract {

    interface Presenter {

        NavigationContract.View getNavigationView();

        void displayCurrentUser();

        String getLoggedInUserInitials();

        void refreshLastSync();

        void sync(Activity activity);
    }

    interface View {

        void refreshCurrentUser(String name);

        void logout(Activity activity);

        void prepareViews(Activity activity);

        void refreshLastSync(Date lastSync);
    }

    interface Interactor {

        Date getLastSync();

    }


}
