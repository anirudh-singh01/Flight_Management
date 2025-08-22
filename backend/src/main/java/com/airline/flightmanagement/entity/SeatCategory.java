package com.airline.flightmanagement.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum SeatCategory {
    ECONOMY("ECONOMY"),
    BUSINESS("BUSINESS"),
    EXECUTIVE("EXECUTIVE");
    
    private final String value;
    
    SeatCategory(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    @JsonCreator
    public static SeatCategory fromValue(String value) {
        for (SeatCategory category : SeatCategory.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown SeatCategory: " + value);
    }
}
