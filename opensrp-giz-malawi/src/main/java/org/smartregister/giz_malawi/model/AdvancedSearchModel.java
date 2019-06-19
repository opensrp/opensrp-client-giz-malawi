package org.smartregister.giz_malawi.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.giz_malawi.util.DBConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class AdvancedSearchModel extends BaseChildAdvancedSearchModel {

    @Override
    public Map<String, String> createEditMap(Map<String, String> editMap_, boolean isLocal) {
        Map<String, String> editMap = new HashMap<>(editMap_);

        String firstName = editMap.get(DBConstants.KEY.FIRST_NAME);
        String lastName = editMap.get(DBConstants.KEY.LAST_NAME);
        String opensrpID = editMap.get(DBConstants.KEY.MER_ID);
        String dob = editMap.get(DBConstants.KEY.DOB);
        String phoneNumber = editMap.get(DBConstants.KEY.CONTACT_PHONE_NUMBER);

        if (StringUtils.isNotBlank(firstName)) {
            editMap.put(isLocal ? DBConstants.KEY.FIRST_NAME : GLOBAL_FIRST_NAME, firstName);
        }
        if (StringUtils.isNotBlank(lastName)) {
            editMap.put(isLocal ? DBConstants.KEY.LAST_NAME : GLOBAL_LAST_NAME, lastName);
        }
        if (StringUtils.isNotBlank(opensrpID)) {
            editMap.put(isLocal ? DBConstants.KEY.MER_ID : GLOBAL_IDENTIFIER,
                    isLocal ? opensrpID : OPENSRP_ID + ":" + opensrpID);
        }

        if (StringUtils.isNotBlank(dob)) {
            editMap.put(isLocal ? DBConstants.KEY.DOB : GLOBAL_BIRTH_DATE, dob);
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            editMap.put(isLocal ? DBConstants.KEY.CONTACT_PHONE_NUMBER : PHONE_NUMBER, phoneNumber);
        }
        return editMap;
    }

    @Override
    protected String getKey(String key) {
        String resKey = "";
        switch (key) {
            case DBConstants.KEY.FIRST_NAME:
                resKey = FIRST_NAME;
                break;
            case DBConstants.KEY.LAST_NAME:
                resKey = LAST_NAME;
                break;
            case DBConstants.KEY.MER_ID:
                resKey = SEARCH_TERM_OPENSRP_ID;
                break;
            case DBConstants.KEY.DOB:
                resKey = DOB;
                break;
            case DBConstants.KEY.CONTACT_PHONE_NUMBER:
                resKey = MOBILE_PHONE_NUMBER;
                break;
            default:
                break;
        }

        return resKey;
    }

    @Override
    protected String[] mainColumns(String tableName, String parentTableName) {
        String[] columns = new String[] {

                tableName + "." + DBConstants.KEY.RELATIONALID,
                tableName + "." + DBConstants.KEY.DETAILS,
                tableName + "." + DBConstants.KEY.MER_ID,
                tableName + "." + DBConstants.KEY.RELATIONAL_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + AllConstants.ChildRegistrationFields.GENDER,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                parentTableName + "." + DBConstants.KEY.MOTHER_FIRST_NAME + " as mother_first_name",
                parentTableName + "." + DBConstants.KEY.MOTHER_LAST_NAME + " as mother_last_name",
                parentTableName + "." + DBConstants.KEY.MOTHER_DOB + " as mother_dob",
                parentTableName + "." + DBConstants.KEY.MOTHER_NID + " as mother_nid",
                tableName + "." + DBConstants.KEY.FATHER_FIRST_NAME,
                tableName + "." + DBConstants.KEY.DOB,
                tableName + "." + DBConstants.KEY.CONTACT_PHONE_NUMBER,
                tableName + "." + DBConstants.KEY.PROVIDER_UC,
                tableName + "." + DBConstants.KEY.PROVIDER_TOWN,
                tableName + "." + DBConstants.KEY.PROVIDER_ID,
                tableName + "." + DBConstants.KEY.PROVIDER_LOCATION_ID,
                tableName + "." + DBConstants.KEY.CLIENT_REG_DATE,
                tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH
        };
        return columns;
    }
}
