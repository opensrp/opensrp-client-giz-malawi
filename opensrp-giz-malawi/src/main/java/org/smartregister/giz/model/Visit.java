package org.smartregister.giz.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Visit {
    private String visitId;
    private String visitType;
    private String visitGroup;
    private String locationId;
    private String childLocationId;
    private String baseEntityId;
    private Date date;
    private Date updatedAt;
    private Date deletedAt;
    private String eventId;
    private String formSubmissionId;
    private Date createdAt;
    private Map<String, List<VisitDetail>> visitDetails = new HashMap<>();

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getVisitGroup() {
        return visitGroup;
    }

    public void setVisitGroup(String visitGroup) {
        this.visitGroup = visitGroup;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getChildLocationId() {
        return childLocationId;
    }

    public void setChildLocationId(String childLocationId) {
        this.childLocationId = childLocationId;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, List<VisitDetail>> getVisitDetails() {
        return visitDetails;
    }

    public void setVisitDetails(Map<String, List<VisitDetail>> visitDetails) {
        this.visitDetails = visitDetails;
    }
}
