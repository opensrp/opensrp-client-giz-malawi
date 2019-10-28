package org.smartregister.giz.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;

import org.smartregister.opd.adapter.ClientLookUpListAdapter;
import org.smartregister.opd.fragment.BaseOpdFormFragment;
import org.smartregister.opd.interactor.OpdFormInteractor;
import org.smartregister.opd.presenter.OpdFormFragmentPresenter;


public class OpdFormFragment extends BaseOpdFormFragment implements ClientLookUpListAdapter.ClickListener {

    private OpdFormFragmentPresenter presenter;

    @Override
    protected JsonWizardFormFragmentPresenter createPresenter() {
        presenter = new OpdFormFragmentPresenter(this, new OpdFormInteractor());
        return presenter;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static JsonWizardFormFragment getFormFragment(String stepName) {
        OpdFormFragment jsonFormFragment = new OpdFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }
}
