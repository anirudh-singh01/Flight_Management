package com.airline.flightmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "flights")
public class Flight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;
    
    @NotBlank(message = "Origin is required")
    @Column(nullable = false)
    private String origin;
    
    @NotBlank(message = "Destination is required")
    @Column(nullable = false)
    private String destination;
    
    @NotNull(message = "Air fare is required")
    @Positive(message = "Air fare must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal airFare;
    
    @Positive(message = "Business seat capacity must be positive")
    @Column(nullable = false)
    private Integer seatCapacityBusiness;
    
    @Positive(message = "Economy seat capacity must be positive")
    @Column(nullable = false)
    private Integer seatCapacityEconomy;
    
    @Positive(message = "Executive seat capacity must be positive")
    @Column(nullable = false)
    private Integer seatCapacityExecutive;
    
    // Default constructor
    public Flight() {}
    
    // Constructor with required fields
    public Flight(Carrier carrier, String origin, String destination, 
                  BigDecimal airFare, Integer seatCapacityBusiness, 
                  Integer seatCapacityEconomy, Integer seatCapacityExecutive) {
        this.carrier = carrier;
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
    
    public Carrier getCarrier() {
        return carrier;
    }
    
    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
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
    
    @Override
    public String toString() {
        return "Flight{" +
                "flightId=" + flightId +
                ", carrier=" + (carrier != null ? carrier.getCarrierName() : "null") +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", airFare=" + airFare +
                ", seatCapacityBusiness=" + seatCapacityBusiness +
                ", seatCapacityEconomy=" + seatCapacityEconomy +
                ", seatCapacityExecutive=" + seatCapacityExecutive +
                '}';
    }
}
