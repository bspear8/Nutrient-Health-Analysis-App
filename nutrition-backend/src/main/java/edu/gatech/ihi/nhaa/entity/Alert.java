package edu.gatech.ihi.nhaa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity(name = "Alert")
public class Alert {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "alertId", updatable = false, nullable = false)
    private long alertId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entryType", nullable = false)
    private GoalEntryType entryType;

    @Column(name = "value", nullable = false)
    private double value;

    @Column(name = "units", nullable = false)
    private String units;

    @Enumerated(EnumType.STRING)
    @Column(name = "alertType", nullable = false)
    private GoalRangeType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "timePeriod", nullable = false)
    private GoalTimePeriod timePeriod;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnore
    private User user;

    public long getAlertId() {
        return alertId;
    }

    public void setAlertId(long alertId) {
        this.alertId = alertId;
    }

    public GoalEntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(GoalEntryType entryType) {
        this.entryType = entryType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public GoalRangeType getAlertType() {
        return alertType;
    }

    public void setAlertType(GoalRangeType alertType) {
        this.alertType = alertType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GoalTimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(GoalTimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }
}
