package com.airline.flightmanagement.service;

import com.airline.flightmanagement.dto.FlightDTO;
import com.airline.flightmanagement.entity.Flight;
import com.airline.flightmanagement.entity.Carrier;
import com.airline.flightmanagement.repository.FlightRepository;
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
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private CarrierRepository carrierRepository;

    @InjectMocks
    private FlightService flightService;

    private Carrier testCarrier;
    private Flight testFlight;
    private FlightDTO testFlightDTO;

    @BeforeEach
    void setUp() {
        // Setup test carrier
        testCarrier = new Carrier();
        testCarrier.setCarrierId(1L);
        testCarrier.setCarrierName("Test Airlines");

        // Setup test flight
        testFlight = new Flight();
        testFlight.setFlightId(1L);
        testFlight.setCarrier(testCarrier);
        testFlight.setOrigin("New York");
        testFlight.setDestination("Los Angeles");
        testFlight.setAirFare(new BigDecimal("299.99"));
        testFlight.setSeatCapacityBusiness(20);
        testFlight.setSeatCapacityEconomy(150);
        testFlight.setSeatCapacityExecutive(10);

        // Setup test flight DTO
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
    void testRegisterFlight_Success() {
        // Arrange
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(testCarrier));
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        // Act
        FlightDTO result = flightService.registerFlight(testFlightDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testFlight.getFlightId(), result.getFlightId());
        assertEquals(testFlight.getCarrier().getCarrierId(), result.getCarrierId());
        assertEquals(testFlight.getCarrier().getCarrierName(), result.getCarrierName());
        assertEquals(testFlight.getOrigin(), result.getOrigin());
        assertEquals(testFlight.getDestination(), result.getDestination());
        assertEquals(testFlight.getAirFare(), result.getAirFare());
        assertEquals(testFlight.getSeatCapacityBusiness(), result.getSeatCapacityBusiness());
        assertEquals(testFlight.getSeatCapacityEconomy(), result.getSeatCapacityEconomy());
        assertEquals(testFlight.getSeatCapacityExecutive(), result.getSeatCapacityExecutive());

        verify(carrierRepository).findById(1L);
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void testRegisterFlight_CarrierNotFound() {
        // Arrange
        when(carrierRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.registerFlight(testFlightDTO);
        });

        assertEquals("Carrier not found with id: 1", exception.getMessage());
        verify(carrierRepository).findById(1L);
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testGetAllFlights_Success() {
        // Arrange
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightRepository.findAll()).thenReturn(flights);

        // Act
        List<FlightDTO> result = flightService.getAllFlights();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFlight.getFlightId(), result.get(0).getFlightId());
        verify(flightRepository).findAll();
    }

    @Test
    void testGetFlightById_Success() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

        // Act
        Optional<FlightDTO> result = flightService.getFlightById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testFlight.getFlightId(), result.get().getFlightId());
        verify(flightRepository).findById(1L);
    }

    @Test
    void testGetFlightById_NotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<FlightDTO> result = flightService.getFlightById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(flightRepository).findById(1L);
    }

    @Test
    void testUpdateFlight_Success() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(testCarrier));
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        // Act
        FlightDTO result = flightService.updateFlight(1L, testFlightDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testFlight.getFlightId(), result.getFlightId());
        assertEquals(testFlight.getCarrier().getCarrierId(), result.getCarrierId());
        assertEquals(testFlight.getCarrier().getCarrierName(), result.getCarrierName());
        assertEquals(testFlight.getOrigin(), result.getOrigin());
        assertEquals(testFlight.getDestination(), result.getDestination());
        assertEquals(testFlight.getAirFare(), result.getAirFare());
        assertEquals(testFlight.getSeatCapacityBusiness(), result.getSeatCapacityBusiness());
        assertEquals(testFlight.getSeatCapacityEconomy(), result.getSeatCapacityEconomy());
        assertEquals(testFlight.getSeatCapacityExecutive(), result.getSeatCapacityExecutive());
        
        verify(flightRepository).findById(1L);
        verify(carrierRepository).findById(1L);
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void testUpdateFlight_FlightNotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.updateFlight(1L, testFlightDTO);
        });

        assertEquals("Flight not found with id: 1", exception.getMessage());
        verify(flightRepository).findById(1L);
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testUpdateFlight_CarrierNotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(carrierRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.updateFlight(1L, testFlightDTO);
        });

        assertEquals("Carrier not found with id: 1", exception.getMessage());
        verify(flightRepository).findById(1L);
        verify(carrierRepository).findById(1L);
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testUpdateFlight_VerifyDataUpdate() {
        // Arrange
        Flight existingFlight = new Flight();
        existingFlight.setFlightId(1L);
        existingFlight.setCarrier(testCarrier);
        existingFlight.setOrigin("Old Origin");
        existingFlight.setDestination("Old Destination");
        existingFlight.setAirFare(new BigDecimal("199.99"));
        existingFlight.setSeatCapacityBusiness(10);
        existingFlight.setSeatCapacityEconomy(100);
        existingFlight.setSeatCapacityExecutive(5);

        FlightDTO updateDTO = new FlightDTO();
        updateDTO.setCarrierId(1L);
        updateDTO.setOrigin("New Origin");
        updateDTO.setDestination("New Destination");
        updateDTO.setAirFare(new BigDecimal("399.99"));
        updateDTO.setSeatCapacityBusiness(30);
        updateDTO.setSeatCapacityEconomy(200);
        updateDTO.setSeatCapacityExecutive(15);

        when(flightRepository.findById(1L)).thenReturn(Optional.of(existingFlight));
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(testCarrier));
        when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);

        // Act
        FlightDTO result = flightService.updateFlight(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(flightRepository).findById(1L);
        verify(carrierRepository).findById(1L);
        verify(flightRepository).save(any(Flight.class));
        
        // Verify that the existing flight object was updated with new values
        assertEquals("New Origin", existingFlight.getOrigin());
        assertEquals("New Destination", existingFlight.getDestination());
        assertEquals(new BigDecimal("399.99"), existingFlight.getAirFare());
        assertEquals(30, existingFlight.getSeatCapacityBusiness());
        assertEquals(200, existingFlight.getSeatCapacityEconomy());
        assertEquals(15, existingFlight.getSeatCapacityExecutive());
    }

    @Test
    void testDeleteFlight_Success() {
        // Arrange
        when(flightRepository.existsById(1L)).thenReturn(true);
        doNothing().when(flightRepository).deleteById(1L);

        // Act
        flightService.deleteFlight(1L);

        // Assert
        verify(flightRepository).existsById(1L);
        verify(flightRepository).deleteById(1L);
    }

    @Test
    void testDeleteFlight_FlightNotFound() {
        // Arrange
        when(flightRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            flightService.deleteFlight(1L);
        });

        assertEquals("Flight not found with id: 1", exception.getMessage());
        verify(flightRepository).existsById(1L);
        verify(flightRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void testSearchFlights_Success() {
        // Arrange
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightRepository.findByOriginAndDestination("New York", "Los Angeles")).thenReturn(flights);

        // Act
        List<FlightDTO> result = flightService.searchFlights("New York", "Los Angeles");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(flightRepository).findByOriginAndDestination("New York", "Los Angeles");
    }

    @Test
    void testGetFlightsByCarrier_Success() {
        // Arrange
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightRepository.findByCarrierCarrierId(1L)).thenReturn(flights);

        // Act
        List<FlightDTO> result = flightService.getFlightsByCarrier(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(flightRepository).findByCarrierCarrierId(1L);
    }

    @Test
    void testGetFlightsByCarrierName_Success() {
        // Arrange
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightRepository.findByCarrierName("Test Airlines")).thenReturn(flights);

        // Act
        List<FlightDTO> result = flightService.getFlightsByCarrierName("Test Airlines");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFlight.getCarrier().getCarrierName(), result.get(0).getCarrierName());
        verify(flightRepository).findByCarrierName("Test Airlines");
    }

    @Test
    void testGetFlightsByCarrierName_NoFlightsFound() {
        // Arrange
        when(flightRepository.findByCarrierName("NonExistent Airlines")).thenReturn(Arrays.asList());

        // Act
        List<FlightDTO> result = flightService.getFlightsByCarrierName("NonExistent Airlines");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(flightRepository).findByCarrierName("NonExistent Airlines");
    }
}
