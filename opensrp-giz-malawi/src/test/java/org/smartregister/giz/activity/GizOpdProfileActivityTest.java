package org.smartregister.giz.activity;

import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.R;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.opd.utils.OpdDbConstants;

import java.util.HashMap;

public class GizOpdProfileActivityTest extends BaseRobolectricTest {

    private GizOpdProfileActivity gizOpdProfileActivity;

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

    @After
    public void tearDown() {
        gizOpdProfileActivity.finish();
    }
}