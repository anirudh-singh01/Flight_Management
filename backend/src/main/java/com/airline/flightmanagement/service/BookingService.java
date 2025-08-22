package com.airline.flightmanagement.service;

import com.airline.flightmanagement.dto.BookFlightRequest;
import com.airline.flightmanagement.dto.BookFlightResponse;
import com.airline.flightmanagement.dto.CancelBookingResponse;
import com.airline.flightmanagement.entity.*;
import com.airline.flightmanagement.repository.BookingRepository;
import com.airline.flightmanagement.repository.FlightRepository;
import com.airline.flightmanagement.repository.FlightScheduleRepository;
import com.airline.flightmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Book a flight with automatic discount calculation and seat availability check
     */
    @Transactional
    public BookFlightResponse bookFlight(BookFlightRequest request, Long userId) {
        // Validate flight exists
        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + request.getFlightId()));
        
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Check seat availability
        checkSeatAvailability(request.getFlightId(), request.getDateOfTravel(), 
                            request.getSeatCategory(), request.getNoOfSeats());
        
        // Calculate booking amount with discounts
        BigDecimal originalAmount = flight.getAirFare().multiply(BigDecimal.valueOf(request.getNoOfSeats()));
        BigDecimal discountAmount = calculateDiscounts(flight, user, request.getDateOfTravel(), request.getNoOfSeats());
        BigDecimal finalAmount = originalAmount.subtract(discountAmount);
        
        // Create or update flight schedule
        FlightSchedule schedule = getOrCreateFlightSchedule(flight, request.getDateOfTravel());
        
        // Create booking
        Booking booking = new Booking();
        booking.setFlightId(request.getFlightId());
        booking.setUserId(userId);
        booking.setNoOfSeats(request.getNoOfSeats());
        booking.setSeatCategory(request.getSeatCategory());
        booking.setDateOfTravel(request.getDateOfTravel());
        booking.setBookingAmount(finalAmount);
        booking.setDiscountAmount(discountAmount);
        booking.setDiscountReason(generateDiscountReason(discountAmount, user, request.getDateOfTravel(), request.getNoOfSeats()));
        booking.setBookingStatus(BookingStatus.BOOKED);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Update flight schedule booked count
        schedule.incrementBookedCount(request.getSeatCategory(), request.getNoOfSeats());
        flightScheduleRepository.save(schedule);
        
        // Build response
        return buildBookFlightResponse(savedBooking, flight, discountAmount);
    }
    
    /**
     * Check if seats are available for the requested category and date
     */
    private void checkSeatAvailability(Long flightId, LocalDate dateOfTravel, 
                                     SeatCategory seatCategory, Integer requestedSeats) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        
        // Get current booked count for this flight and date
        Optional<FlightSchedule> existingSchedule = flightScheduleRepository
                .findByFlightIdAndDateOfTravel(flightId, dateOfTravel);
        
        int currentBookedCount = 0;
        int totalCapacity = 0;
        
        switch (seatCategory) {
            case ECONOMY:
                totalCapacity = flight.getSeatCapacityEconomy();
                if (existingSchedule.isPresent()) {
                    currentBookedCount = existingSchedule.get().getBookedCountEconomy();
                }
                break;
            case BUSINESS:
                totalCapacity = flight.getSeatCapacityBusiness();
                if (existingSchedule.isPresent()) {
                    currentBookedCount = existingSchedule.get().getBookedCountBusiness();
                }
                break;
            case EXECUTIVE:
                totalCapacity = flight.getSeatCapacityExecutive();
                if (existingSchedule.isPresent()) {
                    currentBookedCount = existingSchedule.get().getBookedCountExecutive();
                }
                break;
        }
        
        if (currentBookedCount + requestedSeats > totalCapacity) {
            throw new RuntimeException("Insufficient seats available. Requested: " + requestedSeats + 
                                    ", Available: " + (totalCapacity - currentBookedCount));
        }
    }
    
    /**
     * Calculate discounts based on various factors
     */
    private BigDecimal calculateDiscounts(Flight flight, User user, LocalDate dateOfTravel, Integer noOfSeats) {
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        // 1. Advance booking discount (book 30+ days in advance)
        long daysInAdvance = ChronoUnit.DAYS.between(LocalDate.now(), dateOfTravel);
        if (daysInAdvance >= 30) {
            BigDecimal advanceDiscount = flight.getAirFare().multiply(BigDecimal.valueOf(0.15)); // 15% discount
            totalDiscount = totalDiscount.add(advanceDiscount);
        } else if (daysInAdvance >= 14) {
            BigDecimal advanceDiscount = flight.getAirFare().multiply(BigDecimal.valueOf(0.10)); // 10% discount
            totalDiscount = totalDiscount.add(advanceDiscount);
        } else if (daysInAdvance >= 7) {
            BigDecimal advanceDiscount = flight.getAirFare().multiply(BigDecimal.valueOf(0.05)); // 5% discount
            totalDiscount = totalDiscount.add(advanceDiscount);
        }
        
        // 2. Customer category discount
        BigDecimal customerDiscount = BigDecimal.ZERO;
        switch (user.getCustomerCategory()) {
            case PLATINUM:
                customerDiscount = flight.getAirFare().multiply(BigDecimal.valueOf(0.20)); // 20% discount
                break;
            case GOLD:
                customerDiscount = flight.getAirFare().multiply(BigDecimal.valueOf(0.15)); // 15% discount
                break;
            case SILVER:
                customerDiscount = flight.getAirFare().multiply(BigDecimal.valueOf(0.10)); // 10% discount
                break;
            case PREMIUM:
                customerDiscount = flight.getAirFare().multiply(BigDecimal.valueOf(0.25)); // 25% discount
                break;
            default:
                // REGULAR customers get no additional discount
                break;
        }
        totalDiscount = totalDiscount.add(customerDiscount);
        
        // 3. Bulk booking discount (5+ seats)
        if (noOfSeats >= 5) {
            BigDecimal bulkDiscount = flight.getAirFare().multiply(BigDecimal.valueOf(0.10)); // 10% discount
            totalDiscount = totalDiscount.add(bulkDiscount);
        }
        
        // Apply discount to total seats
        totalDiscount = totalDiscount.multiply(BigDecimal.valueOf(noOfSeats));
        
        // Ensure discount doesn't exceed the total fare
        BigDecimal maxDiscount = flight.getAirFare().multiply(BigDecimal.valueOf(noOfSeats));
        if (totalDiscount.compareTo(maxDiscount) > 0) {
            totalDiscount = maxDiscount;
        }
        
        return totalDiscount.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Generate discount reason description
     */
    private String generateDiscountReason(BigDecimal discountAmount, User user, LocalDate dateOfTravel, Integer noOfSeats) {
        if (discountAmount.compareTo(BigDecimal.ZERO) == 0) {
            return "No discounts applied";
        }
        
        StringBuilder reason = new StringBuilder();
        long daysInAdvance = ChronoUnit.DAYS.between(LocalDate.now(), dateOfTravel);
        
        if (daysInAdvance >= 30) {
            reason.append("Advance booking (30+ days): 15%");
        } else if (daysInAdvance >= 14) {
            reason.append("Advance booking (14+ days): 10%");
        } else if (daysInAdvance >= 7) {
            reason.append("Advance booking (7+ days): 5%");
        }
        
        if (user.getCustomerCategory() != CustomerCategory.REGULAR) {
            if (reason.length() > 0) reason.append(", ");
            reason.append("Customer category (").append(user.getCustomerCategory()).append("): ");
            switch (user.getCustomerCategory()) {
                case PLATINUM:
                    reason.append("20%");
                    break;
                case GOLD:
                    reason.append("15%");
                    break;
                case SILVER:
                    reason.append("10%");
                    break;
                case PREMIUM:
                    reason.append("25%");
                    break;
            }
        }
        
        if (noOfSeats >= 5) {
            if (reason.length() > 0) reason.append(", ");
            reason.append("Bulk booking (5+ seats): 10%");
        }
        
        return reason.toString();
    }
    
    /**
     * Get or create flight schedule for the given flight and date
     */
    private FlightSchedule getOrCreateFlightSchedule(Flight flight, LocalDate dateOfTravel) {
        Optional<FlightSchedule> existingSchedule = flightScheduleRepository
                .findByFlightIdAndDateOfTravel(flight.getFlightId(), dateOfTravel);
        
        if (existingSchedule.isPresent()) {
            return existingSchedule.get();
        }
        
        // Create new schedule
        FlightSchedule newSchedule = new FlightSchedule(
                flight.getFlightId(),
                dateOfTravel,
                flight.getSeatCapacityEconomy(),
                flight.getSeatCapacityBusiness(),
                flight.getSeatCapacityExecutive()
        );
        
        return flightScheduleRepository.save(newSchedule);
    }
    
    /**
     * Build BookFlightResponse from booking and flight data
     */
    private BookFlightResponse buildBookFlightResponse(Booking booking, Flight flight, BigDecimal discountAmount) {
        return new BookFlightResponse(
                booking.getBookingId(),
                booking.getFlightId(),
                booking.getUserId(),
                booking.getNoOfSeats(),
                booking.getSeatCategory(),
                booking.getDateOfTravel(),
                booking.getBookingAmount(),
                booking.getDiscountAmount(),
                booking.getDiscountReason(),
                booking.getBookingStatus(),
                booking.getBookingDate(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getCarrier().getCarrierName(),
                flight.getAirFare()
        );
    }
    
    /**
     * Get booking by ID
     */
    public Optional<BookFlightResponse> getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    Flight flight = flightRepository.findById(booking.getFlightId())
                            .orElseThrow(() -> new RuntimeException("Flight not found"));
                    return buildBookFlightResponse(booking, flight, booking.getDiscountAmount());
                });
    }
    
    /**
     * Get all bookings for a user
     */
    public java.util.List<BookFlightResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(booking -> {
                    Flight flight = flightRepository.findById(booking.getFlightId())
                            .orElseThrow(() -> new RuntimeException("Flight not found"));
                    return buildBookFlightResponse(booking, flight, booking.getDiscountAmount());
                })
                .toList();
    }
    
    /**
     * Cancel a booking and calculate refund amount
     */
    @Transactional
    public CancelBookingResponse cancelBooking(Long bookingId) {
        // Find the booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        
        // Check if booking can be cancelled
        if (booking.getBookingStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException("Booking cannot be cancelled. Current status: " + booking.getBookingStatus());
        }
        
        // Get flight details to access carrier information
        Flight flight = flightRepository.findById(booking.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        
        // Calculate refund amount based on carrier refund percentage
        BigDecimal refundAmount = calculateRefundAmount(booking.getBookingAmount(), flight.getCarrier().getRefundPercentage());
        
        // Update booking status to cancelled
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Update flight schedule booked count (decrease by the number of seats)
        updateFlightScheduleBookedCount(booking.getFlightId(), booking.getDateOfTravel(), 
                                      booking.getSeatCategory(), -booking.getNoOfSeats());
        
        // Build response
        return new CancelBookingResponse(
                booking.getBookingId(),
                booking.getFlightId(),
                booking.getUserId(),
                booking.getNoOfSeats(),
                booking.getSeatCategory(),
                booking.getDateOfTravel(),
                booking.getBookingAmount(),
                refundAmount,
                flight.getCarrier().getRefundPercentage(),
                booking.getBookingStatus(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getCarrier().getCarrierName()
        );
    }
    
    /**
     * Calculate refund amount based on carrier refund percentage
     */
    private BigDecimal calculateRefundAmount(BigDecimal bookingAmount, BigDecimal refundPercentage) {
        return bookingAmount.multiply(refundPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Update flight schedule booked count
     */
    private void updateFlightScheduleBookedCount(Long flightId, LocalDate dateOfTravel, 
                                               SeatCategory seatCategory, Integer seatChange) {
        Optional<FlightSchedule> scheduleOpt = flightScheduleRepository
                .findByFlightIdAndDateOfTravel(flightId, dateOfTravel);
        
        if (scheduleOpt.isPresent()) {
            FlightSchedule schedule = scheduleOpt.get();
            
            if (seatChange > 0) {
                schedule.incrementBookedCount(seatCategory, seatChange);
            } else {
                schedule.decrementBookedCount(seatCategory, Math.abs(seatChange));
            }
            
            flightScheduleRepository.save(schedule);
        }
    }
}
