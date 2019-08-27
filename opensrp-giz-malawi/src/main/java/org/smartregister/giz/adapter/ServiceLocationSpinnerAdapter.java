package org.smartregister.giz.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        hideCheckBox(view);
        TextView text1 = view.findViewById(android.R.id.text1);
        text1.setBackgroundColor(Color.WHITE);
        text1.setTextColor(Color.BLACK);
        text1.setText(LocationHelper.getInstance().getOpenMrsReadableName(getLocationNames().get(position)));
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(org.smartregister.R.layout.location_picker_dropdown_item, null);
        hideCheckBox(view);
        view.setId(position + 2321);
        view.setBackgroundColor(Color.BLACK);

        TextView text1 = view.findViewById(android.R.id.text1);
        text1.setTextColor(Color.WHITE);
        text1.setText(LocationHelper.getInstance().getOpenMrsReadableName(getLocationNames().get(position)));
        return view;
    }

    private void hideCheckBox(View view){
        ImageView checkbox = view.findViewById(R.id.checkbox);
        checkbox.setVisibility(View.GONE);
    }
}
