package org.smartregister.giz.fragment;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.jetbrains.annotations.NotNull;
import org.smartregister.child.fragment.ChildFormFragment;
import org.smartregister.child.presenter.ChildFormFragmentPresenter;
import org.smartregister.child.util.Constants;
import org.smartregister.giz.interactor.ChildFormInteractor;
import org.smartregister.giz.presenter.GizChildFormFragmentPresenter;

import java.lang.ref.WeakReference;
import java.util.HashMap;

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

    @Override
    protected @NotNull HashMap<String, String> getKeyAliasMap() {
        return new HashMap<String, String>() {
            {
                put("mother_guardian_last_name", Constants.KEY.LAST_NAME);
                put("mother_guardian_first_name", Constants.KEY.FIRST_NAME);
                put("mother_guardian_date_birth", Constants.KEY.DOB);
            }
        };
    }
}
