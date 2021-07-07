package org.smartregister.giz.domain;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.smartregister.view.ListContract;

import java.util.List;

public class ProfileAction implements ListContract.Identifiable {

    private String ID;
    private String name;
    private Integer key;
    private List<ProfileActionVisit> visits;
    private ProfileActionVisit selectedAction;

    public ProfileAction(String summary, Integer actionID) {
        this.ID = summary;
        this.name = summary;
        this.key = actionID;
    }

    @NonNull
    @NotNull
    @Override
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public List<ProfileActionVisit> getVisits() {
        return visits;
    }

    public ProfileAction setVisits(List<ProfileActionVisit> visits) {
        this.visits = visits;
        return this;
    }

    public static class ProfileActionVisit {
        private String visitID;
        private String visitTime;

        public String getVisitID() {
            return visitID;
        }

        public void setVisitID(String visitID) {
            this.visitID = visitID;
        }

        public String getVisitTime() {
            return visitTime;
        }

        public void setVisitTime(String visitTime) {
            this.visitTime = visitTime;
        }
    }

    public ProfileActionVisit getSelectedAction() {
        return selectedAction;
    }

    public ProfileAction setSelectedAction(ProfileActionVisit selectedAction) {
        this.selectedAction = selectedAction;
        return this;
    }
}
