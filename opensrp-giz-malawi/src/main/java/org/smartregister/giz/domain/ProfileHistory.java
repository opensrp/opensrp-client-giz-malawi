package org.smartregister.giz.domain;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.smartregister.view.ListContract;

public class ProfileHistory implements ListContract.Identifiable {
    private String ID;
    private String eventDate;
    private String eventTime;
    private String eventType;

    @NonNull
    @NotNull
    @Override
    public String getID() {
        return ID;
    }

    public ProfileHistory setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getEventDate() {
        return eventDate;
    }

    public ProfileHistory setEventDate(String eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public String getEventTime() {
        return eventTime;
    }

    public ProfileHistory setEventTime(String eventTime) {
        this.eventTime = eventTime;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public ProfileHistory setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

}
