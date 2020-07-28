package org.smartregister.giz.configuration;

import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.config.PncRegisterRowOptions;
import org.smartregister.pnc.holder.PncRegisterViewHolder;
import org.smartregister.pnc.pojo.PncBaseDetails;
import org.smartregister.pnc.scheduler.PncVisitScheduler;
import org.smartregister.pnc.scheduler.VisitScheduler;
import org.smartregister.pnc.scheduler.VisitStatus;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.HashMap;

public class GizPncRegisterRowOptions implements PncRegisterRowOptions {


    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }


    @Override
    public void populateClientRow(Cursor cursor, CommonPersonObjectClient commonPersonObjectClient, SmartRegisterClient smartRegisterClient, PncRegisterViewHolder pncRegisterViewHolder) {
        Button button = pncRegisterViewHolder.dueButton;
        button.setTag(R.id.BUTTON_TYPE, R.string.start_pnc);
        button.setText(R.string.start_pnc);
        button.setBackgroundResource(R.drawable.pnc_outcome_bg);
        button.setTypeface(null, Typeface.NORMAL);

        PncBaseDetails pncBaseDetails = new PncBaseDetails();
        pncBaseDetails.setBaseEntityId(commonPersonObjectClient.getCaseId());
        pncBaseDetails = PncLibrary.getInstance().getPncRegistrationDetailsRepository().findOne(pncBaseDetails);
        if (pncBaseDetails != null && pncBaseDetails.getProperties() != null) {
            HashMap<String, String> data = pncBaseDetails.getProperties();

            if ("1".equals(data.get(PncConstants.JsonFormKeyConstants.OUTCOME_SUBMITTED))) {

                String deliveryDateStr = data.get(PncConstants.FormGlobalConstants.DELIVERY_DATE);
                if (StringUtils.isNotBlank(deliveryDateStr)) {
                    LocalDate deliveryDate = LocalDate.parse(deliveryDateStr, DateTimeFormat.forPattern("dd-MM-yyyy"));
                    VisitScheduler pncVisitScheduler = new PncVisitScheduler(deliveryDate, commonPersonObjectClient.getCaseId());

                    if (pncVisitScheduler.getStatus() == VisitStatus.PNC_DUE) {
                        button.setText(R.string.pnc_due);
                        button.setTag(R.id.BUTTON_TYPE, R.string.pnc_due);
                        button.setBackground(button.getContext().getResources().getDrawable(org.smartregister.anc.library.R.drawable.contact_due));
                        button.setTextColor(button.getContext().getResources().getColor(R.color.vaccine_blue_bg_st));
                    } else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_OVERDUE) {
                        button.setText(R.string.pnc_due);
                        button.setTag(R.id.BUTTON_TYPE, R.string.pnc_overdue);
                        button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.pnc_circle_red));
                        button.setBackgroundResource(R.drawable.pnc_overdue_bg);
                    } else if (pncVisitScheduler.getStatus() == VisitStatus.RECORD_PNC) {
                        button.setText(R.string.record_pnc);
                        button.setTag(R.id.BUTTON_TYPE, R.string.record_pnc);
                    } else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_DONE_TODAY) {
                        button.setText(R.string.pnc_done_today);
                        button.setTag(R.id.BUTTON_TYPE, R.string.pnc_done_today);
                        button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.dark_grey));
                        button.setBackground(null);
                    } else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_CLOSE) {
                        button.setText(R.string.pnc_close);
                        button.setTag(R.id.BUTTON_TYPE, R.string.pnc_close);
                    }
                }
            }
        }
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
