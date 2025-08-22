package com.airline.flightmanagement.repository;

import com.airline.flightmanagement.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find bookings by flight ID and date of travel
     */
    List<Booking> findByFlightIdAndDateOfTravel(Long flightId, LocalDate dateOfTravel);
    
    /**
     * Find bookings by user ID
     */
    List<Booking> findByUserId(Long userId);
    
    /**
     * Find bookings by flight ID
     */
    List<Booking> findByFlightId(Long flightId);
    
    /**
     * Find booking by flight ID, user ID, and date of travel
     */
    Optional<Booking> findByFlightIdAndUserIdAndDateOfTravel(Long flightId, Long userId, LocalDate dateOfTravel);
    
    /**
     * Count bookings by flight ID, date of travel, and seat category
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.flightId = :flightId AND b.dateOfTravel = :dateOfTravel AND b.seatCategory = :seatCategory")
    Long countByFlightIdAndDateOfTravelAndSeatCategory(@Param("flightId") Long flightId, 
                                                       @Param("dateOfTravel") LocalDate dateOfTravel, 
                                                       @Param("seatCategory") String seatCategory);
    
    /**
     * Find all active bookings (not cancelled)
     */
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus != 'CANCELLED'")
    List<Booking> findActiveBookings();
}
