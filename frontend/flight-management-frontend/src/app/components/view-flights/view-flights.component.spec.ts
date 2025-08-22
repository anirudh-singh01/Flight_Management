import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { ViewFlightsComponent } from './view-flights.component';
import { FlightService } from '../../services/flight.service';
import { Flight, FlightResponse } from '../../models/flight.model';

describe('ViewFlightsComponent', () => {
  let component: ViewFlightsComponent;
  let fixture: ComponentFixture<ViewFlightsComponent>;
  let flightService: jasmine.SpyObj<FlightService>;

  const mockFlight: Flight = {
    flightId: 1,
    carrierId: 1,
    carrierName: 'Test Airlines',
    origin: 'New York',
    destination: 'Los Angeles',
    airFare: 299.99,
    seatCapacityBusiness: 20,
    seatCapacityEconomy: 150,
    seatCapacityExecutive: 10
  };

  const mockFlightResponse: FlightResponse = {
    success: true,
    message: 'Flights retrieved successfully',
    data: [mockFlight]
  };

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('FlightService', ['getAllFlights', 'getFlightsByCarrierName']);
    
    await TestBed.configureTestingModule({
      imports: [ViewFlightsComponent, FormsModule],
      providers: [
        { provide: FlightService, useValue: spy }
      ]
    }).compileComponents();

    flightService = TestBed.inject(FlightService) as jasmine.SpyObj<FlightService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewFlightsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load all flights on init', () => {
    // Arrange
    flightService.getAllFlights.and.returnValue(of(mockFlightResponse));

    // Act
    fixture.detectChanges();

    // Assert
    expect(flightService.getAllFlights).toHaveBeenCalled();
    expect(component.flights).toEqual([mockFlight]);
    expect(component.filteredFlights).toEqual([mockFlight]);
    expect(component.loading).toBeFalse();
    expect(component.error).toBe('');
  });

  it('should handle error when loading flights fails', () => {
    // Arrange
    const errorResponse = { message: 'Failed to load flights' };
    flightService.getAllFlights.and.returnValue(throwError(() => errorResponse));

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.error).toBe('Error loading flights: Failed to load flights');
    expect(component.loading).toBeFalse();
  });

  it('should search flights by carrier name successfully', () => {
    // Arrange
    component.flights = [mockFlight];
    component.filteredFlights = [mockFlight];
    component.searchCarrierName = 'Test Airlines';
    flightService.getFlightsByCarrierName.and.returnValue(of(mockFlightResponse));

    // Act
    component.searchByCarrier();

    // Assert
    expect(flightService.getFlightsByCarrierName).toHaveBeenCalledWith('Test Airlines');
    expect(component.filteredFlights).toEqual([mockFlight]);
    expect(component.loading).toBeFalse();
    expect(component.error).toBe('');
  });

  it('should handle empty search by showing all flights', () => {
    // Arrange
    component.flights = [mockFlight];
    component.filteredFlights = [];
    component.searchCarrierName = '';

    // Act
    component.searchByCarrier();

    // Assert
    expect(component.filteredFlights).toEqual([mockFlight]);
    expect(flightService.getFlightsByCarrierName).not.toHaveBeenCalled();
  });

  it('should handle search with no results', () => {
    // Arrange
    component.flights = [mockFlight];
    component.searchCarrierName = 'NonExistent Airlines';
    const noResultsResponse: FlightResponse = {
      success: true,
      message: 'No flights found',
      data: []
    };
    flightService.getFlightsByCarrierName.and.returnValue(of(noResultsResponse));

    // Act
    component.searchByCarrier();

    // Assert
    expect(component.filteredFlights).toEqual([]);
    expect(component.error).toBe('No flights found for this carrier');
    expect(component.loading).toBeFalse();
  });

  it('should handle search error', () => {
    // Arrange
    component.flights = [mockFlight];
    component.searchCarrierName = 'Test Airlines';
    const errorResponse = { message: 'Search failed' };
    flightService.getFlightsByCarrierName.and.returnValue(throwError(() => errorResponse));

    // Act
    component.searchByCarrier();

    // Assert
    expect(component.error).toBe('Error searching flights: Search failed');
    expect(component.filteredFlights).toEqual([]);
    expect(component.loading).toBeFalse();
  });

  it('should clear search and show all flights', () => {
    // Arrange
    component.flights = [mockFlight];
    component.filteredFlights = [];
    component.searchCarrierName = 'Test Airlines';
    component.error = 'Some error';

    // Act
    component.clearSearch();

    // Assert
    expect(component.searchCarrierName).toBe('');
    expect(component.filteredFlights).toEqual([mockFlight]);
    expect(component.error).toBe('');
  });

  it('should calculate total seats correctly', () => {
    // Arrange
    const flight: Flight = {
      flightId: 1,
      carrierId: 1,
      origin: 'A',
      destination: 'B',
      airFare: 100,
      seatCapacityBusiness: 10,
      seatCapacityEconomy: 50,
      seatCapacityExecutive: 5
    };

    // Act
    const totalSeats = component.getTotalSeats(flight);

    // Assert
    expect(totalSeats).toBe(65);
  });

  it('should handle null seat capacities', () => {
    // Arrange
    const flight: Flight = {
      flightId: 1,
      carrierId: 1,
      origin: 'A',
      destination: 'B',
      airFare: 100,
      seatCapacityBusiness: undefined as any,
      seatCapacityEconomy: undefined as any,
      seatCapacityExecutive: undefined as any
    };

    // Act
    const totalSeats = component.getTotalSeats(flight);

    // Assert
    expect(totalSeats).toBe(0);
  });
});
