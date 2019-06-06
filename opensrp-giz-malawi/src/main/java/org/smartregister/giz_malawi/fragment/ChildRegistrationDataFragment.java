package org.smartregister.giz_malawi.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TableRow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.giz_malawi.R;
import org.smartregister.giz_malawi.util.GizConstants;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.DateUtil;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-05-30.
 */
public class ChildRegistrationDataFragment extends BaseChildRegistrationDataFragment {
    private CustomFontTextView tvChildsHomeHealthFacility;
    private CustomFontTextView tvChildsOpenSrpId;
    private CustomFontTextView tvChildsRegisterCardNumber;
    private CustomFontTextView tvChildsFirstName;
    private CustomFontTextView tvChildsLastName;
    private CustomFontTextView tvChildsSex;
    private CustomFontTextView tvChildsDOB;
    private CustomFontTextView tvChildsAge;
    private CustomFontTextView tvChildDateFirstSeen;
    private CustomFontTextView tvChildsBirthWeight;
    private CustomFontTextView tvBirthTetanusProtection;
    private CustomFontTextView tvMotherFirstName;
    private CustomFontTextView tvMotherLastName;
    private CustomFontTextView tvMotherDOB;
    private CustomFontTextView tvMotherPhoneNumber;
    private CustomFontTextView tvFatherPhoneNumber;
    private CustomFontTextView tvFatherFullName;
    private CustomFontTextView tvFatherDOB;
    private CustomFontTextView tvChildsPlaceOfBirth;
    private CustomFontTextView tvChildsBirthHealthFacility;
    private CustomFontTextView tvChildsOtherBirthFacility;
    private CustomFontTextView tvChildsResidentialArea;
    private CustomFontTextView tvChildsOtherResidentialArea;
    private CustomFontTextView tvChildsHomeAddress;
    private CustomFontTextView tvLandmark;
    private CustomFontTextView tvPreferredLanguage;
    private TableRow tableRowChildsOtherBirthFacility;
    private TableRow tableRowChildsOtherResidentialArea;

