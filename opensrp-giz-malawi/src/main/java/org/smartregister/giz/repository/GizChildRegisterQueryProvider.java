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
            strFilters = String.format(" where " + getDemographicTable() + ".first_name like '%%%1$s%%' " + " OR " + getDemographicTable() + ".last_name like '%%%1$s%%' " + " OR " + getDemographicTable() + ".opensrp_id like '%%%1$s%%' ", filters);
        }

        return "select " + getDemographicTable() + ".id from " + getDemographicTable() +
                " join " + CommonFtsObject.searchTableName(getChildDetailsTable()) + " ec_child_details on " + getDemographicTable() + ".id =  ec_child_details.object_id "
                + strMainCondition + strFilters;
    }

    @Override
    public String getCountExecuteQuery(String mainCondition, String filters) {
        String strMainCondition = getMainCondition(mainCondition);

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" where " + getDemographicTable() + ".first_name like '%%%1$s%%' " + " OR " + getDemographicTable() + ".last_name like '%%%1$s%%' " + " OR " + getDemographicTable() + ".opensrp_id like '%%%1$s%%' ", filters);
        }

        return "select count(" + getDemographicTable() + ".id) from " + getDemographicTable() +
                " join " + CommonFtsObject.searchTableName(getChildDetailsTable()) + " ec_child_details on " + getDemographicTable() + ".id =  ec_child_details.object_id "
                + strMainCondition + strFilters;
    }

    private String getFilter(String filters) {
        if (StringUtils.isNotBlank(filters)) {
            return String.format(" AND " + getDemographicTable() + ".first_name like '%%%1$s%%' " + " OR " + getDemographicTable() + ".last_name like '%%%1$s%%' " + " OR " + getDemographicTable() + ".opensrp_id like '%%%1$s%%' ", filters);
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
                getDemographicTable() + "." + "middle_name",
                "mother" + "." + Constants.KEY.FIRST_NAME + " as mother_first_name",
                "mother" + "." + Constants.KEY.LAST_NAME + " as mother_last_name",
                getDemographicTable() + "." + Constants.KEY.DOB,
                getDemographicTable() + "." + Constants.KEY.DOD,
                "mother" + "." + Constants.KEY.DOB + " as mother_dob",
                getMotherDetailsTable() + "." + Constants.KEY.NRC_NUMBER + " as nrc_number",
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
                getChildDetailsTable() + "." + "child_hiv_status",
                getChildDetailsTable() + "." + "child_treatment",
                getMotherDetailsTable() + ".second_phone_number as second_phone_number",
                getMotherDetailsTable() + ".alt_phone_number as mother_guardian_number"};
    }

    @Override
    public String getActiveChildrenQuery() {
        return "SELECT count(id) FROM " + this.getChildDetailsTable() + " INNER JOIN " + this.getDemographicTable() + " ON "+ this.getDemographicTable() + ".id = " + this.getChildDetailsTable() + ".id  WHERE (" +
                "date_removed" + " IS NULL  AND (" + this.getChildDetailsTable() + ".inactive is NOT true OR " + this.getChildDetailsTable() + ".inactive is NULL)  AND " + "is_closed" + " IS NOT '1')" +
                " AND " + this.getDemographicTable() + ".is_closed IS NOT '1'";
    }

    @Override
    public String getActiveChildrenIds() {
        return "SELECT " + this.getChildDetailsTable() + ".id" + " FROM " + this.getChildDetailsTable() + " INNER JOIN " + this.getDemographicTable() + " ON " + this.getChildDetailsTable() + "." + "id" + " = " + this.getDemographicTable() + "." + "id" + " WHERE (" + this.getChildDetailsTable() + "." + "date_removed" + " IS NULL AND (" + this.getChildDetailsTable() + ".inactive is NOT true OR " + this.getChildDetailsTable() + ".inactive is NULL) AND " + this.getChildDetailsTable() + "." + "is_closed" + " IS NOT '1') AND " + this.getDemographicTable() + ".is_closed IS NOT '1'  ORDER BY " + this.getDemographicTable() + "." + "last_interacted_with" + " DESC ";
    }

}
