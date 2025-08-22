package com.airline.flightmanagement.config;

import com.airline.flightmanagement.entity.Carrier;
import com.airline.flightmanagement.entity.DiscountType;
import com.airline.flightmanagement.entity.Flight;
import com.airline.flightmanagement.entity.RefundType;
import com.airline.flightmanagement.repository.CarrierRepository;
import com.airline.flightmanagement.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CarrierRepository carrierRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no carriers exist
        if (carrierRepository.count() == 0) {
            initializeCarriers();
            initializeFlights();
        }
    }

    private void initializeCarriers() {
        // Create sample carriers
        Carrier carrier1 = new Carrier();
        carrier1.setCarrierName("Air India");
        carrier1.setDiscountPercentage(new BigDecimal("15.00"));
        carrier1.setRefundPercentage(new BigDecimal("25.00"));
        carrier1.setDiscountType(DiscountType.SILVER);
        carrier1.setRefundType(RefundType.TEN_DAYS);
        carrier1.setDescription("National carrier of India");
        carrier1.setIsActive(true);
        carrierRepository.save(carrier1);

        Carrier carrier2 = new Carrier();
        carrier2.setCarrierName("IndiGo");
        carrier2.setDiscountPercentage(new BigDecimal("20.00"));
        carrier2.setRefundPercentage(new BigDecimal("30.00"));
        carrier2.setDiscountType(DiscountType.GOLD);
        carrier2.setRefundType(RefundType.TWENTY_DAYS);
        carrier2.setDescription("Low-cost carrier");
        carrier2.setIsActive(true);
        carrierRepository.save(carrier2);

        Carrier carrier3 = new Carrier();
        carrier3.setCarrierName("SpiceJet");
        carrier3.setDiscountPercentage(new BigDecimal("12.00"));
        carrier3.setRefundPercentage(new BigDecimal("20.00"));
        carrier3.setDiscountType(DiscountType.THIRTY_DAYS);
        carrier3.setRefundType(RefundType.TWO_DAYS);
        carrier3.setDescription("Budget airline");
        carrier3.setIsActive(true);
        carrierRepository.save(carrier3);

        Carrier carrier4 = new Carrier();
        carrier4.setCarrierName("Vistara");
        carrier4.setDiscountPercentage(new BigDecimal("25.00"));
        carrier4.setRefundPercentage(new BigDecimal("35.00"));
        carrier4.setDiscountType(DiscountType.PLATINUM);
        carrier4.setRefundType(RefundType.TWENTY_DAYS);
        carrier4.setDescription("Full-service carrier");
        carrier4.setIsActive(true);
        carrierRepository.save(carrier4);

        System.out.println("✅ Sample carriers initialized successfully!");
    }

    private void initializeFlights() {
        // Get carriers for reference
        Carrier airIndia = carrierRepository.findByCarrierName("Air India").orElse(null);
        Carrier indiGo = carrierRepository.findByCarrierName("IndiGo").orElse(null);
        Carrier spiceJet = carrierRepository.findByCarrierName("SpiceJet").orElse(null);
        Carrier vistara = carrierRepository.findByCarrierName("Vistara").orElse(null);

        if (airIndia != null) {
            // Air India flights
            Flight flight1 = new Flight();
            flight1.setCarrier(airIndia);
            flight1.setOrigin("Mumbai");
            flight1.setDestination("Delhi");
            flight1.setAirFare(new BigDecimal("8500.00"));
            flight1.setSeatCapacityBusiness(20);
            flight1.setSeatCapacityEconomy(150);
            flight1.setSeatCapacityExecutive(10);
            flightRepository.save(flight1);

            Flight flight2 = new Flight();
            flight2.setCarrier(airIndia);
            flight2.setOrigin("Delhi");
            flight2.setDestination("Bangalore");
            flight2.setAirFare(new BigDecimal("9200.00"));
            flight2.setSeatCapacityBusiness(25);
            flight2.setSeatCapacityEconomy(180);
            flight2.setSeatCapacityExecutive(15);
            flightRepository.save(flight2);
        }

        if (indiGo != null) {
            // IndiGo flights
            Flight flight3 = new Flight();
            flight3.setCarrier(indiGo);
            flight3.setOrigin("Mumbai");
            flight3.setDestination("Chennai");
            flight3.setAirFare(new BigDecimal("6500.00"));
            flight3.setSeatCapacityBusiness(15);
            flight3.setSeatCapacityEconomy(120);
            flight3.setSeatCapacityExecutive(8);
            flightRepository.save(flight3);

            Flight flight4 = new Flight();
            flight4.setCarrier(indiGo);
            flight4.setOrigin("Delhi");
            flight4.setDestination("Hyderabad");
            flight4.setAirFare(new BigDecimal("5800.00"));
            flight4.setSeatCapacityBusiness(12);
            flight4.setSeatCapacityEconomy(100);
            flight4.setSeatCapacityExecutive(6);
            flightRepository.save(flight4);
        }

        if (spiceJet != null) {
            // SpiceJet flights
            Flight flight5 = new Flight();
            flight5.setCarrier(spiceJet);
            flight5.setOrigin("Kolkata");
            flight5.setDestination("Mumbai");
            flight5.setAirFare(new BigDecimal("7200.00"));
            flight5.setSeatCapacityBusiness(18);
            flight5.setSeatCapacityEconomy(140);
            flight5.setSeatCapacityExecutive(12);
            flightRepository.save(flight5);
        }

        if (vistara != null) {
            // Vistara flights
            Flight flight6 = new Flight();
            flight6.setCarrier(vistara);
            flight6.setOrigin("Delhi");
            flight6.setDestination("Mumbai");
            flight6.setAirFare(new BigDecimal("9500.00"));
            flight6.setSeatCapacityBusiness(30);
            flight6.setSeatCapacityEconomy(200);
            flight6.setSeatCapacityExecutive(20);
            flightRepository.save(flight6);
        }

        System.out.println("✅ Sample flights initialized successfully!");
    }
}
