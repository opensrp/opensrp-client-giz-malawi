package org.smartregister.giz.activity;

import android.content.Intent;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.R;
import org.smartregister.giz.util.GizConstants;

import java.util.HashMap;

public class GizAncProfileActivityTest extends BaseRobolectricTest {

    private GizAncProfileActivity gizAncProfileActivity;

    @Before
    public void setUp() {
        HashMap<String, String> map = new HashMap<>();
        map.put(GizConstants.IS_FROM_MATERNITY, "true");
        Intent intent = new Intent();
        intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);
        gizAncProfileActivity = Mockito.spy(Robolectric
                .buildActivity(GizAncProfileActivity.class, intent)
                .create()
                .get());
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        gizAncProfileActivity.finish();
    }

    @Test
    public void testOnCreateOptionsMenuWhenFromMaternityShouldDisableSomeViews() {
        Menu menu = Mockito.spy(new MenuBuilder(gizAncProfileActivity.getApplicationContext()));
        View view = Mockito.mock(View.class);
        MenuItem menuItem = Mockito.mock(MenuItem.class);
        Button button = Mockito.mock(Button.class);
        ViewGroup linearLayout = Mockito.mock(ViewGroup.class);

        Mockito.doReturn(linearLayout)
                .when(button)
                .getParent();

        Mockito.doReturn(button)
                .when(gizAncProfileActivity)
                .getDueButton();

        Mockito.doReturn(menuItem)
                .when(menu)
                .findItem(R.id.overflow_menu_item);

        Mockito.doReturn(view)
                .when(gizAncProfileActivity)
                .findViewById(R.id.btn_profile_registration_info);

        boolean result = gizAncProfileActivity.onCreateOptionsMenu(menu);

        Mockito.verify(view, Mockito.times(1))
                .setEnabled(Mockito.eq(false));

        Mockito.verify(menuItem, Mockito.times(1))
                .setEnabled(Mockito.eq(false));

        Mockito.verify(linearLayout, Mockito.times(1))
                .setVisibility(Mockito.eq(View.GONE));

        Assert.assertTrue(result);
    }

    @Test
    public void testOnCreateOptionsMenuWhenNotFromMaternityShouldShowAllViews() {
        gizAncProfileActivity.setIntent(null);

        Menu menu = Mockito.spy(new MenuBuilder(gizAncProfileActivity.getApplicationContext()));
        MenuItem menuItem = Mockito.mock(MenuItem.class);

        boolean result = gizAncProfileActivity.onCreateOptionsMenu(menu);

        Mockito.verify(menuItem, Mockito.never())
                .setEnabled(Mockito.eq(false));

        Assert.assertTrue(result);
    }

    @Test
    public void testOnOptionsItemSelectedWhenIsFromMaternityShouldFinishActivity() {
        MenuItem menuItem = Mockito.mock(MenuItem.class);
        Mockito.doReturn(android.R.id.home).when(menuItem).getItemId();
        gizAncProfileActivity.onOptionsItemSelected(menuItem);
        Mockito.verify(gizAncProfileActivity, Mockito.times(1))
                .finish();
    }

    @Test
    public void testOnBackPressedWhenFromMaternityShouldFinishActivity() {
        gizAncProfileActivity.onBackPressed();
        Mockito.verify(gizAncProfileActivity, Mockito.times(1))
                .finish();
    }

    @Test
    public void testOnBackPressedWhenNotFromMaternityShouldNotFinishActivity() {
        gizAncProfileActivity.setIntent(null);
        gizAncProfileActivity.onBackPressed();
        Mockito.verify(gizAncProfileActivity, Mockito.never())
                .finish();
    }

}