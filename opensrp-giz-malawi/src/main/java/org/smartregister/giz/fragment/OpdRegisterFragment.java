package org.smartregister.giz.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.giz.activity.OpdRegisterActivity;
import org.smartregister.giz.view.NavDrawerActivity;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.fragment.BaseOpdRegisterFragment;
import org.smartregister.opd.pojos.OpdMetadata;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-17
 */

public class OpdRegisterFragment extends BaseOpdRegisterFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            SwitchCompat switchSelection = view.findViewById(R.id.switch_selection);
            if (switchSelection != null) {
                switchSelection.setText(getDueOnlyText());
                switchSelection.setOnClickListener(registerActionHandler);
            }

            View topLeftLayout = view.findViewById(org.smartregister.opd.R.id.top_left_layout);
            topLeftLayout.setVisibility(View.VISIBLE);

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
                        if (getActivity() instanceof NavDrawerActivity) {
                            ((NavDrawerActivity) getActivity()).openDrawer();
                        }
                    }
                });
            }

            // Disable go-back on clicking the OPD Register title
            view.findViewById(R.id.title_layout).setOnClickListener(null);
        }

        return view;
    }

    @Override
    protected void startRegistration() {
        OpdRegisterActivity opdRegisterActivity = (OpdRegisterActivity) getActivity();
        OpdMetadata opdMetadata = OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();

        if (opdMetadata != null && opdRegisterActivity != null) {
            opdRegisterActivity.startFormActivity(opdMetadata.getOpdRegistrationFormName()
                    , null
                    , null);
        }
    }

    @Override
    protected void performPatientAction(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> clientColumnMaps = commonPersonObjectClient.getColumnmaps();

        OpdRegisterActivity opdRegisterActivity = (OpdRegisterActivity) getActivity();
        if (opdRegisterActivity != null && clientColumnMaps.containsKey(OpdDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT)) {
            HashMap<String, String> injectedValues = new HashMap<String, String>();
            injectedValues.put("patient_gender", clientColumnMaps.get("gender"));

            String diagnoseSchedule = clientColumnMaps.get(OpdDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT);
            String entityTable = clientColumnMaps.get(OpdConstants.IntentKey.ENTITY_TABLE);

            boolean isDiagnoseScheduled = !TextUtils.isEmpty(diagnoseSchedule) && "1".equals(diagnoseSchedule);

            String strVisitEndDate = clientColumnMaps.get(OpdDbConstants.Column.OpdDetails.CURRENT_VISIT_END_DATE);

            if (strVisitEndDate != null && OpdLibrary.getInstance().isPatientInTreatedState(strVisitEndDate)) {
                return;
            }

            if (!isDiagnoseScheduled) {
                opdRegisterActivity.startFormActivity(OpdConstants.Form.OPD_CHECK_IN, commonPersonObjectClient.getCaseId(), null, injectedValues, entityTable);
            } else {
                opdRegisterActivity.startFormActivity(OpdConstants.Form.OPD_DIAGNOSIS_AND_TREAT, commonPersonObjectClient.getCaseId(), null, injectedValues, entityTable);
            }
        }
    }

    @Override
    protected void goToClientDetailActivity(@NonNull final CommonPersonObjectClient commonPersonObjectClient) {
        final Context context = getActivity();
        OpdMetadata opdMetadata = OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();

        if (context != null && opdMetadata != null) {
            Intent intent = new Intent(getActivity(), opdMetadata.getProfileActivity());
            intent.putExtra(OpdConstants.IntentKey.CLIENT_OBJECT, commonPersonObjectClient);
            startActivity(intent);
        }
    }

    @Override
    protected String getDefaultSortQuery() {
        return "";
    }

    @Override
    protected void onViewClicked(View view) {
        super.onViewClicked(view);

        if (view.getId() == R.id.switch_selection) {
            toggleFilterSelection(view);
        }
    }

    @NonNull
    @Override
    public String getDueOnlyText() {
        return getString(R.string.checked_in_only);
    }

}