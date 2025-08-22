package com.airline.flightmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class FlightDTO {
    
    private Long flightId;
    
    @NotNull(message = "Carrier ID is required")
    private Long carrierId;
    
    private String carrierName; // For display purposes
    
    @NotBlank(message = "Origin is required")
    private String origin;
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    @NotNull(message = "Air fare is required")
    @Positive(message = "Air fare must be positive")
    private BigDecimal airFare;
    
    @Positive(message = "Business seat capacity must be positive")
    private Integer seatCapacityBusiness;
    
    @Positive(message = "Economy seat capacity must be positive")
    private Integer seatCapacityEconomy;
    
    @Positive(message = "Executive seat capacity must be positive")
    private Integer seatCapacityExecutive;
    
    // Default constructor
    public FlightDTO() {}
    
    // Constructor with all fields
    public FlightDTO(Long flightId, Long carrierId, String carrierName, String origin, String destination,
                     BigDecimal airFare, Integer seatCapacityBusiness, 
                     Integer seatCapacityEconomy, Integer seatCapacityExecutive) {
        this.flightId = flightId;
        this.carrierId = carrierId;
        this.carrierName = carrierName;
        this.origin = origin;
        this.destination = destination;
        this.airFare = airFare;
        this.seatCapacityBusiness = seatCapacityBusiness;
        this.seatCapacityEconomy = seatCapacityEconomy;
        this.seatCapacityExecutive = seatCapacityExecutive;
    }
    
    // Getters and Setters
    public Long getFlightId() {
        return flightId;
    }
    
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
    
    public Long getCarrierId() {
        return carrierId;
    }
    
    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }
    
    public String getCarrierName() {
        return carrierName;
    }
    
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public BigDecimal getAirFare() {
        return airFare;
    }
    
    public void setAirFare(BigDecimal airFare) {
        this.airFare = airFare;
    }
    
    public Integer getSeatCapacityBusiness() {
        return seatCapacityBusiness;
    }
    
    public void setSeatCapacityBusiness(Integer seatCapacityBusiness) {
        this.seatCapacityBusiness = seatCapacityBusiness;
    }
    
    public Integer getSeatCapacityEconomy() {
        return seatCapacityEconomy;
    }
    
    public void setSeatCapacityEconomy(Integer seatCapacityEconomy) {
        this.seatCapacityEconomy = seatCapacityEconomy;
    }
    
    public Integer getSeatCapacityExecutive() {
        return seatCapacityExecutive;
    }
    
    public void setSeatCapacityExecutive(Integer seatCapacityExecutive) {
        this.seatCapacityExecutive = seatCapacityExecutive;
    }
}
