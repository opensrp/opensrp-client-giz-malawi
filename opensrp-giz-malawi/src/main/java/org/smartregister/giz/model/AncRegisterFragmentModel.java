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
        countQueryBuilder.customJoin(" join " + AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable()
                + " on " + AncLibrary.getInstance().getRegisterQueryProvider().getDemographicTable() + ".base_entity_id = " + AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + ".base_entity_id " +
                "inner join client_register_type on ec_client.id=client_register_type.base_entity_id");
        return countQueryBuilder.mainCondition(" client_register_type.register_type = 'anc'");
    }

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{tableName + "." + DBConstantsUtils.KeyUtils.RELATIONAL_ID, tableName + "." + DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH,
                tableName + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, tableName + "." + DBConstantsUtils.KeyUtils.FIRST_NAME,
                tableName + "." + DBConstantsUtils.KeyUtils.LAST_NAME, tableName + "." + DBConstantsUtils.KeyUtils.ANC_ID,
                tableName + "." + DBConstantsUtils.KeyUtils.DOB, AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PHONE_NUMBER,
                AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_NAME, tableName + "." + DBConstantsUtils.KeyUtils.DATE_REMOVED,
                AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.EDD, AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.CONTACT_STATUS,
                AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT, AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE};
        queryBuilder.SelectInitiateMainTable(tableName, columns);
        queryBuilder.customJoin(" join " + AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable()
                + " on "+AncLibrary.getInstance().getRegisterQueryProvider().getDemographicTable()+".base_entity_id = " + AncLibrary.getInstance().getRegisterQueryProvider().getDetailsTable() + ".base_entity_id " +
                "inner join client_register_type on ec_client.id=client_register_type.base_entity_id");
        mainCondition += " and  client_register_type.register_type = 'anc'";
        return queryBuilder.mainCondition(mainCondition);
    }
}
