package org.smartregister.giz.presenter;

import android.view.View;
import android.widget.AdapterView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.giz.R;
import org.smartregister.giz.activity.ChildFormActivity;
import org.smartregister.giz.util.GizConstants;

public class ChildFormFragmentPresenter extends org.smartregister.child.presenter.ChildFormFragmentPresenter {

    private JsonFormFragment formFragment;

    public ChildFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
        this.formFragment = formFragment;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
        String key = (String) parent.getTag(R.id.key);
        if (key.equals(GizConstants.MOTHER_TDV_DOSES)) {
            DatePi((ChildFormActivity) formFragment.getActivity()).getFormDataView(JsonFormConstants.STEP1 + ":" + "aefi_start_date");
            MaterialSpinner spinnerMotherTdvDoses = (MaterialSpinner) ((ChildFormActivity) formFragment.getActivity()).getFormDataView(JsonFormConstants.STEP1 + ":" + GizConstants.MOTHER_TDV_DOSES);
            MaterialSpinner spinnerProtectedAtBirth = (MaterialSpinner) ((ChildFormActivity) formFragment.getActivity()).getFormDataView(JsonFormConstants.STEP1 + ":" + GizConstants.PROTECTED_AT_BIRTH);
            if (spinnerMotherTdvDoses.getSelectedItemPosition() == 1) {
                spinnerProtectedAtBirth.setSelection(1, true);
            } else if(spinnerMotherTdvDoses.getSelectedItemPosition() != 0) {
                spinnerProtectedAtBirth.setSelection(2, true);
            } else {
                spinnerProtectedAtBirth.setSelection(0, true);
            }
        }
    }
}
