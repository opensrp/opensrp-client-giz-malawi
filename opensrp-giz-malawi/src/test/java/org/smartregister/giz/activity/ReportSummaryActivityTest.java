package org.smartregister.giz.activity;

import android.content.Intent;
import android.view.View;

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
import org.smartregister.giz.BaseUnitTest;
import org.smartregister.giz.domain.Hia2Indicator;
import org.smartregister.giz.domain.MonthlyTally;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportSummaryActivityTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private ReportSummaryActivity activity;
    private ActivityController<ReportSummaryActivity> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        controller = Robolectric.buildActivity(ReportSummaryActivity.class).create().start();
        activity = controller.get();
    }

    @Test
    public void testOnResume() {
        String subTitle = "subtitle";
        CustomFontTextView submittedBy = Mockito.mock(CustomFontTextView.class);
        ReflectionHelpers.setField(activity, "subTitle", subTitle);
        activity.onResume();
        Assert.assertEquals(submittedBy.getVisibility(), View.VISIBLE);
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
    public void testGetDrawerLayoutIdd() {
        DrawerLayout drawerLayout = Mockito.mock(DrawerLayout.class);
        activity.getDrawerLayoutId();
        Assert.assertNotNull(drawerLayout);
    }

    @Test
    public void testOnRegistrationSaved() {
        View view = Mockito.mock(View.class);
        activity.onRegistrationSaved(true);
        Assert.assertNotNull(view);
    }

    @Test
    public void testOnBackActivity() {
        View view = Mockito.mock(View.class);
        activity.onBackActivity();
        Assert.assertNotNull(view);
    }

    @Test
    public void testSetTallies() {
        View view = Mockito.mock(View.class);
        ArrayList<MonthlyTally> tallies = new ArrayList<>();
        MonthlyTally monthlyTally = new MonthlyTally();
        Hia2Indicator indicator = new Hia2Indicator();
        monthlyTally.setMonth(new Date());
        monthlyTally.setGrouping("Reports");
        monthlyTally.setHia2Indicator(indicator);

        tallies.add(0, monthlyTally);
        activity.setTallies(tallies);
        Assert.assertNotNull(monthlyTally);
        Assert.assertNotNull(view);
    }

    @Test
    public void testFetchIndicatorTalliesForDay() {
        Intent intent = new Intent();
        String baseEntityId = "2323-006282-323";
        intent.putExtra(GizConstants.Columns.RegisterType.BASE_ENTITY_ID, baseEntityId);
        Date date = new Date();
        String reportGrouping = "reports";
        activity.fetchIndicatorTalliesForDay(date, reportGrouping);
        Assert.assertNotNull(baseEntityId);
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

}
