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
        String gender = Utils.getValue(columnMaps, GizConstants.KEY.GENDER, true);
        if(gender.startsWith("F") || gender.startsWith("f")){
            columnMaps.put(GizConstants.KEY.GENDER, "Female");
        } else {
            columnMaps.put(GizConstants.KEY.GENDER, "Male");
        }
        return super.getGender(columnMaps);
    }
}
