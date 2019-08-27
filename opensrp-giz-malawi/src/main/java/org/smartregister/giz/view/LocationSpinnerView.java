package org.smartregister.giz.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import org.smartregister.CoreLibrary;
import org.smartregister.giz.R;
import org.smartregister.location.helper.LocationHelper;

import java.util.ArrayList;
import java.util.Collections;

public class LocationSpinnerView extends LinearLayout implements AdapterView.OnItemSelectedListener {
    private final Context context;
    private ServiceLocationSpinnerAdapter serviceLocationsAdapter;

    public LocationSpinnerView(Context context) {
        super(context);
        this.context = context;
    }

    public LocationSpinnerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public LocationSpinnerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init(){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.location_spinner_view, this, true);
        String defaultLocation = LocationHelper.getInstance().getDefaultLocation();
        serviceLocationsAdapter = new ServiceLocationSpinnerAdapter(context, getLocations(defaultLocation));
        AppCompatSpinner locationSpinner = view.findViewById(R.id.location_spinner);
        locationSpinner.setOnItemSelectedListener(this);
        locationSpinner.setAdapter(serviceLocationsAdapter);
    }

    private ArrayList<String> getLocations(String defaultLocation) {
        ArrayList<String> locations = LocationHelper.getInstance().locationNamesFromHierarchy(defaultLocation);

        if (locations.contains(defaultLocation)) {
            locations.remove(defaultLocation);
        }
        Collections.sort(locations);
        locations.add(0, defaultLocation);
        return locations;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(serviceLocationsAdapter != null) {
            CoreLibrary.getInstance().context().allSharedPreferences()
                    .saveCurrentLocality(serviceLocationsAdapter.getLocationAt(i));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
