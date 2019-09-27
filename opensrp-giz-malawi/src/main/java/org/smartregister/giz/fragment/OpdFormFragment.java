package org.smartregister.giz.fragment;


import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.smartregister.opd.adapter.ClientLookUpListAdapter;
import org.smartregister.opd.fragment.BaseOpdFormFragment;
import org.smartregister.opd.interactor.OpdFormInteractor;
import org.smartregister.opd.presenter.OpdFormFragmentPresenter;


public class OpdFormFragment extends BaseOpdFormFragment implements ClientLookUpListAdapter.ClickListener {

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    protected OpdFormFragmentPresenter createPresenter() {
        return new OpdFormFragmentPresenter(this, OpdFormInteractor.getOpdInteractorInstance());
    }


    public static OpdFormFragment getFormFragment(String stepName) {
        OpdFormFragment jsonFormFragment = new OpdFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

}