    @Override
    public void loadData(Map<String, String> detailsMap) {
        if (fragmentView != null) {
            setUpViews();

            ((View) tvBirthTetanusProtection.getParent()).setVisibility(View.GONE);
            tvChildsHomeHealthFacility.setText(LocationHelper.getInstance().getOpenMrsReadableName(
                    LocationHelper.getInstance()
                            .getOpenMrsLocationName(Utils.getValue(detailsMap, GizConstants.HOME_FACILITY, false))));
            tvChildsOpenSrpId.setText(Utils.getValue(detailsMap, GizConstants.ZEIR_ID, false));
            tvChildsRegisterCardNumber.setText(Utils.getValue(detailsMap,
                    GizConstants.CHILD_REGISTER_CARD_NUMBER, false));
            tvChildsFirstName.setText(Utils.getValue(detailsMap, GizConstants.FIRST_NAME, true));
            tvChildsLastName.setText(Utils.getValue(detailsMap, GizConstants.LAST_NAME, true));
            tvChildsSex.setText(Utils.getValue(detailsMap, GizConstants.GENDER, true));
            tvChildsAge.setText(getFormattedAge(detailsMap));
            tvChildDateFirstSeen.setText(getDateString(detailsMap));
            tvChildsBirthWeight.setText(Utils.kgStringSuffix(Utils.getValue(detailsMap, GizConstants.BIRTH_WEIGHT, true)));
            tvBirthTetanusProtection.setText(Utils.getValue(detailsMap, GizConstants.BIRTH_TETANUS_PROTECTION, true).isEmpty() ? Utils
                    .getValue(childDetails, GizConstants.BIRTH_TETANUS_PROTECTION, true) : Utils
                    .getValue(detailsMap, GizConstants.BIRTH_TETANUS_PROTECTION, true));
            tvMotherFirstName.setText(Utils.getValue(detailsMap, GizConstants.MOTHER_FIRST_NAME, true).isEmpty() ? Utils
                    .getValue(childDetails, GizConstants.MOTHER_FIRST_NAME, true) : Utils
                    .getValue(detailsMap, GizConstants.MOTHER_FIRST_NAME, true));
            tvMotherLastName.setText(Utils.getValue(detailsMap, GizConstants.MOTHER_LAST_NAME, true).isEmpty() ? Utils
                    .getValue(childDetails, GizConstants.MOTHER_LAST_NAME, true) : Utils
                    .getValue(detailsMap, GizConstants.MOTHER_LAST_NAME, true));
            tvMotherDOB.setText(getMotherDob());
            if (detailsMap.containsKey(GizConstants.MOTHER_GUARDIAN_PHONE_NUMBER)) {
                tvMotherPhoneNumber.setText(Utils.getValue(detailsMap, GizConstants.MOTHER_GUARDIAN_PHONE_NUMBER, true));
            } else {
                tvMotherPhoneNumber.setText(Utils.getValue(detailsMap, GizConstants.MOTHER_GUARDIAN_NUMBER, true));
            }

            String fatherName = Utils.getValue(detailsMap, GizConstants.FATHER_GUARDIAN_FIRST_NAME, true);
            String fatherNameLast = Utils.getValue(detailsMap, GizConstants.FATHER_GUARDIAN_LAST_NAME, true);
            fatherNameLast = !TextUtils.isEmpty(fatherNameLast) ? fatherNameLast : "";

            tvFatherFullName.setText(fatherName + " " + fatherNameLast);


            if (detailsMap.containsKey(GizConstants.FATHER_GUARDIAN_PHONE_NUMBER)) {
                tvFatherPhoneNumber.setText(Utils.getValue(detailsMap, GizConstants.FATHER_GUARDIAN_PHONE_NUMBER, true));
            } else {

                tvFatherPhoneNumber.setText(Utils.getValue(detailsMap, GizConstants.FATHER_GUARDIAN_NUMBER, true));
            }

            tvFatherDOB.setText(Utils.getValue(detailsMap, GizConstants.FATHER_GUARDIAN_DATE_BIRTH, true));
            tvChildsPlaceOfBirth.setText( getPlaceOfBirthChoice(detailsMap));

            String childsBirthHealthFacility = Utils.getValue(detailsMap,
                    GizConstants.HOME_FACILITY, false);
            childsBirthHealthFacility = TextUtils.isEmpty(childsBirthHealthFacility) ? Utils
                    .getValue(detailsMap, GizConstants.IS_PLACE_BIRTH, false) : childsBirthHealthFacility;
            tvChildsBirthHealthFacility.setText(LocationHelper.getInstance()
                    .getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(childsBirthHealthFacility)));

            if (LocationHelper.getInstance()
                    .getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(childsBirthHealthFacility))
                    .equalsIgnoreCase(GizConstants.OTHER)) {
                tableRowChildsOtherBirthFacility.setVisibility(View.VISIBLE);
                tvChildsOtherBirthFacility.setText(Utils.getValue(detailsMap, GizConstants.BIRTH_FACILITY_NAME_OTHER, true));
            }

