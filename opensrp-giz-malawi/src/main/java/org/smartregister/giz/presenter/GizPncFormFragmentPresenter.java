package org.smartregister.giz.presenter;

import android.text.TextUtils;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.giz.R;
import org.smartregister.giz.fragment.GizPncFormFragment;
import org.smartregister.giz.task.OpenChildVaccineCardTask;
import org.smartregister.pnc.fragment.BasePncFormFragment;
import org.smartregister.pnc.presenter.PncFormFragmentPresenter;

public class GizPncFormFragmentPresenter extends PncFormFragmentPresenter {

    public GizPncFormFragmentPresenter(BasePncFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }


    @Override
    protected boolean moveToNextWizardStep() {
        if (!TextUtils.isEmpty(mStepDetails.optString(JsonFormConstants.NEXT))) {
            JsonFormFragment next = GizPncFormFragment.getFormFragment(mStepDetails.optString(JsonFormConstants.NEXT));
            getView().hideKeyBoard();
            getView().transactThis(next);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (StringUtils.isNotBlank(key) && key.startsWith("open_vaccine_card")) {
            String baseEntityId = (String) v.getTag(R.id.raw_value);
            new OpenChildVaccineCardTask(baseEntityId, getFormFragment().getActivity()).execute();
        }
    }
}
