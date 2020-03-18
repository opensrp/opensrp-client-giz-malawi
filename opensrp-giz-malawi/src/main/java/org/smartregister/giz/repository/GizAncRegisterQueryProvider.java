package org.smartregister.giz.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.repository.RegisterQueryProvider;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;

public class GizAncRegisterQueryProvider extends RegisterQueryProvider {

    @Override
    public String getObjectIdsQuery(String mainCondition, String filters) {
        if (!filters.isEmpty()) {
            filters = String.format(" and ec_client_search.phrase MATCH '*%s*'", filters);
        }

        if (!StringUtils.isBlank(mainCondition)) {
            mainCondition = " and " + mainCondition;
        }

        return "select ec_client_search.object_id from ec_client_search " +
                "join ec_mother_details on ec_client_search.object_id =  ec_mother_details.id " +
                "join client_register_type on ec_client_search.object_id=client_register_type.base_entity_id " +
                "where register_type='anc' " + mainCondition + filters;
    }

    public String getCountExecuteQuery(String mainCondition, String filters) {
        if (!filters.isEmpty()) {
            filters = String.format(" and ec_client_search.phrase MATCH '*%s*'", filters);
        }

        if (!StringUtils.isBlank(mainCondition)) {
            mainCondition = " and " + mainCondition;
        }

        return "select count(ec_client_search.object_id) from ec_client_search " +
                "join ec_mother_details on ec_client_search.object_id =  ec_mother_details.id " +
                "join client_register_type on ec_client_search.object_id=client_register_type.base_entity_id " +
                "where register_type='anc' " + mainCondition + filters;
    }

    @Override
    public String[] mainColumns() {
        return new String[]{DBConstantsUtils.KeyUtils.FIRST_NAME, DBConstantsUtils.KeyUtils.LAST_NAME, DBConstantsUtils.KeyUtils.DOB,
                DBConstantsUtils.KeyUtils.DOB_UNKNOWN, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PHONE_NUMBER, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_NAME,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_PHONE_NUMBER, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " as " + DBConstantsUtils.KeyUtils.ID_LOWER_CASE, DBConstantsUtils.KeyUtils.ANC_ID,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.REMINDERS, DBConstantsUtils.KeyUtils.HOME_ADDRESS, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.EDD,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.CONTACT_STATUS, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.VISIT_START_DATE, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.RELATIONAL_ID, getDemographicTable() + "." + ConstantsUtils.JsonFormKeyUtils.VILLAGE,
                getDemographicTable() + "." + GizConstants.NATIONAL_ID, getDemographicTable() + "." + GizConstants.BHT_MID};
    }

}
