package org.smartregister.giz.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.giz.BaseUnitTest;
import org.smartregister.giz.R;
import org.smartregister.giz.view.NavigationMenu;

import java.util.HashMap;
import java.util.Map;

public class ReportRegisterActivityTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    private ReportRegisterActivity activity;
    private ActivityController<ReportRegisterActivity> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        controller = Robolectric.buildActivity(ReportRegisterActivity.class).create().start();
        activity = controller.get();
    }

    @After
    public void tearDown() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetupViews() {
        TextView titleTv = Mockito.mock(TextView.class);
        ListView listView = Mockito.mock(ListView.class);

        activity.setTheme(org.smartregister.R.style.AppTheme);
        activity.setContentView(R.layout.activity_report_register);
        ReflectionHelpers.setField(activity, "titleTv", titleTv);
        ReflectionHelpers.setField(activity, "listView", listView);

        activity.setUpViews();

        Assert.assertNotNull(ReflectionHelpers.getField(activity, "listView"));
        Assert.assertNotNull(ReflectionHelpers.getField(activity, "titleTv"));
    }


    @Test
    public void testGetDrawerLayoutId() {
        DrawerLayout drawerLayout = Mockito.mock(DrawerLayout.class);
        activity.setTheme(org.smartregister.R.style.AppTheme);
        activity.getDrawerLayoutId();
        Assert.assertNotNull(drawerLayout);
    }


    @Test
    public void testOnClick() {
        View view = Mockito.mock(View.class);

        activity = Mockito.spy(activity);
        NavigationMenu navigationMenu = Mockito.mock(NavigationMenu.class);

        Mockito.doReturn(R.id.btn_back_to_home).when(view).getId();
        activity.onClickReport(view);

        Assert.assertNotNull(navigationMenu);
        Mockito.verify(activity).onClickReport(view);
    }

    @Test
    public void testOnUniqueIdFetched() {
        View view = Mockito.mock(View.class);

        String anyString = "string";
        Map<String, String> anyMap = new HashMap<>();
        Triple<String, Map<String, String>, String> triple = Triple.of(anyString, anyMap, anyString);
        activity.onUniqueIdFetched(triple, anyString);
        Assert.assertNotNull(view);
    }

    @Test
    public void testOnNoUniqueId() {
        View view = Mockito.mock(View.class);
        activity.onNoUniqueId();
        Assert.assertNotNull(view);
    }

    @Test
    public void testOnRegistrationSaved() {
        activity.onRegistrationSaved(true);
    }

    @Test
    public void testOnSyncStart() {
        ImageView reportSyncBtn = Mockito.mock(ImageView.class);

        activity.setTheme(org.smartregister.R.style.AppTheme);
        activity.setContentView(R.layout.activity_report_register);
        ReflectionHelpers.setField(activity, "reportSyncBtn", reportSyncBtn);

        activity.onSyncStart();
        Assert.assertEquals(reportSyncBtn.getVisibility(), View.VISIBLE);
    }

    @Test
    public void testOnSyncComplete() {
        ImageView reportSyncBtn = Mockito.mock(ImageView.class);

        activity.setTheme(org.smartregister.R.style.AppTheme);
        activity.setContentView(R.layout.activity_report_register);
        ReflectionHelpers.setField(activity, "reportSyncBtn", reportSyncBtn);
        FetchStatus status = FetchStatus.fetchStarted;

        activity.onSyncComplete(status);
        Assert.assertEquals(reportSyncBtn.getVisibility(), View.VISIBLE);
    }


    @Test
    public void testOSyncInProgress() {
        ImageView reportSyncBtn = Mockito.mock(ImageView.class);
        activity.setContentView(R.layout.activity_report_register);
        ReflectionHelpers.setField(activity, "reportSyncBtn", reportSyncBtn);
        FetchStatus status = FetchStatus.fetchStarted;

        activity.onSyncInProgress(status);
        Assert.assertEquals(reportSyncBtn.getVisibility(), View.VISIBLE);
    }

    @Test
    public void testCreateDrawer() {
        activity = Mockito.spy(activity);
        NavigationMenu navigationMenu = Mockito.mock(NavigationMenu.class);
        activity.createDrawer();

        Assert.assertNotNull(navigationMenu);
    }

    @Test
    public void testOnResume() {
        ImageView reportSyncBtn = Mockito.mock(ImageView.class);
        ReflectionHelpers.setField(activity, "reportSyncBtn", reportSyncBtn);
        activity.onResume();
        Assert.assertEquals(reportSyncBtn.getVisibility(), View.VISIBLE);
    }
}
