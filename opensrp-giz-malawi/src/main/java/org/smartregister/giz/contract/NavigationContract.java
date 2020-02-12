package org.smartregister.giz.contract;

import android.app.Activity;

import org.smartregister.giz.model.NavigationOption;

import java.util.Date;
import java.util.List;

public interface NavigationContract {

    interface Presenter {

        NavigationContract.View getNavigationView();

        void refreshNavigationCount();

        void refreshLastSync();

        void displayCurrentUser();

        void sync(Activity activity);

        List<NavigationOption> getOptions();
    }

    interface View {

        void prepareViews(Activity activity);

        void refreshLastSync(Date lastSync);

        void refreshCurrentUser(String name);

        void logout(Activity activity);

        void refreshCount();
    }

    interface Model {

        List<NavigationOption> getNavigationItems();

        String getCurrentUser();
    }

    interface Interactor {

        void getRegisterCount(String tableName, InteractorCallback<Integer> callback);

        Date sync();

    }

    interface InteractorCallback<T> {
        void onResult(T result);

        void onError(Exception e);
    }
}
