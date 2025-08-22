package com.airline.flightmanagement.service;

import com.airline.flightmanagement.dto.FlightDTO;
import com.airline.flightmanagement.entity.Flight;
import com.airline.flightmanagement.entity.Carrier;
import com.airline.flightmanagement.repository.FlightRepository;
import com.airline.flightmanagement.repository.CarrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService {
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private CarrierRepository carrierRepository;
    
    // Register a new flight
    public FlightDTO registerFlight(FlightDTO flightDTO) {
        // Validate carrier exists
        Carrier carrier = carrierRepository.findById(flightDTO.getCarrierId())
                .orElseThrow(() -> new RuntimeException("Carrier not found with id: " + flightDTO.getCarrierId()));
        
        Flight flight = new Flight(
            carrier,
            flightDTO.getOrigin(),
            flightDTO.getDestination(),
            flightDTO.getAirFare(),
            flightDTO.getSeatCapacityBusiness(),
            flightDTO.getSeatCapacityEconomy(),
            flightDTO.getSeatCapacityExecutive()
        );
        
        Flight savedFlight = flightRepository.save(flight);
        return convertToDTO(savedFlight);
    }
    
    // Get all flights
    @Transactional(readOnly = true)
    public List<FlightDTO> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get flight by ID
    @Transactional(readOnly = true)
    public Optional<FlightDTO> getFlightById(Long flightId) {
        return flightRepository.findById(flightId)
                .map(this::convertToDTO);
    }
    
    // Update flight
    public FlightDTO updateFlight(Long flightId, FlightDTO flightDTO) {
        Flight existingFlight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));
        
        // Validate carrier exists
        Carrier carrier = carrierRepository.findById(flightDTO.getCarrierId())
                .orElseThrow(() -> new RuntimeException("Carrier not found with id: " + flightDTO.getCarrierId()));
        
        existingFlight.setCarrier(carrier);
        existingFlight.setOrigin(flightDTO.getOrigin());
        existingFlight.setDestination(flightDTO.getDestination());
        existingFlight.setAirFare(flightDTO.getAirFare());
        existingFlight.setSeatCapacityBusiness(flightDTO.getSeatCapacityBusiness());
        existingFlight.setSeatCapacityEconomy(flightDTO.getSeatCapacityEconomy());
        existingFlight.setSeatCapacityExecutive(flightDTO.getSeatCapacityExecutive());
        
        Flight updatedFlight = flightRepository.save(existingFlight);
        return convertToDTO(updatedFlight);
    }
    
    // Delete flight
    public void deleteFlight(Long flightId) {
        if (!flightRepository.existsById(flightId)) {
            throw new RuntimeException("Flight not found with id: " + flightId);
        }
        flightRepository.deleteById(flightId);
    }
    
    // Search flights by origin and destination
    @Transactional(readOnly = true)
    public List<FlightDTO> searchFlights(String origin, String destination) {
        return flightRepository.findByOriginAndDestination(origin, destination).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get flights by carrier
    @Transactional(readOnly = true)
    public List<FlightDTO> getFlightsByCarrier(Long carrierId) {
        return flightRepository.findByCarrierCarrierId(carrierId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get flights by origin
    @Transactional(readOnly = true)
    public List<FlightDTO> getFlightsByOrigin(String origin) {
        return flightRepository.findByOrigin(origin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get flights by destination
    @Transactional(readOnly = true)
    public List<FlightDTO> getFlightsByDestination(String destination) {
        return flightRepository.findByDestination(destination).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get flights by carrier name
    @Transactional(readOnly = true)
    public List<FlightDTO> getFlightsByCarrierName(String carrierName) {
        return flightRepository.findByCarrierName(carrierName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Convert Entity to DTO
    private FlightDTO convertToDTO(Flight flight) {
        return new FlightDTO(
            flight.getFlightId(),
            flight.getCarrier().getCarrierId(),
            flight.getCarrier().getCarrierName(),
            flight.getOrigin(),
            flight.getDestination(),
            flight.getAirFare(),
            flight.getSeatCapacityBusiness(),
            flight.getSeatCapacityEconomy(),
            flight.getSeatCapacityExecutive()
        );
    }
}
