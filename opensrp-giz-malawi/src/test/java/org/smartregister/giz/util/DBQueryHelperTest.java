package org.smartregister.giz.util;

import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.util.Constants;
import org.smartregister.giz.activity.ChildFormActivity;
import org.smartregister.giz.activity.ChildImmunizationActivity;
import org.smartregister.giz.activity.ChildProfileActivity;
import org.smartregister.giz.repository.GizChildRegisterQueryProvider;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.util.VaccineCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({VaccineRepo.class, ImmunizationLibrary.class, ChildLibrary.class})
public class DBQueryHelperTest {
    @Mock
    private ImmunizationLibrary immunizationLibrary;

    @Mock
    private ChildLibrary childLibrary;

    private Map<String, VaccineCache> vaccineCacheMap = new HashMap<>();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getFilterSelectionConditionWithVaccineArrayHavingTwoVaccines() {
        PowerMockito.mockStatic(VaccineRepo.class);
        ArrayList<VaccineRepo.Vaccine> arrayList = new ArrayList<>();
        arrayList.add(VaccineRepo.Vaccine.HepB);
        arrayList.add(VaccineRepo.Vaccine.penta1);

        PowerMockito.mockStatic(ImmunizationLibrary.class);
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ImmunizationLibrary.getInstance()).thenReturn(immunizationLibrary);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);

        ChildMetadata metadata = new ChildMetadata(ChildFormActivity.class, ChildProfileActivity.class,
                ChildImmunizationActivity.class, null,true, new GizChildRegisterQueryProvider());
        metadata.updateChildRegister(GizConstants.JSON_FORM.CHILD_ENROLLMENT, GizConstants.TABLE_NAME.ALL_CLIENTS,
                GizConstants.TABLE_NAME.ALL_CLIENTS, GizConstants.EventType.CHILD_REGISTRATION,
                GizConstants.EventType.UPDATE_CHILD_REGISTRATION, GizConstants.EventType.OUT_OF_CATCHMENT, GizConstants.CONFIGURATION.CHILD_REGISTER,
                GizConstants.RELATIONSHIP.MOTHER, GizConstants.JSON_FORM.OUT_OF_CATCHMENT_SERVICE);

        Mockito.doReturn(metadata).when(childLibrary).metadata();

        VaccineCache vaccineCache = new VaccineCache();
        vaccineCache.vaccines = VaccineRepo.Vaccine.values();
        vaccineCache.vaccineRepo = Lists.newArrayList(arrayList);

        vaccineCacheMap.put(Constants.CHILD_TYPE, vaccineCache);
        PowerMockito.when(immunizationLibrary.getVaccineCacheMap()).thenReturn(vaccineCacheMap);

        String expectedUrgentTrue = " ( dod is NULL OR dod = '' )  AND  ( ec_child_details.inactive IS NULL OR ec_child_details.inactive != 'true' )  AND  ( ec_child_details.lost_to_follow_up IS NULL OR ec_child_details.lost_to_follow_up != 'true' )  AND  (  HepB = 'urgent' OR  PENTA_1 = 'urgent'";
        String expectedUrgentFalse = expectedUrgentTrue + "  OR  HepB = 'normal' OR  PENTA_1 = 'normal'  ) ";
        Assert.assertEquals(expectedUrgentTrue + "  ) ", DBQueryHelper.getFilterSelectionCondition(true));

        Assert.assertEquals(expectedUrgentFalse, DBQueryHelper.getFilterSelectionCondition(false));
    }
}