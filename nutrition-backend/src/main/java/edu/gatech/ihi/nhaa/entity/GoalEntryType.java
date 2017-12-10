package edu.gatech.ihi.nhaa.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GoalEntryType {
    GLUCOSE("Glucose"),
    WEIGHT("Weight"),
    POTASSIUM("Potassium"),
    SUGAR("Sugar"),
    FAT("Fat"),
    FIBER("Fiber"),
    MAGNESIUM("Magnesium"),
    CARBOHYDRATE("Carbohydrate"),
    IRON("Iron");

    private final String text;

    GoalEntryType(final String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    @JsonCreator
    public static GoalEntryType fromString(String text) {
        for (GoalEntryType b : GoalEntryType.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
