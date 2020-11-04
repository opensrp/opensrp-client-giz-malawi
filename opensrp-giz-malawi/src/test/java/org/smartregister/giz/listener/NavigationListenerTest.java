package org.smartregister.giz.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.ActivityConfiguration;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.activity.AllClientsRegisterActivity;
import org.smartregister.giz.activity.AncRegisterActivity;
import org.smartregister.giz.activity.ChildRegisterActivity;
import org.smartregister.giz.activity.MaternityRegisterActivity;
import org.smartregister.giz.activity.OpdRegisterActivity;
import org.smartregister.giz.activity.PncRegisterActivity;
import org.smartregister.giz.adapter.NavigationAdapter;
import org.smartregister.giz.util.GizConstants;

import java.util.ArrayList;

public class NavigationListenerTest extends BaseRobolectricTest {

    private Activity activity;

    private NavigationListener navigationListener;

    @Before
    public void setUp() {
        NavigationAdapter navigationAdapter = new NavigationAdapter(new ArrayList<>(), activity);
        activity = Mockito.spy(Robolectric.buildActivity(OpdRegisterActivity.class).create().get());
        navigationListener = new NavigationListener(activity, navigationAdapter);
    }

    @Test
    public void testOnClickShouldOpenCorrectRegister() {
        View view = new View(activity);

        //check for child register
        view.setTag(GizConstants.DrawerMenu.CHILD_CLIENTS);
        navigationListener.onClick(view);
        Intent intent = new Intent(activity, ChildRegisterActivity.class);
        Intent actual = Shadows.shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Assert.assertEquals(intent.getComponent(), actual.getComponent());

        //check for all clients
        view.setTag(GizConstants.DrawerMenu.ALL_CLIENTS);
        navigationListener.onClick(view);
        intent = new Intent(activity, AllClientsRegisterActivity.class);
        actual = Shadows.shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Assert.assertEquals(intent.getComponent(), actual.getComponent());

        //check for maternity clients
        view.setTag(GizConstants.DrawerMenu.MATERNITY_CLIENTS);
        navigationListener.onClick(view);
        intent = new Intent(activity, MaternityRegisterActivity.class);
        actual = Shadows.shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Assert.assertEquals(intent.getComponent(), actual.getComponent());

        //check for pnc clients
        view.setTag(GizConstants.DrawerMenu.PNC_CLIENTS);
        navigationListener.onClick(view);
        intent = new Intent(activity, PncRegisterActivity.class);
        actual = Shadows.shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Assert.assertEquals(intent.getComponent(), actual.getComponent());

        //check for opd clients
        view.setTag(GizConstants.DrawerMenu.OPD_CLIENTS);
        navigationListener.onClick(view);
        intent = new Intent(activity, OpdRegisterActivity.class);
        actual = Shadows.shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Assert.assertEquals(intent.getComponent(), actual.getComponent());

        //check for anc clients
        AncLibrary ancLibrary = Mockito.mock(AncLibrary.class);
        ActivityConfiguration activityConfiguration = Mockito.mock(ActivityConfiguration.class);
        Mockito.doReturn(activityConfiguration).when(ancLibrary).getActivityConfiguration();
        Mockito.doReturn(AncRegisterActivity.class).when(activityConfiguration).getHomeRegisterActivityClass();
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);

        view.setTag(GizConstants.DrawerMenu.ANC_CLIENTS);
        navigationListener.onClick(view);
        intent = new Intent(activity, AncRegisterActivity.class);
        actual = Shadows.shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Assert.assertEquals(intent.getComponent(), actual.getComponent());
    }

    @After
    public void tearDown() {
        if (activity != null) {
            activity.finish();
        }
    }
}