import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UpdateFlightComponent } from './update-flight.component';
import { FlightService } from '../../services/flight.service';
import { CarrierService } from '../../services/carrier.service';
import { Flight } from '../../models/flight.model';
import { Carrier, DiscountType, RefundType } from '../../models/carrier.model';

describe('UpdateFlightComponent', () => {
  let component: UpdateFlightComponent;
  let fixture: ComponentFixture<UpdateFlightComponent>;
  let mockFlightService: jasmine.SpyObj<FlightService>;
  let mockCarrierService: jasmine.SpyObj<CarrierService>;
  let mockActivatedRoute: any;

  const mockFlight: Flight = {
    flightId: 1,
    carrierId: 1,
    carrierName: 'Test Airline',
    origin: 'New York',
    destination: 'Los Angeles',
    airFare: 299.99,
    seatCapacityBusiness: 20,
    seatCapacityEconomy: 150,
    seatCapacityExecutive: 10
  };

  const mockCarriers: Carrier[] = [
    { 
      carrierId: 1, 
      carrierName: 'Test Airline',
      discountPercentage: 10,
      refundPercentage: 80,
      discountType: DiscountType.THIRTY_DAYS,
      refundType: RefundType.TEN_DAYS
    },
    { 
      carrierId: 2, 
      carrierName: 'Another Airline',
      discountPercentage: 15,
      refundPercentage: 90,
      discountType: DiscountType.SIXTY_DAYS,
      refundType: RefundType.TWENTY_DAYS
    }
  ];

  const mockFlightResponse = {
    success: true,
    message: 'Flight retrieved successfully',
    data: mockFlight
  };

  const mockCarrierResponse = {
    success: true,
    message: 'Carriers retrieved successfully',
    data: mockCarriers
  };

  beforeEach(async () => {
    mockFlightService = jasmine.createSpyObj('FlightService', ['getFlightById', 'updateFlight']);
    mockCarrierService = jasmine.createSpyObj('CarrierService', ['getAllCarriers']);
    mockActivatedRoute = {
      params: of({ id: '1' })
    };

    await TestBed.configureTestingModule({
      imports: [UpdateFlightComponent, ReactiveFormsModule, RouterTestingModule],
      providers: [
        { provide: FlightService, useValue: mockFlightService },
        { provide: CarrierService, useValue: mockCarrierService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateFlightComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with flight ID from route params', () => {
    mockFlightService.getFlightById.and.returnValue(of(mockFlightResponse));
    mockCarrierService.getAllCarriers.and.returnValue(of(mockCarrierResponse));
    
    fixture.detectChanges();
    
    expect(component.flightId).toBe(1);
    expect(mockFlightService.getFlightById).toHaveBeenCalledWith(1);
  });

  it('should load flight details on init', () => {
    mockFlightService.getFlightById.and.returnValue(of(mockFlightResponse));
    mockCarrierService.getAllCarriers.and.returnValue(of(mockCarrierResponse));
    
    fixture.detectChanges();
    
    expect(component.currentFlight).toEqual(mockFlight);
    expect(component.flightForm.get('origin')?.value).toBe('New York');
    expect(component.flightForm.get('destination')?.value).toBe('Los Angeles');
  });

  it('should load carriers on init', () => {
    mockFlightService.getFlightById.and.returnValue(of(mockFlightResponse));
    mockCarrierService.getAllCarriers.and.returnValue(of(mockCarrierResponse));
    
    fixture.detectChanges();
    
    expect(component.carriers).toEqual(mockCarriers);
    expect(mockCarrierService.getAllCarriers).toHaveBeenCalled();
  });

  it('should populate form with flight data', () => {
    component.currentFlight = mockFlight;
    component.carriers = mockCarriers;
    
    component.populateForm();
    
    expect(component.flightForm.get('carrierId')?.value).toBe(1);
    expect(component.flightForm.get('origin')?.value).toBe('New York');
    expect(component.flightForm.get('destination')?.value).toBe('Los Angeles');
    expect(component.flightForm.get('airFare')?.value).toBe(299.99);
  });

  it('should validate required fields', () => {
    const form = component.flightForm;
    
    expect(form.valid).toBeFalsy();
    
    form.patchValue({
      carrierId: 1,
      origin: 'New York',
      destination: 'Los Angeles',
      airFare: 299.99,
      seatCapacityBusiness: 20,
      seatCapacityEconomy: 150,
      seatCapacityExecutive: 10
    });
    
    expect(form.valid).toBeTruthy();
  });

  it('should show error for invalid field', () => {
    const field = component.flightForm.get('origin');
    field?.setValue('');
    field?.markAsTouched();
    
    fixture.detectChanges();
    
    expect(component.isFieldInvalid('origin')).toBeTruthy();
    expect(component.getFieldError('origin')).toContain('Origin is required');
  });

  it('should update flight when form is valid', () => {
    const updateResponse = { success: true, message: 'Flight updated successfully', data: mockFlight };
    mockFlightService.updateFlight.and.returnValue(of(updateResponse));
    
    component.flightId = 1;
    component.flightForm.patchValue({
      carrierId: 1,
      origin: 'New York',
      destination: 'Los Angeles',
      airFare: 299.99,
      seatCapacityBusiness: 20,
      seatCapacityEconomy: 150,
      seatCapacityExecutive: 10
    });
    
    component.onSubmit();
    
    expect(mockFlightService.updateFlight).toHaveBeenCalledWith(1, jasmine.any(Object));
    expect(component.successMessage).toBe('Flight updated successfully!');
  });

  it('should not update flight when form is invalid', () => {
    component.flightForm.patchValue({
      carrierId: '',
      origin: '',
      destination: '',
      airFare: '',
      seatCapacityBusiness: '',
      seatCapacityEconomy: '',
      seatCapacityExecutive: ''
    });
    
    component.onSubmit();
    
    expect(mockFlightService.updateFlight).not.toHaveBeenCalled();
  });

  it('should handle flight update error', () => {
    const errorResponse = { success: false, message: 'Update failed', data: null };
    mockFlightService.updateFlight.and.returnValue(of(errorResponse));
    
    component.flightId = 1;
    component.flightForm.patchValue({
      carrierId: 1,
      origin: 'New York',
      destination: 'Los Angeles',
      airFare: 299.99,
      seatCapacityBusiness: 20,
      seatCapacityEconomy: 150,
      seatCapacityExecutive: 10
    });
    
    component.onSubmit();
    
    expect(component.errorMessage).toBe('Update failed');
  });

  it('should mark all form controls as touched when validation fails', () => {
    const form = component.flightForm;
    const controls = ['carrierId', 'origin', 'destination', 'airFare', 'seatCapacityBusiness', 'seatCapacityEconomy', 'seatCapacityExecutive'];
    
    component.markFormGroupTouched();
    
    controls.forEach(controlName => {
      expect(form.get(controlName)?.touched).toBeTruthy();
    });
  });

  it('should get correct field display names', () => {
    expect(component.getFieldDisplayName('carrierId')).toBe('Carrier');
    expect(component.getFieldDisplayName('origin')).toBe('Origin');
    expect(component.getFieldDisplayName('destination')).toBe('Destination');
    expect(component.getFieldDisplayName('airFare')).toBe('Air Fare');
  });
});
