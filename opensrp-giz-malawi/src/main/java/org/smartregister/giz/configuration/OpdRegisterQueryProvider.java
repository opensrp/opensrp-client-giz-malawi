package org.smartregister.giz.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdRegisterQueryProviderContract;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdUtils;

import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-23
 */

public class OpdRegisterQueryProvider extends OpdRegisterQueryProviderContract {

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
        Date latestValidCheckInDate = OpdLibrary.getInstance().getLatestValidCheckInDate();
        String oneDayAgo = OpdUtils.convertDate(latestValidCheckInDate, OpdDbConstants.DATE_FORMAT);

        if (!TextUtils.isEmpty(filters)) {
            if (!TextUtils.isEmpty(mainCondition)) {
                String sql =
                        "SELECT ec_client_search.object_id, ec_client_search.last_interacted_with, (opd_details.current_visit_start_date IS NOT NULL AND opd_details.current_visit_start_date >= '$latest_start_visit_date' AND opd_details.current_visit_end_date IS NULL) AS checked_in FROM ec_client_search WHERE date_removed IS NULL AND phrase MATCH '%s*') " +
                        "    LEFT JOIN opd_details ON ec_client_search.object_id = opd_details.base_entity_id\n" +
                        "WHERE checked_in " +
                        "ORDER BY last_interacted_with DESC";
                sql = sql.replace("%s", filters);
                sql = sql.replace("$latest_start_visit_date", oneDayAgo);
                return sql;

            } else {
                String sql = "SELECT object_id FROM " +
                        "(SELECT object_id, last_interacted_with FROM ec_client_search WHERE date_removed IS NULL AND phrase MATCH '%s*') " +
                        "ORDER BY last_interacted_with DESC";
                sql = sql.replace("%s", filters);
                return sql;
            }
        } else if (!TextUtils.isEmpty(mainCondition)) {
            String sqlQuery =
                    "Select ec_client.id as object_id, ec_client.last_interacted_with, (opd_details.current_visit_start_date IS NOT NULL AND opd_details.current_visit_start_date >= '$latest_start_visit_date' AND opd_details.current_visit_end_date IS NULL) AS checked_in FROM ec_client \n" +
                    "    LEFT JOIN opd_details ON ec_client.base_entity_id = opd_details.base_entity_id\n" +
                    "    WHERE  checked_in \n" +
                    "ORDER BY last_interacted_with DESC";

            return sqlQuery.replace("$latest_start_visit_date", oneDayAgo);
        } else {
            return "SELECT object_id FROM " +
                    "(SELECT object_id, last_interacted_with FROM ec_client_search) " +
                    "ORDER BY last_interacted_with DESC";
        }
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();

        return new String[] {
                sqb.countQueryFts("ec_client", null, mainCondition, filters)
        };
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        Date latestValidCheckInDate = OpdLibrary.getInstance().getLatestValidCheckInDate();
        String oneDayAgo = OpdUtils.convertDate(latestValidCheckInDate, OpdDbConstants.DATE_FORMAT);
        String sqlQuery =
                "Select  d.id as _id , d.first_name , d.last_name , '' AS middle_name , d.gender , d.dob , '' AS home_address , c.first_name as mother_first_name, c.last_name as mother_last_name, \n" +
                        "null as middle_name, d.relationalid ,d.opensrp_id AS register_id, upper(rt.register_type) as register_type,\n" +
                        " d.last_interacted_with, (opd_details.current_visit_start_date >= '$latest_start_visit_date' AND opd_details.current_visit_end_date IS NULL) AS pending_diagnose_and_treat, 'ec_client' as entity_table, opd_details.current_visit_end_date  FROM ec_client d LEFT JOIN opd_details ON d.base_entity_id = opd_details.base_entity_id left join ec_child_details ecd on ecd.base_entity_id=d.id \n" +
                        " left join client_register_type rt on rt.base_entity_id = d.id  left join ec_client c on ecd.relational_id=c.id " +
                        "WHERE  d.id IN (%s) " +
                        "ORDER BY d.last_interacted_with DESC";

        return sqlQuery.replace("$latest_start_visit_date", oneDayAgo);
    }
}
