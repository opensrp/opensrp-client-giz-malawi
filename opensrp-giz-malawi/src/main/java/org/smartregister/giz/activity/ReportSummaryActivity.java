package org.smartregister.giz.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseActivity;
import org.smartregister.child.toolbar.SimpleToolbar;
import org.smartregister.giz.R;
import org.smartregister.giz.util.AppExecutors;
import org.smartregister.giz.view.IndicatorCategoryView;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

public class ReportSummaryActivity extends BaseActivity {

    public static final String EXTRA_TALLIES = "tallies";
    public static final String EXTRA_DAY = "tally_day";
    public static final String EXTRA_SUB_TITLE = "sub_title";
    public static final String EXTRA_TITLE = "title";
    private LinkedHashMap<String, ArrayList<IndicatorTally>> tallies;
    private String subTitle;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SimpleToolbar toolbar = (SimpleToolbar) getToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.smart_register_blue)));

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable tallyDaySerializable = extras.getSerializable(EXTRA_DAY);
            if (tallyDaySerializable instanceof Date) {
                fetchIndicatorTalliesForDay((Date) tallyDaySerializable);
            }

            Serializable submittedBySerializable = extras.getSerializable(EXTRA_SUB_TITLE);
            if (submittedBySerializable instanceof String) {
                subTitle = (String) submittedBySerializable;
            }

            Serializable titleSerializable = extras.getSerializable(EXTRA_TITLE);
            if (titleSerializable instanceof String) {
                toolbar.setTitle((String) titleSerializable);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomFontTextView submittedBy = findViewById(R.id.submitted_by);
        if (!TextUtils.isEmpty(this.subTitle)) {
            submittedBy.setVisibility(View.VISIBLE);
            submittedBy.setText(this.subTitle);
        } else {
            submittedBy.setVisibility(View.GONE);
        }
        refreshIndicatorViews();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_report_summary;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer_layout;
    }

    @Override
    protected int getToolbarId() {
        return SimpleToolbar.TOOLBAR_ID;
    }

    @Override
    protected Class onBackActivity() {
        return null;
    }

    public void setTallies(ArrayList<IndicatorTally> tallies) {
        setTallies(tallies, true);
    }

    private void setTallies(@NonNull ArrayList<IndicatorTally> tallies, boolean refreshViews) {
        this.tallies = new LinkedHashMap<>();
        this.tallies.put("main", new ArrayList<>());

        Collections.sort(tallies, new Comparator<IndicatorTally>() {
            @Override
            public int compare(IndicatorTally lhs, IndicatorTally rhs) {
                return lhs.getIndicatorCode().compareTo(rhs.getIndicatorCode());
            }
        });

        for (IndicatorTally curTally : tallies) {
            if (curTally != null && !TextUtils.isEmpty(curTally.getIndicatorCode())) {
                this.tallies.get("main").add(curTally);
            }
        }

        if (refreshViews) refreshIndicatorViews();
    }

    private void refreshIndicatorViews() {
        LinearLayout indicatorCanvas = findViewById(R.id.indicator_canvas);
        indicatorCanvas.removeAllViews();

        if (tallies != null) {
            boolean firstExpanded = false;
            for (String curCategoryName : tallies.keySet()) {
                IndicatorCategoryView curCategoryView = new IndicatorCategoryView(this);
                curCategoryView.setTallies(curCategoryName, tallies.get(curCategoryName));
                indicatorCanvas.addView(curCategoryView);

                if (!firstExpanded) {
                    firstExpanded = true;
                    curCategoryView.setExpanded(true);
                }
            }
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

    public void fetchIndicatorTalliesForDay(@NonNull final Date date) {
        AppExecutors appExecutors = new AppExecutors();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<IndicatorTally> indicatorTallies = ReportingLibrary.getInstance()
                        .dailyIndicatorCountRepository()
                        .getIndicatorTalliesForDay(date);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        setTallies(indicatorTallies, true);
                    }
                });
            }
        });
    }
}
