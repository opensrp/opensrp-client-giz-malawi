package org.smartregister.giz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.CoreLibrary;
import org.smartregister.adapter.ServiceLocationsAdapter;
import org.smartregister.giz.R;
import org.smartregister.location.helper.LocationHelper;

import java.util.ArrayList;

public class ServiceLocationSpinnerAdapter extends ServiceLocationsAdapter {
    private Context context;

    public ServiceLocationSpinnerAdapter(Context context, ArrayList<String> locationNames) {
        super(context, locationNames);
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(org.smartregister.R.layout.location_picker_dropdown_item, null);
        view.setId(position + 2321);
        view.setBackgroundColor(Color.WHITE);
        String location = LocationHelper.getInstance().getOpenMrsReadableName(getLocationNames().get(position));
        TextView text1 = view.findViewById(android.R.id.text1);
        text1.setBackgroundColor(Color.WHITE);
        text1.setTextColor(Color.BLACK);
        text1.setText(location);

        refreshView(view, getLocationNames().get(position).contains(getSelectedItem()));
        return view;
    }



    public String getSelectedItem() {
        String selectedLocation = CoreLibrary.getInstance().context().allSharedPreferences().fetchCurrentLocality();
        if (TextUtils.isEmpty(selectedLocation) || !getLocationNames().contains(selectedLocation)) {
            selectedLocation = LocationHelper.getInstance().getDefaultLocation();
            CoreLibrary.getInstance().context().allSharedPreferences().saveCurrentLocality(selectedLocation);
        }
        return selectedLocation;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(org.smartregister.R.layout.location_picker_dropdown_item, null);
//        hideCheckBox(view);
        view.setId(position + 2321);
        view.setBackgroundColor(Color.BLACK);
        TextView text1 = view.findViewById(android.R.id.text1);
        text1.setTextColor(Color.WHITE);
        text1.setText(LocationHelper.getInstance().getOpenMrsReadableName(getLocationNames().get(position)));
        return view;
    }

    private void refreshView(View view, boolean selected) {
        if (selected) {
            ImageView checkbox = view.findViewById(R.id.checkbox);
            checkbox.setVisibility(View.VISIBLE);
        } else {
            ImageView checkbox = view.findViewById(R.id.checkbox);
            checkbox.setVisibility(View.INVISIBLE);
        }
    }

    private void hideCheckBox(View view){
        ImageView checkbox = view.findViewById(R.id.checkbox);
        checkbox.setVisibility(View.GONE);
    }
}
