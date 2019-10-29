package org.smartregister.giz.domain;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-11
 */

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class DailyTally extends Tally implements Serializable {
    @JsonProperty
    private Date day;

    public DailyTally() {
        super();
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }
}