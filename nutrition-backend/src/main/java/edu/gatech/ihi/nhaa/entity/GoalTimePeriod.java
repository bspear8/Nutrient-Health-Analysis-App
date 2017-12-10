package edu.gatech.ihi.nhaa.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GoalTimePeriod {
    INSTANT("Instant"),
    DAY("Last 24 Hours"),
    WEEK("Daily Average for Last Week");

    private final String text;

    GoalTimePeriod(final String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    @JsonCreator
    public static GoalTimePeriod fromString(String text) {
        for (GoalTimePeriod b : GoalTimePeriod.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
