package com.airline.flightmanagement.entity;

public enum DiscountType {
    THIRTY_DAYS("30 Days"),
    SIXTY_DAYS("60 Days"),
    NINETY_DAYS("90 Days"),
    BULK("Bulk"),
    SILVER("Silver"),
    GOLD("Gold"),
    PLATINUM("Platinum");
    
    private final String displayName;
    
    DiscountType(String displayName) {
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
