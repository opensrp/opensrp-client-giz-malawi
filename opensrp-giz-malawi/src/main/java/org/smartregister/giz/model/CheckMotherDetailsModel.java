package org.smartregister.giz.model;

import org.json.JSONObject;
import org.smartregister.giz.util.GizConstants;
import org.smartregister.giz.util.GizJsonFormUtils;

public class CheckMotherDetailsModel {
    private JSONObject client;
    private String motherBaseEntityId;
    private String motherFirstName;
    private String motherLastName;

    public CheckMotherDetailsModel(JSONObject client) {
        this.client = client;
    }

    public String getMotherBaseEntityId() {
        return motherBaseEntityId;
    }

    public String getMotherFirstName() {
        return motherFirstName;
    }

    public String getMotherLastName() {
        return motherLastName;
    }

    public CheckMotherDetailsModel invoke() {
        motherBaseEntityId = "";
        motherFirstName = "";
        motherLastName = "";

        if (client.has(GizConstants.KEY.MOTHER)) {
            JSONObject mother = GizJsonFormUtils.getJsonObject(client, GizConstants.KEY.MOTHER);
            motherFirstName = GizJsonFormUtils.getJsonString(mother, GizConstants.KEY.FIRSTNAME);
            motherLastName = GizJsonFormUtils.getJsonString(mother, GizConstants.KEY.LASTNAME);
            motherBaseEntityId = GizJsonFormUtils.getJsonString(mother, GizConstants.KEY.BASE_ENTITY_ID);
        }
        return this;
    }
}
