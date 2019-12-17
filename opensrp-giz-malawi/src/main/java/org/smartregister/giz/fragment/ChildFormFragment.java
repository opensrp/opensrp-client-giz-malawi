package org.smartregister.giz.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.child.presenter.ChildFormFragmentPresenter;
import org.smartregister.giz.interactor.ChildFormInteractor;

public class ChildFormFragment extends org.smartregister.child.fragment.ChildFormFragment {

    public OnReactionVaccineSelected getOnReactionVaccineSelected() {
        return OnReactionVaccineSelected;
    }

    public void setOnReactionVaccineSelected(OnReactionVaccineSelected onReactionVaccineSelected) {
        this.OnReactionVaccineSelected = onReactionVaccineSelected;
    }

    OnReactionVaccineSelected OnReactionVaccineSelected;

    @Override
    protected ChildFormFragmentPresenter createPresenter() {
        return new org.smartregister.giz.presenter.ChildFormFragmentPresenter(this, ChildFormInteractor.getChildInteractorInstance());
    }

    public static ChildFormFragment getFormFragment(String stepName) {
        ChildFormFragment jsonFormFragment = new ChildFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    public interface OnReactionVaccineSelected {
        void updateDatePicker(String date);
    }
}
