package org.smartregister.giz.processor;

import android.content.ContentValues;
import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.VaccineSchedule;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GizMalawiApplication.class, Utils.class, VaccineSchedule.class, ServiceSchedule.class})
public class GizMalawiProcessorForJavaTest {

    @Mock
    private GizMalawiApplication gizMalawiApplication;
    @Mock
    private WeightRepository weightRepository;

    @Mock
    private ContentValues contentValues;

    @Captor
    private ArgumentCaptor<Weight> processWeightArgumentCaptor;

    private GizMalawiProcessorForJava processorForJava;

    @Before
    public void setUp() {
        processorForJava = Mockito.spy(GizMalawiProcessorForJava.getInstance(Mockito.mock(Context.class)));
    }

    @Test
    public void processWeightWithEventClientNull() throws Exception {
        Object[] params = {null, null, true};
        Boolean result = Whitebox.invokeMethod(processorForJava, "processWeight", params);
        Assert.assertFalse(result);
    }

    @Test
    public void processWeightWithTableNull() throws Exception {
        Object[] params = {new EventClient(new Event(), new Client("23")), null, true};
        Boolean result = Whitebox.invokeMethod(processorForJava, "processWeight", params);
        Assert.assertFalse(result);
    }

    @Test
    public void processWeightWithValidEventClientAndTable() throws Exception {
        PowerMockito.mockStatic(GizMalawiApplication.class);
        PowerMockito.when(GizMalawiApplication.getInstance()).thenReturn(gizMalawiApplication);
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

        Boolean result = Whitebox.invokeMethod(processorForJava, "processWeight", eventClient, table, false);
        Mockito.verify(weightRepository).add(processWeightArgumentCaptor.capture());
        Weight resultWeightObj = processWeightArgumentCaptor.getValue();
        Assert.assertEquals(java.util.Optional.of(0).get(), resultWeightObj.getOutOfCatchment());
        Assert.assertEquals(Float.valueOf("20.0"), resultWeightObj.getKg());
        Assert.assertEquals(Double.valueOf("45.0"), resultWeightObj.getZScore());
        Assert.assertEquals("lombwe", resultWeightObj.getLocationId());
        Assert.assertEquals(WeightRepository.TYPE_Synced, resultWeightObj.getSyncStatus());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultWeightObj.getDate());
        Assert.assertEquals(Utils.getDate("2019-09-27 09:45:44"), resultWeightObj.getCreatedAt());
        Assert.assertTrue(result);
    }
}
