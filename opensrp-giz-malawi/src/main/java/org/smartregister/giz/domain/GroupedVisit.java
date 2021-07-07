package org.smartregister.giz.domain;

import org.smartregister.giz.model.Visit;

import java.util.List;

public class GroupedVisit {
    private String baseEntityId;
    private String memberName;
    private List<Visit> visitList;

    public GroupedVisit(String baseEntityId, String name, List<Visit> visitList) {
        this.baseEntityId = baseEntityId;
        this.memberName = name;
        this.visitList = visitList;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getName() {
        return memberName;
    }

    public void setName(String name) {
        this.memberName = name;
    }

    public List<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }
}
