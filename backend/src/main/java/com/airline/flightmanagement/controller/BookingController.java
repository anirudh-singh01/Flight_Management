package com.airline.flightmanagement.controller;

import com.airline.flightmanagement.dto.BookFlightRequest;
import com.airline.flightmanagement.dto.BookFlightResponse;
import com.airline.flightmanagement.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    /**
     * Book a flight
     * POST /api/bookings/bookFlight
     */
    @PostMapping("/bookFlight")
    public ResponseEntity<?> bookFlight(@Valid @RequestBody BookFlightRequest request,
                                       @RequestParam Long userId) {
        try {
            BookFlightResponse booking = bookingService.bookFlight(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Flight booked successfully", booking));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while booking flight", null));
        }
    }
    
    /**
     * Get booking by ID
     * GET /api/bookings/{bookingId}
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        try {
            Optional<BookFlightResponse> booking = bookingService.getBookingById(bookingId);
            if (booking.isPresent()) {
                return ResponseEntity.ok(new ApiResponse(true, "Booking retrieved successfully", booking.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving booking", null));
        }
    }
    
    /**
     * Get all bookings for a user
     * GET /api/bookings/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBookings(@PathVariable Long userId) {
        try {
            List<BookFlightResponse> bookings = bookingService.getUserBookings(userId);
            return ResponseEntity.ok(new ApiResponse(true, "User bookings retrieved successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving user bookings", null));
        }
    }
    
    /**
     * Get available seats for a flight on a specific date
     * GET /api/bookings/availability?flightId={flightId}&dateOfTravel={dateOfTravel}
     */
    @GetMapping("/availability")
    public ResponseEntity<?> getSeatAvailability(@RequestParam Long flightId,
                                               @RequestParam String dateOfTravel) {
        try {
            // This would need to be implemented in the service
            // For now, returning a placeholder response
            return ResponseEntity.ok(new ApiResponse(true, "Seat availability retrieved successfully", 
                    "Feature to be implemented"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving seat availability", null));
        }
    }
    
    /**
     * Cancel a booking
     * PUT /api/bookings/{bookingId}/cancel
     */
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            var cancelResponse = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(new ApiResponse(true, "Booking cancelled successfully", cancelResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while cancelling booking", null));
        }
    }
    
    /**
     * Cancel a booking (alternative endpoint)
     * DELETE /api/bookings/cancelBooking/{bookingId}
     */
    @DeleteMapping("/cancelBooking/{bookingId}")
    public ResponseEntity<?> cancelBookingAlternative(@PathVariable Long bookingId) {
        try {
            var cancelResponse = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(new ApiResponse(true, "Booking cancelled successfully", cancelResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while cancelling booking", null));
        }
    }
    
    /**
     * Get booking statistics
     * GET /api/bookings/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getBookingStats() {
        try {
            // This would need to be implemented in the service
            // For now, returning a placeholder response
            return ResponseEntity.ok(new ApiResponse(true, "Booking statistics retrieved successfully", 
                    "Feature to be implemented"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving booking statistics", null));
        }
    }
    
    /**
     * API Response wrapper class
     */
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;
        
        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Object getData() {
            return data;
        }
        
        public void setData(Object data) {
            this.data = data;
        }
    }
}
