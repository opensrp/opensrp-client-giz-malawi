package org.smartregister.giz.util;

import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;

public interface FormProcessor {
    interface Host {
        void startForm(JSONObject jsonObject, Form form, Requester requester);
    }

    interface Requester {
        void onFormProcessingResult(String jsonForm);

        Host getHostFormProcessor();
    }
}
