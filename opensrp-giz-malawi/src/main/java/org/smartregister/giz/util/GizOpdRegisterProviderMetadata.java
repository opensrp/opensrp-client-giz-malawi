package org.smartregister.giz.util;

import android.support.annotation.NonNull;

import org.smartregister.opd.configuration.BaseOpdRegisterProviderMetadata;
import org.smartregister.util.Utils;

import java.util.Map;

public class GizOpdRegisterProviderMetadata extends BaseOpdRegisterProviderMetadata {

    @Override
    public boolean isClientHaveGuardianDetails(@NonNull Map<String, String> columnMaps) {
        String registerType = getRegisterType(columnMaps);
        return registerType != null && registerType.contains("CHILD");
    }

    @NonNull
    @Override
    public String getGender(@NonNull Map<String, String> columnMaps) {
        String gender = Utils.getValue(columnMaps, "gender", true);
        if(gender.startsWith("F") || gender.startsWith("f")){
            columnMaps.put("gender", "Female");
        } else {
            columnMaps.put("gender", "Male");
        }
        return super.getGender(columnMaps);
    }
}
