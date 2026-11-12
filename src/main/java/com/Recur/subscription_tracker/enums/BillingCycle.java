package com.Recur.subscription_tracker.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BillingCycle {
    MONTHLY("Monthly"),
    YEARLY("Yearly");

    private final String value;

    BillingCycle(String value) {
        this.value = value;
    }

    @JsonValue // Tells Jackson to serialize this enum as "Monthly" instead of "MONTHLY"
    public String getValue() {
        return value;
    }

    @JsonCreator // Allows incoming JSON "Monthly" (case-insensitive) to map back to MONTHLY
    public static BillingCycle fromValue(String value) {
        for (BillingCycle cycle : values()) {
            if (cycle.value.equalsIgnoreCase(value)) {
                return cycle;
            }
        }
        throw new IllegalArgumentException("Unknown billing cycle: " + value);
    }
}
