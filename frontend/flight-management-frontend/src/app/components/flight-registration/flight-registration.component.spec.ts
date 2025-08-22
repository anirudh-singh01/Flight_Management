import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FlightRegistrationComponent } from './flight-registration.component';
import { FlightService } from '../../services/flight.service';
import { CarrierService } from '../../services/carrier.service';
import { Flight } from '../../models/flight.model';
import { Carrier, DiscountType, RefundType } from '../../models/carrier.model';

describe('FlightRegistrationComponent', () => {
  let component: FlightRegistrationComponent;
  let fixture: ComponentFixture<FlightRegistrationComponent>;
  let mockFlightService: jasmine.SpyObj<FlightService>;
  let mockCarrierService: jasmine.SpyObj<CarrierService>;
  let mockRouter: jasmine.SpyObj<Router>;

  const mockCarriers: Carrier[] = [
    { 
      carrierId: 1, 
      carrierName: 'Test Airlines',
      discountPercentage: 10,
      refundPercentage: 80,
      discountType: DiscountType.THIRTY_DAYS,
      refundType: RefundType.TEN_DAYS
    },
    { 
      carrierId: 2, 
      carrierName: 'Sample Airways',
      discountPercentage: 15,
      refundPercentage: 85,
      discountType: DiscountType.SIXTY_DAYS,
      refundType: RefundType.TWENTY_DAYS
    }
  ];

  const mockFlight: Flight = {
    carrierId: 1,
    origin: 'New York',
    destination: 'Los Angeles',
    airFare: 299.99,
    seatCapacityBusiness: 20,
    seatCapacityEconomy: 150,
    seatCapacityExecutive: 10
  };

  beforeEach(async () => {
    const flightServiceSpy = jasmine.createSpyObj('FlightService', ['registerFlight']);
    const carrierServiceSpy = jasmine.createSpyObj('CarrierService', ['getAllCarriers']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule],
      providers: [
        { provide: FlightService, useValue: flightServiceSpy },
        { provide: CarrierService, useValue: carrierServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    mockFlightService = TestBed.inject(FlightService) as jasmine.SpyObj<FlightService>;
    mockCarrierService = TestBed.inject(CarrierService) as jasmine.SpyObj<CarrierService>;
    mockRouter = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FlightRegistrationComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form', () => {
    expect(component.flightForm).toBeDefined();
    expect(component.flightForm.get('carrierId')?.value).toBe('');
    expect(component.flightForm.get('origin')?.value).toBe('');
    expect(component.flightForm.get('destination')?.value).toBe('');
    expect(component.flightForm.get('airFare')?.value).toBe('');
    expect(component.flightForm.get('seatCapacityBusiness')?.value).toBe('');
    expect(component.flightForm.get('seatCapacityEconomy')?.value).toBe('');
    expect(component.flightForm.get('seatCapacityExecutive')?.value).toBe('');
  });

  it('should load carriers on init', () => {
    mockCarrierService.getAllCarriers.and.returnValue(of({
      success: true,
      message: 'Carriers loaded successfully',
      data: mockCarriers
    }));

    component.ngOnInit();

    expect(mockCarrierService.getAllCarriers).toHaveBeenCalled();
    expect(component.carriers).toEqual(mockCarriers);
  });

  it('should handle carrier loading error', () => {
    mockCarrierService.getAllCarriers.and.returnValue(throwError(() => new Error('Network error')));

    component.ngOnInit();

    expect(component.errorMessage).toBe('Failed to load carriers');
  });

  it('should validate required fields', () => {
    const form = component.flightForm;
    
    expect(form.valid).toBeFalsy();
    expect(form.get('carrierId')?.errors?.['required']).toBeTruthy();
    expect(form.get('origin')?.errors?.['required']).toBeTruthy();
    expect(form.get('destination')?.errors?.['required']).toBeTruthy();
    expect(form.get('airFare')?.errors?.['required']).toBeTruthy();
    expect(form.get('seatCapacityBusiness')?.errors?.['required']).toBeTruthy();
    expect(form.get('seatCapacityEconomy')?.errors?.['required']).toBeTruthy();
    expect(form.get('seatCapacityExecutive')?.errors?.['required']).toBeTruthy();
  });

  it('should validate minimum length for text fields', () => {
    const form = component.flightForm;
    
    form.get('origin')?.setValue('A');
    form.get('destination')?.setValue('B');
    
    expect(form.get('origin')?.errors?.['minlength']).toBeTruthy();
    expect(form.get('destination')?.errors?.['minlength']).toBeTruthy();
  });

  it('should validate minimum values for numeric fields', () => {
    const form = component.flightForm;
    
    form.get('airFare')?.setValue(0);
    form.get('seatCapacityBusiness')?.setValue(0);
    form.get('seatCapacityEconomy')?.setValue(0);
    form.get('seatCapacityExecutive')?.setValue(0);
    
    expect(form.get('airFare')?.errors?.['min']).toBeTruthy();
    expect(form.get('seatCapacityBusiness')?.errors?.['min']).toBeTruthy();
    expect(form.get('seatCapacityEconomy')?.errors?.['min']).toBeTruthy();
    expect(form.get('seatCapacityExecutive')?.errors?.['min']).toBeTruthy();
  });

  it('should submit form successfully with valid data', () => {
    mockFlightService.registerFlight.and.returnValue(of({
      success: true,
      message: 'Flight registered successfully',
      data: { ...mockFlight, flightId: 1 }
    }));

    component.flightForm.patchValue(mockFlight);
    component.onSubmit();

    expect(mockFlightService.registerFlight).toHaveBeenCalledWith(mockFlight);
    expect(component.successMessage).toBe('Flight registered successfully!');
    expect(component.errorMessage).toBe('');
    expect(component.loading).toBeFalse();
  });

  it('should handle form submission error', () => {
    mockFlightService.registerFlight.and.returnValue(throwError(() => new Error('Network error')));

    component.flightForm.patchValue(mockFlight);
    component.onSubmit();

    expect(component.errorMessage).toBe('An error occurred while registering the flight');
    expect(component.successMessage).toBe('');
    expect(component.loading).toBeFalse();
  });

  it('should handle API error response', () => {
    mockFlightService.registerFlight.and.returnValue(of({
      success: false,
      message: 'Carrier not found',
      data: null
    }));

    component.flightForm.patchValue(mockFlight);
    component.onSubmit();

    expect(component.errorMessage).toBe('Carrier not found');
    expect(component.successMessage).toBe('');
    expect(component.loading).toBeFalse();
  });

  it('should not submit invalid form', () => {
    component.onSubmit();

    expect(mockFlightService.registerFlight).not.toHaveBeenCalled();
    expect(component.loading).toBeFalse();
  });

  it('should mark form as touched when submitting invalid form', () => {
    spyOn(component, 'markFormGroupTouched');
    
    component.onSubmit();

    expect(component.markFormGroupTouched).toHaveBeenCalled();
  });

  it('should get field error messages correctly', () => {
    const form = component.flightForm;
    form.get('origin')?.setValue('');
    form.get('origin')?.markAsTouched();
    
    const errorMessage = component.getFieldError('origin');
    expect(errorMessage).toBe('Origin is required');
  });

  it('should get field display names correctly', () => {
    expect(component.getFieldDisplayName('carrierId')).toBe('Carrier');
    expect(component.getFieldDisplayName('origin')).toBe('Origin');
    expect(component.getFieldDisplayName('destination')).toBe('Destination');
    expect(component.getFieldDisplayName('airFare')).toBe('Air Fare');
    expect(component.getFieldDisplayName('seatCapacityBusiness')).toBe('Business Seat Capacity');
    expect(component.getFieldDisplayName('seatCapacityEconomy')).toBe('Economy Seat Capacity');
    expect(component.getFieldDisplayName('seatCapacityExecutive')).toBe('Executive Seat Capacity');
  });

  it('should check field validity correctly', () => {
    const form = component.flightForm;
    form.get('origin')?.setValue('');
    form.get('origin')?.markAsTouched();
    
    expect(component.isFieldInvalid('origin')).toBeTrue();
    expect(component.isFieldInvalid('destination')).toBeFalse();
  });

  it('should navigate back when goBack is called', () => {
    component.goBack();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should reset form after successful submission', () => {
    mockFlightService.registerFlight.and.returnValue(of({
      success: true,
      message: 'Flight registered successfully',
      data: { ...mockFlight, flightId: 1 }
    }));

    component.flightForm.patchValue(mockFlight);
    component.onSubmit();

    expect(component.flightForm.get('carrierId')?.value).toBeNull();
    expect(component.flightForm.get('origin')?.value).toBeNull();
    expect(component.flightForm.get('destination')?.value).toBeNull();
    expect(component.flightForm.get('airFare')?.value).toBeNull();
    expect(component.flightForm.get('seatCapacityBusiness')?.value).toBeNull();
    expect(component.flightForm.get('seatCapacityEconomy')?.value).toBeNull();
    expect(component.flightForm.get('seatCapacityExecutive')?.value).toBeNull();
  });

  it('should set loading state during form submission', () => {
    mockFlightService.registerFlight.and.returnValue(of({
      success: true,
      message: 'Flight registered successfully',
      data: { ...mockFlight, flightId: 1 }
    }));

    component.flightForm.patchValue(mockFlight);
    
    // Before submission
    expect(component.loading).toBeFalse();
    
    component.onSubmit();
    
    // During submission (before response)
    expect(component.loading).toBeTrue();
  });

  it('should clear messages when starting new submission', () => {
    component.errorMessage = 'Previous error';
    component.successMessage = 'Previous success';
    
    mockFlightService.registerFlight.and.returnValue(of({
      success: true,
      message: 'Flight registered successfully',
      data: { ...mockFlight, flightId: 1 }
    }));

    component.flightForm.patchValue(mockFlight);
    component.onSubmit();

    expect(component.errorMessage).toBe('');
    expect(component.successMessage).toBe('Flight registered successfully!');
  });
});
