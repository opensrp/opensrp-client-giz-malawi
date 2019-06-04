package org.smartregister.giz_malawi.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.FetchStatus;
import org.smartregister.giz_malawi.R;
import org.smartregister.giz_malawi.activity.ChildRegisterActivity;
import org.smartregister.giz_malawi.application.GizMalawiApplication;
import org.smartregister.giz_malawi.contract.NavigationContract;
import org.smartregister.giz_malawi.presenter.NavigationPresenter;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NavigationMenu implements NavigationContract.View, SyncStatusBroadcastReceiver.SyncStatusListener {

    private static NavigationMenu instance;
    private static WeakReference<Activity> activityWeakReference;
    private static String[] langArray;
    private static int nfcCardPurgeCount;
    LinearLayout syncMenuItem;
    LinearLayout enrollmentMenuItem;
    LinearLayout outOfAreaMenu;
    LinearLayout registerView;
    private String TAG = NavigationMenu.class.getCanonicalName();
    private TextView loggedInUserTextView;
    private TextView syncTextView;
    private TextView scanCardTextView;
    private TextView userInitialsTextView;
    private TextView logoutButton;
    private NavigationContract.Presenter mPresenter;
    private DrawerLayout drawer;
    private ImageButton cancelButton;
    private Spinner languageSpinner;
    private static final String PURGE_RECORD_STORE = "PURGE STORE";
    private static final String DROP_RECORD_STORE = "DROP STORE";
    private static final String CREATE_RECORD_STORE = "CREATE RECORD STORE";
    private static final String CHECK_STORE_EXISTS = "CHECK STORE EXISTS";
    private static final String CLEAN_CARD = "CLEAN CARD";

    public static NavigationMenu getInstance(Activity activity) {

        nfcCardPurgeCount = 0;

        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(instance);
        int orientation = activity.getResources().getConfiguration().orientation;
        activityWeakReference = new WeakReference<>(activity);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (instance == null) {
                instance = new NavigationMenu();
                langArray = activity.getResources().getStringArray(R.array.languages);
            }
            instance.init(activity);
            SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(instance);
            return instance;
        } else {
            return null;
        }

    }

    private void init(Activity activity) {
        try {
            mPresenter = new NavigationPresenter(this);
            registerDrawer(activity);
            setParentView(activity);
            prepareViews(activity);
            appLogout(activity);
            syncApp(activity);
            enrollment(activity);
            recordOutOfArea(activity);
            attachCloseDrawer();
            goToRegister();
            attachLanguageSpinner(activity);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void prepareViews(final Activity activity) {
        drawer = activity.findViewById(R.id.drawer_layout);
        logoutButton = activity.findViewById(R.id.logout_button);
        syncMenuItem = activity.findViewById(R.id.sync_menu);
        outOfAreaMenu = activity.findViewById(R.id.out_of_area_menu);
        registerView = activity.findViewById(R.id.register_view);
        enrollmentMenuItem = activity.findViewById(R.id.enrollment);
        loggedInUserTextView = activity.findViewById(R.id.logged_in_user_text_view);
        syncTextView = activity.findViewById(R.id.sync_text_view);
        scanCardTextView = activity.findViewById(R.id.scan_card_text_view);
        userInitialsTextView = activity.findViewById(R.id.user_initials_text_view);
        cancelButton = drawer.findViewById(R.id.cancel_button);
        languageSpinner = activity.findViewById(R.id.language_spinner);


        drawer.findViewById(R.id.attribution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nfcCardPurgeCount == 4) {
                    nfcCardPurgeCount = 0;
                }
                nfcCardPurgeCount++;
            }
        });

        mPresenter.refreshLastSync();
    }

    private void registerDrawer(Activity parentActivity) {
        if (drawer != null) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    parentActivity, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

        }
    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        mPresenter.refreshLastSync();
    }

    private void setParentView(Activity activity) {
        ViewGroup current = (ViewGroup) ((ViewGroup) (activity.findViewById(android.R.id.content))).getChildAt(0);
        if (!(current instanceof DrawerLayout)) {
            if (current.getParent() != null) {
                ((ViewGroup) current.getParent()).removeView(current); // <- fix
            }

            LayoutInflater mInflater = LayoutInflater.from(activity);
            ViewGroup contentView = (ViewGroup) mInflater.inflate(R.layout.side_navigation, null);
            activity.setContentView(contentView);

            RelativeLayout rl = activity.findViewById(R.id.navigation_content);

            if (current.getParent() != null) {
                ((ViewGroup) current.getParent()).removeView(current);
            }

            if (current instanceof RelativeLayout) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                current.setLayoutParams(params);
                rl.addView(current);
            } else {
                rl.addView(current);
            }
        }
    }

    private void syncApp(final Activity parentActivity) {
        syncMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.Sync(parentActivity);
            }
        });
    }

    private void enrollment(final Activity parentActivity) {
        enrollmentMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFormActivity(parentActivity,
                        Utils.metadata().childRegister.formName);
            }
        });
    }
    private void recordOutOfArea(final Activity parentActivity) {
        outOfAreaMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFormActivity(parentActivity, "out_of_catchment_service");
            }
        });
    }

    private void attachCloseDrawer() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
    }

    private void appLogout(final Activity parentActivity) {
        mPresenter.displayCurrentUser();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(parentActivity);
            }
        });
    }

    private void goToRegister() {
        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void refreshCurrentUser(String name) {
        if (loggedInUserTextView != null) {
            loggedInUserTextView.setText(name);
        }
        if (userInitialsTextView != null) {
            userInitialsTextView.setText(mPresenter.getLoggedInUserInitials());
        }
    }

    @Override
    public void logout(Activity activity) {
        Toast.makeText(activity.getApplicationContext(), activity.getResources().getText(R.string.action_log_out), Toast.LENGTH_SHORT).show();
        GizMalawiApplication.getInstance().logoutCurrentUser();
    }

    protected void startFormActivity(Activity activity, String formName) {
        try {
            JsonFormUtils.startForm(activity, JsonFormUtils.REQUEST_CODE_GET_JSON, formName, null, null);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    @Override
    public void refreshLastSync(Date lastSync) {
        if (syncTextView != null) {
            String lastSyncTime = getLastSyncTime();
            if (lastSync != null && !TextUtils.isEmpty(lastSyncTime)) {
                lastSyncTime = " " + String.format(activityWeakReference.get().getResources().getString(R.string.last_sync), lastSyncTime);
                syncTextView.setText(String.format(activityWeakReference.get().getResources().getString(R.string.sync_), lastSyncTime));
            }
        }
    }

    private String getLastSyncTime() {
        String lastSync = "";
        long milliseconds = ChildLibrary.getInstance().getEcSyncHelper().getLastCheckTimeStamp();
        if (milliseconds > 0) {
            DateTime lastSyncTime = new DateTime(milliseconds);
            DateTime now = new DateTime(Calendar.getInstance());
            Minutes minutes = Minutes.minutesBetween(lastSyncTime, now);
            if (minutes.getMinutes() < 1) {
                Seconds seconds = Seconds.secondsBetween(lastSyncTime, now);
                lastSync = seconds.getSeconds() + "s";
            } else if (minutes.getMinutes() >= 1 && minutes.getMinutes() < 60) {
                lastSync = minutes.getMinutes() + "m";
            } else if (minutes.getMinutes() >= 60 && minutes.getMinutes() < 1440) {
                Hours hours = Hours.hoursBetween(lastSyncTime, now);
                lastSync = hours.getHours() + "h";
            } else {
                Days days = Days.daysBetween(lastSyncTime, now);
                lastSync = days.getDays() + "d";
            }
        }
        return lastSync;
    }

    public DrawerLayout getDrawer() {
        return drawer;
    }

    private void attachLanguageSpinner(final Activity activity) {

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activityWeakReference.get(),
                R.array.languages, R.layout.language_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        languageSpinner.setAdapter(adapter);
        languageSpinner.setOnItemSelectedListener(null);
        String langPref = org.smartregister.giz_malawi.util.Utils.getLanguage(activity.getApplicationContext());
        for (int i = 0; i < langArray.length; i++) {

            if (langPref != null && langArray[i].toLowerCase().startsWith(langPref)) {
                languageSpinner.setSelection(i);
                break;
            } else {
                languageSpinner.setSelection(2);
            }
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int count = 0;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (count >= 1) {

                    Log.d(TAG, "Selected " + adapter.getItem(i));

                    String lang = adapter.getItem(i).toString().toLowerCase();
                    Locale LOCALE;
                    switch (lang) {
                        case "english":
                            LOCALE = Locale.ENGLISH;
                            break;
                        case "français":
                            LOCALE = Locale.FRENCH;
                            break;
                        case "عربى":
                            LOCALE = new Locale(org.smartregister.giz_malawi.util.Constants.ARABIC_LOCALE);
                            languageSpinner.setSelection(i);
                            break;
                        default:
                            LOCALE = Locale.ENGLISH;
                            break;
                    }

                    org.smartregister.giz_malawi.util.Utils.saveLanguage(activity.getApplicationContext(), LOCALE.getLanguage());

                    // update context as well
                    Locale locale = new Locale(LOCALE.getLanguage());
                    Resources res = activity.getApplicationContext().getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = locale;
                    res.updateConfiguration(conf, dm);
                    ChildLibrary.getInstance().context().updateApplicationContext(activity.getApplicationContext());

                    ChildRegisterActivity act = (ChildRegisterActivity) activity;
                    act.refresh();
                }
                count++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
