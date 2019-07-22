package org.smartregister.giz.model;

import org.json.JSONObject;
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

        if (client.has("mother")) {
            JSONObject mother = GizJsonFormUtils.getJsonObject(client, "mother");
            motherFirstName = GizJsonFormUtils.getJsonString(mother, "firstName");
            motherLastName = GizJsonFormUtils.getJsonString(mother, "lastName");
            motherBaseEntityId = GizJsonFormUtils.getJsonString(mother, "baseEntityId");
        }
        return this;
    }
}
