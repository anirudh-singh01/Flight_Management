package com.airline.flightmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "flight_schedules")
public class FlightSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;
    
    @NotNull(message = "Flight ID is required")
    @Column(name = "flight_id", nullable = false)
    private Long flightId;
    
    @NotNull(message = "Date of travel is required")
    @Future(message = "Date of travel must be in the future")
    @Column(name = "date_of_travel", nullable = false)
    private LocalDate dateOfTravel;
    
    @Column(name = "booked_count_economy", nullable = false)
    private Integer bookedCountEconomy = 0;
    
    @Column(name = "booked_count_business", nullable = false)
    private Integer bookedCountBusiness = 0;
    
    @Column(name = "booked_count_executive", nullable = false)
    private Integer bookedCountExecutive = 0;
    
    @Column(name = "total_capacity_economy", nullable = false)
    private Integer totalCapacityEconomy;
    
    @Column(name = "total_capacity_business", nullable = false)
    private Integer totalCapacityBusiness;
    
    @Column(name = "total_capacity_executive", nullable = false)
    private Integer totalCapacityExecutive;
    
    // Default constructor
    public FlightSchedule() {}
    
    // Constructor with required fields
    public FlightSchedule(Long flightId, LocalDate dateOfTravel, 
                         Integer totalCapacityEconomy, Integer totalCapacityBusiness, 
                         Integer totalCapacityExecutive) {
        this.flightId = flightId;
        this.dateOfTravel = dateOfTravel;
        this.totalCapacityEconomy = totalCapacityEconomy;
        this.totalCapacityBusiness = totalCapacityBusiness;
        this.totalCapacityExecutive = totalCapacityExecutive;
    }
    
    // Getters and Setters
    public Long getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public Long getFlightId() {
        return flightId;
    }
    
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
    
    public LocalDate getDateOfTravel() {
        return dateOfTravel;
    }
    
    public void setDateOfTravel(LocalDate dateOfTravel) {
        this.dateOfTravel = dateOfTravel;
    }
    
    public Integer getBookedCountEconomy() {
        return bookedCountEconomy;
    }
    
    public void setBookedCountEconomy(Integer bookedCountEconomy) {
        this.bookedCountEconomy = bookedCountEconomy;
    }
    
    public Integer getBookedCountBusiness() {
        return bookedCountBusiness;
    }
    
    public void setBookedCountBusiness(Integer bookedCountBusiness) {
        this.bookedCountBusiness = bookedCountBusiness;
    }
    
    public Integer getBookedCountExecutive() {
        return bookedCountExecutive;
    }
    
    public void setBookedCountExecutive(Integer bookedCountExecutive) {
        this.bookedCountExecutive = bookedCountExecutive;
    }
    
    public Integer getTotalCapacityEconomy() {
        return totalCapacityEconomy;
    }
    
    public void setTotalCapacityEconomy(Integer totalCapacityEconomy) {
        this.totalCapacityEconomy = totalCapacityEconomy;
    }
    
    public Integer getTotalCapacityBusiness() {
        return totalCapacityBusiness;
    }
    
    public void setTotalCapacityBusiness(Integer totalCapacityBusiness) {
        this.totalCapacityBusiness = totalCapacityBusiness;
    }
    
    public Integer getTotalCapacityExecutive() {
        return totalCapacityExecutive;
    }
    
    public void setTotalCapacityExecutive(Integer totalCapacityExecutive) {
        this.totalCapacityExecutive = totalCapacityExecutive;
    }
    
    // Helper methods to check seat availability
    public boolean hasAvailableSeats(SeatCategory seatCategory, Integer requestedSeats) {
        switch (seatCategory) {
            case ECONOMY:
                return (bookedCountEconomy + requestedSeats) <= totalCapacityEconomy;
            case BUSINESS:
                return (bookedCountBusiness + requestedSeats) <= totalCapacityBusiness;
            case EXECUTIVE:
                return (bookedCountExecutive + requestedSeats) <= totalCapacityExecutive;
            default:
                return false;
        }
    }
    
    public void incrementBookedCount(SeatCategory seatCategory, Integer seats) {
        switch (seatCategory) {
            case ECONOMY:
                this.bookedCountEconomy += seats;
                break;
            case BUSINESS:
                this.bookedCountBusiness += seats;
                break;
            case EXECUTIVE:
                this.bookedCountExecutive += seats;
                break;
        }
    }
    
    public void decrementBookedCount(SeatCategory seatCategory, Integer seats) {
        switch (seatCategory) {
            case ECONOMY:
                this.bookedCountEconomy = Math.max(0, this.bookedCountEconomy - seats);
                break;
            case BUSINESS:
                this.bookedCountBusiness = Math.max(0, this.bookedCountBusiness - seats);
                break;
            case EXECUTIVE:
                this.bookedCountExecutive = Math.max(0, this.bookedCountExecutive - seats);
                break;
        }
    }
    
    @Override
    public String toString() {
        return "FlightSchedule{" +
                "scheduleId=" + scheduleId +
                ", flightId=" + flightId +
                ", dateOfTravel=" + dateOfTravel +
                ", bookedCountEconomy=" + bookedCountEconomy +
                ", bookedCountBusiness=" + bookedCountBusiness +
                ", bookedCountExecutive=" + bookedCountExecutive +
                ", totalCapacityEconomy=" + totalCapacityEconomy +
                ", totalCapacityBusiness=" + totalCapacityBusiness +
                ", totalCapacityExecutive=" + totalCapacityExecutive +
                '}';
    }
}
