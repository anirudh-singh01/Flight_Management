package com.airline.flightmanagement.repository;

import com.airline.flightmanagement.entity.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Long> {
    
    /**
     * Find flight schedule by flight ID and date of travel
     */
    Optional<FlightSchedule> findByFlightIdAndDateOfTravel(Long flightId, LocalDate dateOfTravel);
    
    /**
     * Find all flight schedules for a specific flight
     */
    List<FlightSchedule> findByFlightId(Long flightId);
    
    /**
     * Find all flight schedules for a specific date
     */
    List<FlightSchedule> findByDateOfTravel(LocalDate dateOfTravel);
    
    /**
     * Find flight schedules with available seats for a specific category
     */
    @Query("SELECT fs FROM FlightSchedule fs WHERE fs.flightId = :flightId AND fs.dateOfTravel = :dateOfTravel " +
           "AND CASE WHEN :seatCategory = 'ECONOMY' THEN (fs.bookedCountEconomy < fs.totalCapacityEconomy) " +
           "WHEN :seatCategory = 'BUSINESS' THEN (fs.bookedCountBusiness < fs.totalCapacityBusiness) " +
           "WHEN :seatCategory = 'EXECUTIVE' THEN (fs.bookedCountExecutive < fs.totalCapacityExecutive) " +
           "ELSE false END = true")
    Optional<FlightSchedule> findAvailableSchedule(@Param("flightId") Long flightId, 
                                                  @Param("dateOfTravel") LocalDate dateOfTravel, 
                                                  @Param("seatCategory") String seatCategory);
    
    /**
     * Check if a flight schedule exists for the given flight and date
     */
    boolean existsByFlightIdAndDateOfTravel(Long flightId, LocalDate dateOfTravel);
}
