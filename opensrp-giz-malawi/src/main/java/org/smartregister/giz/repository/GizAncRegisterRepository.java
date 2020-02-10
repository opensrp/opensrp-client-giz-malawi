package org.smartregister.giz.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.repository.RegisterRepository;

public class GizAncRegisterRepository extends RegisterRepository {
    @Override
    public String getObjectIdsQuery(String mainCondition, String filters, String detailsCondition) {
        if (!filters.isEmpty()) {
            filters = " AND (ec_client.first_name like '%$s%' or ec_client.last_name like '%$s%')".replace("$s", filters);
        }
        if (!StringUtils.isBlank(mainCondition)) {
            mainCondition = " AND " + mainCondition;
        }

        if (!StringUtils.isBlank(detailsCondition)) {
            detailsCondition = " where " + detailsCondition;
        } else {
            detailsCondition = "";
        }

        return "select ec_client.id from ec_client left join client_register_type on ec_client.id=client_register_type.base_entity_id where register_type='anc' and ec_client.id IN (select id from ec_mother_details " + detailsCondition + " )  " + mainCondition + filters;
    }

}
