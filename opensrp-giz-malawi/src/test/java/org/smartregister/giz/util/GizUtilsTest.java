package org.smartregister.giz.util;

import android.content.ContentValues;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.activity.DrishtiApplication;


public class GizUtilsTest extends BaseRobolectricTest {

    @Mock
    private GizMalawiApplication gizMalawiApplication;

    @Mock
    private Context context;

    @Captor
    private ArgumentCaptor argumentCaptorSaveCurrentLocality;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private AllCommonsRepository allCommonsRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private LocationHelper locationHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateClientDeathShouldPassCorrectArgs() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", gizMalawiApplication);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        PowerMockito.when(gizMalawiApplication.context()).thenReturn(context);
        PowerMockito.doReturn(allCommonsRepository).when(context).allCommonsRepositoryobjects(Mockito.eq(GizConstants.TABLE_NAME.ALL_CLIENTS));
        Client client = new Client("123");
        client.setDeathdate(new DateTime());
        EventClient eventClient = new EventClient(new Event(), client);

        GizUtils.updateClientDeath(eventClient);

        Mockito.verify(allCommonsRepository).update(Mockito.eq(GizConstants.TABLE_NAME.ALL_CLIENTS), Mockito.any(ContentValues.class), Mockito.eq(client.getBaseEntityId()));
        Mockito.verify(allCommonsRepository).updateSearch(Mockito.eq(client.getBaseEntityId()));
    }

    @Test
    public void testGetCurrentLocalityShouldReturnCorrectValueIfPresent() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", gizMalawiApplication);
        PowerMockito.when(gizMalawiApplication.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn("child location 1");
        Assert.assertEquals("child location 1", GizUtils.getCurrentLocality());
    }

    @Test
    public void testGetCurrentLocalityShouldReturnCorrectValueIfAbsent() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", gizMalawiApplication);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        PowerMockito.when(locationHelper.getDefaultLocation()).thenReturn("Default Location");
        PowerMockito.when(gizMalawiApplication.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn(null);
        Assert.assertEquals("Default Location", GizUtils.getCurrentLocality());
        Mockito.verify(allSharedPreferences).saveCurrentLocality(String.valueOf(argumentCaptorSaveCurrentLocality.capture()));
        Assert.assertEquals("Default Location", argumentCaptorSaveCurrentLocality.getValue());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
    }
}