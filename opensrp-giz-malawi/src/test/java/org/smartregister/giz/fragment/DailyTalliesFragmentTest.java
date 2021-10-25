package org.smartregister.giz.fragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.repository.DailyIndicatorCountRepository;

import java.util.ArrayList;
import java.util.Date;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReportingLibrary.class})
public class DailyTalliesFragmentTest {

    private DailyTalliesFragment fragment;

    @Before
    public void setUp() {
        fragment = new DailyTalliesFragment();
    }

    @Test
    public void testNewInstance() {
        fragment = DailyTalliesFragment.newInstance("opd");
        Assert.assertEquals("opd", fragment.reportGrouping);
    }

    @Test
    public void testFilterDatesWithNoCount() {
        fragment.reportGrouping = "opd";

        ArrayList<Date> dates = new ArrayList<>();
        Date date = new Date();
        dates.add(date);

        ArrayList<IndicatorTally> tallies = new ArrayList<>();
        IndicatorTally tally = new IndicatorTally();
        tally.setCount(1);
        tallies.add(tally);

        ReportingLibrary reportingLibrary = Mockito.mock(ReportingLibrary.class);
        DailyIndicatorCountRepository indicatorCountRepository = Mockito.mock(DailyIndicatorCountRepository.class);
        PowerMockito.mockStatic(ReportingLibrary.class);
        PowerMockito.when(ReportingLibrary.getInstance()).thenReturn(reportingLibrary);
        PowerMockito.when(reportingLibrary.dailyIndicatorCountRepository()).thenReturn(indicatorCountRepository);
        PowerMockito.when(indicatorCountRepository.getIndicatorTalliesForDay(date, "opd")).thenReturn(tallies);

        ArrayList<Date> finalDates;
        finalDates = fragment.filterDatesWithNoCount(dates);

        Assert.assertEquals(1, finalDates.size());

        tallies.clear();
        tally.setCount(0);
        tallies.add(tally);
        finalDates.clear();
        finalDates = fragment.filterDatesWithNoCount(dates);
        Assert.assertEquals(0, finalDates.size());

    }


}
