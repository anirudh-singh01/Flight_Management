package com.airline.flightmanagement.controller;

import com.airline.flightmanagement.dto.CarrierDTO;
import com.airline.flightmanagement.service.CarrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/carriers")
@CrossOrigin(origins = "*")
public class CarrierController {
    
    @Autowired
    private CarrierService carrierService;
    
    /**
     * Register a new carrier
     * POST /api/carriers/registerCarrier
     */
    @PostMapping("/registerCarrier")
    public ResponseEntity<?> registerCarrier(@Valid @RequestBody CarrierDTO carrierDTO) {
        try {
            CarrierDTO registeredCarrier = carrierService.registerCarrier(carrierDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Carrier registered successfully", registeredCarrier));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while registering carrier", null));
        }
    }
    
    /**
     * Get carrier by ID
     * GET /api/carriers/{carrierId}
     */
    @GetMapping("/{carrierId}")
    public ResponseEntity<?> getCarrierById(@PathVariable Long carrierId) {
        try {
            CarrierDTO carrier = carrierService.getCarrierById(carrierId);
            return ResponseEntity.ok(new ApiResponse(true, "Carrier retrieved successfully", carrier));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving carrier", null));
        }
    }
    
    /**
     * Get carrier by name
     * GET /api/carriers/name/{carrierName}
     */
    @GetMapping("/name/{carrierName}")
    public ResponseEntity<?> getCarrierByName(@PathVariable String carrierName) {
        try {
            CarrierDTO carrier = carrierService.getCarrierByName(carrierName);
            return ResponseEntity.ok(new ApiResponse(true, "Carrier retrieved successfully", carrier));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving carrier", null));
        }
    }
    
    /**
     * Get all carriers
     * GET /api/carriers
     */
    @GetMapping
    public ResponseEntity<?> getAllCarriers() {
        try {
            List<CarrierDTO> carriers = carrierService.getAllCarriers();
            return ResponseEntity.ok(new ApiResponse(true, "Carriers retrieved successfully", carriers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving carriers", null));
        }
    }
    
    /**
     * Get active carriers
     * GET /api/carriers/active
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveCarriers() {
        try {
            List<CarrierDTO> carriers = carrierService.getActiveCarriers();
            return ResponseEntity.ok(new ApiResponse(true, "Active carriers retrieved successfully", carriers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while retrieving active carriers", null));
        }
    }
    
    /**
     * Update carrier
     * PUT /api/carriers/{carrierId}
     */
    @PutMapping("/{carrierId}")
    public ResponseEntity<?> updateCarrier(@PathVariable Long carrierId, @Valid @RequestBody CarrierDTO carrierDTO) {
        try {
            CarrierDTO updatedCarrier = carrierService.updateCarrier(carrierId, carrierDTO);
            return ResponseEntity.ok(new ApiResponse(true, "Carrier updated successfully", updatedCarrier));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while updating carrier", null));
        }
    }
    
    /**
     * Delete carrier
     * DELETE /api/carriers/{carrierId}
     */
    @DeleteMapping("/{carrierId}")
    public ResponseEntity<?> deleteCarrier(@PathVariable Long carrierId) {
        try {
            carrierService.deleteCarrier(carrierId);
            return ResponseEntity.ok(new ApiResponse(true, "Carrier deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "An error occurred while deleting carrier", null));
        }
    }
    
    /**
     * Inner class for API response format
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
