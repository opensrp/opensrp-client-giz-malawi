package org.smartregister.giz.activity;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.view.Menu;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.giz.BaseRobolectricTest;

public class FragmentBaseActivityTest extends BaseRobolectricTest {

    private FragmentBaseActivity activity;

    @Before
    public void setUp() {
        activity = spy(Robolectric
                .buildActivity(FragmentBaseActivity.class)
                .create()
                .resume()
                .get());
    }

    @Test
    public void testSetTitleWillSetTextViewWithTitle() {
        activity = spy(activity);

        TextView textView = Mockito.mock(TextView.class);
        ReflectionHelpers.setField(activity, "titleTextView", textView);

        activity.setTitle(Mockito.anyString());
        verify(textView).setText(Mockito.anyString());
    }

    @Test
    public void testOnCreateOptionsMenuReturnsTrueWhenCalled() {
        Menu menu = Mockito.mock(Menu.class);
        activity = spy(activity);
        assertFalse(activity.onCreateOptionsMenu(menu));
        verify(activity).onCreateOptionsMenu(any());
    }
}

