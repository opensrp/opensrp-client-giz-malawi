package org.smartregister.giz.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.MonthlyTally;
import org.smartregister.giz.repository.MonthlyTalliesRepository;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-29
 */

public class FetchEditedMonthlyTalliesTask extends AsyncTask<Void, Void, List<MonthlyTally>> {
    private final TaskListener taskListener;

    public FetchEditedMonthlyTalliesTask(@NonNull TaskListener taskListener) {
        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<MonthlyTally> doInBackground(Void... params) {
        MonthlyTalliesRepository monthlyTalliesRepository = GizMalawiApplication.getInstance().monthlyTalliesRepository();
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.DAY_OF_MONTH, 1); // Set date to first day of this month
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        endDate.set(Calendar.SECOND, 59);
        endDate.set(Calendar.MILLISECOND, 999);
        endDate.add(Calendar.DATE, -1); // Move the date to last day of last month

        return monthlyTalliesRepository.findEditedDraftMonths(null, endDate.getTime());
    }

    @Override
    protected void onPostExecute(List<MonthlyTally> monthlyTallies) {
        super.onPostExecute(monthlyTallies);
        taskListener.onPostExecute(monthlyTallies);
    }

    public interface TaskListener {
        void onPostExecute(List<MonthlyTally> monthlyTallies);
    }
}