package org.smartregister.giz.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.giz.util.GizConstants;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class CheckChildDetailsModelTest {

    @Test
    public void invoke() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonChildObject = new JSONObject();
        JSONObject jsonIdentifiersObject = new JSONObject();
        JSONObject jsonAttributesObject = new JSONObject();
        jsonAttributesObject.put(GizConstants.KEY.INACTIVE, "inactive");
        jsonAttributesObject.put(GizConstants.KEY.LOST_TO_FOLLOW_UP, "lost to follow up");

        jsonIdentifiersObject.put(JsonFormUtils.ZEIR_ID, "zeir-id");
        jsonChildObject.put(GizConstants.KEY.IDENTIFIERS, jsonIdentifiersObject);
        jsonChildObject.put(GizConstants.KEY.ATTRIBUTES, jsonAttributesObject);

        jsonChildObject.put(GizConstants.KEY.BASE_ENTITY_ID, "entityId");
        jsonChildObject.put(GizConstants.KEY.FIRSTNAME, "first");
        jsonChildObject.put(GizConstants.KEY.LASTNAME, "last");
        jsonChildObject.put(GizConstants.KEY.MIDDLENAME, "middle");
        jsonChildObject.put(GizConstants.KEY.BIRTHDATE, String.valueOf(new Date().getTime()));
        jsonChildObject.put(GizConstants.KEY.GENDER, "male");

        jsonObject.put(GizConstants.KEY.CHILD,jsonChildObject);
        CheckChildDetailsModel checkChildDetailsModel = new CheckChildDetailsModel(jsonObject);
        CheckChildDetailsModel resultChildDetailsModel = checkChildDetailsModel.invoke();
        Assert.assertEquals("entityId", resultChildDetailsModel.getEntityId());
        Assert.assertEquals("zeirid", resultChildDetailsModel.getZeirId());
        Assert.assertEquals("first", resultChildDetailsModel.getFirstName());
        Assert.assertEquals("last", resultChildDetailsModel.getLastName());
        Assert.assertEquals("male", resultChildDetailsModel.getGender());
        Assert.assertEquals("inactive", resultChildDetailsModel.getInactive());
        Assert.assertEquals("lost to follow up", resultChildDetailsModel.getLostToFollowUp());
    }
}