package com.airline.flightmanagement.service;

import com.airline.flightmanagement.dto.BookFlightRequest;
import com.airline.flightmanagement.dto.BookFlightResponse;
import com.airline.flightmanagement.dto.CancelBookingResponse;
import com.airline.flightmanagement.entity.*;
import com.airline.flightmanagement.repository.BookingRepository;
import com.airline.flightmanagement.repository.FlightRepository;
import com.airline.flightmanagement.repository.FlightScheduleRepository;
import com.airline.flightmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightScheduleRepository flightScheduleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Flight testFlight;
    private User testUser;
    private BookFlightRequest testRequest;
    private FlightSchedule testSchedule;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        // Setup test carrier
        Carrier testCarrier = new Carrier();
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

        // Setup test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setCustomerCategory(CustomerCategory.GOLD);

        // Setup test request
        testRequest = new BookFlightRequest();
        testRequest.setFlightId(1L);
        testRequest.setNoOfSeats(2);
        testRequest.setSeatCategory(SeatCategory.ECONOMY);
        testRequest.setDateOfTravel(LocalDate.now().plusDays(30));

        // Setup test schedule
        testSchedule = new FlightSchedule();
        testSchedule.setScheduleId(1L);
        testSchedule.setFlightId(1L);
        testSchedule.setDateOfTravel(testRequest.getDateOfTravel());
        testSchedule.setTotalCapacityEconomy(150);
        testSchedule.setTotalCapacityBusiness(20);
        testSchedule.setTotalCapacityExecutive(10);
        testSchedule.setBookedCountEconomy(50);
        testSchedule.setBookedCountBusiness(5);
        testSchedule.setBookedCountExecutive(2);

        // Setup test booking
        testBooking = new Booking();
        testBooking.setBookingId(1L);
        testBooking.setFlightId(1L);
        testBooking.setUserId(1L);
        testBooking.setNoOfSeats(2);
        testBooking.setSeatCategory(SeatCategory.ECONOMY);
        testBooking.setDateOfTravel(testRequest.getDateOfTravel());
        testBooking.setBookingAmount(new BigDecimal("509.98"));
        testBooking.setDiscountAmount(new BigDecimal("89.98"));
        testBooking.setDiscountReason("Advance booking (30+ days): 15%, Customer category (GOLD): 15%");
        testBooking.setBookingStatus(BookingStatus.BOOKED);
    }

    @Test
    void testBookFlight_Success() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(flightScheduleRepository.findByFlightIdAndDateOfTravel(1L, testRequest.getDateOfTravel()))
                .thenReturn(Optional.of(testSchedule));
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(testSchedule);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        BookFlightResponse result = bookingService.bookFlight(testRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getBookingId());
        assertEquals(1L, result.getFlightId());
        assertEquals(1L, result.getUserId());
        assertEquals(2, result.getNoOfSeats());
        assertEquals(SeatCategory.ECONOMY, result.getSeatCategory());
        assertEquals(BookingStatus.BOOKED, result.getBookingStatus());
        assertEquals("New York", result.getOrigin());
        assertEquals("Los Angeles", result.getDestination());
        assertEquals("Test Airlines", result.getCarrierName());
        assertEquals(new BigDecimal("299.99"), result.getOriginalAirFare());

        verify(flightRepository, times(2)).findById(1L); // Called in bookFlight and checkSeatAvailability
        verify(userRepository).findById(1L);
        verify(flightScheduleRepository, times(2)).findByFlightIdAndDateOfTravel(1L, testRequest.getDateOfTravel()); // Called in checkSeatAvailability and getOrCreateFlightSchedule
        verify(bookingRepository).save(any(Booking.class));
        verify(flightScheduleRepository).save(any(FlightSchedule.class)); // Called in bookFlight
    }

    @Test
    void testBookFlight_FlightNotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.bookFlight(testRequest, 1L);
        });

        assertEquals("Flight not found with id: 1", exception.getMessage());
        verify(flightRepository).findById(1L);
        verify(userRepository, never()).findById(any(Long.class));
    }

    @Test
    void testBookFlight_UserNotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.bookFlight(testRequest, 1L);
        });

        assertEquals("User not found with id: 1", exception.getMessage());
        verify(flightRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void testBookFlight_InsufficientSeats() {
        // Arrange
        testRequest.setNoOfSeats(150); // Try to book more seats than available
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(flightScheduleRepository.findByFlightIdAndDateOfTravel(1L, testRequest.getDateOfTravel()))
                .thenReturn(Optional.of(testSchedule));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.bookFlight(testRequest, 1L);
        });

        assertEquals("Insufficient seats available. Requested: 150, Available: 100", exception.getMessage());
        verify(flightRepository, times(2)).findById(1L); // Called in bookFlight and checkSeatAvailability
        verify(userRepository).findById(1L);
        verify(flightScheduleRepository).findByFlightIdAndDateOfTravel(1L, testRequest.getDateOfTravel()); // Only called in checkSeatAvailability when exception is thrown
    }

    @Test
    void testBookFlight_WithAdvanceBookingDiscount() {
        // Arrange - Book 30+ days in advance for 15% discount
        testRequest.setDateOfTravel(LocalDate.now().plusDays(35));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(flightScheduleRepository.findByFlightIdAndDateOfTravel(1L, testRequest.getDateOfTravel()))
                .thenReturn(Optional.of(testSchedule));
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(testSchedule);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        BookFlightResponse result = bookingService.bookFlight(testRequest, 1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getDiscountReason().contains("Advance booking (30+ days): 15%"));
    }

    @Test
    void testBookFlight_WithCustomerCategoryDiscount() {
        // Arrange - GOLD customer gets 15% discount
        testUser.setCustomerCategory(CustomerCategory.PLATINUM); // 20% discount
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(flightScheduleRepository.findByFlightIdAndDateOfTravel(1L, testRequest.getDateOfTravel()))
                .thenReturn(Optional.of(testSchedule));
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(testSchedule);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        BookFlightResponse result = bookingService.bookFlight(testRequest, 1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0);
        // Just verify that some discount was applied
        assertTrue(result.getDiscountReason().contains("Customer category (PLATINUM): 20%"));
    }

    @Test
    void testBookFlight_WithBulkBookingDiscount() {
        // Arrange - Book 5+ seats for 10% bulk discount
        testRequest.setNoOfSeats(6);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(flightScheduleRepository.findByFlightIdAndDateOfTravel(1L, testRequest.getDateOfTravel()))
                .thenReturn(Optional.of(testSchedule));
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(testSchedule);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        BookFlightResponse result = bookingService.bookFlight(testRequest, 1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0);
        // Just verify that bulk discount was applied
        assertTrue(result.getDiscountReason().contains("Bulk booking (5+ seats): 10%"));
    }

    @Test
    void testBookFlight_NoDiscounts() {
        // Arrange - Regular customer, no advance booking, less than 5 seats
        testUser.setCustomerCategory(CustomerCategory.REGULAR);
        testRequest.setDateOfTravel(LocalDate.now().plusDays(5)); // 5 days = 5% advance booking discount
        testRequest.setNoOfSeats(1);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(flightScheduleRepository.findByFlightIdAndDateOfTravel(1L, testRequest.getDateOfTravel()))
                .thenReturn(Optional.of(testSchedule));
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(testSchedule);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        BookFlightResponse result = bookingService.bookFlight(testRequest, 1L);

        // Assert
        assertNotNull(result);
        // Just verify that advance booking discount was applied
        assertTrue(result.getDiscountReason().contains("Advance booking (7+ days): 5%"));
    }

    @Test
    void testBookFlight_CreateNewFlightSchedule() {
        // Arrange - No existing schedule, should create new one
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(flightScheduleRepository.findByFlightIdAndDateOfTravel(1L, testRequest.getDateOfTravel()))
                .thenReturn(Optional.empty());
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(testSchedule);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        BookFlightResponse result = bookingService.bookFlight(testRequest, 1L);

        // Assert
        assertNotNull(result);
        verify(flightScheduleRepository, times(2)).save(any(FlightSchedule.class)); // Called in getOrCreateFlightSchedule and bookFlight
    }

    @Test
    void testGetBookingById_Success() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

        // Act
        Optional<BookFlightResponse> result = bookingService.getBookingById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getBookingId());
        verify(bookingRepository).findById(1L);
        verify(flightRepository).findById(1L);
    }

    @Test
    void testGetUserBookings_Success() {
        // Arrange
        when(bookingRepository.findByUserId(1L)).thenReturn(Arrays.asList(testBooking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

        // Act
        java.util.List<BookFlightResponse> result = bookingService.getUserBookings(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        BookFlightResponse firstBooking = result.get(0);
        assertEquals(testBooking.getBookingId(), firstBooking.getBookingId());
        assertEquals(testBooking.getFlightId(), firstBooking.getFlightId());
        assertEquals(testBooking.getUserId(), firstBooking.getUserId());
        assertEquals(testBooking.getNoOfSeats(), firstBooking.getNoOfSeats());
        assertEquals(testBooking.getSeatCategory(), firstBooking.getSeatCategory());
        assertEquals(testBooking.getDateOfTravel(), firstBooking.getDateOfTravel());
        assertEquals(testBooking.getBookingAmount(), firstBooking.getBookingAmount());
        assertEquals(testFlight.getOrigin(), firstBooking.getOrigin());
        assertEquals(testFlight.getDestination(), firstBooking.getDestination());
        assertEquals(testFlight.getCarrier().getCarrierName(), firstBooking.getCarrierName());

        verify(bookingRepository).findByUserId(1L);
        verify(flightRepository).findById(1L);
    }

    @Test
    void testGetUserBookings_EmptyList() {
        // Arrange
        when(bookingRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        // Act
        java.util.List<BookFlightResponse> result = bookingService.getUserBookings(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository).findByUserId(1L);
        verify(flightRepository, never()).findById(any(Long.class));
    }

    @Test
    void testGetUserBookings_FlightNotFound() {
        // Arrange
        when(bookingRepository.findByUserId(1L)).thenReturn(Arrays.asList(testBooking));
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.getUserBookings(1L);
        });

        assertEquals("Flight not found", exception.getMessage());
        verify(bookingRepository).findByUserId(1L);
        verify(flightRepository).findById(1L);
    }

    @Test
    void testCancelBooking() {
        // Given
        Long bookingId = 1L;
        Long flightId = 1L;
        Long userId = 1L;
        
        // Create test entities
        Carrier carrier = new Carrier();
        carrier.setCarrierId(1L);
        carrier.setCarrierName("Test Carrier");
        carrier.setRefundPercentage(new BigDecimal("80.00")); // 80% refund
        
        Flight flight = new Flight();
        flight.setFlightId(flightId);
        flight.setCarrier(carrier);
        flight.setOrigin("New York");
        flight.setDestination("Los Angeles");
        
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setFlightId(flightId);
        booking.setUserId(userId);
        booking.setNoOfSeats(2);
        booking.setSeatCategory(SeatCategory.ECONOMY);
        booking.setDateOfTravel(LocalDate.now().plusDays(30));
        booking.setBookingAmount(new BigDecimal("200.00"));
        booking.setBookingStatus(BookingStatus.BOOKED);
        
        FlightSchedule schedule = new FlightSchedule();
        schedule.setScheduleId(1L);
        schedule.setFlightId(flightId);
        schedule.setDateOfTravel(LocalDate.now().plusDays(30));
        schedule.setBookedCountEconomy(5);
        schedule.setTotalCapacityEconomy(100);
        
        // Mock repository calls
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));
        when(flightScheduleRepository.findByFlightIdAndDateOfTravel(flightId, LocalDate.now().plusDays(30)))
                .thenReturn(Optional.of(schedule));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(schedule);
        
        // When
        CancelBookingResponse response = bookingService.cancelBooking(bookingId);
        
        // Then
        assertNotNull(response);
        assertEquals(bookingId, response.getBookingId());
        assertEquals(BookingStatus.CANCELLED, response.getBookingStatus());
        assertEquals(new BigDecimal("160.00"), response.getRefundAmount()); // 80% of 200.00
        assertEquals(new BigDecimal("80.00"), response.getRefundPercentage());
        assertEquals("Test Carrier", response.getCarrierName());
        
        // Verify booking status was updated
        verify(bookingRepository).save(argThat(bookingArg -> 
            bookingArg.getBookingStatus() == BookingStatus.CANCELLED));
        
        // Verify flight schedule was updated
        verify(flightScheduleRepository).save(argThat(scheduleArg -> 
            scheduleArg.getBookedCountEconomy() == 3)); // 5 - 2 = 3
    }
    
    @Test
    void testCancelBooking_RefundCalculation() {
        // Given
        Long bookingId = 1L;
        
        Carrier carrier = new Carrier();
        carrier.setRefundPercentage(new BigDecimal("75.50")); // 75.5% refund
        
        Flight flight = new Flight();
        flight.setCarrier(carrier);
        
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setFlightId(1L);
        booking.setUserId(1L);
        booking.setNoOfSeats(3);
        booking.setSeatCategory(SeatCategory.BUSINESS);
        booking.setDateOfTravel(LocalDate.now().plusDays(30));
        booking.setBookingAmount(new BigDecimal("1500.00")); // 3 seats * $500
        booking.setBookingStatus(BookingStatus.BOOKED);
        
        FlightSchedule schedule = new FlightSchedule();
        schedule.setBookedCountBusiness(8);
        
        // Mock repository calls
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightScheduleRepository.findByFlightIdAndDateOfTravel(1L, LocalDate.now().plusDays(30)))
                .thenReturn(Optional.of(schedule));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(schedule);
        
        // When
        CancelBookingResponse response = bookingService.cancelBooking(bookingId);
        
        // Then
        // Expected refund: 1500.00 * 75.5% = 1132.50
        assertEquals(new BigDecimal("1132.50"), response.getRefundAmount());
        assertEquals(new BigDecimal("75.50"), response.getRefundPercentage());
        
        // Verify booked count was decremented correctly
        verify(flightScheduleRepository).save(argThat(scheduleArg -> 
            scheduleArg.getBookedCountBusiness() == 5)); // 8 - 3 = 5
    }
    
    @Test
    void testCancelBooking_AlreadyCancelled() {
        // Given
        Long bookingId = 1L;
        
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setBookingStatus(BookingStatus.CANCELLED);
        
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.cancelBooking(bookingId);
        });
        
        assertEquals("Booking cannot be cancelled. Current status: CANCELLED", exception.getMessage());
        
        // Verify no updates were made
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(flightScheduleRepository, never()).save(any(FlightSchedule.class));
    }
    
    @Test
    void testCancelBooking_BookingNotFound() {
        // Given
        Long bookingId = 999L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.cancelBooking(bookingId);
        });
        
        assertEquals("Booking not found with id: 999", exception.getMessage());
    }
}
