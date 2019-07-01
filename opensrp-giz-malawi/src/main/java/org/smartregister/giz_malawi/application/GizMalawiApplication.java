package org.smartregister.giz_malawi.application;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;

import com.evernote.android.job.JobManager;

import org.jetbrains.annotations.NotNull;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.giz_malawi.BuildConfig;
import org.smartregister.giz_malawi.activity.ChildImmunizationActivity;
import org.smartregister.giz_malawi.activity.ChildProfileActivity;
import org.smartregister.giz_malawi.activity.LoginActivity;
import org.smartregister.giz_malawi.job.GizMalawiJobCreator;
import org.smartregister.giz_malawi.repository.GizMalawiRepository;
import org.smartregister.giz_malawi.sync.GizMalawiProcessorForJava;
import org.smartregister.giz_malawi.util.DBConstants;
import org.smartregister.giz_malawi.util.GizConstants;
import org.smartregister.giz_malawi.util.GizUtils;
import org.smartregister.growthmonitoring.GrowthMonitoringConfig;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.HeightZScoreRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.WeightZScoreRepository;
import org.smartregister.growthmonitoring.service.intent.ZScoreRefreshIntentService;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.smartregister.util.Log.logInfo;

public class GizMalawiApplication extends DrishtiApplication implements TimeChangedBroadcastReceiver.OnTimeChangedListener {
    public static final String ENGLISH_LOCALE = "en";
    private static final String TAG = GizMalawiApplication.class.getCanonicalName();
    public static boolean isManualClick = true;
    private static CommonFtsObject commonFtsObject;
    private static JsonSpecHelper jsonSpecHelper;
    private static GizMalawiProcessorForJava gizMalawiProcessorForJava;
    private EventClientRepository eventClientRepository;
    private String password;
    private boolean lastModified;
    private ECSyncHelper ecSyncHelper;

    public static JsonSpecHelper getJsonSpecHelper() {
        return jsonSpecHelper;
    }

    public GizMalawiProcessorForJava getClientProcessorForJava() {
        if (gizMalawiProcessorForJava == null) {
            gizMalawiProcessorForJava = GizMalawiProcessorForJava.getInstance(getApplicationContext());
        }
        return gizMalawiProcessorForJava;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();

        String lang = GizUtils.getLanguage(getApplicationContext());
        Locale locale = new Locale(lang);
        Resources res = getApplicationContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context, new GizMalawiSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP);

