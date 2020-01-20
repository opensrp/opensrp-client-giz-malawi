package org.smartregister.giz.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.child.interactor.ChildFormInteractor;
import org.smartregister.giz.util.GizConstants;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class GizMalawiJsonFormFragment extends JsonFormFragment {

    public static GizMalawiJsonFormFragment getFormFragment(String stepName) {
        GizMalawiJsonFormFragment jsonFormFragment = new GizMalawiJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(GizConstants.KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this, ChildFormInteractor.getChildInteractorInstance());
    }

}