            String childsResidentialArea = Utils.getValue(detailsMap, GizConstants.ADDRESS_3, false);
            tvChildsResidentialArea.setText(LocationHelper.getInstance()
                    .getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(childsResidentialArea)));
            if (LocationHelper.getInstance()
                    .getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(childsResidentialArea))
                    .equalsIgnoreCase(GizConstants.OTHER)) {
                tableRowChildsOtherResidentialArea.setVisibility(View.VISIBLE);
                tvChildsOtherResidentialArea.setText(Utils.getValue(detailsMap, GizConstants.ADDRESS_5, true));
            }

            tvChildsHomeAddress.setText(Utils.getValue(detailsMap, GizConstants.ADDRESS_2, true));
            tvLandmark.setText(Utils.getValue(detailsMap, GizConstants.ADDRESS_1, true));

            tvPreferredLanguage.setText(Utils.getValue(detailsMap, GizConstants.PREFERRED_LANGUAGE, true).isEmpty() ? Utils
                    .getValue(childDetails, GizConstants.PREFERRED_LANGUAGE, true) : Utils
                    .getValue(detailsMap, GizConstants.PREFERRED_LANGUAGE, true));

            // remove any empty fields
            removeEmptyValueFields();
        }
    }

    @NotNull
    private String getPlaceOfBirthChoice(Map<String, String> detailsMap) {
        String placeOfBirthChoice = Utils.getValue(detailsMap, GizConstants.PLACE_BIRTH, true);
        if (placeOfBirthChoice.equalsIgnoreCase("1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            placeOfBirthChoice = "Health facility";
        }

        if (placeOfBirthChoice.equalsIgnoreCase("1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
            placeOfBirthChoice = "Home";
        }
        return placeOfBirthChoice;
    }

    @Nullable
    private String getMotherDob() {
        String motherDobString = Utils.getValue(childDetails, GizConstants.MOTHER_DOB, true);
        Date motherDob = Utils.dobStringToDate(motherDobString);
        if (motherDob != null) {
            motherDobString = Constants.DATE_FORMAT.format(motherDob);
        }


        // If default mother dob ... set it as blank
        if (motherDobString != null && motherDobString.equals(JsonFormUtils.MOTHER_DEFAULT_DOB)) {
            motherDobString = "";
        }
        return motherDobString;
    }

    @Nullable
    private String getDateString(Map<String, String> detailsMap) {
        String dateString = Utils.getValue(detailsMap, GizConstants.FIRST_HEALTH_FACILITY_CONTACT, false);
        if (!TextUtils.isEmpty(dateString)) {
            Date date = JsonFormUtils.formatDate(dateString, false);
            if (date != null) {
                dateString = Constants.DATE_FORMAT.format(date);
            }
        }
        return dateString;
    }

    @NotNull
    private String getFormattedAge(Map<String, String> detailsMap) {
        String formattedAge = "";
        String dobString = Utils.getValue(detailsMap, Constants.KEY.DOB, false);
        Date dob = Utils.dobStringToDate(dobString);
        if (dob != null) {
            String childsDateOfBirth = Constants.DATE_FORMAT.format(dob);
            tvChildsDOB.setText(childsDateOfBirth);

            long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();
            if (timeDiff >= 0) {
                formattedAge = DateUtil.getDuration(timeDiff);
            }
        }
        return formattedAge;
    }

    private void setUpViews() {
        tvChildsHomeHealthFacility = fragmentView.findViewById(R.id.value_childs_home_health_facility);
        tvChildsOpenSrpId = fragmentView.findViewById(R.id.value_childs_zeir_id);
        tvChildsRegisterCardNumber = fragmentView.findViewById(R.id.value_childs_register_card_number);
        tvChildsFirstName = fragmentView.findViewById(R.id.value_first_name);
        tvChildsLastName = fragmentView.findViewById(R.id.value_last_name);
        tvChildsSex = fragmentView.findViewById(R.id.value_sex);
        tvChildsDOB = fragmentView.findViewById(R.id.value_childs_dob);
        tvChildsAge = fragmentView.findViewById(R.id.value_age);
        tvChildDateFirstSeen = fragmentView.findViewById(R.id.value_date_first_seen);
        tvChildsBirthWeight = fragmentView.findViewById(R.id.value_birth_weight);
        tvBirthTetanusProtection = fragmentView.findViewById(R.id.value_birth_tetanus_protection);
        ((View) tvBirthTetanusProtection.getParent()).setVisibility(View.GONE);
        tvMotherFirstName = fragmentView.findViewById(R.id.value_mother_guardian_first_name);
        tvMotherLastName = fragmentView.findViewById(R.id.value_mother_guardian_last_name);
        tvMotherDOB = fragmentView.findViewById(R.id.value_mother_guardian_dob);
        tvMotherPhoneNumber = fragmentView.findViewById(R.id.value_mother_guardian_phone_number);
        tvFatherPhoneNumber = fragmentView.findViewById(R.id.father_guardian_phone_number);
        tvFatherFullName = fragmentView.findViewById(R.id.value_father_guardian_full_name);
        tvFatherDOB = fragmentView.findViewById(R.id.value_father_guardian_nrc_number);
        tvChildsPlaceOfBirth = fragmentView.findViewById(R.id.value_place_of_birth);
        tvChildsBirthHealthFacility = fragmentView.findViewById(R.id.value_childs_birth_health_facility);
        tvChildsOtherBirthFacility = fragmentView.findViewById(R.id.value_other_birth_facility);
        tvChildsResidentialArea = fragmentView.findViewById(R.id.value_childs_residential_area);
        tvChildsOtherResidentialArea = fragmentView.findViewById(R.id.value_other_childs_residential_area);
        tvChildsHomeAddress = fragmentView.findViewById(R.id.value_home_address);
        tvLandmark = fragmentView.findViewById(R.id.value_landmark);
        tvPreferredLanguage = fragmentView.findViewById(R.id.value_preferred_language);
        tableRowChildsOtherBirthFacility = fragmentView
                .findViewById(R.id.tableRow_childRegDataFragment_childsOtherBirthFacility);
        tableRowChildsOtherResidentialArea = fragmentView
                .findViewById(R.id.tableRow_childRegDataFragment_childsOtherResidentialArea);
    }
}
