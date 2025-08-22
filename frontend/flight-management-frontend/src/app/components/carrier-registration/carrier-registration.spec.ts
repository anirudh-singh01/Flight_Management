import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { CarrierRegistrationComponent } from './carrier-registration';
import { CarrierService, ApiResponse } from '../../services/carrier.service';
import { Carrier, DiscountType, RefundType } from '../../models/carrier.model';

describe('CarrierRegistrationComponent', () => {
  let component: CarrierRegistrationComponent;
  let fixture: ComponentFixture<CarrierRegistrationComponent>;
  let carrierService: jasmine.SpyObj<CarrierService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('CarrierService', ['registerCarrier']);
    
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: CarrierService, useValue: spy }
      ]
    }).compileComponents();

    carrierService = TestBed.inject(CarrierService) as jasmine.SpyObj<CarrierService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CarrierRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form', () => {
    expect(component.registrationForm.get('carrierName')?.value).toBe('');
    expect(component.registrationForm.get('discountPercentage')?.value).toBe('');
    expect(component.registrationForm.get('refundPercentage')?.value).toBe('');
    expect(component.registrationForm.get('discountType')?.value).toBe('');
    expect(component.registrationForm.get('refundType')?.value).toBe('');
    expect(component.registrationForm.get('description')?.value).toBe('');
  });

  it('should have required validators on mandatory fields', () => {
    const carrierNameControl = component.registrationForm.get('carrierName');
    const discountPercentageControl = component.registrationForm.get('discountPercentage');
    const refundPercentageControl = component.registrationForm.get('refundPercentage');
    const discountTypeControl = component.registrationForm.get('discountType');
    const refundTypeControl = component.registrationForm.get('refundType');

    expect(carrierNameControl?.hasError('required')).toBeTruthy();
    expect(discountPercentageControl?.hasError('required')).toBeTruthy();
    expect(refundPercentageControl?.hasError('required')).toBeTruthy();
    expect(discountTypeControl?.hasError('required')).toBeTruthy();
    expect(refundTypeControl?.hasError('required')).toBeTruthy();
  });

  it('should validate carrier name length', () => {
    const carrierNameControl = component.registrationForm.get('carrierName');
    
    // Test minimum length
    carrierNameControl?.setValue('A');
    expect(carrierNameControl?.hasError('minlength')).toBeTruthy();
    
    // Test valid length
    carrierNameControl?.setValue('Valid Name');
    expect(carrierNameControl?.hasError('minlength')).toBeFalsy();
    expect(carrierNameControl?.hasError('maxlength')).toBeFalsy();
    
    // Test maximum length
    const longName = 'A'.repeat(101);
    carrierNameControl?.setValue(longName);
    expect(carrierNameControl?.hasError('maxlength')).toBeTruthy();
  });

  it('should validate discount percentage range', () => {
    const discountPercentageControl = component.registrationForm.get('discountPercentage');
    
    // Test minimum value
    discountPercentageControl?.setValue(0);
    expect(discountPercentageControl?.hasError('min')).toBeTruthy();
    
    // Test valid value
    discountPercentageControl?.setValue(15.5);
    expect(discountPercentageControl?.hasError('min')).toBeFalsy();
    expect(discountPercentageControl?.hasError('max')).toBeFalsy();
    
    // Test maximum value
    discountPercentageControl?.setValue(100);
    expect(discountPercentageControl?.hasError('max')).toBeTruthy();
  });

  it('should validate refund percentage range', () => {
    const refundPercentageControl = component.registrationForm.get('refundPercentage');
    
    // Test minimum value
    refundPercentageControl?.setValue(0);
    expect(refundPercentageControl?.hasError('min')).toBeTruthy();
    
    // Test valid value
    refundPercentageControl?.setValue(25.0);
    expect(refundPercentageControl?.hasError('min')).toBeFalsy();
    expect(refundPercentageControl?.hasError('max')).toBeFalsy();
    
    // Test maximum value
    refundPercentageControl?.setValue(100);
    expect(refundPercentageControl?.hasError('max')).toBeTruthy();
  });

  it('should have discount type options', () => {
    expect(component.discountTypeOptions).toBeDefined();
    expect(component.discountTypeOptions.length).toBeGreaterThan(0);
    expect(component.discountTypeOptions[0].value).toBeDefined();
    expect(component.discountTypeOptions[0].label).toBeDefined();
  });

  it('should have refund type options', () => {
    expect(component.refundTypeOptions).toBeDefined();
    expect(component.refundTypeOptions.length).toBeGreaterThan(0);
    expect(component.refundTypeOptions[0].value).toBeDefined();
    expect(component.refundTypeOptions[0].label).toBeDefined();
  });

  it('should mark form as invalid when required fields are empty', () => {
    expect(component.registrationForm.valid).toBeFalsy();
  });

  it('should mark form as valid when all required fields are filled', () => {
    component.registrationForm.patchValue({
      carrierName: 'Test Airlines',
      discountPercentage: 15.5,
      refundPercentage: 25.0,
      discountType: DiscountType.SILVER,
      refundType: RefundType.TEN_DAYS
    });

    expect(component.registrationForm.valid).toBeTruthy();
  });

  it('should call service and handle successful registration', () => {
    const mockResponse: ApiResponse = {
      success: true,
      message: 'Carrier registered successfully',
      data: { 
        carrierId: 1, 
        carrierName: 'Test Airlines',
        discountPercentage: 15.5,
        refundPercentage: 25.0,
        discountType: DiscountType.SILVER,
        refundType: RefundType.TEN_DAYS,
        description: undefined,
        isActive: true
      }
    };

    carrierService.registerCarrier.and.returnValue(of(mockResponse));

    component.registrationForm.patchValue({
      carrierName: 'Test Airlines',
      discountPercentage: 15.5,
      refundPercentage: 25.0,
      discountType: DiscountType.SILVER,
      refundType: RefundType.TEN_DAYS
    });

    component.onSubmit();

    expect(carrierService.registerCarrier).toHaveBeenCalledWith({
      carrierName: 'Test Airlines',
      discountPercentage: 15.5,
      refundPercentage: 25.0,
      discountType: DiscountType.SILVER,
      refundType: RefundType.TEN_DAYS,
      description: undefined
    });

    expect(component.message).toBe('Carrier registered successfully!');
    expect(component.messageType).toBe('success');
  });

  it('should handle service error response', () => {
    const mockResponse: ApiResponse = {
      success: false,
      message: 'Carrier name already exists',
      data: null
    };

    carrierService.registerCarrier.and.returnValue(of(mockResponse));

    component.registrationForm.patchValue({
      carrierName: 'Test Airlines',
      discountPercentage: 15.5,
      refundPercentage: 25.0,
      discountType: DiscountType.SILVER,
      refundType: RefundType.TEN_DAYS
    });

    component.onSubmit();

    expect(component.message).toBe('Carrier name already exists');
    expect(component.messageType).toBe('error');
  });

  it('should handle HTTP error', () => {
    carrierService.registerCarrier.and.returnValue(throwError(() => new Error('Network error')));

    component.registrationForm.patchValue({
      carrierName: 'Test Airlines',
      discountPercentage: 15.5,
      refundPercentage: 25.0,
      discountType: DiscountType.SILVER,
      refundType: RefundType.TEN_DAYS
    });

    component.onSubmit();

    expect(component.message).toBe('An error occurred while registering the carrier. Please try again.');
    expect(component.messageType).toBe('error');
  });

  it('should not submit form when invalid', () => {
    component.onSubmit();

    expect(carrierService.registerCarrier).not.toHaveBeenCalled();
  });

  it('should mark form group as touched when submitting invalid form', () => {
    spyOn(component, 'markFormGroupTouched');
    
    component.onSubmit();

    expect(component.markFormGroupTouched).toHaveBeenCalled();
  });

  it('should clear form and messages', () => {
    component.registrationForm.patchValue({
      carrierName: 'Test Airlines',
      discountPercentage: 15.5,
      refundPercentage: 25.0,
      discountType: DiscountType.SILVER,
      refundType: RefundType.TEN_DAYS
    });

    component.message = 'Test message';
    component.messageType = 'success';

    component.clearForm();

    expect(component.registrationForm.get('carrierName')?.value).toBe('');
    expect(component.message).toBe('');
    expect(component.messageType).toBe('');
  });

  it('should identify invalid fields correctly', () => {
    const carrierNameControl = component.registrationForm.get('carrierName');
    carrierNameControl?.setValue('');
    carrierNameControl?.markAsTouched();

    expect(component.isFieldInvalid('carrierName')).toBeTruthy();
  });

  it('should get correct error messages', () => {
    const carrierNameControl = component.registrationForm.get('carrierName');
    carrierNameControl?.setValue('');
    carrierNameControl?.markAsTouched();

    expect(component.getErrorMessage('carrierName')).toBe('Carrier Name is required');
  });

  it('should get correct field display names', () => {
    expect(component.getFieldDisplayName('carrierName')).toBe('Carrier Name');
    expect(component.getFieldDisplayName('discountPercentage')).toBe('Discount Percentage');
    expect(component.getFieldDisplayName('refundPercentage')).toBe('Refund Percentage');
    expect(component.getFieldDisplayName('discountType')).toBe('Discount Type');
    expect(component.getFieldDisplayName('refundType')).toBe('Refund Type');
    expect(component.getFieldDisplayName('description')).toBe('Description');
  });

  it('should handle loading state during submission', () => {
    const mockResponse: ApiResponse = {
      success: true,
      message: 'Success',
      data: {}
    };

    carrierService.registerCarrier.and.returnValue(of(mockResponse));

    component.registrationForm.patchValue({
      carrierName: 'Test Airlines',
      discountPercentage: 15.5,
      refundPercentage: 25.0,
      discountType: DiscountType.SILVER,
      refundType: RefundType.TEN_DAYS
    });

    expect(component.isLoading).toBeFalsy();

    component.onSubmit();

    expect(component.isLoading).toBeTruthy();
  });
});
