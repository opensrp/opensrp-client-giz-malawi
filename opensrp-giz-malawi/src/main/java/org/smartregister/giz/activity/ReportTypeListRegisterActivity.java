package org.smartregister.giz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.smartregister.domain.FetchStatus;
import org.smartregister.giz.R;
import org.smartregister.giz.model.ReportGroupingModel;

import java.util.ArrayList;

public class ReportTypeListRegisterActivity extends ReportRegisterActivity {

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
}
