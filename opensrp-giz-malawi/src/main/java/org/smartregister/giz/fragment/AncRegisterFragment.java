package org.smartregister.giz.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import org.smartregister.anc.library.fragment.HomeRegisterFragment;
import org.smartregister.giz.R;
import org.smartregister.giz.activity.AncRegisterActivity;
import org.smartregister.giz.presenter.GizAncRegisterFragmentPresenter;
import org.smartregister.giz.util.DBQueryHelper;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-10
 */

public class AncRegisterFragment extends HomeRegisterFragment implements CompoundButton.OnCheckedChangeListener {

    private SwitchCompat filterSection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            ImageView addPatientBtn = view.findViewById(R.id.add_child_image_view);

            if (addPatientBtn != null) {
                addPatientBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startRegistration();
                    }
                });
            }

            ImageView hamburgerMenu = view.findViewById(R.id.left_menu);
            if (hamburgerMenu != null) {
                hamburgerMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof AncRegisterActivity) {
                            ((AncRegisterActivity) getActivity()).openDrawer();
                        }
                    }
                });
            }

            // Disable go-back on clicking the ANC Register title
            view.findViewById(R.id.title_layout).setOnClickListener(null);

            filterSection = view.findViewById(R.id.switch_selection);
            filterSection.setOnCheckedChangeListener(this);
        }
        return view;
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((AncRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new GizAncRegisterFragmentPresenter(this, viewConfigurationIdentifier);
    }


    @Override
    protected String getMainCondition() {
        return "ec_client.date_removed IS NULL ";
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        toggleFilterSelection();
    }

    private void toggleFilterSelection() {
        if (filterSection != null) {
            String tagString = "PRESSED";
            if (filterSection.getTag() == null) {
                filter("", "", DBQueryHelper.ancDueOverdueFilter(true), false);
                filterSection.setTag(tagString);
            } else if (filterSection.getTag().toString().equals(tagString)) {
                filter("", "", getMainCondition(), false);
                filterSection.setTag(null);
            }
        }
    }
}
