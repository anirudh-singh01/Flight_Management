package com.airline.flightmanagement.controller;

import com.airline.flightmanagement.dto.CancelBookingResponse;
import com.airline.flightmanagement.entity.BookingStatus;
import com.airline.flightmanagement.entity.SeatCategory;
import com.airline.flightmanagement.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private CancelBookingResponse testCancelResponse;

    @BeforeEach
    void setUp() {
        testCancelResponse = new CancelBookingResponse(
                1L, // bookingId
                1L, // flightId
                1L, // userId
                2,  // noOfSeats
                SeatCategory.ECONOMY, // seatCategory
                LocalDate.now().plusDays(30), // dateOfTravel
                new BigDecimal("200.00"), // originalAmount
                new BigDecimal("160.00"), // refundAmount
                new BigDecimal("80.00"),  // refundPercentage
                BookingStatus.CANCELLED, // bookingStatus
                "New York", // origin
                "Los Angeles", // destination
                "Test Airlines" // carrierName
        );
    }

    @Test
    void testCancelBooking_Success() throws Exception {
        // Mock successful booking cancellation
        when(bookingService.cancelBooking(1L)).thenReturn(testCancelResponse);

        // Perform PUT request to cancel endpoint
        mockMvc.perform(put("/api/bookings/1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking cancelled successfully"))
                .andExpect(jsonPath("$.data.bookingId").value(1))
                .andExpect(jsonPath("$.data.bookingStatus").value("CANCELLED"))
                .andExpect(jsonPath("$.data.refundAmount").value(160.00))
                .andExpect(jsonPath("$.data.refundPercentage").value(80.00))
                .andExpect(jsonPath("$.data.carrierName").value("Test Airlines"));
    }

    @Test
    void testCancelBookingAlternative_Success() throws Exception {
        // Mock successful booking cancellation
        when(bookingService.cancelBooking(1L)).thenReturn(testCancelResponse);

        // Perform DELETE request to alternative cancel endpoint
        mockMvc.perform(delete("/api/bookings/cancelBooking/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking cancelled successfully"))
                .andExpect(jsonPath("$.data.bookingId").value(1))
                .andExpect(jsonPath("$.data.bookingStatus").value("CANCELLED"));
    }

    @Test
    void testCancelBooking_BookingNotFound() throws Exception {
        // Mock service error - booking not found
        when(bookingService.cancelBooking(999L))
                .thenThrow(new RuntimeException("Booking not found with id: 999"));

        // Perform PUT request
        mockMvc.perform(put("/api/bookings/999/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Booking not found with id: 999"));
    }

    @Test
    void testCancelBooking_AlreadyCancelled() throws Exception {
        // Mock service error - booking already cancelled
        when(bookingService.cancelBooking(1L))
                .thenThrow(new RuntimeException("Booking cannot be cancelled. Current status: CANCELLED"));

        // Perform PUT request
        mockMvc.perform(put("/api/bookings/1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Booking cannot be cancelled. Current status: CANCELLED"));
    }

    @Test
    void testCancelBooking_ServiceError() throws Exception {
        // Mock service error
        when(bookingService.cancelBooking(1L))
                .thenThrow(new RuntimeException("Unexpected service error"));

        // Perform PUT request
        mockMvc.perform(put("/api/bookings/1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unexpected service error"));
    }

    @Test
    void testCancelBooking_InternalServerError() throws Exception {
        // Mock unexpected exception
        when(bookingService.cancelBooking(1L))
                .thenThrow(new Exception("Database connection failed"));

        // Perform PUT request
        mockMvc.perform(put("/api/bookings/1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An error occurred while cancelling booking"));
    }
}
