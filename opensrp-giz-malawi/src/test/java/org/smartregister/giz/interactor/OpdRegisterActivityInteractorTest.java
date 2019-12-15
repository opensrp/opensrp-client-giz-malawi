package org.smartregister.giz.interactor;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OpdLibrary.class, DrishtiApplication.class})
public class OpdRegisterActivityInteractorTest {

    private OpdRegisterActivityInteractor interactor;

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private Context context;

    @Mock
    private ECSyncHelper ecSyncHelper;

    @Captor
    private ArgumentCaptor<String> uniqueRepoOpenId;

    @Captor
    private ArgumentCaptor<String> uniqueRepoCloseId;

    @Captor
    private ArgumentCaptor<List<EventClient>> clientProcessorArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> lastUpdatedAtArgumentCaptor;

    @Captor
    private ArgumentCaptor ecSyncHelperArgumentCaptor;

    @Mock
    private OpdLibrary opdLibrary;

    @Mock
    private DrishtiApplication drishtiApplication;

    @Mock
    private ClientProcessorForJava clientProcessorForJava;

    @Before
    public void setUp() throws Exception {
        interactor = new OpdRegisterActivityInteractor();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateOpenSRPIdInEditMode() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getUniqueIdRepository()).thenReturn(uniqueIdRepository);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OpdJsonFormUtils.CURRENT_ZEIR_ID, "333");
        RegisterParams registerParams = new RegisterParams();
        registerParams.setEditMode(true);
        Client client = new Client("234");
        client.addIdentifier(OpdJsonFormUtils.ZEIR_ID, "342");
        Whitebox.invokeMethod(interactor, "updateOpenSRPId", jsonObject.toString(), registerParams, client);
        Mockito.verify(uniqueIdRepository, Mockito.times(1)).open(uniqueRepoOpenId.capture());
        Assert.assertEquals("333", uniqueRepoOpenId.getValue());
    }

    @Test
    public void testUpdateOpenSRPIdNotInEditMode() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getUniqueIdRepository()).thenReturn(uniqueIdRepository);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OpdJsonFormUtils.CURRENT_ZEIR_ID, "333");
        RegisterParams registerParams = new RegisterParams();
        registerParams.setEditMode(false);
        Client client = new Client("234");
        client.addIdentifier(OpdJsonFormUtils.ZEIR_ID, "342");
        Whitebox.invokeMethod(interactor, "updateOpenSRPId", jsonObject.toString(), registerParams, client);
        Mockito.verify(uniqueIdRepository, Mockito.times(1)).close(uniqueRepoCloseId.capture());
        Assert.assertEquals("342", uniqueRepoCloseId.getValue());
    }

    @Test
    public void saveRegistration() throws Exception {
        Long time = (new Date().getTime());
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getEcSyncHelper()).thenReturn(ecSyncHelper);
        PowerMockito.when(opdLibrary.getUniqueIdRepository()).thenReturn(uniqueIdRepository);
        PowerMockito.when(opdLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchLastUpdatedAtDate(0)).thenReturn(time);
        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);
        PowerMockito.when(drishtiApplication.getClientProcessor()).thenReturn(clientProcessorForJava);

        Client client = new Client("234");
        client.setBirthdate(new Date());
        client.setGender("M");
        client.setFirstName("John");
        client.setLastName("Doe");
        client.addIdentifier(OpdJsonFormUtils.ZEIR_ID, "342");

        org.smartregister.domain.db.Client clientDomain = new org.smartregister.domain.db.Client("234");
        clientDomain.setBirthdate(new DateTime());
        clientDomain.setGender("M");
        clientDomain.setFirstName("John");
        clientDomain.setLastName("Doe");
        clientDomain.addIdentifier(OpdJsonFormUtils.ZEIR_ID, "342");

        Event event = new Event();
        event.setBaseEntityId("234");
        event.setChildLocationId("childLoc");
        event.setEventType("Opd Registration");
        event.setEventId("32432432");
        event.setFormSubmissionId("46rt34543rew");
        event.setLocationId("location");

        org.smartregister.domain.db.Event eventDomain = new org.smartregister.domain.db.Event();
        eventDomain.setBaseEntityId("234");
        eventDomain.setChildLocationId("childLoc");
        eventDomain.setEventType("Opd Registration");
        eventDomain.setEventId("32432432");
        eventDomain.setFormSubmissionId("46rt34543rew");
        eventDomain.setLocationId("location");


        OpdEventClient opdEventClient = new OpdEventClient(client, event);

        EventClient eventClient = new EventClient(eventDomain, clientDomain);
        PowerMockito.when(ecSyncHelper.getEvents(Arrays.asList("46rt34543rew")))
                .thenReturn(Arrays.asList(eventClient));

        JSONObject jsonObject = new JSONObject();
        RegisterParams registerParams = new RegisterParams();
        registerParams.setEditMode(false);
        Whitebox.invokeMethod(interactor, "saveRegistration", Collections.singletonList(opdEventClient), jsonObject.toString(), registerParams);

        Mockito.verify(ecSyncHelper).addClient((String) ecSyncHelperArgumentCaptor.capture(), (JSONObject) ecSyncHelperArgumentCaptor.capture());//when(ecSyncHelper.ad)

        Mockito.verify(ecSyncHelper).addEvent((String) ecSyncHelperArgumentCaptor.capture(), (JSONObject) ecSyncHelperArgumentCaptor.capture(), String.valueOf(ecSyncHelperArgumentCaptor.capture()));//when(ecSyncHelper.ad)

        Mockito.verify(clientProcessorForJava).processClient(clientProcessorArgumentCaptor.capture());

        Mockito.verify(allSharedPreferences).saveLastUpdatedAtDate(lastUpdatedAtArgumentCaptor.capture());

        Assert.assertEquals("234", ecSyncHelperArgumentCaptor.getAllValues().get(0));

        Assert.assertEquals("234", ecSyncHelperArgumentCaptor.getAllValues().get(2));

        Assert.assertEquals(new JSONObject(OpdJsonFormUtils.gson.toJson(event)).toString(), ecSyncHelperArgumentCaptor.getAllValues().get(3).toString());

        Assert.assertEquals(new JSONObject(OpdJsonFormUtils.gson.toJson(client)).toString(), ecSyncHelperArgumentCaptor.getAllValues().get(1).toString());

        Assert.assertEquals(time, lastUpdatedAtArgumentCaptor.getValue());

        Assert.assertEquals(Arrays.asList(eventClient), clientProcessorArgumentCaptor.getValue());

    }
}