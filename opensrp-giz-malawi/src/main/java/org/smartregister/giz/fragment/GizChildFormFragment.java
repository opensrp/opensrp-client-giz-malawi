package org.smartregister.giz.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.smartregister.child.fragment.ChildFormFragment;
import org.smartregister.child.presenter.ChildFormFragmentPresenter;
import org.smartregister.giz.interactor.ChildFormInteractor;
import org.smartregister.giz.presenter.GizChildFormFragmentPresenter;

import java.lang.ref.WeakReference;

public class GizChildFormFragment extends ChildFormFragment {

    private OnReactionVaccineSelected OnReactionVaccineSelected;

    public static GizChildFormFragment getFormFragment(String stepName) {
        GizChildFormFragment jsonFormFragment = new GizChildFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    public OnReactionVaccineSelected getOnReactionVaccineSelected() {
        return OnReactionVaccineSelected;
    }

    public void setOnReactionVaccineSelected(OnReactionVaccineSelected onReactionVaccineSelected) {
        this.OnReactionVaccineSelected = onReactionVaccineSelected;
    }

    @Override
    protected ChildFormFragmentPresenter createPresenter() {
        WeakReference gizChildFormFragmentWeakReference = new WeakReference<>(this);
        return new GizChildFormFragmentPresenter((JsonFormFragment) gizChildFormFragmentWeakReference.get(), ChildFormInteractor.getChildInteractorInstance());
    }

    public interface OnReactionVaccineSelected {
        void updateDatePicker(String date);
    }

    @Override
    public void onDestroy() {
        setOnReactionVaccineSelected(null);
        super.onDestroy();
    }
}
