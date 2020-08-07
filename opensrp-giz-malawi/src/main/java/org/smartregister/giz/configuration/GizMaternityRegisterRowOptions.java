package org.smartregister.giz.configuration;

import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.maternity.configuration.MaternityRegisterRowOptions;
import org.smartregister.maternity.holders.MaternityRegisterViewHolder;
import org.smartregister.view.contract.SmartRegisterClient;

public class GizMaternityRegisterRowOptions implements MaternityRegisterRowOptions {
    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }

    @Override
    public void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull MaternityRegisterViewHolder maternityRegisterViewHolder) {
        Button dueButton = maternityRegisterViewHolder.dueButton;
        dueButton.setTypeface(null, Typeface.NORMAL);
        if (commonPersonObjectClient.getColumnmaps().get("mof_id") != null) {
            dueButton.setText(R.string.outcome);
            dueButton.setBackgroundResource(R.drawable.diagnose_treat_bg);
            dueButton.setTextColor(dueButton.getContext().getResources().getColor(R.color.check_in_txt_dark_grey));
        } else if (commonPersonObjectClient.getColumnmaps().get("mmi_base_entity_id") == null) {
            dueButton.setText(R.string.start_maternity);
            dueButton.setBackgroundResource(R.drawable.opd_register_check_in_bg);
            dueButton.setTextColor(dueButton.getContext().getResources().getColor(R.color.check_in_txt_dark_grey));
        } else {
            dueButton.setText(R.string.outcome);
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), org.smartregister.pnc.R.color.due_color));
            dueButton.setBackground(ContextCompat.getDrawable(dueButton.getContext(), org.smartregister.pnc.R.drawable.pnc_btn_due_bg));
        }
    }

    @Override
    public boolean isCustomViewHolder() {
        return false;
    }

    @Nullable
    @Override
    public MaternityRegisterViewHolder createCustomViewHolder(@NonNull View itemView) {
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
