package org.smartregister.opd.holders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.smartregister.opd.R;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewParentName;
    public TextView textViewChildName;
    public TextView textViewAddressGender;
    public Button dueButton;
    public View dueButtonLayout;
    public View childColumn;
    private TextView tvRegisterType;
    private TextView tvLocation;

    public OpdRegisterViewHolder(View itemView) {
        super(itemView);

        textViewParentName = itemView.findViewById(R.id.tv_opdRegisterListRow_parentName);
        textViewChildName = itemView.findViewById(R.id.tv_opdRegisterListRow_childName);
        textViewAddressGender = itemView.findViewById(R.id.tv_opdRegisterListRow_gender);
        dueButton = itemView.findViewById(R.id.btn_opdRegisterListRow_clientAction);
        dueButtonLayout = itemView.findViewById(R.id.ll_opdRegisterListRow_clientActionWrapper);
        tvRegisterType = itemView.findViewById(R.id.tv_opdRegisterListRow_registerType);
        tvLocation = itemView.findViewById(R.id.tv_opdRegisterListRow_location);

        childColumn = itemView.findViewById(R.id.child_column);
    }

    public void showCareGiveName() {
        textViewParentName.setVisibility(View.VISIBLE);
    }
}