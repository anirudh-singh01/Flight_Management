package com.airline.flightmanagement.dto;

import com.airline.flightmanagement.entity.BookingStatus;
import com.airline.flightmanagement.entity.SeatCategory;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CancelBookingResponse {
    private Long bookingId;
    private Long flightId;
    private Long userId;
    private Integer noOfSeats;
    private SeatCategory seatCategory;
    private LocalDate dateOfTravel;
    private BigDecimal originalAmount;
    private BigDecimal refundAmount;
    private BigDecimal refundPercentage;
    private BookingStatus bookingStatus;
    private String origin;
    private String destination;
    private String carrierName;
    
    // Default constructor
    public CancelBookingResponse() {}
    
    // Constructor with all fields
    public CancelBookingResponse(Long bookingId, Long flightId, Long userId, Integer noOfSeats,
                                SeatCategory seatCategory, LocalDate dateOfTravel, BigDecimal originalAmount,
                                BigDecimal refundAmount, BigDecimal refundPercentage, BookingStatus bookingStatus,
                                String origin, String destination, String carrierName) {
        this.bookingId = bookingId;
        this.flightId = flightId;
        this.userId = userId;
        this.noOfSeats = noOfSeats;
        this.seatCategory = seatCategory;
        this.dateOfTravel = dateOfTravel;
        this.originalAmount = originalAmount;
        this.refundAmount = refundAmount;
        this.refundPercentage = refundPercentage;
        this.bookingStatus = bookingStatus;
        this.origin = origin;
        this.destination = destination;
        this.carrierName = carrierName;
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
    
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }
    
    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }
    
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public BigDecimal getRefundPercentage() {
        return refundPercentage;
    }
    
    public void setRefundPercentage(BigDecimal refundPercentage) {
        this.refundPercentage = refundPercentage;
    }
    
    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }
    
    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
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
    
    @Override
    public String toString() {
        return "CancelBookingResponse{" +
                "bookingId=" + bookingId +
                ", flightId=" + flightId +
                ", userId=" + userId +
                ", noOfSeats=" + noOfSeats +
                ", seatCategory=" + seatCategory +
                ", dateOfTravel=" + dateOfTravel +
                ", originalAmount=" + originalAmount +
                ", refundAmount=" + refundAmount +
                ", refundPercentage=" + refundPercentage +
                ", bookingStatus=" + bookingStatus +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", carrierName='" + carrierName + '\'' +
                '}';
    }
}
