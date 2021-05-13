package org.smartregister.giz.fragment;

import android.view.View;
import android.widget.ProgressBar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.view.ListContract;

public class EligibleChildrenReportFragmentTest {
    private EligibleChildrenReportFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(EligibleChildrenReportFragment.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testLoadPresenterAssignsPresenter() {
        Assert.assertNull(Whitebox.getInternalState(fragment, "presenter"));

        Assert.assertNotNull(Whitebox.getInternalState(fragment, "presenter"));
        Whitebox.getInternalState(fragment, "presenter");
    }

    @Test
    public void testSetLoadingStateCalledOnceHidesViewAfterBeingCalled() {
        ProgressBar progressBar = Mockito.mock(ProgressBar.class);
        Whitebox.setInternalState(fragment, "progressBar", progressBar);
        fragment.setLoadingState(true);
        Mockito.verify(progressBar, Mockito.times(1)).setVisibility(View.VISIBLE);

        fragment.setLoadingState(false);
        Mockito.verify(progressBar, Mockito.times(1)).setVisibility(View.INVISIBLE);
    }
}

