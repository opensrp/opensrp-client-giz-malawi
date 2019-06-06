package org.smartregister.giz_malawi.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import org.smartregister.giz_malawi.application.GizMalawiApplication;
import org.smartregister.giz_malawi.contract.NavigationContract;
import org.smartregister.giz_malawi.presenter.NavigationPresenter;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

public class NavigationMenu implements NavigationContract.View, SyncStatusBroadcastReceiver.SyncStatusListener {

    private static NavigationMenu instance;
    private static WeakReference<Activity> activityWeakReference;
    private static int nfcCardPurgeCount;
    private LinearLayout syncMenuItem;
    private LinearLayout enrollmentMenuItem;
    private LinearLayout outOfAreaMenu;
    private TextView loggedInUserTextView;
    private TextView syncTextView;
    private TextView userInitialsTextView;
    private TextView logoutButton;
    private NavigationContract.Presenter mPresenter;
    private DrawerLayout drawer;
    private ImageButton cancelButton;

    public static NavigationMenu getInstance(Activity activity) {
        nfcCardPurgeCount = 0;

        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(instance);
        int orientation = activity.getResources().getConfiguration().orientation;
        activityWeakReference = new WeakReference<>(activity);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (instance == null) {
                instance = new NavigationMenu();
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

        } catch (Exception e) {
            Timber.e(e.toString());
        }
    }

    @Override
    public void prepareViews(final Activity activity) {
        drawer = activity.findViewById(R.id.drawer_layout);
        logoutButton = activity.findViewById(R.id.logout_button);
        syncMenuItem = activity.findViewById(R.id.sync_menu);
        outOfAreaMenu = activity.findViewById(R.id.out_of_area_menu);
        enrollmentMenuItem = activity.findViewById(R.id.enrollment);
        loggedInUserTextView = activity.findViewById(R.id.logged_in_user_text_view);
        syncTextView = activity.findViewById(R.id.sync_text_view);
        userInitialsTextView = activity.findViewById(R.id.user_initials_text_view);
        cancelButton = drawer.findViewById(R.id.cancel_button);
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
        // Todo
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        // Todo
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
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
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
                mPresenter.sync(parentActivity);
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
        Toast.makeText(activity.getApplicationContext(), activity.getResources().getText(R.string.action_log_out),
                Toast.LENGTH_SHORT).show();
        GizMalawiApplication.getInstance().logoutCurrentUser();
    }

    protected void startFormActivity(Activity activity, String formName) {
        try {
            JsonFormUtils.startForm(activity, JsonFormUtils.REQUEST_CODE_GET_JSON, formName, null, null);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    @Override
    public void refreshLastSync(Date lastSync) {
        if (syncTextView != null) {
            String lastSyncTime = getLastSyncTime();
            if (lastSync != null && !TextUtils.isEmpty(lastSyncTime)) {
                lastSyncTime = " " + String
                        .format(activityWeakReference.get().getResources().getString(R.string.last_sync), lastSyncTime);
                syncTextView.setText(
                        String.format(activityWeakReference.get().getResources().getString(R.string.sync_), lastSyncTime));
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
}
