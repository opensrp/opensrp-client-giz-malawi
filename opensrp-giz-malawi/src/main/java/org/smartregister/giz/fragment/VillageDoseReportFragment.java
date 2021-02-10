package org.smartregister.giz.fragment;

import androidx.annotation.NonNull;

import org.smartregister.giz.adapter.VillageDoseAdapter;
import org.smartregister.giz.dao.ReportDao;
import org.smartregister.giz.domain.VillageDose;
import org.smartregister.giz.model.FilterReportFragmentModel;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.adapter.ListableAdapter;
import org.smartregister.view.viewholder.ListableViewHolder;

import java.util.ArrayList;
import java.util.List;

public class VillageDoseReportFragment extends ReportResultFragment<VillageDose> {
    public static final String TAG = "VillageDoseReportFragment";

    @Override
    protected void executeFetch() {
        presenter.fetchList(() -> {
            boolean includeAll = communityName.equalsIgnoreCase("All communities");
            FilterReportFragmentModel model = new FilterReportFragmentModel();
            List<VillageDose> result = new ArrayList<>(ReportDao.fetchLiveVillageDosesReport(communityID, reportDate, includeAll,
                    includeAll ? communityName : null, model.getAllLocations()));

            return result;
        }, fetchRequestType());
    }

    private AppExecutors.Request fetchRequestType() {
        return AppExecutors.Request.DISK_THREAD;
    }

    @Override
    public void onFetchError(Exception e) {
        // Do nothing
    }

    @NonNull
    @Override
    public ListableAdapter<VillageDose, ListableViewHolder<VillageDose>> adapter() {
        return new VillageDoseAdapter(list, this, this.getContext());
    }

    @Override
    public boolean hasDivider() {
        return false;
    }
}

