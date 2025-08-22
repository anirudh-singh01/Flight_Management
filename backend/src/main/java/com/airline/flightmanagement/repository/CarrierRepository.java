package com.airline.flightmanagement.repository;

import com.airline.flightmanagement.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    
    /**
     * Find carrier by name
     */
    Optional<Carrier> findByCarrierName(String carrierName);
    
    /**
     * Find carriers by discount type
     */
    List<Carrier> findByDiscountType(com.airline.flightmanagement.entity.DiscountType discountType);
    
    /**
     * Find carriers by refund type
     */
    List<Carrier> findByRefundType(com.airline.flightmanagement.entity.RefundType refundType);
    
    /**
     * Find active carriers
     */
    List<Carrier> findByIsActiveTrue();
    
    /**
     * Find carriers by discount percentage range
     */
    @Query("SELECT c FROM Carrier c WHERE c.discountPercentage BETWEEN :minDiscount AND :maxDiscount")
    List<Carrier> findByDiscountPercentageRange(@Param("minDiscount") java.math.BigDecimal minDiscount, 
                                               @Param("maxDiscount") java.math.BigDecimal maxDiscount);
    
    /**
     * Find carriers by refund percentage range
     */
    @Query("SELECT c FROM Carrier c WHERE c.refundPercentage BETWEEN :minRefund AND :maxRefund")
    List<Carrier> findByRefundPercentageRange(@Param("minRefund") java.math.BigDecimal minRefund, 
                                             @Param("maxRefund") java.math.BigDecimal maxRefund);
    
    /**
     * Check if carrier name exists
     */
    boolean existsByCarrierName(String carrierName);
}
