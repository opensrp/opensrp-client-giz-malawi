package org.smartregister.giz.model;

import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.model.RegisterFragmentModel;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

public class AncRegisterFragmentModel extends RegisterFragmentModel {

    @Override
    public String countSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.SelectInitiateMainTableCounts(tableName);
        countQueryBuilder.customJoin(" join " + AncLibrary.getInstance().getRegisterRepository().getDetailsTable()
                + " on " + AncLibrary.getInstance().getRegisterRepository().getDemographicTable() + ".base_entity_id = " + AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + ".base_entity_id " +
                "inner join register_type on ec_client.id=register_type.base_entity_id");
        mainCondition = " register_type.register_type = 'anc'";
        return countQueryBuilder.mainCondition(mainCondition);
    }

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{tableName + "." + DBConstantsUtils.KeyUtils.RELATIONAL_ID, tableName + "." + DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH,
                tableName + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, tableName + "." + DBConstantsUtils.KeyUtils.FIRST_NAME,
                tableName + "." + DBConstantsUtils.KeyUtils.LAST_NAME, tableName + "." + DBConstantsUtils.KeyUtils.ANC_ID,
                tableName + "." + DBConstantsUtils.KeyUtils.DOB, AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PHONE_NUMBER,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_NAME, tableName + "." + DBConstantsUtils.KeyUtils.DATE_REMOVED,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.EDD, AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.CONTACT_STATUS,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT, AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE};
        queryBuilder.SelectInitiateMainTable(tableName, columns);
        queryBuilder.customJoin(" join " + AncLibrary.getInstance().getRegisterRepository().getDetailsTable()
                + " on "+AncLibrary.getInstance().getRegisterRepository().getDemographicTable()+".base_entity_id = " + AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + ".base_entity_id " +
                "inner join register_type on ec_client.id=register_type.base_entity_id");
        mainCondition += mainCondition + " and  register_type.register_type = 'anc'";
        return queryBuilder.mainCondition(mainCondition);
    }
}
