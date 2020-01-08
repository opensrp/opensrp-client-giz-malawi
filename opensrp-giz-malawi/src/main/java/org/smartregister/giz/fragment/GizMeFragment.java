package org.smartregister.giz.fragment;

import android.app.Activity;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.smartregister.anc.library.fragment.MeFragment;
import org.smartregister.giz.R;
import org.smartregister.giz.application.GizMalawiApplication;
import org.smartregister.giz.contract.NavigationMenuContract;
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
        if (view != null) {
            View locationLayout = view.findViewById(R.id.me_location_section);
            facilitySelection = view.findViewById(R.id.facility_selection);
            facilitySelection.setClickable(false);
            locationLayout.setOnClickListener(v -> {
                Activity activity = fragment.getActivity();
                if (activity instanceof NavigationMenuContract) {
                    GizUtils.showLocations(getActivity(), fragment, ((NavigationMenuContract) activity).getNavigationMenu());
                }
            });
        }
    }

    @Override
    public void updateUi(@Nullable String location) {
        if (facilitySelection != null && StringUtils.isNotBlank(location)) {
            facilitySelection.setText(location);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser)
            updateUi(GizMalawiApplication.getInstance().context().allSharedPreferences().fetchCurrentLocality());
    }
}
