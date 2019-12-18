package org.smartregister.giz.factory;

import android.content.Context;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONObject;

public class ChildDatePickerFactory extends org.smartregister.child.widgets.ChildDatePickerFactory {
    @Override
    public void attachLayout(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, MaterialEditText editText, TextView duration) {
        super.attachLayout(stepName, context, formFragment, jsonObject, editText, duration);
    }
}
