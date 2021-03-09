package org.smartregister.giz.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.giz.domain.VillageDose;
import org.smartregister.giz.viewholder.VillageDoseViewHolder;
import org.smartregister.view.ListContract;

import java.util.ArrayList;
import java.util.List;

public class VillageDoseAdapterTest {
    private final List<VillageDose> villageDoseList = new ArrayList<>();
    @Mock
    private ListContract.View<VillageDose> view;
    @Mock
    private Context context;
    private VillageDoseAdapter adapter;
    @Mock
    private VillageDoseViewHolder viewHolder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        VillageDose villageDose = new VillageDose();
        VillageDose villageDose1 = new VillageDose();
        villageDoseList.add(villageDose);
        villageDoseList.add(villageDose1);
        adapter = new VillageDoseAdapter(villageDoseList, view, context);
    }

    @Test
    public void testOnBindViewHolder() {

        TextView tvName = Mockito.mock(TextView.class);
        LinearLayout linearLayout = Mockito.mock(LinearLayout.class);
        View currentView = Mockito.mock(View.class);
        Context context = Mockito.mock(Context.class);

        ReflectionHelpers.setField(viewHolder, "tvName", tvName);
        ReflectionHelpers.setField(viewHolder, "linearLayout", linearLayout);
        ReflectionHelpers.setField(viewHolder, "currentView", currentView);
        ReflectionHelpers.setField(viewHolder, "context", context);
        VillageDose villageDose = villageDoseList.get(0);

        adapter.onBindViewHolder(viewHolder, 0);
        Assert.assertNotNull(villageDose);
    }

}