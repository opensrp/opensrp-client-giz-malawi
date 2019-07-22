package org.smartregister.giz.model;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.giz.util.GizJsonFormUtils;

import java.util.Date;
import java.util.Map;

public class CheckChildDetailsModel {
    private boolean myResult;
    private JSONObject client;
    private String entityId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dob;
    private String zeirId;
    private String epiCardNumber;
    private String inactive;
    private String lostToFollowUp;
    private String nfcCardId;

    public CheckChildDetailsModel(JSONObject client, Map<String, String> childDetails) {
        this.client = client;
        this.entityId = childDetails.get("entityId");
        this.firstName = childDetails.get("firstName");
        this.middleName = childDetails.get("middleName");
        this.lastName = childDetails.get("lastName");
        this.gender = childDetails.get("gender");
        this.dob = childDetails.get("dob");
        this.zeirId = childDetails.get("zeirId");
        this.epiCardNumber = childDetails.get("epiCardNumber");
        this.inactive = childDetails.get("inactive");
        this.lostToFollowUp = childDetails.get("lostToFollowUp");
        this.nfcCardId = childDetails.get("nfcCardId");
    }

    public boolean is() {
        return myResult;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getZeirId() {
        return zeirId;
    }

    public String getEpiCardNumber() {
        return epiCardNumber;
    }

    public String getInactive() {
        return inactive;
    }

    public String getLostToFollowUp() {
        return lostToFollowUp;
    }

    public String getNfcCardId() {
        return nfcCardId;
    }

    public CheckChildDetailsModel invoke() {
        if (client.has("child")) {
            JSONObject child = GizJsonFormUtils.getJsonObject(client, "child");

            // Skip deceased children
            if (StringUtils.isNotBlank(GizJsonFormUtils.getJsonString(child, "deathdate"))) {
                myResult = true;
                return this;
            }

            entityId = GizJsonFormUtils.getJsonString(child, "baseEntityId");
            firstName = GizJsonFormUtils.getJsonString(child, "firstName");
            middleName = GizJsonFormUtils.getJsonString(child, "middleName");
            lastName = GizJsonFormUtils.getJsonString(child, "lastName");

            gender = GizJsonFormUtils.getJsonString(child, "gender");
            dob = GizJsonFormUtils.getJsonString(child, "birthdate");
            if (StringUtils.isNotBlank(dob) && StringUtils.isNumeric(dob)) {
                try {
                    Long dobLong = Long.valueOf(dob);
                    Date date = new Date(dobLong);
                    dob = DateUtil.yyyyMMddTHHmmssSSSZ.format(date);
                } catch (Exception e) {
                    Log.e(getClass().getName(), e.toString(), e);
                }
            }

            zeirId = GizJsonFormUtils.getJsonString(GizJsonFormUtils.getJsonObject(child, "identifiers"), JsonFormUtils.ZEIR_ID);
            if (StringUtils.isNotBlank(zeirId)) {
                zeirId = zeirId.replace("-", "");
            }

            epiCardNumber = GizJsonFormUtils.getJsonString(GizJsonFormUtils.getJsonObject(child, "attributes"), "Child_Register_Card_Number");

            inactive = GizJsonFormUtils.getJsonString(GizJsonFormUtils.getJsonObject(child, "attributes"), "inactive");
            lostToFollowUp = GizJsonFormUtils.getJsonString(GizJsonFormUtils.getJsonObject(child, "attributes"), "lost_to_follow_up");
            nfcCardId = GizJsonFormUtils.getJsonString(GizJsonFormUtils.getJsonObject(child, "attributes"), Constants.KEY.NFC_CARD_IDENTIFIER);

        }
        myResult = false;
        return this;
    }
}
