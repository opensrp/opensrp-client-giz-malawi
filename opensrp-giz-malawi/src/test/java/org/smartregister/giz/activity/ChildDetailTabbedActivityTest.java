package org.smartregister.giz.activity;

import android.content.Intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.giz.BaseUnitTest;


public class ChildDetailTabbedActivityTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    private ChildDetailTabbedActivity activity;
    private ActivityController<ChildDetailTabbedActivity> controller;
    @Captor
    private ArgumentCaptor<Intent> intentArgumentCaptor;
    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Context context = Context.getInstance();
        CoreLibrary.init(context);

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());

        controller = Robolectric.buildActivity(ChildDetailTabbedActivity.class).create().start().resume().restart();
        activity = controller.get();
    }

    @Test
    public void testStartFormActivityInvokesChildForm() {
        activity = Mockito.spy(activity);

        Mockito.doNothing().when(activity).startActivityForResult(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());

        activity.startFormActivity("{}");

        Mockito.verify(activity).startActivityForResult(intentArgumentCaptor.capture(), integerArgumentCaptor.capture());

        Intent capturedIntent = intentArgumentCaptor.getValue();
        Assert.assertNotNull(capturedIntent);
        Assert.assertEquals("org.smartregister.giz.activity.ChildFormActivity", capturedIntent.getComponent().getClassName());

        Integer capturedInteger = integerArgumentCaptor.getValue();

        Assert.assertNotNull(capturedInteger);
        int requestCode = ReflectionHelpers.getStaticField(BaseChildDetailTabbedActivity.class, "REQUEST_CODE_GET_JSON");
        Assert.assertEquals(requestCode, capturedInteger.intValue());

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
