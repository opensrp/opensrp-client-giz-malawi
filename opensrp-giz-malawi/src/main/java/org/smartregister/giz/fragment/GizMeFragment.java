package org.smartregister.giz.fragment;

import android.app.Activity;
import android.view.View;

import org.smartregister.anc.library.fragment.MeFragment;
import org.smartregister.giz.R;
import org.smartregister.giz.activity.AncRegisterActivity;
import org.smartregister.giz.activity.ChildRegisterActivity;
import org.smartregister.giz.activity.OpdRegisterActivity;
import org.smartregister.giz.listener.OnLocationChangeListener;
import org.smartregister.giz.util.GizUtils;
import org.smartregister.view.LocationPickerView;

public class GizMeFragment extends MeFragment implements OnLocationChangeListener {

    private View view;

    private GizMeFragment fragment;

    private LocationPickerView facilitySelection;

    @Override
    protected void setUpViews(View view) {
        this.fragment = this;
        super.setUpViews(view);
        this.view = view;

    }

    @Override
    protected void setClickListeners() {
        super.setClickListeners();
        if(view != null) {
            View locationLayout = view.findViewById(R.id.me_location_section);
            facilitySelection = view.findViewById(R.id.facility_selection);
            facilitySelection.setClickable(false);
            locationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = fragment.getActivity();
                    if(activity instanceof AncRegisterActivity) {
                        GizUtils.showLocations(getActivity(), fragment, ((AncRegisterActivity)fragment.getActivity()).getNavigationMenu());
                    } else if (activity instanceof OpdRegisterActivity) {
                        GizUtils.showLocations(getActivity(), fragment, ((OpdRegisterActivity) fragment.getActivity()).getNavigationMenu());
                    } else if (activity instanceof ChildRegisterActivity) {
                        GizUtils.showLocations(getActivity(), fragment, ((ChildRegisterActivity) fragment.getActivity()).getNavigationMenu());
                    }
                }
            });
        }
    }

    @Override
    public void updateTextView(String location) {
        if(facilitySelection != null){
            facilitySelection.setText(location);
        }
    }
}
