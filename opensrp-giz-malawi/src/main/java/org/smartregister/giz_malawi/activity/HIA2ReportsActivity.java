package org.smartregister.giz_malawi.activity;



import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import org.smartregister.giz_malawi.R;
import org.smartregister.giz_malawi.application.GizMalawiApplication;
import org.smartregister.giz_malawi.domain.Hia2Indicator;
import org.smartregister.giz_malawi.domain.MonthlyTally;
import org.smartregister.giz_malawi.fragment.DailyTalliesFragment;
import org.smartregister.giz_malawi.fragment.DraftMonthlyFragment;
import org.smartregister.giz_malawi.fragment.SendMonthlyDraftDialogFragment;
import org.smartregister.giz_malawi.fragment.SentMonthlyFragment;
import org.smartregister.giz_malawi.repository.HIA2IndicatorsRepository;
import org.smartregister.giz_malawi.repository.MonthlyTalliesRepository;
import org.smartregister.giz_malawi.util.GizConstants;
import org.smartregister.giz_malawi.view.NavigationMenu;
import org.smartregister.reporting.service.IndicatorGeneratorIntentService;
import org.smartregister.util.FormUtils;
import org.smartregister.util.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.smartregister.util.JsonFormUtils.KEY;
import static org.smartregister.util.JsonFormUtils.VALUE;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class HIA2ReportsActivity extends BaseActivity {
    private static final String TAG = HIA2ReportsActivity.class.getCanonicalName();
    private static final int REQUEST_CODE_GET_JSON = 3432;
    public static final int MONTH_SUGGESTION_LIMIT = 3;
    private static final String FORM_KEY_CONFIRM = "confirm";
    public static final DateFormat dfyymmdd = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    //private static final List<String> readOnlyList = new ArrayList<>(Arrays.asList(HIA2Service.CHN1_011, HIA2Service.CHN1_021, HIA2Service.CHN1_025, HIA2Service.CHN2_015, HIA2Service.CHN2_030, HIA2Service.CHN2_041, HIA2Service.CHN2_051, HIA2Service.CHN2_061));

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ProgressDialog progressDialog;
    private boolean showFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActionBarDrawerToggle toggle = getDrawerToggle();
        //        toggle.setDrawerIndicatorEnabled(false);
        // toggle.setHomeAsUpIndicator(null);

        //LocationSwitcherToolbar toolbar = (LocationSwitcherToolbar) getToolbar();
        //toolbar.setTitle(getString(R.string.side_nav_hia2));

        tabLayout = findViewById(R.id.tabs);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        //TextView initialsTV = (TextView) findViewById(R.id.name_inits);
        //initialsTV.setText(getLoggedInUserInitials());
	        /*initialsTV.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                openDrawer();
	            }
	        });*/

        // Update Draft Monthly Title
        refreshDraftMonthlyTitle();

        //startService(new Intent(this, IndicatorGeneratorIntentService.class));
    }

    @Override
    public void onSyncStart() {
        super.onSyncStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // TODO: This should go to the base class?
        LinearLayout hia2 = (LinearLayout) drawer.findViewById(R.id.hia2_reports);
        hia2.setBackgroundColor(getResources().getColor(R.color.primary));*/

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

    public void startMonthlyReportForm(String formName, Date date, boolean firstTimeEdit) {
        try {
            Fragment currentFragment = currentFragment();
            if (currentFragment instanceof DraftMonthlyFragment) {
                Utils.startAsyncTask(new StartDraftMonthlyFormTask(this, date, formName, firstTimeEdit), null);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                showFragment = true;
                String jsonString = data.getStringExtra("json");

	                /*final int chunkSize = 4000;
	                for (int i = 0; i < jsonString.length(); i += chunkSize) {
	                    Log.d("JSONSTRING", jsonString.substring(i, Math.min(jsonString.length(), i + chunkSize)));
	                }*/

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
	                /*for (String key : result.keySet()) {
	                    Log.d("TO_SAVE", key + " -> " + result.get(key));
	                }*/
                GizMalawiApplication.getInstance().monthlyTalliesRepository().save(result, month);
                if (saveClicked && !skipValidationSet) {
                    sendReport(month);
                }
            } catch (JSONException e) {
                Log.e(TAG + " " + "JSONException", e.getMessage());
            } catch (ParseException e) {
                Log.e(TAG + "ParseException", e.getMessage());
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        showFragment = false;
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
                    .newInstance(monthString,
                            MonthlyTalliesRepository.DF_DDMMYY.format(Calendar.getInstance().getTime()),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
	                                    /*Intent intent = new Intent(HIA2ReportsActivity.this,
	                                            HIA2IntentService.class);
	                                    intent.putExtra(HIA2IntentService.GENERATE_REPORT, true);
	                                    intent.putExtra(HIA2IntentService.REPORT_MONTH,
	                                            HIA2Service.dfyymm.format(month));
	                                    startService(intent);*/
                                }
                            });
            ft.add(newFragment, "SendMonthlyDraftDialogFragment");
            ft.commitAllowingStateLoss();
        }
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

    private static String retrieveValue(List<MonthlyTally> monthlyTallies, Hia2Indicator hia2Indicator) {
        String defaultValue = "0";
        if (hia2Indicator == null || monthlyTallies == null) {
            return defaultValue;
        }

        for (MonthlyTally monthlyTally : monthlyTallies) {
            if (monthlyTally.getIndicator() != null && monthlyTally.getIndicator().getIndicatorCode()
                    .equalsIgnoreCase(hia2Indicator.getIndicatorCode())) {
                return monthlyTally.getValue();
            }
        }

        return defaultValue;
    }


    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait_message));
    }

    protected void showProgressDialog() {
        if (progressDialog == null) {
            initializeProgressDialog();
        }

        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    public static class StartDraftMonthlyFormTask extends AsyncTask<Void, Void, Intent> {
        private final HIA2ReportsActivity baseActivity;
        private final Date date;
        private final String formName;
        private final boolean firstTimeEdit;

        public StartDraftMonthlyFormTask(HIA2ReportsActivity baseActivity,
                                         Date date, String formName, boolean firstTimeEdit) {
            this.baseActivity = baseActivity;
            this.date = date;
            this.formName = formName;
            this.firstTimeEdit = firstTimeEdit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            baseActivity.showProgressDialog();
        }

        @Override
        protected Intent doInBackground(Void... params) {
            try {
                MonthlyTalliesRepository monthlyTalliesRepository = GizMalawiApplication.getInstance().monthlyTalliesRepository();
                List<MonthlyTally> monthlyTallies = monthlyTalliesRepository.findDrafts(MonthlyTalliesRepository.DF_YYYYMM.format(date));

                HIA2IndicatorsRepository hIA2IndicatorsRepository = GizMalawiApplication.getInstance().hIA2IndicatorsRepository();
                List<Hia2Indicator> hia2Indicators = hIA2IndicatorsRepository.fetchAll();
                if (hia2Indicators == null || hia2Indicators.isEmpty()) {
                    return null;
                }

                JSONObject form = FormUtils.getInstance(baseActivity).getFormJson(formName);
                JSONObject step1 = form.getJSONObject("step1");
                String title = MonthlyTalliesRepository.DF_YYYYMM.format(date).concat(" Draft");
                step1.put(GizConstants.KEY.TITLE, title);

                JSONArray fieldsArray = step1.getJSONArray("fields");

                // This map holds each category as key and all the fields for that category as the
                // value (jsonarray)
                for (Hia2Indicator hia2Indicator : hia2Indicators) {
                    JSONObject jsonObject = new JSONObject();
                    if (hia2Indicator.getDescription() == null) {
                        hia2Indicator.setDescription("");
                    }
                    String label = hia2Indicator.getIndicatorCode() + ": " + hia2Indicator.getDescription() + " *";

                    JSONObject vRequired = new JSONObject();
                    vRequired.put(JsonFormConstants.VALUE, "true");
                    vRequired.put(JsonFormConstants.ERR, "Specify: " + hia2Indicator.getDescription());
                    JSONObject vNumeric = new JSONObject();
                    vNumeric.put(JsonFormConstants.VALUE, "true");
                    vNumeric.put(JsonFormConstants.ERR, "Value should be numeric");

                    jsonObject.put(JsonFormConstants.KEY, hia2Indicator.getIndicatorCode());
                    jsonObject.put(JsonFormConstants.TYPE, "edit_text");
                    //jsonObject.put(JsonFormConstants.READ_ONLY, readOnlyList.contains(hia2Indicator.getIndicatorCode()));
                    jsonObject.put(JsonFormConstants.READ_ONLY, false);
                    jsonObject.put(JsonFormConstants.HINT, label);
                    jsonObject.put(JsonFormConstants.VALUE, retrieveValue(monthlyTallies, hia2Indicator));
	                    /*if (DailyTalliesRepository.IGNORED_INDICATOR_CODES
	                            .contains(hia2Indicator.getIndicatorCode()) && firstTimeEdit) {
	                        jsonObject.put(JsonFormConstants.VALUE, "");
	                    }*/
                    jsonObject.put(JsonFormConstants.V_REQUIRED, vRequired);
                    jsonObject.put(JsonFormConstants.V_NUMERIC, vNumeric);
                    jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "");
                    jsonObject.put(JsonFormConstants.OPENMRS_ENTITY, "");
                    jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, "");
                    jsonObject.put(GizConstants.KEY.HIA_2_INDICATOR, hia2Indicator.getIndicatorCode());

                    fieldsArray.put(jsonObject);
                }

                // Add the confirm button
                JSONObject buttonObject = new JSONObject();
                buttonObject.put(JsonFormConstants.KEY, FORM_KEY_CONFIRM);
                buttonObject.put(JsonFormConstants.VALUE, "false");
                buttonObject.put(JsonFormConstants.TYPE, "button");
                buttonObject.put(JsonFormConstants.HINT, "Confirm");
                buttonObject.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, "");
                buttonObject.put(JsonFormConstants.OPENMRS_ENTITY, "");
                buttonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, "");
                JSONObject action = new JSONObject();
                action.put(JsonFormConstants.BEHAVIOUR, "finish_form");
                buttonObject.put(JsonFormConstants.ACTION, action);

                fieldsArray.put(buttonObject);

                form.put(JsonFormConstants.REPORT_MONTH, dfyymmdd.format(date));
                form.put("identifier", "HIA2ReportForm");

                Intent intent = new Intent(baseActivity, GizJsonFormActivity.class);
                intent.putExtra("json", form.toString());

	                /*final int chunkSize = 4000;
	                String s = form.toString();
	                for (int i = 0; i < s.length(); i += chunkSize) {
	                    Log.d("ORIGINALFORM", s.substring(i, Math.min(s.length(), i + chunkSize)));
	                }*/

                intent.putExtra(JsonFormConstants.SKIP_VALIDATION, false);

                return intent;
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            super.onPostExecute(intent);
            baseActivity.hideProgressDialog();
            if (intent != null) {
                baseActivity.startActivityForResult(intent, REQUEST_CODE_GET_JSON);
            }
        }
    }

    public static class FetchEditedMonthlyTalliesTask extends AsyncTask<Void, Void, List<MonthlyTally>> {
        private final TaskListener taskListener;

        public FetchEditedMonthlyTalliesTask(TaskListener taskListener) {
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return DailyTalliesFragment.newInstance();
                case 1:
                    return DraftMonthlyFragment.newInstance();
                case 2:
                    return SentMonthlyFragment.newInstance();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.hia2_daily_tallies);
                case 1:
                    return getString(R.string.hia2_draft_monthly);
                case 2:
                    return getString(R.string.hia2_sent_monthly);
                default:
                    break;
            }
            return null;
        }
    }

    public void onClickReport(View view) {
        switch (view.getId()) {
            case R.id.btn_back_to_home:

                openDrawer();
                break;
            default:
                break;
        }
    }
}