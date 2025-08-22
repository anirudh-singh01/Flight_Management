package com.airline.flightmanagement.dto;

import com.airline.flightmanagement.entity.DiscountType;
import com.airline.flightmanagement.entity.RefundType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CarrierDTO {
    
    private Long carrierId;
    
    @NotBlank(message = "Carrier name is required")
    @Size(min = 2, max = 100, message = "Carrier name must be between 2 and 100 characters")
    private String carrierName;
    
    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount percentage must be greater than 0")
    @DecimalMax(value = "100.0", inclusive = false, message = "Discount percentage must be less than 100")
    private BigDecimal discountPercentage;
    
    @NotNull(message = "Refund percentage is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Refund percentage must be greater than 0")
    @DecimalMax(value = "100.0", inclusive = false, message = "Refund percentage must be less than 100")
    private BigDecimal refundPercentage;
    
    @NotNull(message = "Discount type is required")
    private DiscountType discountType;
    
    @NotNull(message = "Refund type is required")
    private RefundType refundType;
    
    private String description;
    
    private Boolean isActive = true;
    
    // Default constructor
    public CarrierDTO() {}
    
    // Constructor with required fields
    public CarrierDTO(String carrierName, BigDecimal discountPercentage, BigDecimal refundPercentage,
                      DiscountType discountType, RefundType refundType) {
        this.carrierName = carrierName;
        this.discountPercentage = discountPercentage;
        this.refundPercentage = refundPercentage;
        this.discountType = discountType;
        this.refundType = refundType;
    }
    
    // Constructor with all fields
    public CarrierDTO(Long carrierId, String carrierName, BigDecimal discountPercentage, 
                      BigDecimal refundPercentage, DiscountType discountType, RefundType refundType,
                      String description, Boolean isActive) {
        this.carrierId = carrierId;
        this.carrierName = carrierName;
        this.discountPercentage = discountPercentage;
        this.refundPercentage = refundPercentage;
        this.discountType = discountType;
        this.refundType = refundType;
        this.description = description;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public Long getCarrierId() {
        return carrierId;
    }
    
    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }
    
    public String getCarrierName() {
        return carrierName;
    }
    
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
    
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public BigDecimal getRefundPercentage() {
        return refundPercentage;
    }
    
    public void setRefundPercentage(BigDecimal refundPercentage) {
        this.refundPercentage = refundPercentage;
    }
    
    public DiscountType getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }
    
    public RefundType getRefundType() {
        return refundType;
    }
    
    public void setRefundType(RefundType refundType) {
        this.refundType = refundType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    @Override
    public String toString() {
        return "CarrierDTO{" +
                "carrierId=" + carrierId +
                ", carrierName='" + carrierName + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", refundPercentage=" + refundPercentage +
                ", discountType=" + discountType +
                ", refundType=" + refundType +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
