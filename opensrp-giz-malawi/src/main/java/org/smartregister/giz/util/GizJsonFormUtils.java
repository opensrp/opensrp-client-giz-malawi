package org.smartregister.giz.util;

import android.content.Context;

import com.google.common.reflect.TypeToken;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.enums.LocationHierarchy;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class GizJsonFormUtils extends JsonFormUtils {

    public static String getMetadataForEditForm(Context context, Map<String, String> childDetails, List<String> nonEditableFields) {
        try {
            JSONObject birthRegistrationForm = FormUtils.getInstance(context)
                    .getFormJson(Utils.metadata().childRegister.formName);
            updateRegistrationEventType(birthRegistrationForm);
            JsonFormUtils.addChildRegLocHierarchyQuestions(birthRegistrationForm, GizConstants.REGISTRATION_HOME_ADDRESS,
                    LocationHierarchy.ENTIRE_TREE);

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
                updateFormDetailsForEdit(childDetails, jsonArray, nonEditableFields);
                return birthRegistrationForm.toString();
            }
        } catch (Exception e) {
            Timber.e(e, "GizJsonFormUtils --> getMetadataForEditForm");
        }

        return "";
    }

    private static void updateFormDetailsForEdit(Map<String, String> childDetails, JSONArray jsonArray, List<String> nonEditableFields)
            throws JSONException {
        String prefix;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            prefix = getPrefix(jsonObject);

            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.KEY.PHOTO)) {
                processPhoto(childDetails.get(Constants.KEY.BASE_ENTITY_ID), jsonObject);
            } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.DOB_UNKNOWN)) {
                getDobUnknown(childDetails, jsonObject);
            } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.AGE)) {
                processAge(Utils.getValue(childDetails, Constants.JSON_FORM_KEY.DOB, false), jsonObject);
            } else if (jsonObject.getString(JsonFormConstants.TYPE).equalsIgnoreCase(JsonFormConstants.DATE_PICKER)) {
                processDate(childDetails, prefix, jsonObject);
            } else if (jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(JsonFormUtils.PERSON_INDENTIFIER)) {
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

            processLocationTree(childDetails, nonEditableFields, jsonObject);

            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(GizConstants.MIDDLE_NAME)) {
                String middleName = Utils.getValue(childDetails, GizConstants.MIDDLE_NAME, true);
                jsonObject.put(JsonFormUtils.VALUE, middleName);
            }
            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.MOTHER_NRC_NUMBER)) {
                String nidNumber = Utils.getValue(childDetails, DBConstants.KEY.MOTHER_NRC_NUMBER, true);
                jsonObject.put(JsonFormUtils.VALUE, nidNumber);
            }
            if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.MOTHER_SECOND_PHONE_NUMBER)) {
                String secondaryNumber = Utils.getValue(childDetails, DBConstants.KEY.MOTHER_SECOND_PHONE_NUMBER, true);
                jsonObject.put(JsonFormUtils.VALUE, secondaryNumber);
            }
        }
    }

    private static void getDobUnknown(Map<String, String> childDetails, JSONObject jsonObject) throws JSONException {
        JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
        optionsObject.put(JsonFormUtils.VALUE,
                Utils.getValue(childDetails, Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));
    }

    @NotNull
    private static String getPrefix(JSONObject jsonObject) throws JSONException {
        String prefix;
        prefix = jsonObject.has(JsonFormUtils.ENTITY_ID) && jsonObject.getString(JsonFormUtils.ENTITY_ID)
                .equalsIgnoreCase(GizConstants.MOTHER) ? GizConstants.MOTHER_ : "";
        return prefix;
    }

    private static void processLocationTree(Map<String, String> childDetails, List<String> nonEditableFields, JSONObject jsonObject) throws JSONException {
        updateBirthFacilityHierarchy(childDetails, jsonObject);
        if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(GizConstants.BIRTH_FACILITY_NAME_OTHER)) {
            jsonObject
                    .put(JsonFormUtils.VALUE, Utils.getValue(childDetails,
                            GizConstants.BIRTH_FACILITY_NAME_OTHER, false));
            jsonObject.put(JsonFormUtils.READ_ONLY, true);
        }
        updateResidentialAreaHierarchy(childDetails, jsonObject);
        updateHomeFacilityHierarchy(childDetails, jsonObject);
        addNonEditableFields(nonEditableFields, jsonObject);
    }

    private static void updateBirthFacilityHierarchy(Map<String, String> childDetails, JSONObject jsonObject) throws JSONException {
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
    }

    private static void updateResidentialAreaHierarchy(Map<String, String> childDetails, JSONObject jsonObject) throws JSONException {
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
    }

    private static void updateHomeFacilityHierarchy(Map<String, String> childDetails, JSONObject jsonObject) throws JSONException {
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
    }

    private static void addNonEditableFields(List<String> nonEditableFields, JSONObject jsonObject) throws JSONException {
        jsonObject.put(JsonFormUtils.READ_ONLY,
                nonEditableFields.contains(jsonObject.getString(JsonFormUtils.KEY)));
    }

    private static void updateRegistrationEventType(JSONObject form) throws JSONException {
        if (form.has(JsonFormUtils.ENCOUNTER_TYPE) && form.getString(JsonFormUtils.ENCOUNTER_TYPE)
                .equals(Constants.EventType.BITRH_REGISTRATION)) {
            form.put(JsonFormUtils.ENCOUNTER_TYPE, Constants.EventType.UPDATE_BITRH_REGISTRATION);
        }

        if (form.has(JsonFormUtils.STEP1) && form.getJSONObject(JsonFormUtils.STEP1).has(GizConstants.TITLE) && form.getJSONObject(JsonFormUtils.STEP1).getString(GizConstants.TITLE)
                .equals(Constants.EventType.BITRH_REGISTRATION)) {
            form.getJSONObject(JsonFormUtils.STEP1).put(GizConstants.TITLE, Constants.EventType.UPDATE_BITRH_REGISTRATION);
        }
    }

    public static String getJsonString(JSONObject jsonObject, String field) {
        try {
            if (jsonObject != null && jsonObject.has(field)) {
                String string = jsonObject.getString(field);
                if (StringUtils.isBlank(string)) {
                    return "";
                }

                return string;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return "";
    }

    public static JSONObject getJsonObject(JSONObject jsonObject, String field) {
        try {
            if (jsonObject != null && jsonObject.has(field)) {
                return jsonObject.getJSONObject(field);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }
}
