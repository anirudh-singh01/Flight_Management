package com.airline.flightmanagement.controller;

import com.airline.flightmanagement.dto.FlightDTO;
import com.airline.flightmanagement.dto.ApiResponse;
import com.airline.flightmanagement.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    
    @Autowired
    private FlightService flightService;
    
    /**
     * Register a new flight
     * POST /api/flights/registerFlight
     */
    @PostMapping("/registerFlight")
    public ResponseEntity<?> registerFlight(@Valid @RequestBody FlightDTO flightDTO) {
        try {
            FlightDTO registeredFlight = flightService.registerFlight(flightDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Flight registered successfully", registeredFlight));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while registering flight", null));
        }
    }
    
    /**
     * Get all flights
     * GET /api/flights
     */
    @GetMapping
    public ResponseEntity<?> getAllFlights() {
        try {
            List<FlightDTO> flights = flightService.getAllFlights();
            return ResponseEntity.ok(new ApiResponse(true, "Flights retrieved successfully", flights));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving flights", null));
        }
    }
    
    /**
     * Get flight by ID
     * GET /api/flights/{flightId}
     */
    @GetMapping("/{flightId}")
    public ResponseEntity<?> getFlightById(@PathVariable Long flightId) {
        try {
            return flightService.getFlightById(flightId)
                    .map(flight -> ResponseEntity.ok(new ApiResponse(true, "Flight retrieved successfully", flight)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving flight", null));
        }
    }
    
    /**
     * Update flight
     * PUT /api/flights/{flightId}
     */
    @PutMapping("/{flightId}")
    public ResponseEntity<?> updateFlight(@PathVariable Long flightId, @Valid @RequestBody FlightDTO flightDTO) {
        try {
            FlightDTO updatedFlight = flightService.updateFlight(flightId, flightDTO);
            return ResponseEntity.ok(new ApiResponse(true, "Flight updated successfully", updatedFlight));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while updating flight", null));
        }
    }
    
    /**
     * Delete flight
     * DELETE /api/flights/{flightId}
     */
    @DeleteMapping("/{flightId}")
    public ResponseEntity<?> deleteFlight(@PathVariable Long flightId) {
        try {
            flightService.deleteFlight(flightId);
            return ResponseEntity.ok(new ApiResponse(true, "Flight deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while deleting flight", null));
        }
    }
    
    /**
     * Search flights by origin and destination
     * GET /api/flights/search?origin={origin}&destination={destination}
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(@RequestParam String origin, @RequestParam String destination) {
        try {
            List<FlightDTO> flights = flightService.searchFlights(origin, destination);
            return ResponseEntity.ok(new ApiResponse(true, "Flights found successfully", flights));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while searching flights", null));
        }
    }
    
    /**
     * Get flights by carrier
     * GET /api/flights/carrier/{carrierId}
     */
    @GetMapping("/carrier/{carrierId}")
    public ResponseEntity<?> getFlightsByCarrier(@PathVariable Long carrierId) {
        try {
            List<FlightDTO> flights = flightService.getFlightsByCarrier(carrierId);
            return ResponseEntity.ok(new ApiResponse(true, "Flights retrieved successfully", flights));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving flights", null));
        }
    }
    
    /**
     * Get flights by carrier name
     * GET /api/flights/carrierName/{carrierName}
     */
    @GetMapping("/carrierName/{carrierName}")
    public ResponseEntity<?> getFlightsByCarrierName(@PathVariable String carrierName) {
        try {
            List<FlightDTO> flights = flightService.getFlightsByCarrierName(carrierName);
            return ResponseEntity.ok(new ApiResponse(true, "Flights retrieved successfully", flights));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving flights", null));
        }
    }
}
