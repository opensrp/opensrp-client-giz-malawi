package org.smartregister.giz.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.growthmonitoring.util.GrowthMonitoringConstants;

public class GizChildRegisterQueryProvider extends RegisterQueryProvider {

    @Override
    public String getObjectIdsQuery(String mainCondition, String filters) {
        String strMainCondition = getMainCondition(mainCondition);

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" where " + getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }

        return "select " + getDemographicTable() + ".object_id from " + CommonFtsObject.searchTableName(getDemographicTable()) + " " + getDemographicTable() + "  " +
                "join " + getChildDetailsTable() + " on " + getDemographicTable() + ".object_id =  " + getChildDetailsTable() + ".id " +
                "left join (select * from " + CommonFtsObject.searchTableName(getChildDetailsTable()) + ") " + CommonFtsObject.searchTableName(getChildDetailsTable()) + " on " + getDemographicTable() + ".object_id =  " + CommonFtsObject.searchTableName(getChildDetailsTable()) + ".object_id "
                + strMainCondition + strFilters;
    }

    @Override
    public String getCountExecuteQuery(String mainCondition, String filters) {
        String strMainCondition = getMainCondition(mainCondition);

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" where " + getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }

        return "select count(" + getDemographicTable() + ".object_id) from " + CommonFtsObject.searchTableName(getDemographicTable()) + " " + getDemographicTable() + "  " +
                "join " + getChildDetailsTable() + " on " + getDemographicTable() + ".object_id =  " + getChildDetailsTable() + ".id " +
                "left join (select * from " + CommonFtsObject.searchTableName(getChildDetailsTable()) + ") " + CommonFtsObject.searchTableName(getChildDetailsTable()) + " on " + getDemographicTable() + ".object_id =  " + CommonFtsObject.searchTableName(getChildDetailsTable()) + ".object_id "
                + strMainCondition + strFilters;
    }

    private String getFilter(String filters) {
        if (StringUtils.isNotBlank(filters)) {
            return String.format(" AND " + getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }
        return "";
    }

    private String getMainCondition(String mainCondition) {
        if (!StringUtils.isBlank(mainCondition)) {
            return " where " + mainCondition;
        }
        return "";
    }

    @Override
    public String[] mainColumns() {
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
                getDemographicTable() + "." + Constants.KEY.DOD,
                "mother" + "." + Constants.KEY.DOB + " as mother_dob",
                getMotherDetailsTable() + "." + Constants.KEY.NRC_NUMBER + " as mother_nrc_number",
                getDemographicTable() + "." + Constants.KEY.CLIENT_REG_DATE,
                getDemographicTable() + "." + Constants.KEY.LAST_INTERACTED_WITH,
                getChildDetailsTable() + "." + "inactive",
                getChildDetailsTable() + "." + Constants.KEY.LOST_TO_FOLLOW_UP,
                getDemographicTable() + "." + "village",
                getDemographicTable() + "." + "home_address",
                getChildDetailsTable() + "." + Constants.SHOW_BCG_SCAR,
                getChildDetailsTable() + "." + Constants.SHOW_BCG2_REMINDER,
                getMotherDetailsTable() + "." + GizConstants.PROTECTED_AT_BIRTH,
                getMotherDetailsTable() + "." + GizConstants.MOTHER_TDV_DOSES,
                getMotherDetailsTable() + "." + GizConstants.MOTHER_HIV_STATUS,
                getChildDetailsTable() + "." + GizConstants.BIRTH_REGISTRATION_NUMBER,
                getChildDetailsTable() + "." + GrowthMonitoringConstants.PMTCT_STATUS,
                getChildDetailsTable() + "." + Constants.KEY.CHILD_HIV_STATUS,
                getChildDetailsTable() + "." + Constants.KEY.CHILD_TREATMENT,

        };
    }

}
