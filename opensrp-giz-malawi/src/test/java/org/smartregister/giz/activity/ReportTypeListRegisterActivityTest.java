package org.smartregister.giz.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import org.smartregister.giz.R;

public class ReportTypeListRegisterActivityTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    private ReportTypeListRegisterActivity activity;
    private ActivityController<ReportTypeListRegisterActivity> controller;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        controller = Robolectric.buildActivity(ReportTypeListRegisterActivity.class).create().start();
        activity = controller.get();
    }


    @Test
    public void testSetupViewsSetTextsToTextView() {
        activity = Mockito.spy(ReportTypeListRegisterActivity.class);
        TextView titleTv = Mockito.mock(TextView.class);
        ListView listView = Mockito.mock(ListView.class);

        Mockito.doReturn(listView).when(activity).findViewById(R.id.lv_reportRegister_groupings);
        Mockito.doReturn(titleTv).when(activity).findViewById(R.id.title);

        activity.setUpViews();

        Mockito.verify(titleTv).setText(R.string.reports_type);
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
    public void testLoadDataCorrectlyLoadsData() {
        ImageView reportSyncBtn = Mockito.mock(ImageView.class);

        activity.setTheme(org.smartregister.R.style.AppTheme);
        activity.setContentView(R.layout.report_types_list_item);
        ReflectionHelpers.setField(activity, "reportSyncBtn", reportSyncBtn);

        activity.loadData();
        Assert.assertEquals(View.VISIBLE, reportSyncBtn.getVisibility());
    }
}
