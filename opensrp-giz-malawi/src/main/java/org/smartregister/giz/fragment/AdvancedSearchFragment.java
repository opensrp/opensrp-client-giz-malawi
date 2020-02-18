package org.smartregister.giz.fragment;

import android.text.TextUtils;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.giz.R;
import org.smartregister.giz.presenter.AdvancedSearchPresenter;
import org.smartregister.giz.util.DBQueryHelper;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 08/03/2019.
 */
public class AdvancedSearchFragment extends BaseAdvancedSearchFragment {
    protected MaterialEditText openSrpId;
    protected MaterialEditText motherGuardianLastName;
    protected MaterialEditText motherGuardianFirstName;
    protected MaterialEditText motherGuardianNid;
    protected MaterialEditText motherGuardianPhoneNumber;
    private AdvancedSearchPresenter presenter;
    private MaterialEditText firstName;
    private MaterialEditText lastName;

    @Override
    protected BaseChildAdvancedSearchPresenter getPresenter() {
        if (presenter == null) {
            String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
            presenter = new AdvancedSearchPresenter(this, viewConfigurationIdentifier);
        }

        return presenter;
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomeRegisterCondition();
    }

    @Override
    protected Map<String, String> getSearchMap(boolean searchFlag) {
        Map<String, String> searchParams = new HashMap<>();

        String firstName = this.firstName.getText().toString();
        String lastName = this.lastName.getText().toString();


        String motherGuardianFirstNameString = motherGuardianFirstName.getText().toString();
        String motherGuardianLastNameString = motherGuardianLastName.getText().toString();
        String motherGuardianNidString = motherGuardianNid.getText().toString();
        String motherGuardianPhoneNumberString = motherGuardianPhoneNumber.getText().toString();
        String merId = openSrpId.getText().toString();


        if (StringUtils.isNotBlank(motherGuardianFirstNameString)) {
            searchParams.put(GizConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianLastNameString)) {
            searchParams.put(GizConstants.KEY.MOTHER_FIRST_NAME, motherGuardianLastNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianNidString)) {
            searchParams.put(GizConstants.KEY.MOTHER_NRC_NUMBER, motherGuardianNidString);
        }

        if (StringUtils.isNotBlank(motherGuardianPhoneNumberString)) {
            searchParams.put(GizConstants.KEY.MOTHER_GUARDIAN_NUMBER, motherGuardianPhoneNumberString);
        }


        if (!TextUtils.isEmpty(firstName)) {
            searchParams.put(GizConstants.KEY.FIRST_NAME, firstName);
        }

        if (!TextUtils.isEmpty(lastName)) {
            searchParams.put(GizConstants.KEY.LAST_NAME, lastName);
        }

        if (!TextUtils.isEmpty(merId)) {
            searchParams.put(GizConstants.KEY.ZEIR_ID, merId);
        }

        return searchParams;
    }

    @Override
    public void assignedValuesBeforeBarcode() {
        if (searchFormData.size() > 0) {
            firstName.setText(searchFormData.get(GizConstants.KEY.FIRST_NAME));
            lastName.setText(searchFormData.get(GizConstants.KEY.LAST_NAME));
            motherGuardianFirstName.setText(searchFormData.get(GizConstants.KEY.MOTHER_FIRST_NAME));
            motherGuardianLastName.setText(searchFormData.get(GizConstants.KEY.MOTHER_LAST_NAME));
            motherGuardianNid.setText(searchFormData.get(GizConstants.KEY.MOTHER_NRC_NUMBER));
            motherGuardianPhoneNumber.setText(searchFormData.get(GizConstants.KEY.MOTHER_GUARDIAN_NUMBER));
            openSrpId.setText(searchFormData.get(GizConstants.KEY.ZEIR_ID));
        }
    }

    @Override
    public void populateSearchableFields(View view) {
        //TODO: Come back and re-enable here
//        firstName = view.findViewById(R.id.first_name);
//        advancedFormSearchableFields.put(GizConstants.KEY.FIRST_NAME, firstName);
//
//        lastName = view.findViewById(R.id.last_name);
//        advancedFormSearchableFields.put(GizConstants.KEY.LAST_NAME, lastName);
//
//        openSrpId = view.findViewById(R.id.opensrp_id);
//        advancedFormSearchableFields.put(GizConstants.KEY.ZEIR_ID, openSrpId);
//
//        motherGuardianFirstName = view.findViewById(R.id.mother_guardian_first_name);
//        advancedFormSearchableFields.put(GizConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName);
//
//        motherGuardianLastName = view.findViewById(R.id.mother_guardian_last_name);
//        advancedFormSearchableFields.put(GizConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName);
//
//        motherGuardianNid = view.findViewById(R.id.mother_guardian_nrc);
//        advancedFormSearchableFields.put(GizConstants.KEY.MOTHER_NRC_NUMBER, motherGuardianNid);
//
//        motherGuardianPhoneNumber = view.findViewById(R.id.mother_guardian_phone_number);
//        advancedFormSearchableFields.put(GizConstants.KEY.MOTHER_GUARDIAN_NUMBER, motherGuardianPhoneNumber);
//
//
//        firstName.addTextChangedListener(advancedSearchTextwatcher);
//        lastName.addTextChangedListener(advancedSearchTextwatcher);
//        openSrpId.addTextChangedListener(advancedSearchTextwatcher);
//        motherGuardianFirstName.addTextChangedListener(advancedSearchTextwatcher);
//        motherGuardianLastName.addTextChangedListener(advancedSearchTextwatcher);
//        motherGuardianNid.addTextChangedListener(advancedSearchTextwatcher);
//        motherGuardianPhoneNumber.addTextChangedListener(advancedSearchTextwatcher);
    }

    @Override
    protected HashMap<String, String> createSelectedFieldMap() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(GizConstants.KEY.FIRST_NAME, firstName.getText().toString());
        fields.put(GizConstants.KEY.LAST_NAME, lastName.getText().toString());
        fields.put(GizConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName.getText().toString());
        fields.put(GizConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName.getText().toString());
        fields.put(GizConstants.KEY.MOTHER_NRC_NUMBER, motherGuardianNid.getText().toString());
        fields.put(GizConstants.KEY.MOTHER_GUARDIAN_NUMBER, motherGuardianPhoneNumber.getText().toString());
        fields.put(GizConstants.KEY.ZEIR_ID, openSrpId.getText().toString());
        return fields;
    }

    @Override

    protected void clearFormFields() {
        super.clearFormFields();
        openSrpId.setText("");
        firstName.setText("");
        lastName.setText("");
        motherGuardianFirstName.setText("");
        motherGuardianLastName.setText("");
        motherGuardianNid.setText("");
        motherGuardianPhoneNumber.setText("");

    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter.getDefaultSortQuery();
    }

    @Override
    protected String filterSelectionCondition(boolean urgentOnly) {
        return DBQueryHelper.getFilterSelectionCondition(urgentOnly);
    }

    @Override
    public void onClick(View view) {
        view.toString();
    }
}

