package com.airline.flightmanagement.controller;

import com.airline.flightmanagement.dto.FlightDTO;
import com.airline.flightmanagement.service.FlightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    private FlightDTO testFlightDTO;

    @BeforeEach
    void setUp() {
        testFlightDTO = new FlightDTO();
        testFlightDTO.setCarrierId(1L);
        testFlightDTO.setOrigin("New York");
        testFlightDTO.setDestination("Los Angeles");
        testFlightDTO.setAirFare(new BigDecimal("299.99"));
        testFlightDTO.setSeatCapacityBusiness(20);
        testFlightDTO.setSeatCapacityEconomy(150);
        testFlightDTO.setSeatCapacityExecutive(10);
    }

    @Test
    void testRegisterFlight_Success() throws Exception {
        // Mock successful flight registration
        FlightDTO registeredFlight = new FlightDTO();
        registeredFlight.setFlightId(1L);
        registeredFlight.setCarrierId(1L);
        registeredFlight.setCarrierName("Test Airlines");
        registeredFlight.setOrigin("New York");
        registeredFlight.setDestination("Los Angeles");
        registeredFlight.setAirFare(new BigDecimal("299.99"));
        registeredFlight.setSeatCapacityBusiness(20);
        registeredFlight.setSeatCapacityEconomy(150);
        registeredFlight.setSeatCapacityExecutive(10);

        when(flightService.registerFlight(any(FlightDTO.class))).thenReturn(registeredFlight);

        // Perform POST request
        mockMvc.perform(post("/api/flights/registerFlight")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlightDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Flight registered successfully"))
                .andExpect(jsonPath("$.data.flightId").value(1))
                .andExpect(jsonPath("$.data.carrierId").value(1))
                .andExpect(jsonPath("$.data.origin").value("New York"))
                .andExpect(jsonPath("$.data.destination").value("Los Angeles"))
                .andExpect(jsonPath("$.data.airFare").value(299.99))
                .andExpect(jsonPath("$.data.seatCapacityBusiness").value(20))
                .andExpect(jsonPath("$.data.seatCapacityEconomy").value(150))
                .andExpect(jsonPath("$.data.seatCapacityExecutive").value(10));
    }

    @Test
    void testRegisterFlight_ValidationError() throws Exception {
        // Test with invalid data (missing required fields)
        FlightDTO invalidFlight = new FlightDTO();
        // Missing required fields

        mockMvc.perform(post("/api/flights/registerFlight")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidFlight)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterFlight_ServiceError() throws Exception {
        // Mock service error
        when(flightService.registerFlight(any(FlightDTO.class)))
                .thenThrow(new RuntimeException("Carrier not found"));

        // Perform POST request
        mockMvc.perform(post("/api/flights/registerFlight")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlightDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Carrier not found"));
    }

    @Test
    void testRegisterFlight_InternalServerError() throws Exception {
        // Mock unexpected error
        when(flightService.registerFlight(any(FlightDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Perform POST request
        mockMvc.perform(post("/api/flights/registerFlight")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFlightDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An error occurred while registering flight"));
    }
}
