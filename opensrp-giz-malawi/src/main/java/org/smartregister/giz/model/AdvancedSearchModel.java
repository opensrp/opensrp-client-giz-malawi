package org.smartregister.giz.model;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.domain.Response;
import org.smartregister.giz.util.DBConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class AdvancedSearchModel extends BaseChildAdvancedSearchModel {
    private static final String MOTHER_BASE_ENTITY_ID = "mother_base_entity_id";
    private static final String MOTHER_GUARDIAN_FIRST_NAME = "mother_first_name";
    private static final String MOTHER_GUARDIAN_LAST_NAME = "mother_last_name";

    @Override
    public Map<String, String> createEditMap(Map<String, String> editMap) {
        return editMap;
    }

    @Override
    protected String[] mainColumns(String tableName, String parentTableName) {
        return new String[]{
                tableName + "." + DBConstants.KEY.RELATIONALID,
                tableName + "." + DBConstants.KEY.DETAILS,
                tableName + "." + DBConstants.KEY.MER_ID,
                tableName + "." + DBConstants.KEY.RELATIONAL_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + DBConstants.KEY.HOME_ADDRESS,
                tableName + "." + DBConstants.KEY.VILLAGE,
                tableName + "." + DBConstants.KEY.TRADITIONAL_AUTHORITY,
                tableName + "." + DBConstants.KEY.CHILD_TREATMENT,
                tableName + "." + DBConstants.KEY.CHILD_HIV_STATUS,
                tableName + "." + DBConstants.KEY.GENDER,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                parentTableName + "." + DBConstants.KEY.MOTHER_FIRST_NAME + " as mother_first_name",
                parentTableName + "." + DBConstants.KEY.MOTHER_LAST_NAME + " as mother_last_name",
                parentTableName + "." + DBConstants.KEY.MOTHER_DOB + " as mother_dob",
                parentTableName + "." + DBConstants.KEY.MOTHER_NRC_NUMBER,
                parentTableName + "." + DBConstants.KEY.MOTHER_GUARDIAN_NUMBER,
                parentTableName + "." + DBConstants.KEY.MOTHER_SECOND_PHONE_NUMBER,
                parentTableName + "." + DBConstants.KEY.PROTECTED_AT_BIRTH,
                parentTableName + "." + DBConstants.KEY.MOTHER_HIV_STATUS,
                tableName + "." + DBConstants.KEY.DOB,
                tableName + "." + DBConstants.KEY.CLIENT_REG_DATE,
                tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH
        };
    }

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {

        String[] columns = new String[]{Constants.KEY.ID_LOWER_CASE, Constants.KEY.RELATIONALID, Constants.KEY.FIRST_NAME, "middle_name", Constants.KEY.LAST_NAME, Constants.KEY.GENDER, Constants.KEY.DOB, Constants.KEY.ZEIR_ID, Constants.KEY.EPI_CARD_NUMBER, Constants.KEY.NFC_CARD_IDENTIFIER, MOTHER_BASE_ENTITY_ID, MOTHER_GUARDIAN_FIRST_NAME, MOTHER_GUARDIAN_LAST_NAME, org.smartregister.child.util.Constants.CHILD_STATUS.INACTIVE, org.smartregister.child.util.Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP};
        AdvancedMatrixCursor matrixCursor = new AdvancedMatrixCursor(columns);

        if (response == null || response.isFailure() || StringUtils.isBlank(response.payload())) {
            return matrixCursor;
        }

        JSONArray jsonArray = getJsonArray(response);
        if (jsonArray != null) {

            List<JSONObject> jsonValues = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonValues.add(getJsonObject(jsonArray, i));
            }

            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject lhs, JSONObject rhs) {

                    if (!lhs.has("child") || !rhs.has("child")) {
                        return 0;
                    }

                    JSONObject lhsChild = getJsonObject(lhs, "child");
                    JSONObject rhsChild = getJsonObject(rhs, "child");

                    String lhsInactive = getJsonString(getJsonObject(lhsChild, "attributes"), "inactive");
                    String rhsInactive = getJsonString(getJsonObject(rhsChild, "attributes"), "inactive");

                    int aComp = 0;
                    if (lhsInactive.equalsIgnoreCase(Boolean.TRUE.toString()) && !rhsInactive.equalsIgnoreCase(Boolean.TRUE.toString())) {
                        aComp = 1;
                    } else if (!lhsInactive.equalsIgnoreCase(Boolean.TRUE.toString()) && rhsInactive.equalsIgnoreCase(Boolean.TRUE.toString())) {
                        aComp = -1;
                    }

                    if (aComp != 0) {
                        return aComp;
                    } else {
                        String lhsLostToFollowUp = getJsonString(getJsonObject(lhsChild, "attributes"), "lost_to_follow_up");
                        String rhsLostToFollowUp = getJsonString(getJsonObject(rhsChild, "attributes"), "lost_to_follow_up");
                        if (lhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString()) && !rhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString())) {
                            return 1;
                        } else if (!lhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString()) && rhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString())) {
                            return -1;
                        }
                    }

                    return 0;

                }
            });

            for (JSONObject client : jsonValues) {
                String entityId = "";
                String firstName = "";
                String middleName = "";
                String lastName = "";
                String gender = "";
                String dob = "";
                String zeirId = "";
                String epiCardNumber = "";
                String inactive = "";
                String lostToFollowUp = "";
                String nfcCardId = "";

                if (client == null) {
                    continue;
                }

                if (client.has("child")) {
                    JSONObject child = getJsonObject(client, "child");

                    // Skip deceased children
                    if (StringUtils.isNotBlank(getJsonString(child, "deathdate"))) {
                        continue;
                    }

                    entityId = getJsonString(child, "baseEntityId");
                    firstName = getJsonString(child, "firstName");
                    middleName = getJsonString(child, "middleName");
                    lastName = getJsonString(child, "lastName");

                    gender = getJsonString(child, "gender");
                    dob = getJsonString(child, "birthdate");
                    if (StringUtils.isNotBlank(dob) && StringUtils.isNumeric(dob)) {
                        try {
                            Long dobLong = Long.valueOf(dob);
                            Date date = new Date(dobLong);
                            dob = DateUtil.yyyyMMddTHHmmssSSSZ.format(date);
                        } catch (Exception e) {
                            Log.e(getClass().getName(), e.toString(), e);
                        }
                    }

                    zeirId = getJsonString(getJsonObject(child, "identifiers"), JsonFormUtils.ZEIR_ID);
                    if (StringUtils.isNotBlank(zeirId)) {
                        zeirId = zeirId.replace("-", "");
                    }

                    epiCardNumber = getJsonString(getJsonObject(child, "attributes"), "Child_Register_Card_Number");

                    inactive = getJsonString(getJsonObject(child, "attributes"), "inactive");
                    lostToFollowUp = getJsonString(getJsonObject(child, "attributes"), "lost_to_follow_up");
                    nfcCardId = getJsonString(getJsonObject(child, "attributes"), Constants.KEY.NFC_CARD_IDENTIFIER);

                }


                String motherBaseEntityId = "";
                String motherFirstName = "";
                String motherLastName = "";

                if (client.has("mother")) {
                    JSONObject mother = getJsonObject(client, "mother");
                    motherFirstName = getJsonString(mother, "firstName");
                    motherLastName = getJsonString(mother, "lastName");
                    motherBaseEntityId = getJsonString(mother, "baseEntityId");
                }

                matrixCursor.addRow(new Object[]{entityId, null, firstName, middleName, lastName, gender, dob, zeirId, epiCardNumber, nfcCardId, motherBaseEntityId, motherFirstName, motherLastName, inactive, lostToFollowUp});
            }

            return matrixCursor;
        } else {
            return matrixCursor;
        }
    }

}
