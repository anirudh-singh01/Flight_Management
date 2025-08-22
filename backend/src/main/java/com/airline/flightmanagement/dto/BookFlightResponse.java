package com.airline.flightmanagement.dto;

import com.airline.flightmanagement.entity.SeatCategory;
import com.airline.flightmanagement.entity.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookFlightResponse {
    
    private Long bookingId;
    private Long flightId;
    private Long userId;
    private Integer noOfSeats;
    private SeatCategory seatCategory;
    private LocalDate dateOfTravel;
    private BigDecimal bookingAmount;
    private BigDecimal discountAmount;
    private String discountReason;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDate;
    
    // Flight details for summary
    private String origin;
    private String destination;
    private String carrierName;
    private BigDecimal originalAirFare;
    
    // Default constructor
    public BookFlightResponse() {}
    
    // Constructor with all fields
    public BookFlightResponse(Long bookingId, Long flightId, Long userId, Integer noOfSeats, 
                             SeatCategory seatCategory, LocalDate dateOfTravel, BigDecimal bookingAmount,
                             BigDecimal discountAmount, String discountReason, BookingStatus bookingStatus,
                             LocalDateTime bookingDate, String origin, String destination, 
                             String carrierName, BigDecimal originalAirFare) {
        this.bookingId = bookingId;
        this.flightId = flightId;
        this.userId = userId;
        this.noOfSeats = noOfSeats;
        this.seatCategory = seatCategory;
        this.dateOfTravel = dateOfTravel;
        this.bookingAmount = bookingAmount;
        this.discountAmount = discountAmount;
        this.discountReason = discountReason;
        this.bookingStatus = bookingStatus;
        this.bookingDate = bookingDate;
        this.origin = origin;
        this.destination = destination;
        this.carrierName = carrierName;
        this.originalAirFare = originalAirFare;
    }
    
    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
    
    public Long getFlightId() {
        return flightId;
    }
    
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public BigDecimal getBookingAmount() {
        return bookingAmount;
    }
    
    public void setBookingAmount(BigDecimal bookingAmount) {
        this.bookingAmount = bookingAmount;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public String getDiscountReason() {
        return discountReason;
    }
    
    public void setDiscountReason(String discountReason) {
        this.discountReason = discountReason;
    }
    
    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }
    
    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
    
    public LocalDateTime getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
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
    
    public String getCarrierName() {
        return carrierName;
    }
    
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
    
    public BigDecimal getOriginalAirFare() {
        return originalAirFare;
    }
    
    public void setOriginalAirFare(BigDecimal originalAirFare) {
        this.originalAirFare = originalAirFare;
    }
    
    @Override
    public String toString() {
        return "BookFlightResponse{" +
                "bookingId=" + bookingId +
                ", flightId=" + flightId +
                ", userId=" + userId +
                ", noOfSeats=" + noOfSeats +
                ", seatCategory=" + seatCategory +
                ", dateOfTravel=" + dateOfTravel +
                ", bookingAmount=" + bookingAmount +
                ", discountAmount=" + discountAmount +
                ", discountReason='" + discountReason + '\'' +
                ", bookingStatus=" + bookingStatus +
                ", bookingDate=" + bookingDate +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", carrierName='" + carrierName + '\'' +
                ", originalAirFare=" + originalAirFare +
                '}';
    }
}
