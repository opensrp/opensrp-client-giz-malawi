package org.smartregister.giz.activity;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.MotherLookUpUtils;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.giz.fragment.GizChildFormFragment;

import java.util.Map;

public class ChildFormActivity extends BaseChildFormActivity {
    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        GizChildFormFragment gizChildFormFragment = GizChildFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, gizChildFormFragment).commit();
    }

    @Override
    public String lookUpQuery(Map<String, String> entityMap, String tableName) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName,
                new String[]{MotherLookUpUtils.RELATIONALID, MotherLookUpUtils.DETAILS, Constants.KEY.ZEIR_ID,
                        Constants.KEY.FIRST_NAME, Constants.KEY.LAST_NAME,Constants.KEY.DOB, Constants.KEY.BASE_ENTITY_ID}

        );
        String query = queryBuilder.mainCondition(getMainConditionString(entityMap));
        return queryBuilder.Endquery(query);
    }
}
