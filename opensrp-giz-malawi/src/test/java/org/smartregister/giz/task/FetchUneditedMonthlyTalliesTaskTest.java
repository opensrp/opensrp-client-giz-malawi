package org.smartregister.giz.task;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.MonthlyTally;
import org.smartregister.giz.repository.MonthlyTalliesRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GizMalawiApplication.class})
public class FetchUneditedMonthlyTalliesTaskTest {

    FetchUneditedMonthlyTalliesTask monthlyTalliesTask;

    @Before
    public void setUp() {
        monthlyTalliesTask = new FetchUneditedMonthlyTalliesTask("opd", new FetchUneditedMonthlyTalliesTask.TaskListener() {
            @Override
            public void onPostExecute(List<Date> monthlyTallies) {
                // Do Nothing
            }
        });
    }

    @Test
    public void testFilterMonthsWithNoCount() {
        ArrayList<Date> dates = new ArrayList<>();
        Date date = new Date();
        dates.add(date);

        ArrayList<MonthlyTally> tallies = new ArrayList<>();
        MonthlyTally tally = new MonthlyTally();
        tally.setValue("1");
        tallies.add(tally);

        GizMalawiApplication application = Mockito.mock(GizMalawiApplication.class);
        MonthlyTalliesRepository repository = Mockito.mock(MonthlyTalliesRepository.class);
        PowerMockito.mockStatic(GizMalawiApplication.class);
        PowerMockito.when(GizMalawiApplication.getInstance()).thenReturn(application);
        PowerMockito.when(application.monthlyTalliesRepository()).thenReturn(repository);
        PowerMockito.when(repository.findDrafts(MonthlyTalliesRepository.DF_YYYYMM.format(date), "opd")).thenReturn(tallies);

        ArrayList<Date> finalDates;
        finalDates = (ArrayList<Date>) monthlyTalliesTask.filterMonthsWithNoCount(dates);

        Assert.assertEquals(1, finalDates.size());

        tallies.clear();
        tally.setValue("0");
        tallies.add(tally);
        finalDates.clear();
        finalDates = (ArrayList<Date>) monthlyTalliesTask.filterMonthsWithNoCount(dates);
        Assert.assertEquals(0, finalDates.size());
    }


}
