package org.smartregister.giz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vijay.jsonwizard.domain.Form;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.giz.adapter.GroupedListableAdapter;
import org.smartregister.giz.adapter.ProfileHistoryAdapter;
import org.smartregister.giz.contract.GizOpdProfileFragmentContract;
import org.smartregister.giz.dao.GizVisitDao;
import org.smartregister.giz.domain.ProfileHistory;
import org.smartregister.giz.presenter.GizOpdProfileVisitsFragmentPresenter;
import org.smartregister.giz.util.FormProcessor;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.viewholder.GroupedListableViewHolder;
import org.smartregister.opd.listener.OnSendActionToFragment;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.view.fragment.BaseListFragment;

import java.util.List;
import java.util.concurrent.Callable;

import timber.log.Timber;

public class GizOpdProfileVisitsFragment extends BaseListFragment<ProfileHistory> implements OnSendActionToFragment, GizOpdProfileFragmentContract.View<ProfileHistory>, FormProcessor.Requester {

    private String baseEntityID;

    public static GizOpdProfileVisitsFragment newInstance(Bundle bundle) {
        GizOpdProfileVisitsFragment fragment = new GizOpdProfileVisitsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
                    .getSerializable(OpdConstants.IntentKey.CLIENT_OBJECT);

            if (commonPersonObjectClient != null) {
                baseEntityID = commonPersonObjectClient.getCaseId();
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @NotNull
    @Override
    protected Callable<List<ProfileHistory>> onStartCallable(@Nullable @org.jetbrains.annotations.Nullable Bundle bundle) {
        return () -> GizVisitDao.getVisitHistory(baseEntityID);
    }

    @Override
    protected int getRootLayout() {
        return R.layout.opd_fragment_profile_overview;
    }

    @Override
    protected int getRecyclerViewID() {
        return R.id.opd_profile_overview_recycler;
    }

    @Override
    protected int getProgressBarID() {
        return R.id.progress_bar;
    }

    @Override
    public void onListItemClicked(ProfileHistory profileHistory, int layoutID) {
        if (layoutID != R.id.tv_edit) return;

        String formName;
        switch (profileHistory.getEventType()) {
            case GizConstants.OpdModuleEvents.OPD_CHECK_IN:
                formName = GizConstants.JsonForm.OPD_CHECKIN;
                break;
            case GizConstants.OpdModuleEvents.OPD_VITAL_DANGER_SIGNS_CHECK:
                formName = GizConstants.JsonForm.VITAL_DANGER_SIGNS;
                break;
            case GizConstants.OpdModuleEvents.OPD_DIAGNOSIS:
                formName = GizConstants.JsonForm.DIAGNOSIS;
                break;
            case GizConstants.OpdModuleEvents.OPD_TREATMENT:
                formName = GizConstants.JsonForm.TREATMENT;
                break;
            case GizConstants.OpdModuleEvents.OPD_LABORATORY:
                formName = GizConstants.JsonForm.LAB_RESULTS;
                break;
            case GizConstants.OpdModuleEvents.OPD_PHARMACY:
                formName = GizConstants.JsonForm.PHARMACY;
                break;
            case GizConstants.OpdModuleEvents.OPD_FINAL_OUTCOME:
                formName = GizConstants.JsonForm.FINAL_OUTCOME;
                break;
            case GizConstants.OpdModuleEvents.OPD_SERVICE_CHARGE:
                formName = GizConstants.JsonForm.SERVICE_FEE;
                break;
            default:
                throw new IllegalArgumentException("Unknown Form");
        }

        loadPresenter().openForm(getContext(), formName, baseEntityID, profileHistory.getID());
    }

    @Override
    public void onFormProcessingResult(String jsonForm) {
        loadPresenter().saveForm(jsonForm, getContext());
    }

    public FormProcessor.Host getHostFormProcessor() {
        return (FormProcessor.Host) getActivity();
    }

    @Override
    public void onFetchError(Exception e) {
        Toast.makeText(getContext(), "An error occurred. " + e.getMessage(), Toast.LENGTH_SHORT).show();
        Timber.e(e);
    }

    @NonNull
    @Override
    public GroupedListableAdapter<ProfileHistory, GroupedListableViewHolder<ProfileHistory>> adapter() {
        return new ProfileHistoryAdapter(list, this);
    }

    @Override
    public void onActionReceive() {
        //DO NOTHING
    }

    @NonNull
    @NotNull
    @Override
    public GizOpdProfileVisitsFragmentPresenter loadPresenter() {
        if (presenter == null) {
            presenter = new GizOpdProfileVisitsFragmentPresenter()
                    .with(this);
        }
        return (GizOpdProfileVisitsFragmentPresenter) presenter;
    }

    @Override
    public void startJsonForm(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        getHostFormProcessor().startForm(jsonObject, form, this);
    }

    @Override
    public void reloadFromSource() {
        loadPresenter().fetchList(this.onStartCallable(this.getArguments()), this.fetchRequestType());
    }


}
