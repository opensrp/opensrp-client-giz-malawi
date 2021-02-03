package org.smartregister.giz.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseActivity;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.domain.FetchStatus;
import org.smartregister.giz.R;
import org.smartregister.giz.model.ReportGroupingModel;
import org.smartregister.giz.view.NavigationMenu;

import java.util.ArrayList;
import java.util.Map;

public class ReportTypeListRegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = findViewById(R.id.lv_reportRegister_groupings);
        TextView titleTv = findViewById(R.id.title);

        if (titleTv != null) {
            titleTv.setText(R.string.reports_type);
        }


        final ArrayList<ReportGroupingModel.ReportGrouping> reportGroupings = getReportGroupings();
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.report_types_list_item, reportGroupings));
        listView.setOnItemClickListener((parent, view, position, id) -> {

            switch (position) {
                case 0:
                case 1:
                case 2:
                    Intent intent = new Intent(ReportTypeListRegisterActivity.this, ReportRegisterActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }

        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_report_register;
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

    protected ArrayList<ReportGroupingModel.ReportGrouping> getReportGroupings() {
        return (new ReportGroupingModel(this)).getReportListGroupings();
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

    @Override
    public void onUniqueIdFetched(Triple<String, Map<String, String>, String> triple, String s) {
        //do nothing
    }

    @Override
    public void onNoUniqueId() {
        // Nothing to happen here
    }

    @Override
    public void onRegistrationSaved(boolean b) {
        // Nothing to happen here
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        // Nothing to happen here

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        // Nothing to happen here
    }


    @Override
    public void onSyncStart() {
        // Nothing to happen here
    }

    @Override
    protected void onResume() {
        super.onResume();
        createDrawer();
    }

    public void createDrawer() {
        NavigationMenu navigationMenu = NavigationMenu.getInstance(this, null, null);
        if (navigationMenu != null) {
            navigationMenu.getNavigationAdapter().setSelectedView(null);
            navigationMenu.runRegisterCount();
        }
    }

}
