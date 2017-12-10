package edu.gatech.ihi.nhaa.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AlertState {
    ACCEPTABLE_RANGE("Acceptable Range"),
    OUT_OF_RANGE("Out of Range"),
    UNKNOWN("Unknown");

    private final String text;

    AlertState(final String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    @JsonCreator
    public static AlertState fromString(String text) {
        for (AlertState b : AlertState.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
