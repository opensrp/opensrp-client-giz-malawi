package org.smartregister.giz.model;

import java.util.Date;

public class VisitDetail {
    private String visitDetailsId;
    private String visitId;
    private String baseEntityId;
    private String visitKey;
    private String parentCode;
    private String details; //
    private String humanReadable; //
    private Date updatedAt;
    private Date createdAt;

    public String getVisitDetailsId() {
        return visitDetailsId;
    }

    public void setVisitDetailsId(String visitDetailsId) {
        this.visitDetailsId = visitDetailsId;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getVisitKey() {
        return visitKey;
    }

    public void setVisitKey(String visitKey) {
        this.visitKey = visitKey;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getHumanReadable() {
        return humanReadable;
    }

    public void setHumanReadable(String humanReadable) {
        this.humanReadable = humanReadable;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
