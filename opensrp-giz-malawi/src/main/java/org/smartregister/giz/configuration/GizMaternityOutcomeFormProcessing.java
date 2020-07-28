package org.smartregister.giz.configuration;

import android.support.annotation.NonNull;

import org.smartregister.maternity.configuration.MaternityOutcomeFormProcessingTask;
import org.smartregister.maternity.utils.MaternityConstants;

import java.util.HashMap;

public class GizMaternityOutcomeFormProcessing extends MaternityOutcomeFormProcessingTask {

    @NonNull
    public String childRegistrationEvent() {
        return MaternityConstants.EventType.BIRTH_REGISTRATION;
    }

    @NonNull
    public String getChildFormName() {
        return "child_enrollment";
    }

    @NonNull
    public String childOpensrpId() {
        return MaternityConstants.JSON_FORM_KEY.ZEIR_ID;
    }

    @NonNull
    public HashMap<String, String> childFormKeyToKeyMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("first_name", "baby_first_name");
        hashMap.put("last_name", "baby_last_name");
        hashMap.put("Date_Birth", "baby_dob");
        hashMap.put("Sex", "baby_gender");
        hashMap.put("Birth_Height", "birth_height_entered");
        hashMap.put("Birth_Weight", "birth_weight_entered");
        return hashMap;
    }

    @NonNull
    public HashMap<String, String> childKeyToColumnMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("mother_guardian_first_name", "first_name");
        hashMap.put("mother_guardian_last_name", "last_name");
        hashMap.put("mother_guardian_date_birth", "dob");
        hashMap.put("village", "village");
        hashMap.put("home_address", "home_address");
        return hashMap;
    }
}
