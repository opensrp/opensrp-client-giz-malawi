package org.smartregister.giz.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;

import org.smartregister.giz.presenter.GizPncFormFragmentPresenter;
import org.smartregister.pnc.fragment.BasePncFormFragment;
import org.smartregister.pnc.interactor.PncFormInteractor;

public class GizPncFormFragment extends BasePncFormFragment {

    private GizPncFormFragmentPresenter presenter;

    public static JsonWizardFormFragment getFormFragment(String stepName) {
        BasePncFormFragment jsonFormFragment = new GizPncFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    public GizPncFormFragmentPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected JsonWizardFormFragmentPresenter createPresenter() {
        presenter = new GizPncFormFragmentPresenter(this, PncFormInteractor.getInstance());
        return presenter;
    }
}
