package com.airline.flightmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;
    
    @NotNull(message = "Flight ID is required")
    @Column(name = "flight_id", nullable = false)
    private Long flightId;
    
    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @NotNull(message = "Number of seats is required")
    @Positive(message = "Number of seats must be positive")
    @Column(name = "no_of_seats", nullable = false)
    private Integer noOfSeats;
    
    @NotBlank(message = "Seat category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_category", nullable = false)
    private SeatCategory seatCategory;
    
    @NotNull(message = "Date of travel is required")
    @Future(message = "Date of travel must be in the future")
    @Column(name = "date_of_travel", nullable = false)
    private LocalDate dateOfTravel;
    
    @NotNull(message = "Booking amount is required")
    @Positive(message = "Booking amount must be positive")
    @Column(name = "booking_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal bookingAmount;
    
    @NotBlank(message = "Booking status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus;
    
    @Column(name = "booking_date")
    private LocalDateTime bookingDate;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(name = "discount_reason")
    private String discountReason;
    
    @PrePersist
    protected void onCreate() {
        bookingDate = LocalDateTime.now();
    }
    
    // Default constructor
    public Booking() {}
    
    // Constructor with required fields
    public Booking(Long flightId, Long userId, Integer noOfSeats, SeatCategory seatCategory, 
                   LocalDate dateOfTravel, BigDecimal bookingAmount, BookingStatus bookingStatus) {
        this.flightId = flightId;
        this.userId = userId;
        this.noOfSeats = noOfSeats;
        this.seatCategory = seatCategory;
        this.dateOfTravel = dateOfTravel;
        this.bookingAmount = bookingAmount;
        this.bookingStatus = bookingStatus;
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
    
    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", flightId=" + flightId +
                ", userId=" + userId +
                ", noOfSeats=" + noOfSeats +
                ", seatCategory=" + seatCategory +
                ", dateOfTravel=" + dateOfTravel +
                ", bookingAmount=" + bookingAmount +
                ", bookingStatus=" + bookingStatus +
                ", bookingDate=" + bookingDate +
                ", discountAmount=" + discountAmount +
                ", discountReason='" + discountReason + '\'' +
                '}';
    }
}
