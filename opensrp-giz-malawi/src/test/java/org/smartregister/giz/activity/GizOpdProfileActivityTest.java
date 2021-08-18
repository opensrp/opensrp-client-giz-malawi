package org.smartregister.giz.activity;

import static org.junit.Assert.assertEquals;

import androidx.appcompat.view.menu.MenuBuilder;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.R;
import org.smartregister.giz.shadow.ShadowBaseJob;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncSettingsServiceJob;
import org.smartregister.opd.utils.FormProcessor;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class GizOpdProfileActivityTest extends BaseRobolectricTest {

    private GizOpdProfileActivity gizOpdProfileActivity;
    @Captor
    private ArgumentCaptor<Intent> intentArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Before
    public void setUp() {
        gizOpdProfileActivity = Mockito.spy(Robolectric
                .buildActivity(GizOpdProfileActivity.class)
                .create()
                .get());
    }

    @Test
    public void testOnCreateOptionsMenuShouldEnableCloseIfRegisterTypeIsOpd() {
        Menu menu = Mockito.spy(new MenuBuilder(gizOpdProfileActivity.getApplicationContext()));

        Mockito.doReturn(GizConstants.RegisterType.OPD)
                .when(gizOpdProfileActivity).getRegisterType();

        Mockito.doNothing().when(gizOpdProfileActivity).buildAllClientsMenu(Mockito.eq(menu));

        gizOpdProfileActivity.onCreateOptionsMenu(menu);

        Mockito.verify(gizOpdProfileActivity, Mockito.times(1))
                .buildAllClientsMenu(Mockito.eq(menu));
    }

    @Test
    public void testOnCreateOptionsMenuShouldDisableCloseIfRegisterTypeNotOpd() {
        Menu menu = new MenuBuilder(gizOpdProfileActivity.getApplicationContext());
        Mockito.doReturn(GizConstants.RegisterType.ANC)
                .when(gizOpdProfileActivity).getRegisterType();
        gizOpdProfileActivity.onCreateOptionsMenu(menu);

        Mockito.verify(gizOpdProfileActivity, Mockito.times(0))
                .buildAllClientsMenu(Mockito.eq(menu));
    }

    @Test
    public void testBuildAllClientsMenuShouldNotShowAncMaternityAndPncTransfer() {
        String baseEntityId = "324wewe23-wewe";
        HashMap<String, String> detailsHashMap = new HashMap<>();
        detailsHashMap.put(OpdDbConstants.Column.Client.DOB, "2000-09-09");
        detailsHashMap.put(OpdDbConstants.Column.Client.GENDER, "Male");

        CommonPersonObjectClient personObjectClient = new CommonPersonObjectClient(
                baseEntityId, detailsHashMap, "");

        Mockito.doReturn(personObjectClient)
                .when(gizOpdProfileActivity)
                .getClient();
        Menu menu = Mockito.spy(new MenuBuilder(gizOpdProfileActivity.getApplicationContext()));

        MenuItem ancMenuItem = Mockito.mock(MenuItem.class);

        Mockito.doReturn(ancMenuItem)
                .when(menu).findItem(R.id.opd_menu_item_enrol_anc);

        gizOpdProfileActivity.buildAllClientsMenu(menu);

        Mockito.verify(ancMenuItem, Mockito.never())
                .setVisible(Mockito.anyBoolean());
    }

    @Test
    public void testBuildAllClientsMenuShouldShowAncMaternityAndPncTransfer() {
        Menu menu = Mockito.spy(new MenuBuilder(gizOpdProfileActivity.getApplicationContext()));

        String baseEntityId = "324wewe23-wewe";
        HashMap<String, String> detailsHashMap = new HashMap<>();
        detailsHashMap.put(OpdDbConstants.Column.Client.DOB, "2000-09-09");
        detailsHashMap.put(OpdDbConstants.Column.Client.GENDER, "Female");

        CommonPersonObjectClient personObjectClient = new CommonPersonObjectClient(
                baseEntityId, detailsHashMap, "");

        Mockito.doReturn(personObjectClient)
                .when(gizOpdProfileActivity)
                .getClient();

        MenuItem maternityMenuItem = Mockito.mock(MenuItem.class);

        MenuItem ancMenuItem = Mockito.mock(MenuItem.class);

        MenuItem pncMenuItem = Mockito.mock(MenuItem.class);

        Mockito.doReturn(maternityMenuItem)
                .when(menu).findItem(R.id.opd_menu_item_enrol_maternity);

        Mockito.doReturn(ancMenuItem)
                .when(menu).findItem(R.id.opd_menu_item_enrol_anc);

        Mockito.doReturn(pncMenuItem)
                .when(menu).findItem(R.id.opd_menu_item_enrol_pnc);

        gizOpdProfileActivity.buildAllClientsMenu(menu);

        Mockito.verify(ancMenuItem, Mockito.times(1))
                .setVisible(Mockito.eq(true));

        Mockito.verify(pncMenuItem, Mockito.times(1))
                .setVisible(Mockito.eq(true));

        Mockito.verify(maternityMenuItem, Mockito.times(1))
                .setVisible(Mockito.eq(true));
    }

    @Test
    public void initiateSyncShouldCallEachJobToScheduleImmediateExecution() {
        gizOpdProfileActivity.initiateSync();

        Assert.assertTrue(ShadowBaseJob.getShadowHelper().isCalled(ShadowBaseJob.scheduleJobImmediatelyMN));
        HashMap<Integer, ArrayList<Object>> methodCalls = ShadowBaseJob.getShadowHelper().getMethodCalls(ShadowBaseJob.scheduleJobImmediatelyMN);
        assertEquals(6, methodCalls.size());
        assertEquals(ImageUploadServiceJob.TAG, methodCalls.get(0).get(0));
        assertEquals(SyncServiceJob.TAG, methodCalls.get(1).get(0));
        assertEquals(SyncSettingsServiceJob.TAG, methodCalls.get(0).get(0));
        assertEquals(ZScoreRefreshIntentServiceJob.TAG, methodCalls.get(3).get(0));
        assertEquals(WeightIntentServiceJob.TAG, methodCalls.get(2).get(0));
        assertEquals(HeightIntentServiceJob.TAG, methodCalls.get(2).get(0));
        assertEquals(VaccineServiceJob.TAG, methodCalls.get(4).get(0));
    }

    @Test
    public void testStartFormInvokesOpdForm() throws Exception {
        gizOpdProfileActivity = Mockito.spy(gizOpdProfileActivity);

        Mockito.doNothing().when(gizOpdProfileActivity).startActivityForResult(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());
        gizOpdProfileActivity.startForm(new JSONObject(), new Form(), null);
        Mockito.verify(gizOpdProfileActivity).startActivityForResult(intentArgumentCaptor.capture(), integerArgumentCaptor.capture());

        Intent capturedIntent = intentArgumentCaptor.getValue();
        Assert.assertNotNull(capturedIntent);
        Assert.assertEquals("org.smartregister.giz.activity.OpdFormActivity", capturedIntent.getComponent().getClassName());
        Integer capturedInteger = integerArgumentCaptor.getValue();

        Assert.assertNotNull(capturedInteger);
        Assert.assertEquals(GizOpdProfileActivity.REQUEST_CODE_GET_JSON, capturedInteger.intValue());

    }

    @After
    public void tearDown() {
        gizOpdProfileActivity.finish();
    }
}