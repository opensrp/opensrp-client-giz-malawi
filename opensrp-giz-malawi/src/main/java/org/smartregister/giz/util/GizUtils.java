package org.smartregister.giz.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.event.PatientRemovedEvent;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.giz.BuildConfig;
import org.smartregister.giz.R;
import org.smartregister.giz.activity.GizAncProfileActivity;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.configuration.GizPncRegisterQueryProvider;
import org.smartregister.giz.event.BaseEvent;
import org.smartregister.giz.listener.OnLocationChangeListener;
import org.smartregister.giz.task.OpenMaternityProfileTask;
import org.smartregister.giz.view.NavigationMenu;
import org.smartregister.giz.widget.GizTreeViewDialog;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AssetHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class GizUtils extends Utils {

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String FACILITY = "Facility";
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";
    public static final SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd");
    public static final String LANGUAGE = "language";
    private static final String PREFERENCES_FILE = "lang_prefs";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    public static void showDialogMessage(Context context, int title, int message) {
        showDialogMessage(context, title > 0 ? context.getResources().getString(title) : "",
                message > 0 ? context.getResources().getString(message) : "");
    }

    public static void showDialogMessage(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)

                .setPositiveButton(android.R.string.ok, null)

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void saveLanguage(Context ctx, String language) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LANGUAGE, language);
        editor.apply();
        saveGlobalLanguage(language, ctx);
    }

    public static void saveGlobalLanguage(String language, Context activity) {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(
                GizMalawiApplication.getInstance().getApplicationContext()));
        allSharedPreferences.saveLanguagePreference(language);
        setLocale(new Locale(language), activity);
    }

    public static void setLocale(Locale locale, Context activity) {
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        GizMalawiApplication.getInstance().getApplicationContext().createConfigurationContext(configuration);
    }

    public static String getLanguage(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE, "en");
    }

    public static Context setAppLocale(Context context, String language) {
        Context newContext = context;
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = newContext.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        newContext = newContext.createConfigurationContext(config);
        return newContext;
    }

    public static void postStickyEvent(
            BaseEvent event) {//Each Sticky event must be manually cleaned by calling GizUtils.removeStickyEvent
        // after
        // handling
        EventBus.getDefault().postSticky(event);
    }

    public static void removeStickyEvent(BaseEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
    }

    public static String childAgeLimitFilter() {
        return childAgeLimitFilter(GizConstants.KEY.DOB, GizConstants.KEY.FIVE_YEAR);
    }

    private static String childAgeLimitFilter(String dateColumn, int age) {
        return " ((( julianday('now') - julianday(" + dateColumn + "))/365.25) <" + age + ")";
    }

    public static boolean updateClientDeath(@NonNull EventClient eventClient) {
        Client client = eventClient.getClient();
        ContentValues values = new ContentValues();
        if (client != null) {
            if (client.getDeathdate() == null) {
                Timber.e(new Exception(), "Death event for %s cannot be processed because deathdate is NULL : %s"
                        , client.getFirstName() + " " + client.getLastName(), new Gson().toJson(eventClient));
                return false;
            }
            values.put(Constants.KEY.DOD, Utils.convertDateFormat(client.getDeathdate()));
            values.put(Constants.KEY.DATE_REMOVED, Utils.convertDateFormat(client.getDeathdate().toDate(), Utils.DB_DF));
            AllCommonsRepository allCommonsRepository = GizMalawiApplication.getInstance().context().allCommonsRepositoryobjects(GizConstants.TABLE_NAME.ALL_CLIENTS);
            if (allCommonsRepository != null) {
                allCommonsRepository.update(GizConstants.TABLE_NAME.ALL_CLIENTS, values, client.getBaseEntityId());
                allCommonsRepository.updateSearch(client.getBaseEntityId());
            }
            return true;
        }
        return false;
    }

    @NonNull
    public static Locale getLocale(Context context) {
        if (context == null) {
            return Locale.getDefault();
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    @NonNull
    public static ArrayList<String> getLocationLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_LEVELS));
    }

    @NonNull
    public static ArrayList<String> getHealthFacilityLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.HEALTH_FACILITY_LEVELS));
    }

    @NonNull
    public static String getCurrentLocality() {
        String selectedLocation = GizMalawiApplication.getInstance().context().allSharedPreferences().fetchCurrentLocality();
        if (StringUtils.isBlank(selectedLocation)) {
            selectedLocation = LocationHelper.getInstance().getDefaultLocation();
            GizMalawiApplication.getInstance().context().allSharedPreferences().saveCurrentLocality(selectedLocation);
        }
        return selectedLocation;
    }

    public static void showLocations(@Nullable Activity context, @NonNull OnLocationChangeListener onLocationChangeListener, @Nullable NavigationMenu navigationMenu) {
        try {
            ArrayList<String> allLevels = getLocationLevels();
            ArrayList<String> healthFacilities = getHealthFacilityLevels();
            ArrayList<String> defaultLocation = (ArrayList<String>) LocationHelper.getInstance().generateDefaultLocationHierarchy(allLevels);
            List<FormLocation> upToFacilities = LocationHelper.getInstance().generateLocationHierarchyTree(false, healthFacilities);
            String upToFacilitiesString = AssetHandler.javaToJsonString(upToFacilities, new TypeToken<List<FormLocation>>() {
            }.getType());
            GizTreeViewDialog treeViewDialog = new GizTreeViewDialog(context,
                    new JSONArray(upToFacilitiesString), defaultLocation, new ArrayList<>());

            treeViewDialog.setCancelable(true);
            treeViewDialog.setCanceledOnTouchOutside(true);
            treeViewDialog.setOnDismissListener(dialog -> {
                ArrayList<String> treeViewDialogName = treeViewDialog.getName();
                if (!treeViewDialogName.isEmpty()) {
                    String newLocation = treeViewDialogName.get(treeViewDialogName.size() - 1);
                    GizMalawiApplication.getInstance().context().allSharedPreferences().saveCurrentLocality(newLocation);
                    onLocationChangeListener.updateUi(newLocation);
                    if (navigationMenu != null) {
                        navigationMenu.updateUi(newLocation);
                    }
                }

            });
            treeViewDialog.show();
        } catch (JSONException e) {
            Timber.e(e);
        }
    }


    public static void startReportJob(Context context) {
        String reportJobExecutionTime = GizMalawiApplication.getInstance().context().allSharedPreferences().getPreference("report_job_execution_time");
        if (StringUtils.isBlank(reportJobExecutionTime) || timeBetweenLastExecutionAndNow(30, reportJobExecutionTime)) {
            GizMalawiApplication.getInstance().context().allSharedPreferences().savePreference("report_job_execution_time", String.valueOf(System.currentTimeMillis()));
            Toast.makeText(context, "Reporting Job Has Started, It will take some time", Toast.LENGTH_LONG).show();
            RecurringIndicatorGeneratingJob.scheduleJobImmediately(RecurringIndicatorGeneratingJob.TAG);
        } else {
            Toast.makeText(context, "Reporting Job Has Already Been Started, Try again in 30 mins", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean timeBetweenLastExecutionAndNow(int i, String reportJobExecutionTime) {
        try {
            long executionTime = Long.parseLong(reportJobExecutionTime);
            long now = System.currentTimeMillis();
            long diffNowExecutionTime = now - executionTime;
            return TimeUnit.MILLISECONDS.toMinutes(diffNowExecutionTime) > i;
        } catch (NumberFormatException e) {
            Timber.e(e);
            return false;
        }
    }

    public static boolean getSyncStatus() {
        String synComplete = GizMalawiApplication.getInstance().context().allSharedPreferences().getPreference("syncComplete");
        boolean isSyncComplete = false;
        if (StringUtils.isBlank(synComplete)) {
            GizMalawiApplication.getInstance().context().allSharedPreferences().savePreference("syncComplete", String.valueOf(false));
        } else {
            isSyncComplete = Boolean.parseBoolean(synComplete);
        }
        return isSyncComplete;
    }

    public static void updateSyncStatus(boolean isComplete) {
        GizMalawiApplication.getInstance().context().allSharedPreferences().savePreference("syncComplete", String.valueOf(isComplete));
    }

    public static void showAncMaternityNavigationDialog(@NonNull PatientRemovedEvent event, @NonNull Activity activity) {
        if (event.getClosedNature() != null && ConstantsUtils.ClosedNature.TRANSFERRED.equals(event.getClosedNature())) {
            AlertDialog dialog = new AlertDialog.Builder(activity, R.style.AppThemeAlertDialog)
                    .setCancelable(true)
                    .setMessage(R.string.anc_migration_to_maternity_text)
                    .setNegativeButton("GO TO PROFILE", (dialog1, which) -> {
                        String baseEntityId = activity.getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
                        new OpenMaternityProfileTask(activity, baseEntityId).execute();
                    }).create();

            dialog.show();
        } else {
            if (activity instanceof GizAncProfileActivity) {
                activity.finish();
            }
        }
    }

    public static HashMap<String, String> generateKeyValuesFromEvent(@NonNull Event
                                                                             event) {
        HashMap<String, String> keyValues = new HashMap<>();
        List<Obs> obs = event.getObs();

        for (Obs observation : obs) {
            String key = observation.getFormSubmissionField();

            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);

                if (!TextUtils.isEmpty(value)) {

                    if (humanReadableValues.size() > 1) {
                        value = humanReadableValues.toString();
                    }

                    keyValues.put(key, value);
                    continue;
                }
            }

            List<Object> values = observation.getValues();
            if (values.size() > 0) {
                String value = (String) values.get(0);

                if (!TextUtils.isEmpty(value)) {

                    if (values.size() > 1) {
                        value = values.toString();
                    }

                    keyValues.put(key, value);
                    continue;
                }
            }

        }

        return keyValues;
    }

    public static JSONArray getMultiStepFormFields(JSONObject jsonForm) {
        JSONArray fields = new JSONArray();
        try {
            if (jsonForm.has(JsonFormConstants.COUNT)) {
                int stepCount = Integer.parseInt(jsonForm.getString(JsonFormConstants.COUNT));
                for (int i = 0; i < stepCount; i++) {
                    String stepName = JsonFormConstants.STEP + (i + 1);
                    JSONObject step = jsonForm.optJSONObject(stepName);
                    if (step != null) {
                        JSONArray stepFields = step.optJSONArray(JsonFormConstants.FIELDS);
                        if (stepFields != null) {
                            for (int k = 0; k < stepFields.length(); k++) {
                                JSONObject field = stepFields.optJSONObject(k);
                                if (field != null) {
                                    fields.put(field);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> getMultiStepFormFields()");
        }
        return fields;
    }

    public static void openAncProfilePage(@NonNull final CommonPersonObjectClient commonPersonObjectClient, @NonNull final Context context) {
        final AppExecutors appExecutors = new AppExecutors();
        appExecutors.diskIO()
                .execute(new Runnable() {

                    @Override
                    public void run() {
                        final Map<String, String> ancUserDetails = PatientRepository.getWomanProfileDetails(commonPersonObjectClient.getCaseId());
                        ancUserDetails.putAll(commonPersonObjectClient.getColumnmaps());
                        appExecutors.mainThread().execute(() -> org.smartregister.anc.library.util.Utils.navigateToProfile(context, (HashMap<String, String>) ancUserDetails));

                    }
                });
    }

    public static void openPncProfile(Context activity, String baseEntityId) {
        final AppExecutors appExecutors = new AppExecutors();
        appExecutors.diskIO().execute(() -> {
            PncMetadata pncMetadata = PncLibrary.getInstance().getPncConfiguration().getPncMetadata();

            GizPncRegisterQueryProvider pncRegisterQueryProvider = new GizPncRegisterQueryProvider();
            String query = pncRegisterQueryProvider.mainSelectWhereIDsIn().replace("%s", "'" + baseEntityId + "'");
            Cursor cursor = PncLibrary.getInstance().getRepository().getReadableDatabase().rawQuery(query, null);
            cursor.moveToFirst();

            CommonPersonObject personinlist = PncLibrary.getInstance().context().commonrepository("ec_client").readAllcommonforCursorAdapter(cursor);
            CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(),
                    personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
            pClient.setColumnmaps(personinlist.getColumnmaps());

            if (pncMetadata != null) {
                Intent intent = new Intent(activity, pncMetadata.getProfileActivity());
                intent.putExtra(PncConstants.IntentKey.CLIENT_OBJECT, pClient);
                activity.startActivity(intent);
            }
        });
    }

    public static void createOpdTransferEvent(String eventType, String baseEntityId,
                                              JSONArray fields) {
        try {
            FormTag formTag = GizJsonFormUtils.getFormTag(Utils.getAllSharedPreferences());

            org.smartregister.clientandeventmodel.Event event = ANCJsonFormUtils.createEvent(fields,
                    new JSONObject(), formTag, baseEntityId, eventType, "");

            GizJsonFormUtils.tagEventSyncMetadata(event);

            saveEvent(event, baseEntityId);

            initiateEventProcessing(Collections.singletonList(event.getFormSubmissionId()));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void saveEvent(@NonNull org.smartregister.clientandeventmodel.Event event, @NonNull String baseEntityId) throws JSONException {
        JSONObject eventJson = new JSONObject(GizJsonFormUtils.gson.toJson(event));
        GizMalawiApplication.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);
    }

    public static void initiateEventProcessing(List<String> formSubmissionIds) throws Exception {
        long lastSyncTimeStamp = Utils.getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        GizMalawiApplication.getInstance().getClientProcessor()
                .processClient(
                        GizMalawiApplication.getInstance()
                                .getEcSyncHelper()
                                .getEvents(formSubmissionIds));

        Utils.getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
    }

}
