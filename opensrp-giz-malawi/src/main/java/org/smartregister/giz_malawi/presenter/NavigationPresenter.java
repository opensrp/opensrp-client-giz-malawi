package org.smartregister.giz_malawi.presenter;

import android.app.Activity;

import org.smartregister.giz_malawi.contract.NavigationContract;
import org.smartregister.giz_malawi.interactor.NavigationInteractor;
import org.smartregister.giz_malawi.model.NavigationModel;
import org.smartregister.giz_malawi.model.NavigationOption;
import org.smartregister.giz_malawi.util.GizConstants;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.SyncServiceJob;

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
        tableMap.put(GizConstants.DrawerMenu.ALL_FAMILIES, GizConstants.TABLE_NAME.FAMILY);
        tableMap.put(GizConstants.DrawerMenu.CHILD_CLIENTS, GizConstants.TABLE_NAME.CHILD);
        tableMap.put(GizConstants.DrawerMenu.ANC_CLIENTS, GizConstants.TABLE_NAME.ANC_MEMBER);
        tableMap.put(GizConstants.DrawerMenu.ANC, GizConstants.TABLE_NAME.ANC_MEMBER);
    }

    @Override
    public NavigationContract.View getNavigationView() {
        return mView.get();
    }

    @Override
    public void refreshNavigationCount(final Activity activity) {

        int x = 0;
        while (x < mModel.getNavigationItems().size()) {
            final int finalX = x;
            mInteractor.getRegisterCount(tableMap.get(mModel.getNavigationItems().get(x).getMenuTitle()),
                    new NavigationContract.InteractorCallback<Integer>() {
                        @Override
                        public void onResult(Integer result) {
                            mModel.getNavigationItems().get(finalX).setRegisterCount(result);
                            getNavigationView().refreshCount();
                        }

                        @Override
                        public void onError(Exception e) {
                            // getNavigationView().displayToast(activity, "Error retrieving count for " + tableMap.get(mModel.getNavigationItems().get(finalX).getMenuTitle()));
                            Timber.e("Error retrieving count for %s",
                                    tableMap.get(mModel.getNavigationItems().get(finalX).getMenuTitle()));
                        }
                    });
            x++;
        }

    }

    @Override
    public void refreshLastSync() {
        // get last sync date
        getNavigationView().refreshLastSync(mInteractor.Sync());
    }

    @Override
    public void displayCurrentUser() {
        getNavigationView().refreshCurrentUser(mModel.getCurrentUser());
    }

    @Override
    public void sync(Activity activity) {
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        ZScoreRefreshIntentServiceJob.scheduleJobImmediately(ZScoreRefreshIntentServiceJob.TAG);
        WeightIntentServiceJob.scheduleJobImmediately(WeightIntentServiceJob.TAG);
        HeightIntentServiceJob.scheduleJobImmediately(HeightIntentServiceJob.TAG);
        VaccineServiceJob.scheduleJobImmediately(VaccineServiceJob.TAG);
    }

    @Override
    public List<NavigationOption> getOptions() {
        return mModel.getNavigationItems();
    }
}
