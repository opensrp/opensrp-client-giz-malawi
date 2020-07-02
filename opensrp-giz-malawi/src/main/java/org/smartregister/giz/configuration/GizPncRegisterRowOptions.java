package org.smartregister.giz.configuration;

import android.database.Cursor;
import android.view.View;
import android.widget.Button;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.config.PncRegisterRowOptions;
import org.smartregister.pnc.holder.PncRegisterViewHolder;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.view.contract.SmartRegisterClient;

public class GizPncRegisterRowOptions implements PncRegisterRowOptions {


    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }


    @Override
    public void populateClientRow(Cursor cursor, CommonPersonObjectClient commonPersonObjectClient, SmartRegisterClient smartRegisterClient, PncRegisterViewHolder pncRegisterViewHolder) {
        Button dueButton = pncRegisterViewHolder.dueButton;
        PncUtils.setVisitButtonStatus(dueButton, commonPersonObjectClient.getCaseId());
    }

    @Override
    public boolean isCustomViewHolder() {
        return false;
    }


    @Override
    public PncRegisterViewHolder createCustomViewHolder(View view) {
        return null;
    }

    @Override
    public boolean useCustomViewLayout() {
        return false;
    }

    @Override
    public int getCustomViewLayoutId() {
        return 0;
    }
}
