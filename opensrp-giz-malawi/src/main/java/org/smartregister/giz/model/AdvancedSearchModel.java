package org.smartregister.giz.model;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.domain.Response;
import org.smartregister.giz.util.GizConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class AdvancedSearchModel extends BaseChildAdvancedSearchModel {
    @Override
    public Map<String, String> createEditMap(Map<String, String> editMap) {
        return editMap;
    }

    @Override
    protected String[] mainColumns(String tableName, String parentTableName) {
        return new String[]{
                tableName + "." + GizConstants.KEY.RELATIONALID,
                tableName + "." + GizConstants.KEY.DETAILS,
                tableName + "." + GizConstants.KEY.ZEIR_ID,
                tableName + "." + GizConstants.KEY.RELATIONAL_ID,
                tableName + "." + GizConstants.KEY.FIRST_NAME,
                tableName + "." + GizConstants.KEY.LAST_NAME,
                tableName + "." + GizConstants.KEY.HOME_ADDRESS,
                tableName + "." + GizConstants.KEY.VILLAGE,
                tableName + "." + GizConstants.KEY.TRADITIONAL_AUTHORITY,
                tableName + "." + GizConstants.KEY.CHILD_TREATMENT,
                tableName + "." + GizConstants.KEY.CHILD_HIV_STATUS,
                tableName + "." + GizConstants.KEY.GENDER,
                tableName + "." + GizConstants.KEY.BASE_ENTITY_ID,
                parentTableName + "." + GizConstants.KEY.FIRST_NAME + " as " + GizConstants.KEY.MOTHER_FIRST_NAME,
                parentTableName + "." + GizConstants.KEY.LAST_NAME + " as " + GizConstants.KEY.MOTHER_LAST_NAME,
                parentTableName + "." + GizConstants.KEY.MOTHER_DOB + " as " + GizConstants.KEY.DB_MOTHER_DOB,
                parentTableName + "." + GizConstants.KEY.MOTHER_NRC_NUMBER,
                parentTableName + "." + GizConstants.KEY.MOTHER_GUARDIAN_NUMBER,
                parentTableName + "." + GizConstants.KEY.MOTHER_SECOND_PHONE_NUMBER,
                parentTableName + "." + GizConstants.KEY.PROTECTED_AT_BIRTH,
                parentTableName + "." + GizConstants.KEY.MOTHER_HIV_STATUS,
                tableName + "." + GizConstants.KEY.DOB,
                tableName + "." + GizConstants.KEY.CLIENT_REG_DATE,
                tableName + "." + GizConstants.KEY.LAST_INTERACTED_WITH
        };
    }

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {

        String[] columns = new String[]{GizConstants.KEY.ID_LOWER_CASE, GizConstants.KEY.RELATIONALID,
                GizConstants.KEY.FIRST_NAME, GizConstants.KEY.MIDDLE_NAME, GizConstants.KEY.LAST_NAME,
                GizConstants.KEY.GENDER, GizConstants.KEY.DOB, GizConstants.KEY.ZEIR_ID,
                GizConstants.KEY.MOTHER_BASE_ENTITY_ID, GizConstants.KEY.MOTHER_FIRST_NAME,
                GizConstants.KEY.MOTHER_FIRST_NAME, GizConstants.KEY.INACTIVE,
                GizConstants.KEY.LOST_TO_FOLLOW_UP};

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

            sortValues(jsonValues);

            for (JSONObject client : jsonValues) {

                if (client == null) {
                    continue;
                }

                CheckChildDetailsModel checkChildDetails = new CheckChildDetailsModel(client).invoke();
                if (checkChildDetails.is())
                    continue;
                String entityId = checkChildDetails.getEntityId();
                String firstName = checkChildDetails.getFirstName();
                String middleName = checkChildDetails.getMiddleName();
                String lastName = checkChildDetails.getLastName();
                String gender = checkChildDetails.getGender();
                String dob = checkChildDetails.getDob();
                String zeirId = checkChildDetails.getZeirId();
                String inactive = checkChildDetails.getInactive();
                String lostToFollowUp = checkChildDetails.getLostToFollowUp();


                CheckMotherDetailsModel checkMotherDetails = new CheckMotherDetailsModel(client).invoke();
                String motherBaseEntityId = checkMotherDetails.getMotherBaseEntityId();
                String motherFirstName = checkMotherDetails.getMotherFirstName();
                String motherLastName = checkMotherDetails.getMotherLastName();

                matrixCursor.addRow(new Object[]{entityId, null, firstName, middleName, lastName, gender, dob, zeirId, motherBaseEntityId, motherFirstName, motherLastName, inactive, lostToFollowUp});
            }

            return matrixCursor;
        } else {
            return matrixCursor;
        }
    }

    private void sortValues(List<JSONObject> jsonValues) {
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {

                if (!lhs.has(GizConstants.KEY.CHILD) || !rhs.has(GizConstants.KEY.CHILD)) {
                    return 0;
                }

                JSONObject lhsChild = getJsonObject(lhs, GizConstants.KEY.CHILD);
                JSONObject rhsChild = getJsonObject(rhs, GizConstants.KEY.CHILD);

                String lhsInactive = getJsonString(getJsonObject(lhsChild, GizConstants.KEY.ATTRIBUTES), GizConstants.KEY.INACTIVE);
                String rhsInactive = getJsonString(getJsonObject(rhsChild, GizConstants.KEY.ATTRIBUTES), GizConstants.KEY.INACTIVE);

                int aComp = 0;
                if (lhsInactive.equalsIgnoreCase(Boolean.TRUE.toString()) && !rhsInactive.equalsIgnoreCase(Boolean.TRUE.toString())) {
                    aComp = 1;
                } else if (!lhsInactive.equalsIgnoreCase(Boolean.TRUE.toString()) && rhsInactive.equalsIgnoreCase(Boolean.TRUE.toString())) {
                    aComp = -1;
                }

                if (aComp != 0) {
                    return aComp;
                } else {
                    Integer lostToFollowUp = getLostToFollowUp(lhsChild, rhsChild);
                    if (lostToFollowUp != null) return lostToFollowUp;
                }

                return 0;

            }
        });
    }

    @Nullable
    private Integer getLostToFollowUp(JSONObject lhsChild, JSONObject rhsChild) {
        String lhsLostToFollowUp = getJsonString(getJsonObject(lhsChild, GizConstants.KEY.ATTRIBUTES), GizConstants.KEY.LOST_TO_FOLLOW_UP);
        String rhsLostToFollowUp = getJsonString(getJsonObject(rhsChild, GizConstants.KEY.ATTRIBUTES), GizConstants.KEY.LOST_TO_FOLLOW_UP);
        if (lhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString()) && !rhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString())) {
            return 1;
        } else if (!lhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString()) && rhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString())) {
            return -1;
        }
        return null;
    }

}
