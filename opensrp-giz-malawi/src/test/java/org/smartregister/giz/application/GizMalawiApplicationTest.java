package org.smartregister.giz.application;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.giz.BaseUnitTest;
import org.smartregister.giz.TestGizMalawiApplication;

/**
 * Created by ndegwamartin on 2019-12-13.
 */
public class GizMalawiApplicationTest extends BaseUnitTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateCommonFtsObjectFunctionsCorrectly() {

        GizMalawiApplication gizMalawiApplication = new TestGizMalawiApplication();
        Assert.assertNotNull(gizMalawiApplication);

        CommonFtsObject commonFtsObject = gizMalawiApplication.createCommonFtsObject(RuntimeEnvironment.application);
        Assert.assertNotNull(commonFtsObject);

        String[] ftsObjectTables = commonFtsObject.getTables();
        Assert.assertNotNull(ftsObjectTables);
        Assert.assertEquals(3, ftsObjectTables.length);

        String scheduleName = commonFtsObject.getAlertScheduleName("bcg");
        Assert.assertNotNull(scheduleName);

        scheduleName = commonFtsObject.getAlertScheduleName("penta1");
        Assert.assertNotNull(scheduleName);

        scheduleName = commonFtsObject.getAlertScheduleName("mr1");
        Assert.assertNotNull(scheduleName);

        scheduleName = commonFtsObject.getAlertScheduleName("SomeNonExistentVaccine");
        Assert.assertNull(scheduleName);
    }
}
