package org.smartregister.giz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.maternity.activity.BaseMaternityProfileActivity;
import org.smartregister.maternity.domain.YamlConfigWrapper;
import org.smartregister.maternity.fragment.MaternityProfileVisitsFragment;
import org.smartregister.maternity.utils.MaternityConstants;

import java.util.ArrayList;
import java.util.List;

public class GizMaternityProfileVisitsFragment extends MaternityProfileVisitsFragment implements View.OnClickListener {

    public static MaternityProfileVisitsFragment newInstance(@Nullable Bundle bundle) {
        Bundle args = bundle;
        MaternityProfileVisitsFragment fragment = new GizMaternityProfileVisitsFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(org.smartregister.maternity.R.layout.maternity_fragment_profile_visits, container, false);

        Button btnAncHistory = fragmentView.findViewById(R.id.btn_anc_history);
        btnAncHistory.setOnClickListener(this);
        TextView txtAncHistory = fragmentView.findViewById(R.id.txt_no_anc_history);

        GizMalawiApplication.getInstance().getAppExecutors().diskIO().execute(() -> {
            boolean hasAncProfile = hasAncProfile();
            GizMalawiApplication.getInstance().getAppExecutors().mainThread().execute(() -> {
                if (hasAncProfile) {
                    txtAncHistory.setVisibility(View.GONE);
                    btnAncHistory.setVisibility(View.VISIBLE);
                } else {
                    txtAncHistory.setVisibility(View.VISIBLE);
                    btnAncHistory.setVisibility(View.GONE);
                }
            });

        });


        return fragmentView;
    }

    private boolean hasAncProfile() {
        return ((BaseMaternityProfileActivity) getActivity()).presenter().hasAncProfile();
    }

    @Override
    protected void onResumption() {
        //Not implemented
    }

    @Override
    public void showPageCountText(@NonNull String pageCounterText) {
        //Not implemented

    }

    @Override
    public void showNextPageBtn(boolean show) {
        //Not implemented
    }

    @Override
    public void showPreviousPageBtn(boolean show) {
        //Not implemented
    }

    @Override
    public void displayVisits(@NonNull List<Object> ancVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
        //Not implemented
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_anc_history) {
            BaseMaternityProfileActivity activity = (BaseMaternityProfileActivity) getActivity();
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) activity.getIntent()
                    .getSerializableExtra(MaternityConstants.IntentKey.CLIENT_OBJECT);
            commonPersonObjectClient.getColumnmaps().put(GizConstants.IS_FROM_MATERNITY, "true");
            GizUtils.openAncProfilePage(commonPersonObjectClient, activity);
        }
    }
}
