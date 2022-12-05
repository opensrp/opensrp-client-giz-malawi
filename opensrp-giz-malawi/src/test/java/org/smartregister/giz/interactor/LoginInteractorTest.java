package org.smartregister.giz.interactor;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.work.Configuration;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.impl.utils.SynchronousExecutor;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.google.common.util.concurrent.ListenableFuture;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.giz.BaseRobolectricTest;
import org.smartregister.giz.shadow.ShadowBaseJob;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.RecurringServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.DuplicateZeirIdsCleanerWorker;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncSettingsServiceJob;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 05-03-2020.
 */
public class LoginInteractorTest extends BaseRobolectricTest {

    private LoginInteractor loginInteractor;

    @Before
    public void setUp() throws Exception {
        loginInteractor = Mockito.spy(new LoginInteractor(Mockito.mock(BaseLoginContract.Presenter.class)));
        Mockito.doReturn((Context) RuntimeEnvironment.application).when(loginInteractor).getApplicationContext();
    }

    @Test
    public void scheduleJobsPeriodically() {
        loginInteractor.scheduleJobsPeriodically();

        Assert.assertTrue(ShadowBaseJob.getShadowHelper().isCalled(ShadowBaseJob.scheduleJobMN));
        HashMap<Integer, ArrayList<Object>> methodCalls = ShadowBaseJob.getShadowHelper().getMethodCalls(ShadowBaseJob.scheduleJobMN);
        assertEquals(8, methodCalls.size());
        assertEquals(VaccineServiceJob.TAG, methodCalls.get(0).get(0));
        assertEquals(RecurringServiceJob.TAG, methodCalls.get(1).get(0));
        assertEquals(WeightIntentServiceJob.TAG, methodCalls.get(2).get(0));
        assertEquals(HeightIntentServiceJob.TAG, methodCalls.get(3).get(0));
        assertEquals(ZScoreRefreshIntentServiceJob.TAG, methodCalls.get(4).get(0));
        assertEquals(SyncServiceJob.TAG, methodCalls.get(5).get(0));
        assertEquals(PullUniqueIdsServiceJob.TAG, methodCalls.get(6).get(0));
        assertEquals(ImageUploadServiceJob.TAG, methodCalls.get(7).get(0));
        ShadowBaseJob.getShadowHelper().getMethodCalls().clear();
    }

    @Test
    public void scheduleJobsImmediatelyShouldCallEachJobToScheduleImmediateExecution() throws ExecutionException, InterruptedException {
        initializeWorkManager();

        loginInteractor.scheduleJobsImmediately();

        Assert.assertTrue(ShadowBaseJob.getShadowHelper().isCalled(ShadowBaseJob.scheduleJobImmediatelyMN));
        HashMap<Integer, ArrayList<Object>> methodCalls = ShadowBaseJob.getShadowHelper().getMethodCalls(ShadowBaseJob.scheduleJobImmediatelyMN);
        assertEquals(5, methodCalls.size());
        assertEquals(SyncSettingsServiceJob.TAG, methodCalls.get(0).get(0));
        assertEquals(SyncServiceJob.TAG, methodCalls.get(1).get(0));
        assertEquals(PullUniqueIdsServiceJob.TAG, methodCalls.get(2).get(0));
        assertEquals(ZScoreRefreshIntentServiceJob.TAG, methodCalls.get(3).get(0));
        assertEquals(ImageUploadServiceJob.TAG, methodCalls.get(4).get(0));
        ShadowBaseJob.getShadowHelper().getMethodCalls().clear();

        ListenableFuture<List<WorkInfo>> listenableFuture = WorkManager.getInstance(RuntimeEnvironment.application)
                .getWorkInfosForUniqueWork(DuplicateZeirIdsCleanerWorker.TAG);

        assertEquals(1, listenableFuture.get().size());
    }


    private void initializeWorkManager() {
        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(new SynchronousExecutor())
                .build();

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(
                RuntimeEnvironment.application, config);
    }
}