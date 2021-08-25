package org.smartregister.giz.activity;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.presenter.FragmentBaseActivityPresenter;

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

        activity.setTitle("jane");
        verify(textView).setText(Mockito.anyString());
    }

    @Test
    public void testInitializePresenter() {
        activity.initializePresenter();
        Assert.assertTrue(ReflectionHelpers.getField(activity, "presenter") instanceof FragmentBaseActivityPresenter);
    }
}

