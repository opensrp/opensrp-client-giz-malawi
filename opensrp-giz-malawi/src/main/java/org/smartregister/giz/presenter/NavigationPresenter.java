package org.smartregister.giz.presenter;

import android.app.Activity;

import org.smartregister.giz.contract.NavigationContract;
import org.smartregister.giz.interactor.NavigationInteractor;
import org.smartregister.giz.model.NavigationModel;
import org.smartregister.giz.model.NavigationOption;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncSettingsServiceJob;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class NavigationPresenter implements NavigationContract.Presenter {

    private NavigationContract.Model mModel;
    private NavigationContract.Interactor mInteractor;
    private WeakReference<NavigationContract.View> mView;

    private HashMap<String, String> tableMap = new HashMap<>();

    public NavigationPresenter(NavigationContract.View view) {
        mView = new WeakReference<>(view);
        mInteractor = NavigationInteractor.getInstance();
        mModel = NavigationModel.getInstance();
        initialize();
    }

    private void initialize() {
        tableMap.put(GizConstants.DrawerMenu.CHILD_CLIENTS, GizConstants.RegisterType.CHILD);
        tableMap.put(GizConstants.DrawerMenu.ANC_CLIENTS, GizConstants.RegisterType.ANC);
        tableMap.put(GizConstants.DrawerMenu.ANC, GizConstants.RegisterType.ANC);
        tableMap.put(GizConstants.DrawerMenu.ALL_CLIENTS, GizConstants.RegisterType.OPD);
    }

    @Override
    public NavigationContract.View getNavigationView() {
        return mView.get();
    }

    @Override
    public void refreshNavigationCount() {
        int navigationItems = 0;
        while (navigationItems < mModel.getNavigationItems().size()) {
            final int finalNavigationItems = navigationItems;
            String menuTitle = mModel.getNavigationItems().get(navigationItems).getMenuTitle();
            mInteractor.getRegisterCount(tableMap.get(menuTitle),
                    new NavigationContract.InteractorCallback<Integer>() {
                        @Override
                        public void onResult(Integer result) {
                            mModel.getNavigationItems().get(finalNavigationItems).setRegisterCount(result);
                            getNavigationView().refreshCount();
                        }

                        @Override
                        public void onError(Exception e) {
                            Timber.e(e, "Error retrieving count for %s",
                                    tableMap.get(mModel.getNavigationItems().get(finalNavigationItems).getMenuTitle()));
                        }
                    });
            navigationItems++;
        }

    }

    @Override
    public void refreshLastSync() {
        // get last sync date
        getNavigationView().refreshLastSync(mInteractor.sync());
    }

    @Override
    public void displayCurrentUser() {
        getNavigationView().refreshCurrentUser(mModel.getCurrentUser());
    }

    @Override
    public void sync(Activity activity) {
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        SyncSettingsServiceJob.scheduleJobImmediately(SyncSettingsServiceJob.TAG);
        ZScoreRefreshIntentServiceJob.scheduleJobImmediately(ZScoreRefreshIntentServiceJob.TAG);
        WeightIntentServiceJob.scheduleJobImmediately(WeightIntentServiceJob.TAG);
        HeightIntentServiceJob.scheduleJobImmediately(HeightIntentServiceJob.TAG);
        VaccineServiceJob.scheduleJobImmediately(VaccineServiceJob.TAG);
        RecurringIndicatorGeneratingJob.scheduleJobImmediately(RecurringIndicatorGeneratingJob.TAG);
    }

    @Override
    public List<NavigationOption> getOptions() {
        return mModel.getNavigationItems();
    }
}
