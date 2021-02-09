package org.smartregister.giz.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.joda.time.DateTime;
import org.smartregister.giz.R;
import org.smartregister.giz.activity.FragmentBaseActivity;
import org.smartregister.giz.contract.FindReportContract;
import org.smartregister.giz.interactor.FindReportInteractor;
import org.smartregister.giz.model.FilterReportFragmentModel;
import org.smartregister.giz.presenter.FilterReportFragmentPresenter;
import org.smartregister.giz.util.GizConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FilterReportFragment extends Fragment implements FindReportContract.View {
    public static final String TAG = "FilterReportFragment";
    public static final String REPORT_NAME = "REPORT_NAME";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
    private final Calendar myCalendar = Calendar.getInstance();
    private final List<String> communityList = new ArrayList<>();
    protected AutoCompleteTextView autoCompleteTextView;
    protected FindReportContract.Presenter presenter;
    private View view;
    private String titleName;
    private EditText editTextDate;
    private ProgressBar progressBar;
    private LinkedHashMap<String, String> communityIDList = new LinkedHashMap<>();
    private String selectedItem;
    private Integer selectedItemPosition;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filter_report_fragment, container, false);
        bindLayout();
        loadPresenter();
        presenter.initializeViews();

        Bundle bundle = getArguments();
        if (bundle != null) {
            titleName = bundle.getString(FilterReportFragment.REPORT_NAME);
        }
        return view;
    }

    @Override
    public void setLoadingState(boolean loadingState) {
        if (progressBar != null)
            progressBar.setVisibility(loadingState ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void bindLayout() {
        Button buttonSave = view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> runReport());
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        editTextDate = view.findViewById(R.id.editTextDate);
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);

        communityList.add("All COMMUNITIES");

        bindAutoCompleteText();
        bindDatePicker();
        updateLabel();
    }

    @Override
    public void onLocationDataLoaded(Map<String, String> locationData) {
        communityIDList = new LinkedHashMap<>(locationData);
        communityList.addAll(communityIDList.values());
        bindAutoCompleteText();
    }

    private void bindAutoCompleteText() {
        Context context = getContext();
        if (context == null) return;
        ArrayAdapter myAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, communityList);
        autoCompleteTextView.setAdapter(myAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                selectedItem = item.toString();
                selectedItemPosition = position;
            }
        });
    }


    @Override
    public void runReport() {
        if (selectedItem != null) {
            Map<String, String> map = new HashMap<>();
            map.put(GizConstants.ReportParametersHelper.COMMUNITY, selectedItem);
            String communityID = new ArrayList<>(communityIDList.keySet()).get(selectedItemPosition);
            map.put(GizConstants.ReportParametersHelper.COMMUNITY_ID, communityID);
            map.put(GizConstants.ReportParametersHelper.REPORT_DATE, dateFormat.format(myCalendar.getTime()));
            presenter.runReport(map);
        } else {
            Toast.makeText(getActivity(), "No Village selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void bindDatePicker() {
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        editTextDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setSpinnersShown(true);
            dialog.getDatePicker().setMinDate(new Date().getTime());
            dialog.getDatePicker().setMaxDate(new DateTime().plusMonths(6).toDate().getTime());

            dialog.show();
        });
    }

    private void updateLabel() {
        editTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public void loadPresenter() {
        presenter = new FilterReportFragmentPresenter()
                .with(this)
                .withModel(new FilterReportFragmentModel())
                .withInteractor(new FindReportInteractor());
    }

    @Override
    public void startResultsView(Bundle bundle) {
        if (titleName == null) return;

        if (titleName.equalsIgnoreCase(getString(R.string.child_due_report_grouping_title))) {
            FragmentBaseActivity.startMe(getActivity(), EligibleChildrenReportFragment.TAG, getString(R.string.child_due_report_grouping_title), bundle);
        } else if (titleName.equalsIgnoreCase(getString(R.string.doses_needed))) {
            FragmentBaseActivity.startMe(getActivity(), EligibleChildrenReportFragment.TAG, getString(R.string.doses_needed), bundle);
        }
    }
}
