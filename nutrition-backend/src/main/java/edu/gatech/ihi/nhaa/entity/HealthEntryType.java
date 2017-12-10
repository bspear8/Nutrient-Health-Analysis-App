package edu.gatech.ihi.nhaa.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HealthEntryType {
    GLUCOSE("Glucose"),
    WEIGHT("Weight");

    private final String text;

    HealthEntryType(final String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    @JsonCreator
    public static HealthEntryType fromString(String text) {
        for (HealthEntryType b : HealthEntryType.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
