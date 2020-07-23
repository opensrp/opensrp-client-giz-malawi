package org.smartregister.giz.configuration;


import org.jetbrains.annotations.NotNull;
import org.smartregister.anc.library.configuration.AncMaternityTransferProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GizAncMaternityTransferProcessor extends AncMaternityTransferProcessor {

    @Override
    public String transferForm() {
        return "anc_maternity_transfer";
    }

    @Override
    public Map<String, String> columnMap() {
        HashMap<String, String> columnMap = new HashMap<>();
        columnMap.put("gravida", "gravidity");
        columnMap.put("miscarriages_abortions", "abortion_number");
        columnMap.put("gest_age", "ga_calculated");//? weeks ? days
        return columnMap;
    }

    @NotNull
    protected String[] previousContactKeys() {
        return new String[]{"gravida", "parity", "prev_preg_comps", "prev_preg_comps_other", "miscarriages_abortions", "occupation", "occupation_other", "religion_other", "religion", "marital_status", "educ_level"};
    }

    protected String updateValue(String key, String value) {
        String newValue = value;
        if (key.equals("gest_age")) {
            newValue = value + " weeks";
        }
        return newValue;
    }

    protected List<String> getCloseFormFieldsList() {
        return new ArrayList<>(Arrays.asList("onset_labour_date", "onset_labour_time"));
    }
}
