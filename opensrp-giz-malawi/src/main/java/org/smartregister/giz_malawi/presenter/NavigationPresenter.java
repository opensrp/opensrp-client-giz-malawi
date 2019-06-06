package org.smartregister.giz_malawi.presenter;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.smartregister.giz_malawi.application.GizMalawiApplication;
import org.smartregister.giz_malawi.contract.NavigationContract;
import org.smartregister.giz_malawi.interactor.NavigationInteractor;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;

public class NavigationPresenter implements NavigationContract.Presenter {

    private AllSharedPreferences allSharedPreferences;
    private WeakReference<NavigationContract.View> mView;
    private NavigationContract.Interactor mInteractor;

    public NavigationPresenter(NavigationContract.View view) {
        mView = new WeakReference<>(view);
        allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(
                GizMalawiApplication.getInstance().getApplicationContext()));
        mInteractor = NavigationInteractor.getInstance();
    }

    @Override
    public NavigationContract.View getNavigationView() {
        return mView.get();
    }

    @Override
    public void displayCurrentUser() {
        getNavigationView().refreshCurrentUser(allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM()));
    }

    @Override
    public String getLoggedInUserInitials() {

        try {
            String preferredName = allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM());
            if (!TextUtils.isEmpty(preferredName)) {
                String[] initialsArray = preferredName.split(" ");
                String initials = "";
                if (initialsArray.length > 0) {
                    initials = initialsArray[0].substring(0, 1);
                    if (initialsArray.length > 1) {
                        initials = initials + initialsArray[1].substring(0, 1);
                    }
                }
                return initials.toUpperCase();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void sync(Activity activity) {
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        WeightIntentServiceJob.scheduleJobImmediately(WeightIntentServiceJob.TAG);
        VaccineServiceJob.scheduleJobImmediately(VaccineServiceJob.TAG);
    }

    @Override
    public void refreshLastSync() {
        getNavigationView().refreshLastSync(mInteractor.getLastSync());
    }
}
