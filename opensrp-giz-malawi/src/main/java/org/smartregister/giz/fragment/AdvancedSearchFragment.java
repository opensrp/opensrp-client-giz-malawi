package org.smartregister.giz.fragment;

import android.text.TextUtils;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.giz.presenter.AdvancedSearchPresenter;
import org.smartregister.giz.util.DBConstants;
import org.smartregister.giz.util.DBQueryHelper;
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
    protected Map<String, String> getSearchMap(boolean b) {
        Map<String, String> searchParams = new HashMap<>();

        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();


        String motherGuardianFirstNameString = motherGuardianFirstName.getText().toString();
        String motherGuardianLastNameString = motherGuardianLastName.getText().toString();
        String motherGuardianNidString = motherGuardianNid.getText().toString();
        String motherGuardianPhoneNumberString = motherGuardianPhoneNumber.getText().toString();
        String zeir = openSrpId.getText().toString();


        if (StringUtils.isNotBlank(motherGuardianFirstNameString)) {
            searchParams.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianLastNameString)) {
            searchParams.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianLastNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianNidString)) {
            searchParams.put(DBConstants.KEY.MOTHER_NRC_NUMBER, motherGuardianNidString);
        }

        if (StringUtils.isNotBlank(motherGuardianPhoneNumberString)) {
            searchParams.put(DBConstants.KEY.MOTHER_GUARDIAN_NUMBER, motherGuardianPhoneNumberString);
        }


        if (!TextUtils.isEmpty(fn)) {
            searchParams.put(DBConstants.KEY.FIRST_NAME, fn);
        }

        if (!TextUtils.isEmpty(ln)) {
            searchParams.put(DBConstants.KEY.LAST_NAME, ln);
        }

        if (!TextUtils.isEmpty(zeir)) {
            searchParams.put(DBConstants.KEY.MER_ID, zeir);
        }

        return searchParams;
    }

    @Override
    public void assignedValuesBeforeBarcode() {
        if (searchFormData.size() > 0) {
            firstName.setText(searchFormData.get(DBConstants.KEY.FIRST_NAME));
            lastName.setText(searchFormData.get(DBConstants.KEY.LAST_NAME));
            motherGuardianFirstName.setText(searchFormData.get(DBConstants.KEY.MOTHER_FIRST_NAME));
            motherGuardianLastName.setText(searchFormData.get(DBConstants.KEY.MOTHER_LAST_NAME));
            motherGuardianNid.setText(searchFormData.get(DBConstants.KEY.MOTHER_NRC_NUMBER));
            motherGuardianPhoneNumber.setText(searchFormData.get(DBConstants.KEY.MOTHER_GUARDIAN_NUMBER));
            openSrpId.setText(searchFormData.get(DBConstants.KEY.MER_ID));
        }
    }

    @Override
    public void populateSearchableFields(View view) {
        firstName = view.findViewById(org.smartregister.child.R.id.first_name);
        advancedFormSearchableFields.put(DBConstants.KEY.FIRST_NAME, firstName);

        lastName = view.findViewById(org.smartregister.child.R.id.last_name);
        advancedFormSearchableFields.put(DBConstants.KEY.LAST_NAME, lastName);

        openSrpId = view.findViewById(org.smartregister.child.R.id.opensrp_id);
        advancedFormSearchableFields.put(DBConstants.KEY.MER_ID, openSrpId);

        motherGuardianFirstName = view.findViewById(org.smartregister.child.R.id.mother_guardian_first_name);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName);

        motherGuardianLastName = view.findViewById(org.smartregister.child.R.id.mother_guardian_last_name);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName);

        motherGuardianNid = view.findViewById(org.smartregister.child.R.id.mother_guardian_nrc);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_NRC_NUMBER, motherGuardianNid);

        motherGuardianPhoneNumber = view.findViewById(org.smartregister.child.R.id.mother_guardian_phone_number);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_GUARDIAN_NUMBER, motherGuardianPhoneNumber);


        firstName.addTextChangedListener(advancedSearchTextwatcher);
        lastName.addTextChangedListener(advancedSearchTextwatcher);
        openSrpId.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianFirstName.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianLastName.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianNid.addTextChangedListener(advancedSearchTextwatcher);
        motherGuardianPhoneNumber.addTextChangedListener(advancedSearchTextwatcher);
    }

    @Override
    protected HashMap<String, String> createSelectedFieldMap() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(DBConstants.KEY.FIRST_NAME, firstName.getText().toString());
        fields.put(DBConstants.KEY.LAST_NAME, lastName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_NRC_NUMBER, motherGuardianNid.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_GUARDIAN_NUMBER, motherGuardianPhoneNumber.getText().toString());
        fields.put(DBConstants.KEY.MER_ID, openSrpId.getText().toString());
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

