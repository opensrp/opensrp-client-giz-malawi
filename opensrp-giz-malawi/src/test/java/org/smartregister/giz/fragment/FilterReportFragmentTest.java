package org.smartregister.giz.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.giz.R;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.contract.FindReportContract;
import org.smartregister.giz.util.GizConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(application = GizMalawiApplication.class, sdk = 22)
public class FilterReportFragmentTest {
    @Mock
    private LayoutInflater layoutInflater;
    @Mock
    private ViewGroup container;
    @Mock
    private Bundle bundle;

    @Mock
    private View view;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateSelectedCommunitiesView() {
        FilterReportFragment spyFragment = Mockito.spy(FilterReportFragment.class);

        TextView selectedCommunitiesTV = Mockito.mock(TextView.class);
        boolean[] checkedCommunities = new boolean[2];
        checkedCommunities[0] = true;
        checkedCommunities[1] = true;
        List<String> communityList = new ArrayList<>();
        communityList.add("community 1");
        communityList.add("community 2");
        ReflectionHelpers.setField(spyFragment, "checkedCommunities", checkedCommunities);
        ReflectionHelpers.setField(spyFragment, "communityList", communityList);
        ReflectionHelpers.setField(spyFragment, "selectedCommunitiesTV", selectedCommunitiesTV);

        spyFragment.updateSelectedCommunitiesView();

        selectedCommunitiesTV.setText("community 1 \n community 2");
    }

    @Test
    public void testRunReport() {
        FilterReportFragment spyFragment = Mockito.spy(FilterReportFragment.class);
        FindReportContract.Presenter presenter = Mockito.spy(FindReportContract.Presenter.class);
        ReflectionHelpers.setField(spyFragment, "presenter", presenter);

        LinkedHashMap<String, String> communityIDList = new LinkedHashMap<>();
        communityIDList.put("All communities", "");
        communityIDList.put("community 1", "456");
        boolean[] checkedCommunities = new boolean[2];
        checkedCommunities[0] = true;
        List<String> communityList = new ArrayList<>();
        communityList.add("All communities");
        communityList.add("community 1");
        ReflectionHelpers.setField(spyFragment, "checkedCommunities", checkedCommunities);
        ReflectionHelpers.setField(spyFragment, "communityList", communityList);
        ReflectionHelpers.setField(spyFragment, "communityIDList", communityIDList);
        spyFragment.runReport();


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        Calendar myCalendar = Calendar.getInstance();
        List<String> communityIds = new ArrayList<>();
        communityIds.add("");
        List<String> communities = new ArrayList<>();
        communities.add("All communities");
        Map<String, String> map = new HashMap<>();
        Gson gson = new Gson();
        map.put(GizConstants.ReportParametersHelper.COMMUNITY, gson.toJson(communities));
        map.put(GizConstants.ReportParametersHelper.COMMUNITY_ID, gson.toJson(communityIds));
        map.put(GizConstants.ReportParametersHelper.REPORT_DATE, dateFormat.format(myCalendar.getTime()));
        Mockito.verify(presenter).runReport(map);
    }

    @Test
    public void testHandleCommunityMultiChoiceItemsDialog() {
        FilterReportFragment spyFragment = Mockito.spy(FilterReportFragment.class);

        boolean[] checkedCommunities = new boolean[2];
        checkedCommunities[0] = true;
        List<String> communityList = new ArrayList<>();
        communityList.add("All communities");
        communityList.add("community 1");
        ReflectionHelpers.setField(spyFragment, "checkedCommunities", checkedCommunities);
        ReflectionHelpers.setField(spyFragment, "communityList", communityList);
        DialogInterface dialog = Mockito.spy(DialogInterface.class);
        int which = 0;

        Mockito.doNothing().when(spyFragment).updateDialogCheckItem(dialog, 1, false);
        spyFragment.handleCommunityMultiChoiceItemsDialog(dialog, which, true);
        Mockito.verify(spyFragment).updateDialogCheckItem(dialog, 1, false);
    }

    @Test
    public void testLoadPresenterAssignsPresenter() {
        FilterReportFragment spyFragment = Mockito.spy(FilterReportFragment.class);

        Assert.assertNull(Whitebox.getInternalState(spyFragment, "presenter"));

        spyFragment.loadPresenter();

        Assert.assertNotNull(Whitebox.getInternalState(spyFragment, "presenter"));
    }

    @Test
    public void testOnCreateViewsInitializesViews() {
        FilterReportFragment spyFragment = Mockito.spy(FilterReportFragment.class);
        ActivityController<AppCompatActivity> activityController;
        activityController = Robolectric.buildActivity(AppCompatActivity.class).create().resume();
        FragmentActivity activity = activityController.get();
        when(spyFragment.getActivity()).thenReturn(activity);
        when(spyFragment.getContext()).thenReturn(activity);

        Mockito.doReturn(view).when(layoutInflater).inflate(R.layout.filter_report_fragment, container, false);
        AppCompatActivity activitySpy = (AppCompatActivity) Mockito.spy(activity);
        Mockito.doReturn(activitySpy).when(spyFragment).getActivity();

        Mockito.doNothing().when(spyFragment).bindLayout();
        spyFragment.onCreateView(layoutInflater, container, bundle);
        verify(spyFragment).onCreateView(layoutInflater, container, bundle);
    }
}

