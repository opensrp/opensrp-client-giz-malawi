package org.smartregister.giz.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.opd.configuration.OpdRegisterQueryProviderContract;
import org.smartregister.opd.utils.OpdDbConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-23
 */

public class OpdRegisterQueryProvider extends OpdRegisterQueryProviderContract {

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters) {
        if (TextUtils.isEmpty(filters)) {
            return "SELECT object_id FROM " +
                    "(SELECT object_id, last_interacted_with FROM ec_child_search " +
                    "UNION ALL SELECT object_id, last_interacted_with FROM ec_mother_search " +
                    "UNION ALL SELECT object_id, last_interacted_with FROM ec_client_search) " +
                    "ORDER BY last_interacted_with DESC";
        } else {
            String sql = "SELECT object_id FROM " +
                    "(SELECT object_id, last_interacted_with FROM ec_child_search WHERE date_removed IS NULL AND phrase MATCH '%s*' " +
                    "UNION ALL SELECT object_id, last_interacted_with FROM ec_mother_search WHERE date_removed IS NULL AND phrase MATCH '%s*' " +
                    "UNION ALL SELECT object_id, last_interacted_with FROM ec_client_search WHERE date_removed IS NULL AND phrase MATCH '%s*') " +
                    "ORDER BY last_interacted_with DESC";
            sql = sql.replace("%s", filters);
            return sql;
        }
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();

        return new String[] {
                sqb.countQueryFts("ec_child", null, null, filters),
                sqb.countQueryFts("ec_mother", null, null, filters),
                sqb.countQueryFts("ec_client", null, null, filters)
        };
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String oneDayAgo = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT, Locale.US).format(calendar.getTime());
        String sqlQuery = "Select ec_child.id as _id, ec_child.first_name, ec_child.last_name, ec_child.middle_name, ec_child.gender, ec_child.dob, ec_child.home_address, 'Child' AS register_type, ec_child.relational_id AS relationalid, ec_child.zeir_id AS register_id, ec_child.last_interacted_with, ec_mother.first_name AS mother_first_name, ec_mother.last_name AS mother_last_name, ec_mother.middle_name AS mother_middle_name, opd_details.current_visit_start_date >= '$latest_start_visit_date' AS pending_diagnose_and_treat, 'ec_child' as entity_table FROM ec_child \n" +
                "    INNER JOIN ec_mother ON ec_child.relational_id = ec_mother.base_entity_id \n" +
                "    LEFT JOIN opd_details ON ec_child.base_entity_id = opd_details.base_entity_id\n" +
                "    WHERE  ec_child.id IN (%s) \n" +
                "UNION ALL \n" +
                "Select ec_mother.id as _id , first_name , last_name , middle_name , 'Female' AS gender , dob , home_address , 'ANC' AS register_type , relationalid , register_id , last_interacted_with , NULL AS mother_first_name , NULL AS mother_last_name , NULL AS mother_middle_name, opd_details.current_visit_start_date >= '$latest_start_visit_date' AS pending_diagnose_and_treat, 'ec_mother' as entity_table FROM ec_mother\n" +
                "    LEFT JOIN opd_details ON ec_mother.base_entity_id = opd_details.base_entity_id\n" +
                "    WHERE  ec_mother.id IN (%s) \n" +
                "UNION ALL \n" +
                "Select ec_client.id as _id , first_name , last_name , '' AS middle_name , gender , dob , '' AS home_address , 'OPD' AS register_type , relationalid , opensrp_id AS register_id , last_interacted_with , NULL AS mother_first_name , NULL AS mother_last_name , NULL AS mother_middle_name, opd_details.current_visit_start_date >= '$latest_start_visit_date' AS pending_diagnose_and_treat, 'ec_client' as entity_table FROM ec_client \n" +
                "    LEFT JOIN opd_details ON ec_client.base_entity_id = opd_details.base_entity_id\n" +
                "WHERE  ec_client.id IN (%s)\n" +
                "ORDER BY last_interacted_with DESC";

        return sqlQuery.replace("$latest_start_visit_date", oneDayAgo);
    }
}
