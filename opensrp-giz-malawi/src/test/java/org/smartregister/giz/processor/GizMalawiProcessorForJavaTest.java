package org.smartregister.giz.processor;

import android.content.ContentValues;
import android.content.Context;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.MonthlyTally;
import org.smartregister.giz.repository.ClientRegisterTypeRepository;
import org.smartregister.giz.repository.MonthlyTalliesRepository;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.pojo.PncBaseDetails;
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.pojo.PncStillBorn;
import org.smartregister.pnc.repository.PncChildRepository;
import org.smartregister.pnc.repository.PncMedicInfoRepository;
import org.smartregister.pnc.repository.PncStillBornRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GizMalawiProcessorForJavaTest extends BaseRobolectricTest {

    @Mock
    private GizMalawiApplication gizMalawiApplication;

    @Mock
    private HeightRepository heightRepository;

    @Mock
    private WeightRepository weightRepository;

    @Mock
    private ContentValues contentValues;

    @Captor
    private ArgumentCaptor<Weight> processWeightArgumentCaptor;

    @Captor
    private ArgumentCaptor<Height> processHeightArgumentCaptor;

    @Captor
    private ArgumentCaptor<ServiceRecord> recordServiceArgumentCaptor;

    @Mock
    private RecurringServiceTypeRepository recurringServiceTypeRepository;

    @Mock
    private RecurringServiceRecordRepository recurringServiceRecordRepository;

    private GizMalawiProcessorForJava processorForJava;

    @Before
    public void setUp() {
        processorForJava = Mockito.spy(GizMalawiProcessorForJava.getInstance(Mockito.mock(Context.class)));
    }

    @Test
    public void processHeightWithValidEventClientAndTableShouldReturnTrue() throws Exception {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", gizMalawiApplication);
        PowerMockito.when(gizMalawiApplication.heightRepository()).thenReturn(heightRepository);
        Mockito.when(processorForJava.processCaseModel(ArgumentMatchers.any(EventClient.class), ArgumentMatchers.any(Table.class))).thenReturn(contentValues);
        Mockito.when(contentValues.size()).thenReturn(7);
        Mockito.when(contentValues.getAsString(HeightRepository.DATE)).thenReturn("2019-09-27 09:45:44");
        Mockito.when(contentValues.getAsString(HeightRepository.BASE_ENTITY_ID)).thenReturn("234");
        Mockito.when(contentValues.containsKey(HeightRepository.CM)).thenReturn(true);
        Mockito.when(contentValues.getAsString(HeightRepository.CM)).thenReturn("230");
        Mockito.when(contentValues.getAsString(HeightRepository.ANMID)).thenReturn("provider");
        Mockito.when(contentValues.getAsString(HeightRepository.LOCATIONID)).thenReturn("lombwe");
        Mockito.when(contentValues.containsKey(HeightRepository.Z_SCORE)).thenReturn(true);
        Mockito.when(contentValues.getAsString(HeightRepository.Z_SCORE)).thenReturn("45.0");
        Mockito.when(contentValues.getAsString(HeightRepository.CREATED_AT)).thenReturn("2019-09-27 09:45:44");
        Table table = new Table();
        table.name = "heights";
        Event event = new Event();
        event.setEventId("231");
        event.setFormSubmissionId("343");
        Client client = new Client("234");
        EventClient eventClient = new EventClient(event, client);

        Whitebox.invokeMethod(processorForJava, "processHeight", eventClient, table, false);
        Mockito.verify(heightRepository).add(processHeightArgumentCaptor.capture());
        Height resultHeightobj = processHeightArgumentCaptor.getValue();
        Assert.assertEquals(java.util.Optional.of(0).get(), resultHeightobj.getOutOfCatchment());
        Assert.assertEquals(Float.valueOf("230.0"), resultHeightobj.getCm());
        Assert.assertEquals(Double.valueOf("45.0"), resultHeightobj.getZScore());
        Assert.assertEquals("lombwe", resultHeightobj.getLocationId());
        Assert.assertEquals(HeightRepository.TYPE_Synced, resultHeightobj.getSyncStatus());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultHeightobj.getDate());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultHeightobj.getCreatedAt());
    }

    @Test
    public void processServiceWithNameNotHavingITNShouldReturnTrue() throws Exception {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", gizMalawiApplication);
        PowerMockito.when(gizMalawiApplication.recurringServiceTypeRepository()).thenReturn(recurringServiceTypeRepository);
        PowerMockito.when(gizMalawiApplication.recurringServiceRecordRepository()).thenReturn(recurringServiceRecordRepository);
        ServiceType serviceTypeDeworming2 = new ServiceType();
        serviceTypeDeworming2.setId(1L);
        serviceTypeDeworming2.setName("Deworming_2");
        serviceTypeDeworming2.setPrerequisite("prerequisite|Deworming 1");
        serviceTypeDeworming2.setPreOffset("+6m");
        serviceTypeDeworming2.setUnits("200 mg");
        serviceTypeDeworming2.setDateEntity("encounter");

        ServiceType serviceTypeDeworming3 = new ServiceType();
        serviceTypeDeworming3.setName("Deworming_3");
        serviceTypeDeworming3.setPrerequisite("prerequisite|Deworming 2");
        serviceTypeDeworming3.setPreOffset("+6m");
        serviceTypeDeworming3.setUnits("400 mg");
        serviceTypeDeworming3.setDateEntity("encounter");

        Mockito.when(recurringServiceTypeRepository.searchByName("Deworming 1")).thenReturn(Arrays.asList(serviceTypeDeworming2, serviceTypeDeworming3));
        Mockito.when(processorForJava.processCaseModel(ArgumentMatchers.any(EventClient.class), ArgumentMatchers.any(Table.class))).thenReturn(contentValues);
        Mockito.when(contentValues.size()).thenReturn(7);
        Mockito.when(contentValues.getAsString(HeightRepository.ANMID)).thenReturn("provider");
        Mockito.when(contentValues.getAsString(HeightRepository.LOCATIONID)).thenReturn("lombwe");
        Mockito.when(contentValues.getAsString(RecurringServiceTypeRepository.NAME)).thenReturn("Deworming_1");
        Mockito.when(contentValues.getAsString(RecurringServiceRecordRepository.DATE)).thenReturn("2019-09-27 09:45:44");

        Mockito.when(contentValues.getAsString(HeightRepository.CREATED_AT)).thenReturn("2019-09-27 09:45:44");

        Event event = new Event();
        event.setEventId("231");
        event.setFormSubmissionId("1234");

        Table table = new Table();
        table.name = "recurring_service_records";
        Object[] params = {new EventClient(event, new Client("23")), table};
        Whitebox.invokeMethod(processorForJava, "processService", params);

        Mockito.verify(recurringServiceRecordRepository).add(recordServiceArgumentCaptor.capture());
        ServiceRecord resultServiceRecord = recordServiceArgumentCaptor.getValue();
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultServiceRecord.getDate());
        Assert.assertEquals(java.util.Optional.of(1L).get(), resultServiceRecord.getRecurringServiceId());
        Assert.assertEquals("Deworming 1", resultServiceRecord.getName());
        Assert.assertEquals(RecurringServiceRecordRepository.TYPE_Synced, resultServiceRecord.getSyncStatus());

        Assert.assertEquals("231", resultServiceRecord.getEventId());
        Assert.assertEquals("1234", resultServiceRecord.getFormSubmissionId());
        Assert.assertEquals("1234", resultServiceRecord.getFormSubmissionId());
    }

    @Test
    public void processWeightWithValidEventClientAndTableShouldReturnTrue() throws Exception {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", gizMalawiApplication);
        PowerMockito.when(gizMalawiApplication.weightRepository()).thenReturn(weightRepository);
        Mockito.when(processorForJava.processCaseModel(ArgumentMatchers.any(EventClient.class), ArgumentMatchers.any(Table.class))).thenReturn(contentValues);
        Mockito.when(contentValues.size()).thenReturn(7);
        Mockito.when(contentValues.getAsString(WeightRepository.DATE)).thenReturn("2019-09-27 09:45:44");
        Mockito.when(contentValues.getAsString(WeightRepository.BASE_ENTITY_ID)).thenReturn("234");
        Mockito.when(contentValues.containsKey(WeightRepository.KG)).thenReturn(true);
        Mockito.when(contentValues.getAsString(WeightRepository.KG)).thenReturn("20");
        Mockito.when(contentValues.getAsString(WeightRepository.ANMID)).thenReturn("provider");
        Mockito.when(contentValues.getAsString(WeightRepository.LOCATIONID)).thenReturn("lombwe");
        Mockito.when(contentValues.containsKey(WeightRepository.Z_SCORE)).thenReturn(true);
        Mockito.when(contentValues.getAsString(WeightRepository.Z_SCORE)).thenReturn("45.0");
        Mockito.when(contentValues.getAsString(WeightRepository.CREATED_AT)).thenReturn("2019-09-27 09:45:44");
        Table table = new Table();
        table.name = "weights";
        Event event = new Event();
        event.setEventId("231");
        event.setFormSubmissionId("343");
        Client client = new Client("234");
        EventClient eventClient = new EventClient(event, client);

        Whitebox.invokeMethod(processorForJava, "processWeight", eventClient, table, false);
        Mockito.verify(weightRepository).add(processWeightArgumentCaptor.capture());
        Weight resultWeightObj = processWeightArgumentCaptor.getValue();
        Assert.assertEquals(java.util.Optional.of(0).get(), resultWeightObj.getOutOfCatchment());
        Assert.assertEquals(Float.valueOf("20.0"), resultWeightObj.getKg());
        Assert.assertEquals(Double.valueOf("45.0"), resultWeightObj.getZScore());
        Assert.assertEquals("lombwe", resultWeightObj.getLocationId());
        Assert.assertEquals(WeightRepository.TYPE_Synced, resultWeightObj.getSyncStatus());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultWeightObj.getDate());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultWeightObj.getCreatedAt());
    }

    @Test
    public void testOpdTransferHandlerShouldUpdateRegisterType() throws Exception {

        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", gizMalawiApplication);

        String baseEntityId = "324-sdr32423-234";
        Event event = new Event();
        event.setBaseEntityId(baseEntityId);
        Client client = new Client(baseEntityId);
        ClientClassification clientClassification = new ClientClassification();

        ClientRegisterTypeRepository clientRegisterTypeRepositoryMock = Mockito.mock(ClientRegisterTypeRepository.class);
        Mockito.doReturn(clientRegisterTypeRepositoryMock).when(gizMalawiApplication).registerTypeRepository();

        event.setEventType(GizConstants.EventType.OPD_ANC_TRANSFER);
        processorForJava.opdTransferHandler(new EventClient(event, client), GizConstants.EventType.OPD_ANC_TRANSFER, clientClassification);
        Mockito.verify(clientRegisterTypeRepositoryMock).add(Mockito.eq(GizConstants.RegisterType.ANC), Mockito.eq(baseEntityId));

        event.setEventType(GizConstants.EventType.OPD_CHILD_TRANSFER);
        processorForJava.opdTransferHandler(new EventClient(event, client), GizConstants.EventType.OPD_CHILD_TRANSFER, clientClassification);
        Mockito.verify(clientRegisterTypeRepositoryMock).add(Mockito.eq(GizConstants.RegisterType.CHILD), Mockito.eq(baseEntityId));

        event.setEventType(GizConstants.EventType.OPD_MATERNITY_TRANSFER);
        processorForJava.opdTransferHandler(new EventClient(event, client), GizConstants.EventType.OPD_MATERNITY_TRANSFER, clientClassification);
        Mockito.verify(clientRegisterTypeRepositoryMock).add(Mockito.eq(GizConstants.RegisterType.MATERNITY), Mockito.eq(baseEntityId));

        event.setEventType(GizConstants.EventType.OPD_PNC_TRANSFER);
        processorForJava.opdTransferHandler(new EventClient(event, client), GizConstants.EventType.OPD_PNC_TRANSFER, clientClassification);
        Mockito.verify(clientRegisterTypeRepositoryMock).add(Mockito.eq(GizConstants.RegisterType.PNC), Mockito.eq(baseEntityId));

        Mockito.verify(clientRegisterTypeRepositoryMock, Mockito.times(4)).removeAll(Mockito.eq(baseEntityId));

        Mockito.verify(processorForJava, Mockito.times(4)).processEvent(Mockito.eq(event),
                Mockito.eq(client), Mockito.eq(clientClassification));
    }

    @Test
    public void testProcessReportShouldSaveMonthlyTallies() throws Exception {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", gizMalawiApplication);
        Event event = new Event();
        event.addDetails("reportJson", "{\"dateCreated\":\"2020-08-04T15:16:55.539+03:00\",\"duration\":0,\"formSubmissionId\":\"9028b90e-be65-4ec0-91d0-86e9ba9d6c01\",\"hia2Indicators\":[{\"categoryOptionCombo\":\"pE37AYgl7bx\",\"dhisId\":\"BOAUQVmPxf3\",\"indicatorCode\":\"ME_Anti_Malaria_Treatment_Confirmed_Under_5\",\"value\":\"0\"},{\"dhisId\":\"unknown\",\"indicatorCode\":\"ME_Malaria_Cases_Presumed_Over_5\",\"value\":\"0\"}],\"locationId\":\"76bc21e4-6670-480a-8628-7fd44b1d8d14\",\"providerId\":\"chigodi\",\"reportDate\":\"2020-02-27T00:00:00.000+03:00\",\"reportType\":\"Malaria Health Facility Report\"}");
        MonthlyTalliesRepository monthlyTalliesRepositoryMock = Mockito.mock(MonthlyTalliesRepository.class);
        Mockito.doReturn(monthlyTalliesRepositoryMock).when(gizMalawiApplication).monthlyTalliesRepository();
        Whitebox.invokeMethod(processorForJava,
                "processReport", event);
        Mockito.verify(monthlyTalliesRepositoryMock, Mockito.times(2)).save(Mockito.any(MonthlyTally.class));
    }


    @Test
    public void testProcessMaternityPncTransferShouldInvokeRequiredMethods() throws Exception {
        String baseEntityId = "324-sdr32423-234";

        String babiesBorn = "{\"3b562659b3f64f998dccfae199f7ea0d\":" +
                "{\"baby_care_mgt\":\"[\\\"kmc\\\",\\\"antibiotics\\\"]\",\"apgar\":\"10\",\"base_entity_id\":\"2323-2323-sds\",\"child_hiv_status\":\"Exposed\",\"nvp_administration\":\"Yes\",\"baby_first_cry\":\"Yes\",\"baby_complications\":\"[\\\"premature\\\",\\\"asphyxia\\\"]\",\"baby_first_name\":\"Nameless\",\"baby_last_name\":\"Master\",\"baby_dob\":\"03-06-2020\",\"discharged_alive\":\"Yes\",\"birth_weight_entered\":\"2300\",\"birth_height_entered\":\"54\",\"baby_gender\":\"Male\",\"bf_first_hour\":\"Yes\"}}";

        String stillBorn = "{\"cabdeffcdca64a63800c8718f94d72ee\":{\"stillbirth_condition\":\"Fresh\"},\"70bb07814ded40a59f1346928909d134\":{\"stillbirth_condition\":\"Macerated\"}}";

        Obs obsStillBornMap = new Obs();
        obsStillBornMap.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.BABIES_STILL_BORN_MAP);
        obsStillBornMap.setHumanReadableValues(new ArrayList<>());
        obsStillBornMap.setValue(stillBorn);

        Obs obsBabiesBornMap = new Obs();
        obsBabiesBornMap.setFormSubmissionField(MaternityConstants.JSON_FORM_KEY.BABIES_BORN_MAP);
        obsBabiesBornMap.setHumanReadableValues(new ArrayList<>());
        obsBabiesBornMap.setValue(babiesBorn);

        Event event = new Event();
        event.setEventDate(new DateTime());
        event.addObs(obsBabiesBornMap);
        event.addObs(obsStillBornMap);

        event.setBaseEntityId(baseEntityId);
        Client client = new Client(baseEntityId);
        ClientClassification clientClassification = new ClientClassification();

        PncLibrary pncLibrary = Mockito.mock(PncLibrary.class);
        PncMedicInfoRepository pncMedicInfoRepository = Mockito.mock(PncMedicInfoRepository.class);
        PncChildRepository pncChildRepository = Mockito.mock(PncChildRepository.class);
        PncStillBornRepository pncStillBornRepository = Mockito.mock(PncStillBornRepository.class);

        ClientRegisterTypeRepository registerTypeRepository = Mockito.mock(ClientRegisterTypeRepository.class);

        Mockito.doReturn(registerTypeRepository)
                .when(gizMalawiApplication).registerTypeRepository();

        Mockito.doReturn(pncMedicInfoRepository)
                .when(pncLibrary).getPncMedicInfoRepository();

        Mockito.doReturn(pncStillBornRepository)
                .when(pncLibrary).getPncStillBornRepository();

        Mockito.doReturn(pncChildRepository)
                .when(pncLibrary).getPncChildRepository();

        ReflectionHelpers.setStaticField(PncLibrary.class,
                "instance", pncLibrary);

        ReflectionHelpers.setStaticField(DrishtiApplication.class,
                "mInstance", gizMalawiApplication);

        Mockito.doReturn(true).when(processorForJava)
                .processEvent(Mockito.eq(event), Mockito.eq(client), Mockito.eq(clientClassification));


        Whitebox.invokeMethod(processorForJava,
                "processMaternityPncTransfer", new EventClient(event, client),
                clientClassification);

        Mockito.verify(processorForJava, Mockito.times(1))
                .processEvent(Mockito.eq(event), Mockito.eq(client), Mockito.eq(clientClassification));

        Mockito.verify(registerTypeRepository)
                .add(Mockito.eq(GizConstants.RegisterType.PNC), Mockito.eq(baseEntityId));

        Mockito.verify(registerTypeRepository, Mockito.times(1))
                .removeAll(Mockito.eq(baseEntityId));

        Mockito.verify(pncMedicInfoRepository, Mockito.times(1))
                .saveOrUpdate(Mockito.any(PncBaseDetails.class));

        Mockito.verify(pncStillBornRepository, Mockito.times(2))
                .saveOrUpdate(Mockito.any(PncStillBorn.class));

        Mockito.verify(pncChildRepository, Mockito.times(1))
                .saveOrUpdate(Mockito.any(PncChild.class));
    }


    @Test
    public void testProcessVaccineShouldInvokeCorrectMethods() throws Exception {
        ReflectionHelpers.setStaticField(DrishtiApplication.class,
                "mInstance", gizMalawiApplication);

        String baseEntityId = "324-sdr32423-234";
        Event event = new Event();
        event.setEventType(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT);
        event.setBaseEntityId(baseEntityId);
        Client client = new Client(baseEntityId);

        EventClient eventClient = new EventClient(event, client);

        ContentValues contentValues = new ContentValues();
        contentValues.put(VaccineRepository.BASE_ENTITY_ID, baseEntityId);
        contentValues.put(VaccineRepository.NAME, "opv_0");
        contentValues.put(VaccineRepository.ANMID, "demo");
        contentValues.put(VaccineRepository.LOCATION_ID, "32432-23423demo");
        contentValues.put(VaccineRepository.DATE, "2020-01-15 14:54:38");
        contentValues.put(VaccineRepository.CREATED_AT, "2020-01-15 14:54:38");


        EventClientRepository eventClientRepository = Mockito.mock(EventClientRepository.class);
        VaccineRepository vaccineRepository = new VaccineRepository(
                Mockito.mock(CommonFtsObject.class),
                Mockito.mock(AlertService.class));

        VaccineRepository vaccineRepositorySpy = Mockito.spy(vaccineRepository);

        Mockito.doReturn(eventClientRepository)
                .when(gizMalawiApplication)
                .eventClientRepository();

        Mockito.doReturn(vaccineRepositorySpy)
                .when(gizMalawiApplication)
                .vaccineRepository();

        Mockito.doReturn(false).when(eventClientRepository)
                .checkIfExists(
                        Mockito.eq(EventClientRepository.Table.client),
                        Mockito.eq(event.getBaseEntityId()));

        Mockito.doReturn(contentValues)
                .when(processorForJava)
                .processCaseModel(Mockito.eq(eventClient), Mockito.any(Table.class));


        Whitebox.invokeMethod(processorForJava,
                "processVaccinationEvent",
                new Table(), eventClient);

        Mockito.verify(eventClientRepository)
                .checkIfExists(
                        Mockito.eq(EventClientRepository.Table.client),
                        Mockito.eq(event.getBaseEntityId()));

        Mockito.verify(vaccineRepositorySpy).add(Mockito.any(Vaccine.class));

        HashMap<String, DateTime> dateTimeHashMap = ReflectionHelpers.getField(processorForJava, "clientsForAlertUpdates");

        Assert.assertFalse(dateTimeHashMap.isEmpty());
        Assert.assertTrue(dateTimeHashMap.containsKey(baseEntityId));
    }


    @Test
    public void testUpdateFtsSearchShouldInvokeCorrectMethods() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class,
                "mInstance", gizMalawiApplication);


        String baseEntityId = "324-sdr32423-234";

        AllCommonsRepository allCommonsRepository = Mockito.mock(AllCommonsRepository.class);

        org.smartregister.Context opensrpContext = Mockito.mock(org.smartregister.Context.class);

        ClientRegisterTypeRepository registerTypeRepository = Mockito.mock(ClientRegisterTypeRepository.class);

        Mockito.doReturn(registerTypeRepository)
                .when(gizMalawiApplication).registerTypeRepository();

        Mockito.doReturn(true)
                .when(registerTypeRepository)
                .findByRegisterType(
                        Mockito.eq(baseEntityId),
                        Mockito.eq(GizConstants.RegisterType.CHILD));

        Mockito.doReturn(opensrpContext).when(gizMalawiApplication).context();

        Mockito.doReturn(allCommonsRepository)
                .when(opensrpContext).allCommonsRepositoryobjects(Mockito.eq(GizConstants.TABLE_NAME.ALL_CLIENTS));

        Mockito.doNothing().when(processorForJava)
                .updateOfflineAlerts(Mockito.eq(baseEntityId), Mockito.any(DateTime.class));


        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.KEY.DOB, "2018-01-15T14:00:00.000+02:00");

        processorForJava
                .updateFTSsearch(
                        GizConstants.TABLE_NAME.ALL_CLIENTS,
                        baseEntityId,
                        contentValues);

        Mockito.verify(allCommonsRepository, Mockito.times(1))
                .updateSearch(Mockito.eq(baseEntityId));

        Mockito.verify(processorForJava, Mockito.times(1))
                .updateOfflineAlerts(Mockito.eq(baseEntityId), Mockito.any(DateTime.class));
    }


    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", null);
    }
}