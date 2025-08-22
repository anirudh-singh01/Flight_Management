package com.airline.flightmanagement.repository;

import com.airline.flightmanagement.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    List<Flight> findByOriginAndDestination(String origin, String destination);
    
    List<Flight> findByCarrierCarrierId(Long carrierId);
    
    @Query("SELECT f FROM Flight f WHERE f.carrier.carrierName = :carrierName")
    List<Flight> findByCarrierName(@Param("carrierName") String carrierName);
    
    @Query("SELECT f FROM Flight f WHERE f.origin = :origin")
    List<Flight> findByOrigin(@Param("origin") String origin);
    
    @Query("SELECT f FROM Flight f WHERE f.destination = :destination")
    List<Flight> findByDestination(@Param("destination") String destination);
}
