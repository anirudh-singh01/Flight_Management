package com.airline.flightmanagement.entity;

public enum RefundType {
    TWO_DAYS("2 Days Before Travel"),
    TEN_DAYS("10 Days Before Travel"),
    TWENTY_DAYS("20 Days Before Travel");
    
    private final String displayName;
    
    RefundType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
