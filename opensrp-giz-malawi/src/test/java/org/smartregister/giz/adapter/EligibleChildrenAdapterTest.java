package org.smartregister.giz.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.giz.domain.EligibleChild;
import org.smartregister.giz.viewholder.EligibleChildrenViewHolder;
import org.smartregister.view.ListContract;

import java.util.Arrays;
import java.util.List;

public class EligibleChildrenAdapterTest {

    @Mock
    ListContract.View<EligibleChild> view;
    @Mock
    Context context;
    private EligibleChildrenAdapter adapter;
    private List<EligibleChild> eligibleChildList;
    @Mock
    private EligibleChildrenViewHolder viewHolder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        EligibleChild eligibleChild1 = new EligibleChild();
        EligibleChild eligibleChild = new EligibleChild();
        eligibleChildList = Arrays.asList(eligibleChild1, eligibleChild);
        adapter = new EligibleChildrenAdapter(eligibleChildList, view, context);
    }

    @Test
    public void testOnBindViewHolder() {

        TextView tvName = Mockito.mock(TextView.class);
        TextView tvAge = Mockito.mock(TextView.class);
        TextView tvFamily = Mockito.mock(TextView.class);
        TextView tvDue = Mockito.mock(TextView.class);
        View currentView = Mockito.mock(View.class);
        Context context = Mockito.mock(Context.class);

        ReflectionHelpers.setField(viewHolder, "tvName", tvName);
        ReflectionHelpers.setField(viewHolder, "tvAge", tvAge);
        ReflectionHelpers.setField(viewHolder, "tvFamily", tvFamily);
        ReflectionHelpers.setField(viewHolder, "tvDue", tvDue);
        ReflectionHelpers.setField(viewHolder, "currentView", currentView);
        ReflectionHelpers.setField(viewHolder, "context", context);
        EligibleChild eligibleChild = eligibleChildList.get(0);

        adapter.onBindViewHolder(viewHolder, 0);
        Assert.assertNotNull(eligibleChild);
    }

}