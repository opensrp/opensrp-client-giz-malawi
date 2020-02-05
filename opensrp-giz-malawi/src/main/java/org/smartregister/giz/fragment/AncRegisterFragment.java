package org.smartregister.giz.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.apache.commons.lang3.ArrayUtils;
import org.smartregister.anc.library.fragment.HomeRegisterFragment;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.giz.R;
import org.smartregister.giz.activity.AncRegisterActivity;
import org.smartregister.giz.presenter.GizAncRegisterFragmentPresenter;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-10
 */

public class AncRegisterFragment extends HomeRegisterFragment {

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
        }
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        // Count query
                        if (args != null && args.getBoolean("count_execute")) {
                            countExecute();
                        }
                        String query = "";
                        // Select register query

                        if (ArrayUtils.isNotEmpty(joinTables)) {//clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()))
                            query = "";
                        } else {
                            query = "Select ec_client.id as _id , ec_client.relationalid , ec_client.last_interacted_with , ec_client.base_entity_id , ec_client.first_name , ec_client.last_name , ec_client.register_id , ec_client.dob , ec_mother_details.phone_number , ec_mother_details.alt_name , ec_client.date_removed , ec_mother_details.edd , ec_mother_details.red_flag_count , ec_mother_details.yellow_flag_count , ec_mother_details.contact_status , ec_mother_details.next_contact , ec_mother_details.next_contact_date , ec_mother_details.last_contact_record_date FROM ec_client  join ec_mother_details on ec_client.base_entity_id = ec_mother_details.base_entity_id inner join client_register_type on ec_client.id=client_register_type.base_entity_id WHERE  ec_client.date_removed IS NULL and  " +
                                    "client_register_type.register_type = 'anc' order by ec_client.last_interacted_with DESC LIMIT " + clientAdapter.getCurrentoffset() + "," + clientAdapter.getCurrentlimit() +";";
                        }
                        return commonRepository().rawCustomQueryForAdapter(query);
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }
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
    public void countExecute() {
        String query = "Select count(*) FROM client_register_type where client_register_type.register_type = 'anc' ";
        int count = commonRepository().countSearchIds(query);
        clientAdapter.setTotalcount(count);
    }

    @Override
    protected String getMainCondition() {
        return " ec_client_search." + DBConstantsUtils.KeyUtils.DATE_REMOVED + " IS NULL ";
    }
}
