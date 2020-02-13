package org.smartregister.giz.repository;

import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;

public class GizChildRegisterQueryProvider extends RegisterQueryProvider {

    @Override
    public String getCountExecuteQuery(String mainCondition, String filters) {
        return super.getCountExecuteQuery(mainCondition, filters);
    }

    @Override
    public String mainRegisterQuery() {
        return super.mainRegisterQuery();
    }

    @Override
    public String[] getMainColumns() {
        return new String[]{
                getDemographicTable() + "." + Constants.KEY.ID + " as _id",
                getDemographicTable() + "." + Constants.KEY.RELATIONALID,
                getDemographicTable() + "." + Constants.KEY.ZEIR_ID,
                getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID,
                getDemographicTable() + "." + Constants.KEY.GENDER,
                getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID,
                getDemographicTable() + "." + Constants.KEY.FIRST_NAME,
                getDemographicTable() + "." + Constants.KEY.LAST_NAME,
                "mother" + "." + Constants.KEY.FIRST_NAME + " as mother_first_name",
                "mother" + "." + Constants.KEY.LAST_NAME + " as mother_last_name",
                getDemographicTable() + "." + Constants.KEY.DOB,
                "mother" + "." + Constants.KEY.DOB + " as mother_dob",
                getMotherDetailsTable() + "." + Constants.KEY.NRC_NUMBER + " as mother_nrc_number",
                getDemographicTable() + "." + Constants.KEY.CLIENT_REG_DATE,
                getDemographicTable() + "." + Constants.KEY.LAST_INTERACTED_WITH,
                getChildDetailsTable() + "." + Constants.KEY.INACTIVE,
                getChildDetailsTable() + "." + Constants.KEY.LOST_TO_FOLLOW_UP,
        };
    }

}
