package com.airline.flightmanagement.service;

import com.airline.flightmanagement.dto.CarrierDTO;
import com.airline.flightmanagement.entity.Carrier;
import com.airline.flightmanagement.entity.DiscountType;
import com.airline.flightmanagement.entity.RefundType;
import com.airline.flightmanagement.repository.CarrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarrierService {
    
    @Autowired
    private CarrierRepository carrierRepository;
    
    /**
     * Register a new carrier
     */
    public CarrierDTO registerCarrier(CarrierDTO carrierDTO) {
        // Check if carrier name already exists
        if (carrierRepository.existsByCarrierName(carrierDTO.getCarrierName())) {
            throw new RuntimeException("Carrier with name '" + carrierDTO.getCarrierName() + "' already exists");
        }
        
        // Validate discount and refund percentages
        validatePercentages(carrierDTO.getDiscountPercentage(), carrierDTO.getRefundPercentage());
        
        // Create carrier entity
        Carrier carrier = new Carrier();
        carrier.setCarrierName(carrierDTO.getCarrierName());
        carrier.setDiscountPercentage(carrierDTO.getDiscountPercentage());
        carrier.setRefundPercentage(carrierDTO.getRefundPercentage());
        carrier.setDiscountType(carrierDTO.getDiscountType());
        carrier.setRefundType(carrierDTO.getRefundType());
        carrier.setDescription(carrierDTO.getDescription());
        carrier.setIsActive(true);
        
        // Save carrier
        Carrier savedCarrier = carrierRepository.save(carrier);
        
        // Return DTO
        return convertToDTO(savedCarrier);
    }
    
    /**
     * Get carrier by ID
     */
    public CarrierDTO getCarrierById(Long carrierId) {
        Carrier carrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Carrier not found with ID: " + carrierId));
        return convertToDTO(carrier);
    }
    
    /**
     * Get carrier by name
     */
    public CarrierDTO getCarrierByName(String carrierName) {
        Carrier carrier = carrierRepository.findByCarrierName(carrierName)
                .orElseThrow(() -> new RuntimeException("Carrier not found with name: " + carrierName));
        return convertToDTO(carrier);
    }
    
    /**
     * Get all carriers
     */
    public List<CarrierDTO> getAllCarriers() {
        List<Carrier> carriers = carrierRepository.findAll();
        return carriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active carriers
     */
    public List<CarrierDTO> getActiveCarriers() {
        List<Carrier> carriers = carrierRepository.findByIsActiveTrue();
        return carriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Update carrier
     */
    public CarrierDTO updateCarrier(Long carrierId, CarrierDTO carrierDTO) {
        Carrier existingCarrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Carrier not found with ID: " + carrierId));
        
        // Check if new name conflicts with existing carriers (excluding current one)
        if (!existingCarrier.getCarrierName().equals(carrierDTO.getCarrierName()) &&
            carrierRepository.existsByCarrierName(carrierDTO.getCarrierName())) {
            throw new RuntimeException("Carrier with name '" + carrierDTO.getCarrierName() + "' already exists");
        }
        
        // Validate discount and refund percentages
        validatePercentages(carrierDTO.getDiscountPercentage(), carrierDTO.getRefundPercentage());
        
        // Update fields
        existingCarrier.setCarrierName(carrierDTO.getCarrierName());
        existingCarrier.setDiscountPercentage(carrierDTO.getDiscountPercentage());
        existingCarrier.setRefundPercentage(carrierDTO.getRefundPercentage());
        existingCarrier.setDiscountType(carrierDTO.getDiscountType());
        existingCarrier.setRefundType(carrierDTO.getRefundType());
        existingCarrier.setDescription(carrierDTO.getDescription());
        
        // Save updated carrier
        Carrier updatedCarrier = carrierRepository.save(existingCarrier);
        
        // Return DTO
        return convertToDTO(updatedCarrier);
    }
    
    /**
     * Delete carrier (soft delete by setting isActive to false)
     */
    public void deleteCarrier(Long carrierId) {
        Carrier carrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Carrier not found with ID: " + carrierId));
        
        carrier.setIsActive(false);
        carrierRepository.save(carrier);
    }
    
    /**
     * Get carriers by discount type
     */
    public List<CarrierDTO> getCarriersByDiscountType(DiscountType discountType) {
        List<Carrier> carriers = carrierRepository.findByDiscountType(discountType);
        return carriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get carriers by refund type
     */
    public List<CarrierDTO> getCarriersByRefundType(RefundType refundType) {
        List<Carrier> carriers = carrierRepository.findByRefundType(refundType);
        return carriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Validate discount and refund percentages
     */
    private void validatePercentages(BigDecimal discountPercentage, BigDecimal refundPercentage) {
        if (discountPercentage.compareTo(BigDecimal.ZERO) <= 0 || 
            discountPercentage.compareTo(new BigDecimal("100")) >= 0) {
            throw new RuntimeException("Discount percentage must be between 0 and 100");
        }
        
        if (refundPercentage.compareTo(BigDecimal.ZERO) <= 0 || 
            refundPercentage.compareTo(new BigDecimal("100")) >= 0) {
            throw new RuntimeException("Refund percentage must be between 0 and 100");
        }
    }
    
    /**
     * Convert entity to DTO
     */
    private CarrierDTO convertToDTO(Carrier carrier) {
        return new CarrierDTO(
            carrier.getCarrierId(),
            carrier.getCarrierName(),
            carrier.getDiscountPercentage(),
            carrier.getRefundPercentage(),
            carrier.getDiscountType(),
            carrier.getRefundType(),
            carrier.getDescription(),
            carrier.getIsActive()
        );
    }
}
