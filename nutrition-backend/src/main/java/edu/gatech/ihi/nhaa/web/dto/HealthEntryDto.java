package edu.gatech.ihi.nhaa.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.gatech.ihi.nhaa.entity.HealthEntryType;

public class HealthEntryDto {

    private String id;
    private HealthEntryType healthEntryType;
    private BigDecimal value;
    private String units;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date date;
    private String notes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HealthEntryType getHealthEntryType() {
        return healthEntryType;
    }

    public void setHealthEntryType(HealthEntryType healthEntryType) {
        this.healthEntryType = healthEntryType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
