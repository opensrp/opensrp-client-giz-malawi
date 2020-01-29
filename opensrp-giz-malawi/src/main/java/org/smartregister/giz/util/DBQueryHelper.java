package org.smartregister.giz.util;

import org.smartregister.child.util.Constants;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.util.VaccinateActionUtils;

import java.util.List;

/**
 * Created by ndegwamartin on 2019-05-30.
 */

public class DBQueryHelper {

    public static final String getHomeRegisterCondition() {
        return GizConstants.TABLE_NAME.CHILD + "." + Constants.KEY.DATE_REMOVED + " IS NULL ";
    }

    public static String getFilterSelectionCondition(boolean urgentOnly) {

        final String AND = " AND ";
        final String OR = " OR ";
        final String IS_NULL_OR = " IS NULL OR ";
        final String TRUE = "'tru/**/e'";

        String mainCondition = " ( " + Constants.KEY.DOD + " is NULL OR " + Constants.KEY.DOD + " = '' ) " +
                AND + " (" + Constants.CHILD_STATUS.INACTIVE + IS_NULL_OR + Constants.CHILD_STATUS.INACTIVE + " != " + TRUE + " ) " +
                AND + " (" + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + IS_NULL_OR + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != " + TRUE + " ) " +
                AND + " ( ";
        List<VaccineRepo.Vaccine> vaccines = ImmunizationLibrary.getInstance().getVaccineCacheMap().get(Constants.CHILD_TYPE).vaccineRepo;

        vaccines.remove(VaccineRepo.Vaccine.bcg2);

        final String URGENT = "'" + AlertStatus.urgent.value() + "'";
        final String NORMAL = "'" + AlertStatus.normal.value() + "'";

        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + OR;
            }
        }

        if (urgentOnly) {
            return mainCondition + " ) ";
        }

        mainCondition += OR;
        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + OR;
            }
        }

        return mainCondition + " ) ";
    }

    public static String getSortQuery() {
        return GizConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }
}
