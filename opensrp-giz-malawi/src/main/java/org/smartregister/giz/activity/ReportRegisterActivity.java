package org.smartregister.giz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.smartregister.giz.R;
import org.smartregister.giz.util.GizConstants;

import java.util.ArrayList;

public class ReportRegisterActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_register);

        listView = findViewById(R.id.lv_reportRegister_groupings);

        final ArrayList<ReportGrouping> reportGroupings = getReportGroupings();
        listView.setAdapter(new ArrayAdapter<ReportGrouping>(this, android.R.layout.simple_list_item_1, reportGroupings));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ReportRegisterActivity.this, HIA2ReportsActivity.class);
                intent.putExtra(GizConstants.IntentKey.REPORT_GROUPING, reportGroupings.get(position).grouping);
                startActivity(intent);
            }
        });
    }

    protected ArrayList<ReportGrouping> getReportGroupings() {
        ArrayList<ReportGrouping> groupings = new ArrayList<>();
        groupings.add(new ReportGrouping("Child", "child"));
        groupings.add(new ReportGrouping("ANC", "anc"));
        return groupings;
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
