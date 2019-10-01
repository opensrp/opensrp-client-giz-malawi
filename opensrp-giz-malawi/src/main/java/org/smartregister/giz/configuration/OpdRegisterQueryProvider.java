package org.smartregister.giz.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.opd.configuration.OpdRegisterQueryProviderContract;
import org.smartregister.opd.pojos.InnerJoinObject;
import org.smartregister.opd.pojos.QueryTable;

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
                sqb.countQueryFts("ec_mother", null, null, filters)
        };
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        QueryTable childTableCol = new QueryTable();
        childTableCol.setTableName("ec_child");
        childTableCol.setColNames(new String[]{
                "first_name",
                "last_name",
                "middle_name",
                "gender",
                "dob",
                "home_address",
                "'Child' AS register_type",
                "relational_id AS relationalid",
                "last_interacted_with"
        });

        QueryTable womanTableCol = new QueryTable();
        womanTableCol.setTableName("ec_mother");
        womanTableCol.setColNames(new String[]{
                "first_name",
                "last_name",
                "middle_name",
                "'Female' AS gender",
                "dob",
                "home_address",
                "'ANC' AS register_type",
                "relationalid",
                "last_interacted_with",
                "NULL AS mother_first_name",
                "NULL AS mother_last_name",
                "NULL AS mother_middle_name",
        });

        QueryTable clientTableCol = new QueryTable();
        clientTableCol.setTableName("ec_client");
        clientTableCol.setColNames(new String[]{
                "first_name",
                "last_name",
                "'' AS middle_name",
                "gender",
                "dob",
                "'' AS home_address",
                "'OPD' AS register_type",
                "relationalid",
                "last_interacted_with",
                "NULL AS mother_first_name",
                "NULL AS mother_last_name",
                "NULL AS mother_middle_name",
        });

        InnerJoinObject[] tablesWithInnerJoins = new InnerJoinObject[1];
        InnerJoinObject tableColsInnerJoin = new InnerJoinObject();
        tableColsInnerJoin.setFirstTable(childTableCol);

        QueryTable innerJoinMotherTable = new QueryTable();
        innerJoinMotherTable.setTableName("ec_mother");
        innerJoinMotherTable.setColNames(new String[]{
                "first_name AS mother_first_name",
                "last_name AS mother_last_name",
                "middle_name AS mother_middle_name"
        });
        tableColsInnerJoin.innerJoinOn("ec_child.relational_id = ec_mother.base_entity_id");
        tableColsInnerJoin.innerJoinTable(innerJoinMotherTable);
        tablesWithInnerJoins[0] = tableColsInnerJoin;

        return mainSelectWhereIdsIn(tablesWithInnerJoins, new QueryTable[]{womanTableCol, clientTableCol});
    }
}
