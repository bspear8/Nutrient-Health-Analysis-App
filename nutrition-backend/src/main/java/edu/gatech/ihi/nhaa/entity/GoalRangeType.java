package edu.gatech.ihi.nhaa.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GoalRangeType {
    GREATER_THAN("Greater Than"),
    LESS_THAN("Less Than");

    private final String text;

    GoalRangeType(final String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    @JsonCreator
    public static GoalRangeType fromString(String text) {
        for (GoalRangeType b : GoalRangeType.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
