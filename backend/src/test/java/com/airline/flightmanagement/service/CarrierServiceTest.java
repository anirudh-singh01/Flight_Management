package com.airline.flightmanagement.service;

import com.airline.flightmanagement.dto.CarrierDTO;
import com.airline.flightmanagement.entity.Carrier;
import com.airline.flightmanagement.entity.DiscountType;
import com.airline.flightmanagement.entity.RefundType;
import com.airline.flightmanagement.repository.CarrierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrierServiceTest {

    @Mock
    private CarrierRepository carrierRepository;

    @InjectMocks
    private CarrierService carrierService;

    private CarrierDTO testCarrierDTO;
    private Carrier testCarrier;

    @BeforeEach
    void setUp() {
        testCarrierDTO = new CarrierDTO();
        testCarrierDTO.setCarrierName("Test Airlines");
        testCarrierDTO.setDiscountPercentage(new BigDecimal("15.50"));
        testCarrierDTO.setRefundPercentage(new BigDecimal("25.00"));
        testCarrierDTO.setDiscountType(DiscountType.SILVER);
        testCarrierDTO.setRefundType(RefundType.TEN_DAYS);
        testCarrierDTO.setDescription("Test carrier for unit testing");

        testCarrier = new Carrier();
        testCarrier.setCarrierId(1L);
        testCarrier.setCarrierName("Test Airlines");
        testCarrier.setDiscountPercentage(new BigDecimal("15.50"));
        testCarrier.setRefundPercentage(new BigDecimal("25.00"));
        testCarrier.setDiscountType(DiscountType.SILVER);
        testCarrier.setRefundType(RefundType.TEN_DAYS);
        testCarrier.setDescription("Test carrier for unit testing");
        testCarrier.setIsActive(true);
    }

    @Test
    void testRegisterCarrier_Success() {
        // Arrange
        when(carrierRepository.existsByCarrierName("Test Airlines")).thenReturn(false);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(testCarrier);

        // Act
        CarrierDTO result = carrierService.registerCarrier(testCarrierDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Airlines", result.getCarrierName());
        assertEquals(new BigDecimal("15.50"), result.getDiscountPercentage());
        assertEquals(new BigDecimal("25.00"), result.getRefundPercentage());
        assertEquals(DiscountType.SILVER, result.getDiscountType());
        assertEquals(RefundType.TEN_DAYS, result.getRefundType());
        assertEquals("Test carrier for unit testing", result.getDescription());
        assertTrue(result.getIsActive());

        verify(carrierRepository).existsByCarrierName("Test Airlines");
        verify(carrierRepository).save(any(Carrier.class));
    }

    @Test
    void testRegisterCarrier_DuplicateName() {
        // Arrange
        when(carrierRepository.existsByCarrierName("Test Airlines")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrierService.registerCarrier(testCarrierDTO);
        });

        assertEquals("Carrier with name 'Test Airlines' already exists", exception.getMessage());
        verify(carrierRepository).existsByCarrierName("Test Airlines");
        verify(carrierRepository, never()).save(any(Carrier.class));
    }

    @Test
    void testRegisterCarrier_InvalidDiscountPercentage() {
        // Arrange
        testCarrierDTO.setDiscountPercentage(new BigDecimal("150.00")); // Invalid percentage

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrierService.registerCarrier(testCarrierDTO);
        });

        assertEquals("Discount percentage must be between 0 and 100", exception.getMessage());
        verify(carrierRepository, never()).save(any(Carrier.class));
    }

    @Test
    void testRegisterCarrier_InvalidRefundPercentage() {
        // Arrange
        testCarrierDTO.setRefundPercentage(new BigDecimal("0.00")); // Invalid percentage

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrierService.registerCarrier(testCarrierDTO);
        });

        assertEquals("Refund percentage must be between 0 and 100", exception.getMessage());
        verify(carrierRepository, never()).save(any(Carrier.class));
    }

    @Test
    void testGetCarrierById_Success() {
        // Arrange
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(testCarrier));

        // Act
        CarrierDTO result = carrierService.getCarrierById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCarrierId());
        assertEquals("Test Airlines", result.getCarrierName());
        verify(carrierRepository).findById(1L);
    }

    @Test
    void testGetCarrierById_NotFound() {
        // Arrange
        when(carrierRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrierService.getCarrierById(999L);
        });

        assertEquals("Carrier not found with ID: 999", exception.getMessage());
        verify(carrierRepository).findById(999L);
    }

    @Test
    void testGetCarrierByName_Success() {
        // Arrange
        when(carrierRepository.findByCarrierName("Test Airlines")).thenReturn(Optional.of(testCarrier));

        // Act
        CarrierDTO result = carrierService.getCarrierByName("Test Airlines");

        // Assert
        assertNotNull(result);
        assertEquals("Test Airlines", result.getCarrierName());
        verify(carrierRepository).findByCarrierName("Test Airlines");
    }

    @Test
    void testGetCarrierByName_NotFound() {
        // Arrange
        when(carrierRepository.findByCarrierName("NonExistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrierService.getCarrierByName("NonExistent");
        });

        assertEquals("Carrier not found with name: NonExistent", exception.getMessage());
        verify(carrierRepository).findByCarrierName("NonExistent");
    }

    @Test
    void testGetAllCarriers_Success() {
        // Arrange
        Carrier carrier2 = new Carrier();
        carrier2.setCarrierId(2L);
        carrier2.setCarrierName("Another Airlines");
        carrier2.setDiscountPercentage(new BigDecimal("20.00"));
        carrier2.setRefundPercentage(new BigDecimal("30.00"));
        carrier2.setDiscountType(DiscountType.GOLD);
        carrier2.setRefundType(RefundType.TWENTY_DAYS);
        carrier2.setIsActive(true);

        List<Carrier> carriers = Arrays.asList(testCarrier, carrier2);
        when(carrierRepository.findAll()).thenReturn(carriers);

        // Act
        List<CarrierDTO> result = carrierService.getAllCarriers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Airlines", result.get(0).getCarrierName());
        assertEquals("Another Airlines", result.get(1).getCarrierName());
        verify(carrierRepository).findAll();
    }

    @Test
    void testGetActiveCarriers_Success() {
        // Arrange
        when(carrierRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testCarrier));

        // Act
        List<CarrierDTO> result = carrierService.getActiveCarriers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Airlines", result.get(0).getCarrierName());
        assertTrue(result.get(0).getIsActive());
        verify(carrierRepository).findByIsActiveTrue();
    }

    @Test
    void testUpdateCarrier_Success() {
        // Arrange
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setCarrierName("Updated Airlines");
        updateDTO.setDiscountPercentage(new BigDecimal("25.00"));
        updateDTO.setRefundPercentage(new BigDecimal("35.00"));
        updateDTO.setDiscountType(DiscountType.PLATINUM);
        updateDTO.setRefundType(RefundType.TWENTY_DAYS);
        updateDTO.setDescription("Updated description");

        when(carrierRepository.findById(1L)).thenReturn(Optional.of(testCarrier));
        when(carrierRepository.existsByCarrierName("Updated Airlines")).thenReturn(false);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(testCarrier);

        // Act
        CarrierDTO result = carrierService.updateCarrier(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(carrierRepository).findById(1L);
        verify(carrierRepository).existsByCarrierName("Updated Airlines");
        verify(carrierRepository).save(any(Carrier.class));
    }

    @Test
    void testDeleteCarrier_Success() {
        // Arrange
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(testCarrier));
        when(carrierRepository.save(any(Carrier.class))).thenReturn(testCarrier);

        // Act
        carrierService.deleteCarrier(1L);

        // Assert
        verify(carrierRepository).findById(1L);
        verify(carrierRepository).save(any(Carrier.class));
        assertFalse(testCarrier.getIsActive());
    }

    @Test
    void testUpdateCarrier_NotFound() {
        // Arrange
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setCarrierName("Updated Airlines");
        updateDTO.setDiscountPercentage(new BigDecimal("25.00"));
        updateDTO.setRefundPercentage(new BigDecimal("35.00"));
        updateDTO.setDiscountType(DiscountType.PLATINUM);
        updateDTO.setRefundType(RefundType.TWENTY_DAYS);

        when(carrierRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrierService.updateCarrier(999L, updateDTO);
        });

        assertEquals("Carrier not found with ID: 999", exception.getMessage());
        verify(carrierRepository).findById(999L);
        verify(carrierRepository, never()).save(any(Carrier.class));
    }

    @Test
    void testUpdateCarrier_NameConflict() {
        // Arrange
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setCarrierName("Another Airlines"); // Different name that conflicts
        updateDTO.setDiscountPercentage(new BigDecimal("25.00"));
        updateDTO.setRefundPercentage(new BigDecimal("35.00"));
        updateDTO.setDiscountType(DiscountType.PLATINUM);
        updateDTO.setRefundType(RefundType.TWENTY_DAYS);

        when(carrierRepository.findById(1L)).thenReturn(Optional.of(testCarrier));
        when(carrierRepository.existsByCarrierName("Another Airlines")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrierService.updateCarrier(1L, updateDTO);
        });

        assertEquals("Carrier with name 'Another Airlines' already exists", exception.getMessage());
        verify(carrierRepository).findById(1L);
        verify(carrierRepository).existsByCarrierName("Another Airlines");
        verify(carrierRepository, never()).save(any(Carrier.class));
    }

    @Test
    void testUpdateCarrier_SameNameNoConflict() {
        // Arrange
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setCarrierName("Test Airlines"); // Same name as existing
        updateDTO.setDiscountPercentage(new BigDecimal("25.00"));
        updateDTO.setRefundPercentage(new BigDecimal("35.00"));
        updateDTO.setDiscountType(DiscountType.PLATINUM);
        updateDTO.setRefundType(RefundType.TWENTY_DAYS);

        when(carrierRepository.findById(1L)).thenReturn(Optional.of(testCarrier));
        when(carrierRepository.existsByCarrierName("Test Airlines")).thenReturn(false);
        when(carrierRepository.save(any(Carrier.class))).thenReturn(testCarrier);

        // Act
        CarrierDTO result = carrierService.updateCarrier(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(carrierRepository).findById(1L);
        verify(carrierRepository).existsByCarrierName("Test Airlines");
        verify(carrierRepository).save(any(Carrier.class));
    }

    @Test
    void testUpdateCarrier_InvalidPercentages() {
        // Arrange
        CarrierDTO updateDTO = new CarrierDTO();
        updateDTO.setCarrierName("Updated Airlines");
        updateDTO.setDiscountPercentage(new BigDecimal("0.00")); // Invalid: 0%
        updateDTO.setRefundPercentage(new BigDecimal("100.00")); // Invalid: 100%
        updateDTO.setDiscountType(DiscountType.PLATINUM);
        updateDTO.setRefundType(RefundType.TWENTY_DAYS);

        when(carrierRepository.findById(1L)).thenReturn(Optional.of(testCarrier));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carrierService.updateCarrier(1L, updateDTO);
        });

        assertEquals("Discount percentage must be between 0 and 100", exception.getMessage());
        verify(carrierRepository).findById(1L);
        verify(carrierRepository, never()).save(any(Carrier.class));
    }
}
