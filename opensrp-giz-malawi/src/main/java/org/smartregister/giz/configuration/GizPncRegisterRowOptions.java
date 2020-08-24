package org.smartregister.giz.configuration;

import android.database.Cursor;
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
import org.smartregister.pnc.scheduler.PncVisitScheduler;
import org.smartregister.pnc.scheduler.VisitStatus;
import org.smartregister.pnc.utils.ConfigurationInstancesHelper;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.view.contract.SmartRegisterClient;

public class GizPncRegisterRowOptions implements PncRegisterRowOptions {


    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }


    @Override
    public void populateClientRow(Cursor cursor, CommonPersonObjectClient client, SmartRegisterClient smartRegisterClient, PncRegisterViewHolder pncRegisterViewHolder) {
        Button button = pncRegisterViewHolder.dueButton;
        button.setTag(R.id.BUTTON_TYPE, R.string.start_pnc);
        button.setText(R.string.start_pnc);
        button.setBackgroundResource(R.drawable.pnc_status_btn_bg);
        button.setTextColor(button.getContext().getResources().getColor(R.color.check_in_txt_dark_grey));

        if (client.getColumnmaps().get(PncConstants.JsonFormKeyConstants.PMI_BASE_ENTITY_ID) != null) {

            String deliveryDateStr = client.getColumnmaps().get(PncConstants.FormGlobalConstants.DELIVERY_DATE);
            if (StringUtils.isNotBlank(deliveryDateStr)) {
                LocalDate deliveryDate = LocalDate.parse(deliveryDateStr, DateTimeFormat.forPattern("dd-MM-yyyy"));
                PncVisitScheduler pncVisitScheduler = ConfigurationInstancesHelper.newInstance(PncLibrary.getInstance().getPncConfiguration().getPncVisitScheduler());
                pncVisitScheduler.setDeliveryDate(deliveryDate);
                String strLatestVisitDate = client.getColumnmaps().get(PncDbConstants.Column.PncVisitInfo.LATEST_VISIT_DATE);
                if (strLatestVisitDate != null) {
                    LocalDate latestVisitDate = LocalDate.parse(client.getColumnmaps().get(PncDbConstants.Column.PncVisitInfo.LATEST_VISIT_DATE), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                    pncVisitScheduler.setLatestVisitDateInMills(String.valueOf(latestVisitDate.toDate().getTime()));
                } else {
                    pncVisitScheduler.setLatestVisitDateInMills(null);
                }

                if (pncVisitScheduler.getStatus() == VisitStatus.PNC_DUE) {
                    button.setText(R.string.pnc_due);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_due);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.due_color));
                    button.setBackground(ContextCompat.getDrawable(button.getContext(), R.drawable.pnc_btn_due_bg));
                } else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_OVERDUE) {
                    button.setText(R.string.pnc_due);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_overdue);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.white));
                    button.setBackgroundColor(ContextCompat.getColor(button.getContext(), R.color.overdue_color));
                } else if (pncVisitScheduler.getStatus() == VisitStatus.RECORD_PNC) {
                    button.setText(R.string.record_pnc);
                    button.setTag(R.id.BUTTON_TYPE, R.string.record_pnc);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.due_color));
                    button.setBackground(ContextCompat.getDrawable(button.getContext(), R.drawable.pnc_btn_due_bg));

                } else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_DONE_TODAY) {
                    button.setText(R.string.pnc_done_today);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_done_today);
                    button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.dark_grey));
                    button.setBackground(ContextCompat.getDrawable(button.getContext(), R.drawable.pnc_btn_done_today));
                } else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_CLOSE) {
                    button.setText(R.string.pnc_close);
                    button.setTag(R.id.BUTTON_TYPE, R.string.pnc_close);
                }
            }
        }

        if (client.getColumnmaps().get("ppf_id") != null) {
            String formType = client.getColumnmaps().get("ppf_form_type");
            if ("PNC Medic Information".equals(formType)) {
                if (client.getColumnmaps().get(PncConstants.JsonFormKeyConstants.PMI_BASE_ENTITY_ID) == null)
                    button.setText(R.string.start_pnc);
            }
            button.setBackgroundResource(R.drawable.diagnose_treat_bg);
            button.setTextColor(button.getContext().getResources().getColor(R.color.check_in_txt_dark_grey));
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
