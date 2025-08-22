import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BookFlightComponent } from './book-flight.component';
import { BookFlightService } from '../../services/book-flight.service';
import { FlightService } from '../../services/flight.service';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('BookFlightComponent', () => {
  let component: BookFlightComponent;
  let fixture: ComponentFixture<BookFlightComponent>;
  let bookFlightService: jasmine.SpyObj<BookFlightService>;
  let flightService: jasmine.SpyObj<FlightService>;
  let router: jasmine.SpyObj<Router>;
  let activatedRoute: jasmine.SpyObj<ActivatedRoute>;

  const mockFlights = [
    {
      flightId: 1,
      carrierId: 1,
      origin: 'New York',
      destination: 'Los Angeles',
      carrierName: 'Test Airlines',
      airFare: 299.99,
      seatCapacityBusiness: 20,
      seatCapacityEconomy: 150,
      seatCapacityExecutive: 10
    }
  ];

  const mockBookingResponse = {
    success: true,
    data: {
      bookingId: 1,
      flightId: 1,
      userId: 1,
      noOfSeats: 2,
      seatCategory: 'ECONOMY',
      dateOfTravel: '2024-02-15',
      bookingAmount: 509.98,
      discountAmount: 89.98,
      discountReason: 'Advance booking discount',
      bookingStatus: 'BOOKED',
      origin: 'New York',
      destination: 'Los Angeles',
      carrierName: 'Test Airlines',
      originalAirFare: 299.99
    }
  };

  beforeEach(async () => {
    const bookFlightServiceSpy = jasmine.createSpyObj('BookFlightService', [
      'bookFlight', 'getBookingById', 'getUserBookings'
    ]);
    const flightServiceSpy = jasmine.createSpyObj('FlightService', ['getAllFlights']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const activatedRouteSpy = jasmine.createSpyObj('ActivatedRoute', [], {
      params: of({})
    });

    await TestBed.configureTestingModule({
      declarations: [BookFlightComponent],
      imports: [
        ReactiveFormsModule,
        FormsModule,
        RouterTestingModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: BookFlightService, useValue: bookFlightServiceSpy },
        { provide: FlightService, useValue: flightServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteSpy }
      ]
    }).compileComponents();

    bookFlightService = TestBed.inject(BookFlightService) as jasmine.SpyObj<BookFlightService>;
    flightService = TestBed.inject(FlightService) as jasmine.SpyObj<FlightService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    activatedRoute = TestBed.inject(ActivatedRoute) as jasmine.SpyObj<ActivatedRoute>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BookFlightComponent);
    component = fixture.componentInstance;
    
    // Mock localStorage
    spyOn(localStorage, 'getItem').and.returnValue(JSON.stringify({ userId: 1 }));
    
    // Mock flight service response
    flightService.getAllFlights.and.returnValue(of({
      success: true,
      message: 'Flights retrieved successfully',
      data: mockFlights
    }));
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form', () => {
    expect(component.bookingForm.get('flightId')?.value).toBe('');
    expect(component.bookingForm.get('noOfSeats')?.value).toBe('');
    expect(component.bookingForm.get('seatCategory')?.value).toBe('');
    expect(component.bookingForm.get('dateOfTravel')?.value).toBe('');
  });

  it('should load available flights on init', () => {
    expect(flightService.getAllFlights).toHaveBeenCalled();
    expect(component.availableFlights).toEqual(mockFlights);
  });

  it('should validate required fields', () => {
    const form = component.bookingForm;
    
    expect(form.valid).toBeFalsy();
    
    form.patchValue({
      flightId: 1,
      noOfSeats: 2,
      seatCategory: 'ECONOMY',
      dateOfTravel: '2024-02-15'
    });
    
    expect(form.valid).toBeTruthy();
  });

  it('should validate number of seats range', () => {
    const noOfSeatsControl = component.bookingForm.get('noOfSeats');
    
    noOfSeatsControl?.setValue(0);
    expect(noOfSeatsControl?.hasError('min')).toBeTruthy();
    
    noOfSeatsControl?.setValue(11);
    expect(noOfSeatsControl?.hasError('max')).toBeTruthy();
    
    noOfSeatsControl?.setValue(5);
    expect(noOfSeatsControl?.hasError('min')).toBeFalsy();
    expect(noOfSeatsControl?.hasError('max')).toBeFalsy();
  });

  it('should validate future date', () => {
    const dateControl = component.bookingForm.get('dateOfTravel');
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    
    dateControl?.setValue(yesterday.toISOString().split('T')[0]);
    expect(dateControl?.hasError('pastDate')).toBeTruthy();
    
    dateControl?.setValue(tomorrow.toISOString().split('T')[0]);
    expect(dateControl?.hasError('pastDate')).toBeFalsy();
  });

  it('should calculate estimated cost when flight is selected', () => {
    component.bookingForm.patchValue({
      flightId: 1,
      noOfSeats: 2,
      seatCategory: 'ECONOMY',
      dateOfTravel: '2024-02-15'
    });
    
    component.onFlightSelection('1');
    
    expect(component.selectedFlight).toEqual(mockFlights[0]);
    expect(component.bookingSummary).toBeTruthy();
    expect(component.bookingSummary.estimatedTotal).toBe(599.98);
  });

  it('should submit booking successfully', () => {
    bookFlightService.bookFlight.and.returnValue(of(mockBookingResponse));
    
    component.bookingForm.patchValue({
      flightId: 1,
      noOfSeats: 2,
      seatCategory: 'ECONOMY',
      dateOfTravel: '2024-02-15'
    });
    
    component.onSubmit();
    
    expect(bookFlightService.bookFlight).toHaveBeenCalledWith(
      {
        flightId: 1,
        noOfSeats: 2,
        seatCategory: 'ECONOMY',
        dateOfTravel: '2024-02-15'
      },
      1
    );
    expect(component.successMessage).toBe('Flight booked successfully!');
    expect(component.bookingSummary).toEqual(mockBookingResponse.data);
  });

  it('should handle booking error', () => {
    const errorResponse = { success: false, message: 'Booking failed' };
    bookFlightService.bookFlight.and.returnValue(of(errorResponse));
    
    component.bookingForm.patchValue({
      flightId: 1,
      noOfSeats: 2,
      seatCategory: 'ECONOMY',
      dateOfTravel: '2024-02-15'
    });
    
    component.onSubmit();
    
    expect(component.errorMessage).toBe('Booking failed');
  });

  it('should handle service error', () => {
    bookFlightService.bookFlight.and.returnValue(throwError(() => new Error('Network error')));
    
    component.bookingForm.patchValue({
      flightId: 1,
      noOfSeats: 2,
      seatCategory: 'ECONOMY',
      dateOfTravel: '2024-02-15'
    });
    
    component.onSubmit();
    
    expect(component.errorMessage).toBe('Error booking flight: Network error');
  });

  it('should mark form as touched when submitting invalid form', () => {
    spyOn(component, 'markFormGroupTouched');
    
    component.onSubmit();
    
    expect(component.markFormGroupTouched).toHaveBeenCalled();
  });

  it('should get current user ID from localStorage', () => {
    const userId = component.getCurrentUserId();
    expect(userId).toBe(1);
  });

  it('should return default user ID when localStorage is empty', () => {
    localStorage.getItem = jasmine.createSpy().and.returnValue(null);
    
    const userId = component.getCurrentUserId();
    expect(userId).toBe(1);
  });

  it('should get field label correctly', () => {
    expect(component.getFieldLabel('flightId')).toBe('Flight');
    expect(component.getFieldLabel('noOfSeats')).toBe('Number of seats');
    expect(component.getFieldLabel('seatCategory')).toBe('Seat category');
    expect(component.getFieldLabel('dateOfTravel')).toBe('Date of travel');
  });

  it('should check if field is invalid', () => {
    const flightIdControl = component.bookingForm.get('flightId');
    flightIdControl?.markAsTouched();
    
    expect(component.isFieldInvalid('flightId')).toBeTruthy();
    
    flightIdControl?.setValue(1);
    expect(component.isFieldInvalid('flightId')).toBeFalsy();
  });

  it('should get error message for required field', () => {
    const flightIdControl = component.bookingForm.get('flightId');
    flightIdControl?.markAsTouched();
    
    const errorMessage = component.getErrorMessage('flightId');
    expect(errorMessage).toBe('Flight is required');
  });

  it('should get error message for min validation', () => {
    const noOfSeatsControl = component.bookingForm.get('noOfSeats');
    noOfSeatsControl?.setValue(0);
    noOfSeatsControl?.markAsTouched();
    
    const errorMessage = component.getErrorMessage('noOfSeats');
    expect(errorMessage).toBe('Number of seats must be at least 1');
  });

  it('should get error message for max validation', () => {
    const noOfSeatsControl = component.bookingForm.get('noOfSeats');
    noOfSeatsControl?.setValue(11);
    noOfSeatsControl?.markAsTouched();
    
    const errorMessage = component.getErrorMessage('noOfSeats');
    expect(errorMessage).toBe('Number of seats must be at most 10');
  });

  it('should get error message for past date', () => {
    const dateControl = component.bookingForm.get('dateOfTravel');
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    dateControl?.setValue(yesterday.toISOString().split('T')[0]);
    dateControl?.markAsTouched();
    
    const errorMessage = component.getErrorMessage('dateOfTravel');
    expect(errorMessage).toBe('Date of travel must be in the future');
  });

  it('should navigate to my bookings', () => {
    component.viewMyBookings();
    expect(router.navigate).toHaveBeenCalledWith(['/my-bookings']);
  });

  it('should navigate back to flights', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/view-flights']);
  });

  it('should handle flight selection from route params', () => {
    activatedRoute.params = of({ flightId: '1' });
    
    component.ngOnInit();
    
    expect(component.bookingForm.get('flightId')?.value).toBe('1');
    expect(component.selectedFlight).toEqual(mockFlights[0]);
  });

  it('should update form and calculate cost on form change', () => {
    spyOn(component, 'calculateEstimatedCost');
    
    component.bookingForm.patchValue({
      flightId: 1,
      noOfSeats: 2,
      seatCategory: 'ECONOMY',
      dateOfTravel: '2024-02-15'
    });
    
    component.onFormChange();
    
    expect(component.calculateEstimatedCost).toHaveBeenCalled();
  });
});
