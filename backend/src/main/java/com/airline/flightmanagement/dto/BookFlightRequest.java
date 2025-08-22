package com.airline.flightmanagement.dto;

import jakarta.validation.constraints.*;
import com.airline.flightmanagement.entity.SeatCategory;
import java.time.LocalDate;

public class BookFlightRequest {
    
    @NotNull(message = "Flight ID is required")
    private Long flightId;
    
    @NotNull(message = "Number of seats is required")
    @Positive(message = "Number of seats must be positive")
    @Max(value = 10, message = "Cannot book more than 10 seats at once")
    private Integer noOfSeats;
    
    @NotNull(message = "Seat category is required")
    private SeatCategory seatCategory;
    
    @NotNull(message = "Date of travel is required")
    @Future(message = "Date of travel must be in the future")
    private LocalDate dateOfTravel;
    
    // Default constructor
    public BookFlightRequest() {}
    
    // Constructor with all fields
    public BookFlightRequest(Long flightId, Integer noOfSeats, SeatCategory seatCategory, LocalDate dateOfTravel) {
        this.flightId = flightId;
        this.noOfSeats = noOfSeats;
        this.seatCategory = seatCategory;
        this.dateOfTravel = dateOfTravel;
    }
    
    // Getters and Setters
    public Long getFlightId() {
        return flightId;
    }
    
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
    
    public Integer getNoOfSeats() {
        return noOfSeats;
    }
    
    public void setNoOfSeats(Integer noOfSeats) {
        this.noOfSeats = noOfSeats;
    }
    
    public SeatCategory getSeatCategory() {
        return seatCategory;
    }
    
    public void setSeatCategory(SeatCategory seatCategory) {
        this.seatCategory = seatCategory;
    }
    
    public LocalDate getDateOfTravel() {
        return dateOfTravel;
    }
    
    public void setDateOfTravel(LocalDate dateOfTravel) {
        this.dateOfTravel = dateOfTravel;
    }
    
    @Override
    public String toString() {
        return "BookFlightRequest{" +
                "flightId=" + flightId +
                ", noOfSeats=" + noOfSeats +
                ", seatCategory=" + seatCategory +
                ", dateOfTravel=" + dateOfTravel +
                '}';
    }
}
