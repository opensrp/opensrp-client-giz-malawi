package org.smartregister.giz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseActivity;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.giz.R;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.view.NavigationMenu;

import java.util.ArrayList;

public class ReportRegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = findViewById(R.id.lv_reportRegister_groupings);
        TextView titleTv = findViewById(R.id.title);

        if (titleTv != null) {
            titleTv.setText(R.string.dhis2_reports);
        }

        final ArrayList<ReportGrouping> reportGroupings = getReportGroupings();
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.report_grouping_list_item, reportGroupings));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ReportRegisterActivity.this, HIA2ReportsActivity.class);
            intent.putExtra(GizConstants.IntentKey.REPORT_GROUPING, reportGroupings.get(position).grouping);
            startActivity(intent);
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

    protected ArrayList<ReportGrouping> getReportGroupings() {
        ArrayList<ReportGrouping> groupings = new ArrayList<>();
        groupings.add(new ReportGrouping("Child", "child"));
        groupings.add(new ReportGrouping("ANC", "anc"));
        return groupings;
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
    public void onUniqueIdFetched(Triple<String, String, String> triple, String s) {

    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public void onRegistrationSaved(boolean b) {

    }

    public static class ReportGrouping {

        private String displayName;
        private String grouping;

        public ReportGrouping(@Nullable String displayName, @Nullable String grouping) {
            this.displayName = displayName;
            this.grouping = grouping;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
