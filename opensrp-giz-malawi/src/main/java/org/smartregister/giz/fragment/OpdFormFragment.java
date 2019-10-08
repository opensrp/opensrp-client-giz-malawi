package org.smartregister.giz.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.smartregister.giz.activity.OpdFormActivity;
import org.smartregister.opd.adapter.ClientLookUpListAdapter;
import org.smartregister.opd.fragment.BaseOpdFormFragment;
import org.smartregister.opd.interactor.OpdFormInteractor;
import org.smartregister.opd.presenter.OpdFormFragmentPresenter;

import java.util.HashMap;


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

    @Override
    public void finishWithResult(Intent returnIntent) {
        Activity activity = getActivity();

        if (activity instanceof OpdFormActivity) {
            OpdFormActivity opdFormActivity = (OpdFormActivity) activity;

            HashMap<String, String> parcelableData = opdFormActivity.getParcelableData();

            for (String key: parcelableData.keySet()) {
                String value = parcelableData.get(key);

                if (value != null) {
                    returnIntent.putExtra(key, value);
                }
            }
        }

        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }
}
