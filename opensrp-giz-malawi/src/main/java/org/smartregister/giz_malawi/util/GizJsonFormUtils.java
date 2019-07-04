package org.smartregister.giz_malawi.util;

import android.content.Context;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.enums.LocationHierarchy;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.FormUtils;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.AssetHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GizJsonFormUtils extends JsonFormUtils {
    private static final String TAG = JsonFormUtils.class.getCanonicalName();

    public static String getMetadataForEditForm(Context context, Map<String, String> childDetails) {
        try {
            JSONObject birthRegistrationForm = FormUtils.getInstance(context)
                    .getFormJson(Utils.metadata().childRegister.formName);
            updateRegistrationEventType(birthRegistrationForm);
            JsonFormUtils.addChildRegLocHierarchyQuestions(birthRegistrationForm, GizConstants.REGISTRATION_HOME_ADDRESS,
                    LocationHierarchy.ENTIRE_TREE);

            Log.d(TAG, "Form is " + birthRegistrationForm.toString());
            if (birthRegistrationForm != null) {
                birthRegistrationForm.put(JsonFormUtils.ENTITY_ID, childDetails.get(Constants.KEY.BASE_ENTITY_ID));
                birthRegistrationForm.put(JsonFormUtils.ENCOUNTER_TYPE, Utils.metadata().childRegister.updateEventType);
                birthRegistrationForm.put(JsonFormUtils.RELATIONAL_ID, childDetails.get(RELATIONAL_ID));
                birthRegistrationForm.put(JsonFormUtils.CURRENT_ZEIR_ID,
                        Utils.getValue(childDetails, GizConstants.MER_ID, true).replace("-",
                                ""));
                birthRegistrationForm.put(JsonFormUtils.CURRENT_OPENSRP_ID,
                        Utils.getValue(childDetails, Constants.JSON_FORM_KEY.UNIQUE_ID, false));

                JSONObject metadata = birthRegistrationForm.getJSONObject(JsonFormUtils.METADATA);
                metadata.put(JsonFormUtils.ENCOUNTER_LOCATION,
                        ChildLibrary.getInstance().getLocationPickerView(context).getSelectedItem());

                //inject zeir id into the birthRegistrationForm
                JSONObject stepOne = birthRegistrationForm.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                updateFormDetailsForEdit(childDetails, jsonArray);
                return birthRegistrationForm.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return "";
    }

    private static void updateFormDetailsForEdit(Map<String, String> childDetails, JSONArray jsonArray)
    throws JSONException {
        String prefix;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            prefix = jsonObject.has(JsonFormUtils.ENTITY_ID) && jsonObject.getString(JsonFormUtils.ENTITY_ID)
                    .equalsIgnoreCase(GizConstants.MOTHER) ? GizConstants.MOTHER_ : "";

            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.KEY.PHOTO)) {
                processPhoto(childDetails.get(Constants.KEY.BASE_ENTITY_ID), jsonObject);
            } else if (jsonObject.getString(JsonFormUtils.KEY)
                    .equalsIgnoreCase(Constants.JSON_FORM_KEY.DOB_UNKNOWN)) {
                JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
                optionsObject.put(JsonFormUtils.VALUE,
                        Utils.getValue(childDetails, Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));
            } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.AGE)) {
                processAge(Utils.getValue(childDetails, Constants.JSON_FORM_KEY.DOB, false), jsonObject);
            } else if (jsonObject.getString(JsonFormConstants.TYPE)
                    .equalsIgnoreCase(JsonFormConstants.DATE_PICKER)) {
                processDate(childDetails, prefix, jsonObject);
            } else if (jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY)
                    .equalsIgnoreCase(JsonFormUtils.PERSON_INDENTIFIER)) {
                jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(childDetails,
                        jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY_ID).toLowerCase(), true).replace("-", ""));
            } else if (jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(JsonFormUtils.CONCEPT)) {
                jsonObject.put(JsonFormUtils.VALUE,
                        getMappedValue(jsonObject.getString(JsonFormUtils.KEY), childDetails));
            } else {
                jsonObject.put(JsonFormUtils.VALUE,
                        getMappedValue(prefix + jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY_ID),
                                childDetails));
            }

            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(GizConstants.BIRTH_FACILITY_NAME)) {
                jsonObject.put(JsonFormUtils.READ_ONLY, true);
                List<String> birthFacilityHierarchy = null;
                String birthFacilityName = Utils.getValue(childDetails, GizConstants.BIRTH_FACILITY_NAME, false);
                if (birthFacilityName != null) {
                    if (birthFacilityName.equalsIgnoreCase(GizConstants.OTHER)) {
                        birthFacilityHierarchy = new ArrayList<>();
                        birthFacilityHierarchy.add(birthFacilityName);
                    } else {
                        birthFacilityHierarchy = LocationHelper.getInstance()
                                .getOpenMrsLocationHierarchy(birthFacilityName, true);
                    }
                }

                String birthFacilityHierarchyString = AssetHandler
                        .javaToJsonString(birthFacilityHierarchy, new TypeToken<List<String>>() {
                        }.getType());
                if (StringUtils.isNotBlank(birthFacilityHierarchyString)) {
                    jsonObject.put(JsonFormUtils.VALUE, birthFacilityHierarchyString);
                }
            }
            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(GizConstants.BIRTH_FACILITY_NAME_OTHER)) {
                jsonObject
                        .put(JsonFormUtils.VALUE, Utils.getValue(childDetails,
                                GizConstants.BIRTH_FACILITY_NAME_OTHER, false));
                jsonObject.put(JsonFormUtils.READ_ONLY, true);
            }
            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(GizConstants.RESIDENTIAL_AREA)) {
                List<String> residentialAreaHierarchy;
                String address3 = Utils.getValue(childDetails, GizConstants.ADDRESS_3, false);
                if (address3 != null && address3.equalsIgnoreCase(GizConstants.OTHER)) {
                    residentialAreaHierarchy = new ArrayList<>();
                    residentialAreaHierarchy.add(address3);
                } else {
                    residentialAreaHierarchy = LocationHelper.getInstance()
                            .getOpenMrsLocationHierarchy(address3, true);
                }

                String residentialAreaHierarchyString = AssetHandler
                        .javaToJsonString(residentialAreaHierarchy, new TypeToken<List<String>>() {
                        }.getType());
                if (StringUtils.isNotBlank(residentialAreaHierarchyString)) {
                    jsonObject.put(JsonFormUtils.VALUE, residentialAreaHierarchyString);
                }
            }
            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(GizConstants.HOME_FACILITY)) {
                List<String> homeFacilityHierarchy = LocationHelper.getInstance()
                        .getOpenMrsLocationHierarchy(Utils.getValue(childDetails,
                                GizConstants.HOME_FACILITY, false), true);
                String homeFacilityHierarchyString = AssetHandler
                        .javaToJsonString(homeFacilityHierarchy, new TypeToken<List<String>>() {
                        }.getType());
                if (StringUtils.isNotBlank(homeFacilityHierarchyString)) {
                    jsonObject.put(JsonFormUtils.VALUE, homeFacilityHierarchyString);
                }
            }
            jsonObject.put(JsonFormUtils.READ_ONLY,
                    nonEditableFields.contains(jsonObject.getString(JsonFormUtils.KEY)));

            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(GizConstants.MIDDLE_NAME)) {
                String middleName = Utils.getValue(childDetails, GizConstants.MIDDLE_NAME, true);
                jsonObject.put(JsonFormUtils.VALUE, middleName);
            }
            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.MOTHER_NRC_NUMBER)) {
                String nidNumber = Utils.getValue(childDetails, DBConstants.KEY.MOTHER_NID, true);
                jsonObject.put(JsonFormUtils.VALUE, nidNumber);
            }
            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.MOTHER_SECOND_PHONE_NUMBER)) {
                String middleName = Utils.getValue(childDetails, DBConstants.KEY.MOTHER_SECOND_PHONE_NUMBER, true);
                jsonObject.put(JsonFormUtils.VALUE, middleName);
            }
            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.HOME_ADDRESS)) {
                String homeAddress = Utils.getValue(childDetails, DBConstants.KEY.HOME_ADDRESS, true);
                jsonObject.put(JsonFormUtils.VALUE, homeAddress);
            }
        }
    }

    private static void updateRegistrationEventType(JSONObject form) throws JSONException {
        if (form.has(JsonFormUtils.ENCOUNTER_TYPE) && form.getString(JsonFormUtils.ENCOUNTER_TYPE)
                .equals(Constants.EventType.BITRH_REGISTRATION)) {
            form.put(JsonFormUtils.ENCOUNTER_TYPE, Constants.EventType.UPDATE_BITRH_REGISTRATION);
        }

        if (form.has(JsonFormUtils.STEP1) && form.has(GizConstants.TITLE) && form.getJSONObject(JsonFormUtils.STEP1)
                .getString(
                        GizConstants.TITLE)
                .equals(Constants.EventType.BITRH_REGISTRATION)) {
            form.getJSONObject(JsonFormUtils.STEP1).put(GizConstants.TITLE, Constants.EventType.UPDATE_BITRH_REGISTRATION);
        }
    }
}
