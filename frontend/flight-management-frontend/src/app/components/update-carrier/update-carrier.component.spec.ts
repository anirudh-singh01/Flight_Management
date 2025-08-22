import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UpdateCarrierComponent } from './update-carrier.component';
import { CarrierService } from '../../services/carrier.service';
import { Carrier, DiscountType, RefundType } from '../../models/carrier.model';

describe('UpdateCarrierComponent', () => {
  let component: UpdateCarrierComponent;
  let fixture: ComponentFixture<UpdateCarrierComponent>;
  let mockCarrierService: jasmine.SpyObj<CarrierService>;
  let mockActivatedRoute: any;

  const mockCarrier: Carrier = {
    carrierId: 1,
    carrierName: 'Test Airlines',
    discountPercentage: 15.5,
    refundPercentage: 80.0,
    discountType: DiscountType.THIRTY_DAYS,
    refundType: RefundType.TEN_DAYS,
    description: 'Test carrier description',
    isActive: true
  };

  const mockCarrierResponse = {
    success: true,
    message: 'Carrier retrieved successfully',
    data: mockCarrier
  };

  const mockUpdateResponse = {
    success: true,
    message: 'Carrier updated successfully',
    data: mockCarrier
  };

  beforeEach(async () => {
    mockCarrierService = jasmine.createSpyObj('CarrierService', ['getCarrierById', 'updateCarrier']);
    mockActivatedRoute = {
      params: of({ id: 1 })
    };

    await TestBed.configureTestingModule({
      imports: [UpdateCarrierComponent, ReactiveFormsModule, RouterTestingModule],
      providers: [
        { provide: CarrierService, useValue: mockCarrierService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateCarrierComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form', () => {
    expect(component.updateForm).toBeTruthy();
    expect(component.updateForm.get('carrierName')?.value).toBe('');
    expect(component.updateForm.get('discountPercentage')?.value).toBe('');
    expect(component.updateForm.get('refundPercentage')?.value).toBe('');
    expect(component.updateForm.get('discountType')?.value).toBe('');
    expect(component.updateForm.get('refundType')?.value).toBe('');
    expect(component.updateForm.get('description')?.value).toBe('');
  });

  it('should load carrier details on init', () => {
    mockCarrierService.getCarrierById.and.returnValue(of(mockCarrierResponse));
    
    fixture.detectChanges();
    
    expect(component.carrierId).toBe(1);
    expect(mockCarrierService.getCarrierById).toHaveBeenCalledWith(1);
  });

  it('should populate form with carrier data', () => {
    component.currentCarrier = mockCarrier;
    
    component.populateForm();
    
    expect(component.updateForm.get('carrierName')?.value).toBe('Test Airlines');
    expect(component.updateForm.get('discountPercentage')?.value).toBe(15.5);
    expect(component.updateForm.get('refundPercentage')?.value).toBe(80.0);
    expect(component.updateForm.get('discountType')?.value).toBe(DiscountType.THIRTY_DAYS);
    expect(component.updateForm.get('refundType')?.value).toBe(RefundType.TEN_DAYS);
    expect(component.updateForm.get('description')?.value).toBe('Test carrier description');
  });

  it('should validate required fields', () => {
    const form = component.updateForm;
    
    expect(form.valid).toBeFalsy();
    
    form.patchValue({
      carrierName: 'Test Airlines',
      discountPercentage: 15.5,
      refundPercentage: 80.0,
      discountType: DiscountType.THIRTY_DAYS,
      refundType: RefundType.TEN_DAYS
    });
    
    expect(form.valid).toBeTruthy();
  });

  it('should validate field lengths', () => {
    const carrierNameControl = component.updateForm.get('carrierName');
    
    carrierNameControl?.setValue('A');
    expect(carrierNameControl?.errors?.['minlength']).toBeTruthy();
    
    carrierNameControl?.setValue('A'.repeat(101));
    expect(carrierNameControl?.errors?.['maxlength']).toBeTruthy();
    
    carrierNameControl?.setValue('Valid Name');
    expect(carrierNameControl?.errors).toBeNull();
  });

  it('should validate percentage ranges', () => {
    const discountControl = component.updateForm.get('discountPercentage');
    const refundControl = component.updateForm.get('refundPercentage');
    
    discountControl?.setValue(0);
    expect(discountControl?.errors?.['min']).toBeTruthy();
    
    discountControl?.setValue(100);
    expect(discountControl?.errors?.['max']).toBeTruthy();
    
    discountControl?.setValue(50);
    expect(discountControl?.errors).toBeNull();
    
    refundControl?.setValue(0);
    expect(refundControl?.errors?.['min']).toBeTruthy();
    
    refundControl?.setValue(100);
    expect(refundControl?.errors?.['max']).toBeTruthy();
    
    refundControl?.setValue(75);
    expect(refundControl?.errors).toBeNull();
  });

  it('should update carrier successfully', () => {
    component.carrierId = 1;
    component.currentCarrier = mockCarrier;
    component.populateForm();
    
    mockCarrierService.updateCarrier.and.returnValue(of(mockUpdateResponse));
    
    component.onSubmit();
    
    expect(mockCarrierService.updateCarrier).toHaveBeenCalledWith(1, jasmine.any(Object));
    expect(component.successMessage).toBe('Carrier updated successfully!');
    expect(component.loading).toBeFalse();
  });

  it('should handle update error', () => {
    component.carrierId = 1;
    component.currentCarrier = mockCarrier;
    component.populateForm();
    
    const errorResponse = {
      success: false,
      message: 'Update failed',
      data: null
    };
    
    mockCarrierService.updateCarrier.and.returnValue(of(errorResponse));
    
    component.onSubmit();
    
    expect(component.errorMessage).toBe('Update failed');
    expect(component.loading).toBeFalse();
  });

  it('should handle service error', () => {
    component.carrierId = 1;
    component.currentCarrier = mockCarrier;
    component.populateForm();
    
    mockCarrierService.updateCarrier.and.returnValue(of().pipe(() => {
      throw new Error('Service error');
    }));
    
    component.onSubmit();
    
    expect(component.errorMessage).toBe('An error occurred while updating the carrier. Please try again.');
    expect(component.loading).toBeFalse();
  });

  it('should not submit if form is invalid', () => {
    component.carrierId = 1;
    
    component.onSubmit();
    
    expect(mockCarrierService.updateCarrier).not.toHaveBeenCalled();
  });

  it('should mark form as touched when submitting invalid form', () => {
    component.carrierId = 1;
    const markAsTouchedSpy = spyOn(component.updateForm, 'markAsTouched');
    
    component.onSubmit();
    
    expect(markAsTouchedSpy).toHaveBeenCalled();
  });

  it('should identify invalid fields correctly', () => {
    const carrierNameControl = component.updateForm.get('carrierName');
    carrierNameControl?.setValue('');
    carrierNameControl?.markAsTouched();
    
    expect(component.isFieldInvalid('carrierName')).toBeTruthy();
    expect(component.isFieldInvalid('discountPercentage')).toBeFalsy();
  });

  it('should return correct error messages', () => {
    const carrierNameControl = component.updateForm.get('carrierName');
    carrierNameControl?.setValue('');
    carrierNameControl?.markAsTouched();
    
    expect(component.getFieldError('carrierName')).toBe('carrierName is required');
    expect(component.getFieldError('discountPercentage')).toBe('');
  });

  it('should handle loading state correctly', () => {
    component.loading = true;
    fixture.detectChanges();
    
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.disabled).toBeTruthy();
  });

  it('should disable submit button when form is invalid', () => {
    component.currentCarrier = mockCarrier;
    component.populateForm();
    component.updateForm.get('carrierName')?.setValue('');
    
    fixture.detectChanges();
    
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
    expect(submitButton.disabled).toBeTruthy();
  });
});
