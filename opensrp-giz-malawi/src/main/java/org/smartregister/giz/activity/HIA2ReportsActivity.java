package org.smartregister.giz.activity;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.activity.BaseActivity;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.giz.R;
import org.smartregister.giz.adapter.ReportsSectionsPagerAdapter;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.domain.MonthlyTally;
import org.smartregister.giz.domain.ReportHia2Indicator;
import org.smartregister.giz.fragment.DraftMonthlyFragment;
import org.smartregister.giz.repository.MonthlyTalliesRepository;
import org.smartregister.giz.task.FetchEditedMonthlyTalliesTask;
import org.smartregister.giz.task.StartDraftMonthlyFormTask;
import org.smartregister.giz.util.AppExecutors;
import org.smartregister.giz.util.GizReportUtils;
import org.smartregister.giz.view.NavigationMenu;
import org.smartregister.giz.fragment.SendMonthlyDraftDialogFragment;
import org.smartregister.repository.Hia2ReportRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.Utils;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.KEY;
import static org.smartregister.util.JsonFormUtils.VALUE;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class HIA2ReportsActivity extends BaseActivity {

    public static final int REQUEST_CODE_GET_JSON = 3432;
    public static final int MONTH_SUGGESTION_LIMIT = 3;
    public static final String FORM_KEY_CONFIRM = "confirm";
    public static final DateFormat dfyymmdd = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static final DateFormat yyyMMdd_T_HHmmss_SSSZZZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH);

    public static final String REPORT_NAME = "HIA2";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ReportsSectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabLayout = findViewById(R.id.tabs);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ReportsSectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        // Update Draft Monthly Title
        refreshDraftMonthlyTitle();
    }

    @Override
    public void onSyncStart() {
        super.onSyncStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        openDrawer();
    }

    public void openDrawer() {
        NavigationMenu navigationMenu = NavigationMenu.getInstance(this, null, null);
        navigationMenu.runRegisterCount();
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        super.onSyncComplete(fetchStatus);
    }

    private Fragment currentFragment() {
        if (mViewPager == null || mSectionsPagerAdapter == null) {
            return null;
        }

        return mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    public void startMonthlyReportForm(@NonNull String formName, @NonNull Date date) {
        Fragment currentFragment = currentFragment();
        if (currentFragment instanceof DraftMonthlyFragment) {
            Utils.startAsyncTask(new StartDraftMonthlyFormTask(this, date, formName), null);
        }
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        // This method is useless -> Do nothing
    }

    @Override
    public void onNoUniqueId() {
        // This method is useless -> Do nothing
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        // This method is useless -> Do nothing
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra("json");

                boolean skipValidationSet = data.getBooleanExtra(JsonFormConstants.SKIP_VALIDATION, false);
                JSONObject form = new JSONObject(jsonString);
                String monthString = form.getString("report_month");
                Date month = dfyymmdd.parse(monthString);

                JSONObject monthlyDraftForm = new JSONObject(jsonString);

                //Map<String, String> result = JsonFormUtils.sectionFields(monthlyDraftForm);
                JSONArray fieldsArray = JsonFormUtils.fields(monthlyDraftForm);

                Map<String, String> result = new HashMap<>();
                for (int j = 0; j < fieldsArray.length(); j++) {
                    JSONObject fieldJsonObject = fieldsArray.getJSONObject(j);
                    String key = fieldJsonObject.getString(KEY);
                    String value = fieldJsonObject.getString(VALUE);
                    result.put(key, value);
                }

                boolean saveClicked;
                if (result.containsKey(FORM_KEY_CONFIRM)) {
                    saveClicked = Boolean.valueOf(result.get(FORM_KEY_CONFIRM));
                    result.remove(FORM_KEY_CONFIRM);
                    if (skipValidationSet) {
                        Snackbar.make(tabLayout, R.string.all_changes_saved, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    saveClicked = true;
                }
                GizMalawiApplication.getInstance().monthlyTalliesRepository().save(result, month);
                if (saveClicked && !skipValidationSet) {
                    sendReport(month);
                }
            } catch (JSONException e) {
                Timber.e(e);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }
    }

    private void sendReport(final Date month) {
        if (month != null) {
            FragmentTransaction ft = getFragmentManager()
                    .beginTransaction();
            android.app.Fragment prev = getFragmentManager()
                    .findFragmentByTag("SendMonthlyDraftDialogFragment");
            if (prev != null) {
                ft.remove(prev);
            }

            String monthString = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(month);
            // Create and show the dialog.
            SendMonthlyDraftDialogFragment newFragment = SendMonthlyDraftDialogFragment
                    .newInstance(
                            monthString,
                            MonthlyTalliesRepository.DF_DDMMYY.format(Calendar.getInstance().getTime()),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    generateAndSendMonthlyReport(month);
                                }
                            });
            ft.add(newFragment, "SendMonthlyDraftDialogFragment");
            ft.commitAllowingStateLoss();
        }
    }

    private void generateAndSendMonthlyReport(@NonNull Date month) {
        showProgressDialog();

        AppExecutors appExecutors = new AppExecutors();
        appExecutors.networkIO()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        generateAndSaveMonthlyReport(month);

                        // push report to server
                        pushUnsentReportsToServer();

                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressDialog();
                            }
                        });
                    }
                });

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_hia2_reports;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer_layout;
    }

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    protected Class onBackActivity() {
        return null;
    }

    public void refreshDraftMonthlyTitle() {
        Utils.startAsyncTask(new FetchEditedMonthlyTalliesTask(new FetchEditedMonthlyTalliesTask.TaskListener() {
            @Override
            public void onPostExecute(final List<MonthlyTally> monthlyTallies) {
                tabLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                            TabLayout.Tab tab = tabLayout.getTabAt(i);
                            if (tab != null && tab.getText() != null && tab.getText().toString()
                                    .contains(getString(R.string.hia2_draft_monthly))) {
                                tab.setText(String.format(
                                        getString(R.string.hia2_draft_monthly_with_count),
                                        monthlyTallies == null ? 0 : monthlyTallies.size()));
                            }
                        }
                    }
                });
            }
        }), null);
    }


    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait_message));
    }

    @Override
    public void showProgressDialog() {
        if (progressDialog == null) {
            initializeProgressDialog();
        }

        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void onClickReport(View view) {
        switch (view.getId()) {
            case R.id.btn_back_to_home:

                NavigationMenu navigationMenu = NavigationMenu.getInstance(this, null, null);
                if (navigationMenu != null) {
                    navigationMenu.getDrawer()
                            .openDrawer(GravityCompat.START);
                }
                break;
            default:
                break;
        }
    }

    private void generateAndSaveMonthlyReport(@Nullable Date month) {
        MonthlyTalliesRepository monthlyTalliesRepository = GizMalawiApplication.getInstance().monthlyTalliesRepository();
        try {
            if (month != null) {
                List<MonthlyTally> tallies = monthlyTalliesRepository
                        .find(MonthlyTalliesRepository.DF_YYYYMM.format(month));
                if (tallies != null) {
                    List<ReportHia2Indicator> reportHia2Indicators = new ArrayList<>();
                    for (MonthlyTally curTally : tallies) {
                        String createdAtString = yyyMMdd_T_HHmmss_SSSZZZ.format(curTally.getCreatedAt() != null ? curTally.getCreatedAt() : curTally.getUpdatedAt());
                        String updatedAtString = yyyMMdd_T_HHmmss_SSSZZZ.format(curTally.getUpdatedAt());

                        ReportHia2Indicator reportHia2Indicator = new ReportHia2Indicator(curTally.getIndicator()
                                , curTally.getIndicator()
                                , curTally.getIndicator()
                                , curTally.getIndicator()
                                , "Immunization"
                                , curTally.getValue(), curTally.getProviderId(), createdAtString, updatedAtString);

                        reportHia2Indicators.add(reportHia2Indicator);
                    }

                    GizReportUtils.createReportAndSaveReport(reportHia2Indicators, month, REPORT_NAME);

                    for (MonthlyTally curTally : tallies) {
                        curTally.setDateSent(Calendar.getInstance().getTime());
                        monthlyTalliesRepository.save(curTally);
                    }
                } else {
                    Timber.d("Tallies month is null");
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void pushUnsentReportsToServer() {
        final String REPORTS_SYNC_PATH = "/rest/report/add";
        final Context context = GizMalawiApplication.getInstance().context().applicationContext();
        HTTPAgent httpAgent = GizMalawiApplication.getInstance().context().getHttpAgent();
        Hia2ReportRepository hia2ReportRepository = GizMalawiApplication.getInstance().hia2ReportRepository();

        try {
            boolean keepSyncing = true;
            int limit = 50;
            while (keepSyncing) {
                List<JSONObject> pendingReports = hia2ReportRepository.getUnSyncedReports(limit);

                if (pendingReports.isEmpty()) {
                    return;
                }

                String baseUrl = GizMalawiApplication.getInstance().context().configuration().dristhiBaseURL();
                if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
                }
                // create request body
                JSONObject request = new JSONObject();

                request.put("reports", pendingReports);
                String jsonPayload = request.toString();
                Response<String> response = httpAgent.post(
                        MessageFormat.format("{0}/{1}",
                                baseUrl,
                                REPORTS_SYNC_PATH),
                        jsonPayload);

                if (response.isFailure()) {
                    Timber.e("Sending DHIS2 Report failed");
                    return;
                }

                hia2ReportRepository.markReportsAsSynced(pendingReports);
                Timber.i("Reports synced successfully.");

                // update drafts view
                refreshDraftMonthlyTitle();
                org.smartregister.child.util.Utils.startAsyncTask(new FetchEditedMonthlyTalliesTask(new FetchEditedMonthlyTalliesTask.TaskListener() {
                    @Override
                    public void onPostExecute(List<MonthlyTally> monthlyTallies) {
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
                        if (fragment != null) {
                            ((DraftMonthlyFragment) fragment).updateDraftsReportListView(monthlyTallies);
                        }
                    }
                }), null);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}