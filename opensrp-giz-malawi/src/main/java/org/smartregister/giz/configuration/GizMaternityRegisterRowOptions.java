package org.smartregister.giz.configuration;

import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.giz.R;
import org.smartregister.maternity.configuration.MaternityRegisterRowOptions;
import org.smartregister.maternity.holders.MaternityRegisterViewHolder;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.view.contract.SmartRegisterClient;

public class GizMaternityRegisterRowOptions implements MaternityRegisterRowOptions {
    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }

    @Override
    public void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull MaternityRegisterViewHolder maternityRegisterViewHolder) {
        Button dueButton = maternityRegisterViewHolder.dueButton;
        dueButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, dueButton.getResources().getDimension(R.dimen.text_size));
        MaternityUtils.setActionButtonStatus(dueButton, commonPersonObjectClient);
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