        GrowthMonitoringConfig growthMonitoringConfig = new GrowthMonitoringConfig();
        GrowthMonitoringLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION,
                growthMonitoringConfig);
        ImmunizationLibrary.init(context, getRepository(), createCommonFtsObject(), BuildConfig.VERSION_CODE,
                BuildConfig.DATABASE_VERSION);
        ConfigurableViewsLibrary.init(context, getRepository());
        ChildLibrary.init(context, getRepository(), getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        initRepositories();
        initOfflineSchedules();

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(GizUtils.ALLOWED_LEVELS, GizUtils.DEFAULT_LOCATION_LEVEL);
        jsonSpecHelper = new JsonSpecHelper(this);

        //init Job Manager
        JobManager.create(this).addJobCreator(new GizMalawiJobCreator());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        commonFtsObject.updateAlertScheduleMap(getAlertScheduleMap());

        return commonFtsObject;
    }

    private ChildMetadata getMetadata() {
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, ChildProfileActivity.class,
                ChildImmunizationActivity.class, true);
        metadata.updateChildRegister(GizConstants.JSON_FORM.CHILD_ENROLLMENT, GizConstants.TABLE_NAME.CHILD,
                GizConstants.TABLE_NAME.MOTHER_TABLE_NAME, GizConstants.EventType.CHILD_REGISTRATION,
                GizConstants.EventType.UPDATE_CHILD_REGISTRATION, GizConstants.CONFIGURATION.CHILD_REGISTER,
                GizConstants.RELATIONSHIP.MOTHER, GizConstants.JSON_FORM.OUT_OF_CATCHMENT_SERVICE);
        return metadata;
    }

    private void initRepositories() {
        weightRepository();
        heightRepository();
        vaccineRepository();
        weightZScoreRepository();
        heightZScoreRepository();
    }

    private void initOfflineSchedules() {
        try {
            List<VaccineGroup> childVaccines = VaccinatorUtils.getSupportedVaccines(this);
            List<Vaccine> specialVaccines = VaccinatorUtils.getSpecialVaccines(this);
            VaccineSchedule.init(childVaccines, specialVaccines, "child");
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private static String[] getFtsTables() {
        return new String[] {GizConstants.TABLE_NAME.CHILD};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(GizConstants.TABLE_NAME.CHILD)) {
            return new String[] {DBConstants.KEY.MER_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals(GizConstants.TABLE_NAME.CHILD)) {

            ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines(GizConstants.VACCINE.CHILD);
            List<String> names = new ArrayList<>();
            names.add(DBConstants.KEY.FIRST_NAME);
            names.add(DBConstants.KEY.DOB);
            names.add(DBConstants.KEY.MER_ID);
            names.add(DBConstants.KEY.LAST_INTERACTED_WITH);
            names.add(DBConstants.KEY.INACTIVE);
            names.add(DBConstants.KEY.LOST_TO_FOLLOW_UP);
            names.add(DBConstants.KEY.DOD);
            names.add(DBConstants.KEY.DATE_REMOVED);

            for (VaccineRepo.Vaccine vaccine : vaccines) {
                names.add("alerts." + VaccinateActionUtils.addHyphen(vaccine.display()));
            }

            return names.toArray(new String[names.size()]);
        }
        return null;
    }

    private static Map<String, Pair<String, Boolean>> getAlertScheduleMap() {
        ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines("child");
        Map<String, Pair<String, Boolean>> map = new HashMap<>();
        for (VaccineRepo.Vaccine vaccine : vaccines) {
            map.put(vaccine.display(), Pair.create(GizConstants.TABLE_NAME.CHILD, false));
        }
        return map;
    }

    public static synchronized GizMalawiApplication getInstance() {
        return (GizMalawiApplication) mInstance;
    }

    public WeightRepository weightRepository() {
        return GrowthMonitoringLibrary.getInstance().weightRepository();
    }

    public HeightRepository heightRepository() {
        return GrowthMonitoringLibrary.getInstance().heightRepository();
    }

    public VaccineRepository vaccineRepository() {
        return ImmunizationLibrary.getInstance().vaccineRepository();
    }

    public WeightZScoreRepository weightZScoreRepository() {
        return GrowthMonitoringLibrary.getInstance().weightZScoreRepository();
    }

    public HeightZScoreRepository heightZScoreRepository() {
        return GrowthMonitoringLibrary.getInstance().heightZScoreRepository();
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new GizMalawiRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return repository;
    }

    public String getPassword() {
        if (password == null) {
            String username = getContext().userService().getAllSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
    }

    public Context getContext() {
        return context;
    }

    @NotNull
    @Override
    public ClientProcessorForJava getClientProcessor() {
        return GizMalawiProcessorForJava.getInstance(this);
    }

    public void startZscoreRefreshService() {
        Intent intent = new Intent(this.getApplicationContext(), ZScoreRefreshIntentService.class);
        this.getApplicationContext().startService(intent);
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        TimeChangedBroadcastReceiver.destroy(this);
        SyncStatusBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    protected void cleanUpSyncState() {
        try {
            DrishtiSyncScheduler.stop(getApplicationContext());
            context.allSharedPreferences().saveIsSyncInProgress(false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onTimeChanged() {
        context.userService().forceRemoteLogin();
        logoutCurrentUser();
    }

    @Override
    public void onTimeZoneChanged() {
        context.userService().forceRemoteLogin();
        logoutCurrentUser();
    }

    public Context context() {
        return context;
    }

    public RecurringServiceTypeRepository recurringServiceTypeRepository() {
        return ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
    }

    public EventClientRepository eventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }

    public RecurringServiceRecordRepository recurringServiceRecordRepository() {
        return ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
    }

    public boolean isLastModified() {
        return lastModified;
    }

    public void setLastModified(boolean lastModified) {
        this.lastModified = lastModified;
    }

    public ECSyncHelper getEcSyncHelper() {
        if (ecSyncHelper == null) {
            ecSyncHelper = ECSyncHelper.getInstance(getApplicationContext());
        }
        return ecSyncHelper;
    }
}

