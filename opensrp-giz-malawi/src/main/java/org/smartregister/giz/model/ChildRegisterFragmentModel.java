package org.smartregister.giz.model;

import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.domain.Response;
import org.smartregister.giz.util.GizConstants;

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
}
