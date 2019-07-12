package org.smartregister.giz_malawi.model;

import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.domain.Response;
import org.smartregister.giz_malawi.util.DBConstants;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class ChildRegisterFragmentModel extends BaseChildRegisterFragmentModel {
    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {
        //Just overriddenn
        return null;
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
}
