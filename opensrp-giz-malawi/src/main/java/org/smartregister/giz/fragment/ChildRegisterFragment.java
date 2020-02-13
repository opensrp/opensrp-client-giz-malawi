package org.smartregister.giz.fragment;

import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;

import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.fragment.BaseChildRegisterFragment;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.giz.activity.ChildImmunizationActivity;
import org.smartregister.giz.activity.ChildRegisterActivity;
import org.smartregister.giz.model.ChildRegisterFragmentModel;
import org.smartregister.giz.presenter.ChildRegisterFragmentPresenter;
import org.smartregister.giz.util.DBQueryHelper;
import org.smartregister.giz.view.NavigationMenu;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;

public class ChildRegisterFragment extends BaseChildRegisterFragment implements CompoundButton.OnCheckedChangeListener {
    private View view;
    private SwitchCompat filterSection;

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new ChildRegisterFragmentPresenter(this, new ChildRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
        //do nothing
    }


    protected void toggleFilterSelection() {
        if (filterSection != null) {
            String tagString = "PRESSED";
            if (filterSection.getTag() == null) {
                filter("", "", filterSelectionCondition(false), false);
                filterSection.setTag(tagString);
                filterSection.setBackgroundResource(R.drawable.transparent_clicked_background);
            } else if (filterSection.getTag().toString().equals(tagString)) {
                filter("", "", "", false);
                filterSection.setTag(null);
                filterSection.setBackgroundResource(R.drawable.transparent_gray_background);
            }
        }
    }

    @Override
    public void setupViews(View view) {
        this.view = view;
        filterSection = view.findViewById(R.id.switch_selection);
        filterSection.setOnCheckedChangeListener(this);
        super.setupViews(view);
    }

    @Override
    public String getMainCondition() {
        return presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);
        RegisterClickables registerClickables = new RegisterClickables();
        if (view.getTag(R.id.record_action) != null) {
            registerClickables.setRecordWeight(
                    Constants.RECORD_ACTION.GROWTH.equals(view.getTag(R.id.record_action)));
            registerClickables.setRecordAll(
                    Constants.RECORD_ACTION.VACCINATION.equals(view.getTag(R.id.record_action)));
        }

        CommonPersonObjectClient client = null;
        if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
            client = (CommonPersonObjectClient) view.getTag();
        }

        switch (view.getId()) {
            case R.id.child_profile_info_layout:
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;
            case R.id.record_growth:
                registerClickables.setRecordWeight(true);
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;
            case R.id.record_vaccination:
                registerClickables.setRecordAll(true);
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;
            case R.id.filter_selection:
                toggleFilterSelection();
                break;
            case R.id.global_search:
                ((ChildRegisterActivity) getActivity()).startAdvancedSearch();
                break;
            case R.id.scan_qr_code:
                ((ChildRegisterActivity) getActivity()).startQrCodeScanner();
                break;
            case R.id.back_button:
                ((ChildRegisterActivity) getActivity()).openDrawer();
                break;
            default:
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        toolbar.setNavigationIcon(R.drawable.ic_action_menu);

        NavigationMenu.getInstance(getActivity(), null, toolbar);
    }

    @Override
    protected String filterSelectionCondition(boolean urgentOnly) {
        return DBQueryHelper.getFilterSelectionCondition(urgentOnly);
    }

    @Override
    public void onClick(View view) {
        onViewClicked(view);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        toggleFilterSelection();
    }
}
