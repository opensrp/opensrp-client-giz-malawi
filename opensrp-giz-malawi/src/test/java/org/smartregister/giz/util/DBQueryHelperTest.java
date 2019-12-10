package org.smartregister.giz.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.immunization.db.VaccineRepo;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest(VaccineRepo.class)
public class DBQueryHelperTest {

    @Test
    public void getFilterSelectionConditionWithVaccineArrayHavingTwoVaccines() {
        PowerMockito.mockStatic(VaccineRepo.class);
        ArrayList<VaccineRepo.Vaccine> arrayList = new ArrayList<>();
        arrayList.add(VaccineRepo.Vaccine.HepB);
        arrayList.add(VaccineRepo.Vaccine.penta1);
        PowerMockito.when(VaccineRepo.getVaccines("child")).thenReturn(arrayList);
        String expectedUrgentTrue = " ( dod is NULL OR dod = '' )  AND  (inactive IS NULL OR inactive != 'true' )  AND  (lost_to_follow_up IS NULL OR lost_to_follow_up != 'true' )  AND  (  HepB = 'urgent' OR  PENTA_1 = 'urgent'  OR  ( OPV_0 = 'urgent' AND OPV_4 != 'complete' )  OR  ( OPV_4 = 'urgent' AND OPV_0 != 'complete' )  OR  ( MEASLES_1 = 'urgent' AND MR_1 != 'complete' )  OR  ( MR_1 = 'urgent' AND MEASLES_1 != 'complete' )  OR  ( MEASLES_2 = 'urgent' AND MR_2 != 'complete' )  OR  ( MR_2 = 'urgent' AND MEASLES_2 != 'complete' )";
        String expectedUrgentFalse = expectedUrgentTrue + "  OR  HepB = 'normal' OR  PENTA_1 = 'normal'  OR  ( OPV_0 = 'normal' AND OPV_4 != 'complete' )  OR  ( OPV_4 = 'normal' AND OPV_0 != 'complete' )  OR  ( MEASLES_1 = 'normal' AND MR_1 != 'complete' )  OR  ( MR_1 = 'normal' AND MEASLES_1 != 'complete' )  " +
                "OR  ( MEASLES_2 = 'normal' AND MR_2 != 'complete' )  OR  ( MR_2 = 'normal' AND MEASLES_2 != 'complete' )  ) ";
        Assert.assertEquals(expectedUrgentTrue + "  ) ", DBQueryHelper.getFilterSelectionCondition(true));

        Assert.assertEquals(expectedUrgentFalse, DBQueryHelper.getFilterSelectionCondition(false));
    }
}