import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CustomerRegistrationComponent } from './customer-registration';
import { UserService } from '../../services/user.service';
import { User, UserRole, CustomerCategory } from '../../models/user.model';
import { of, throwError } from 'rxjs';

describe('CustomerRegistrationComponent', () => {
  let component: CustomerRegistrationComponent;
  let fixture: ComponentFixture<CustomerRegistrationComponent>;
  let userService: jasmine.SpyObj<UserService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('UserService', ['registerUser']);
    
    await TestBed.configureTestingModule({
      imports: [CustomerRegistrationComponent, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: UserService, useValue: spy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerRegistrationComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.registrationForm.get('role')?.value).toBe(UserRole.CUSTOMER);
    expect(component.registrationForm.get('customerCategory')?.value).toBe(CustomerCategory.REGULAR);
  });

  it('should validate required fields', () => {
    const form = component.registrationForm;
    
    expect(form.valid).toBeFalsy();
    
    // Fill required fields
    form.patchValue({
      userName: 'testuser',
      password: 'password123',
      confirmPassword: 'password123',
      phone: '+1234567890',
      emailId: 'test@example.com',
      address1: '123 Test St',
      city: 'Test City',
      state: 'Test State',
      zipCode: '12345',
      dob: '1990-01-01'
    });
    
    expect(form.valid).toBeTruthy();
  });

  it('should validate password match', () => {
    const form = component.registrationForm;
    
    form.patchValue({
      password: 'password123',
      confirmPassword: 'different'
    });
    
    expect(form.hasError('passwordMismatch')).toBeTruthy();
    
    form.patchValue({
      confirmPassword: 'password123'
    });
    
    expect(form.hasError('passwordMismatch')).toBeFalsy();
  });

  it('should validate email format', () => {
    const emailControl = component.registrationForm.get('emailId');
    
    emailControl?.setValue('invalid-email');
    expect(emailControl?.errors?.['email']).toBeTruthy();
    
    emailControl?.setValue('valid@email.com');
    expect(emailControl?.errors?.['email']).toBeFalsy();
  });

  it('should validate phone format', () => {
    const phoneControl = component.registrationForm.get('phone');
    
    phoneControl?.setValue('invalid-phone');
    expect(phoneControl?.errors?.['pattern']).toBeTruthy();
    
    phoneControl?.setValue('+1234567890');
    expect(phoneControl?.errors?.['pattern']).toBeFalsy();
  });

  it('should validate ZIP code format', () => {
    const zipControl = component.registrationForm.get('zipCode');
    
    zipControl?.setValue('invalid');
    expect(zipControl?.errors?.['pattern']).toBeTruthy();
    
    zipControl?.setValue('12345');
    expect(zipControl?.errors?.['pattern']).toBeFalsy();
    
    zipControl?.setValue('12345-6789');
    expect(zipControl?.errors?.['pattern']).toBeFalsy();
  });

  it('should handle successful form submission', () => {
    const mockUser: User = {
      userName: 'testuser',
      password: 'password123',
      role: UserRole.CUSTOMER,
      customerCategory: CustomerCategory.REGULAR,
      phone: '+1234567890',
      emailId: 'test@example.com',
      address1: '123 Test St',
      city: 'Test City',
      state: 'Test State',
      zipCode: '12345',
      dob: '1990-01-01'
    };

    const mockResponse = {
      success: true,
      message: 'User registered successfully',
      data: mockUser
    };

    userService.registerUser.and.returnValue(of(mockResponse));

    // Fill form
    component.registrationForm.patchValue({
      userName: 'testuser',
      password: 'password123',
      confirmPassword: 'password123',
      phone: '+1234567890',
      emailId: 'test@example.com',
      address1: '123 Test St',
      city: 'Test City',
      state: 'Test State',
      zipCode: '12345',
      dob: '1990-01-01'
    });

    component.onSubmit();

    expect(userService.registerUser).toHaveBeenCalledWith(mockUser);
    expect(component.message).toBe('User registered successfully!');
    expect(component.messageType).toBe('success');
  });

  it('should handle registration failure', () => {
    const mockResponse = {
      success: false,
      message: 'Username already exists',
      data: null
    };

    userService.registerUser.and.returnValue(of(mockResponse));

    // Fill form
    component.registrationForm.patchValue({
      userName: 'testuser',
      password: 'password123',
      confirmPassword: 'password123',
      phone: '+1234567890',
      emailId: 'test@example.com',
      address1: '123 Test St',
      city: 'Test City',
      state: 'Test State',
      zipCode: '12345',
      dob: '1990-01-01'
    });

    component.onSubmit();

    expect(component.message).toBe('Username already exists');
    expect(component.messageType).toBe('error');
  });

  it('should handle HTTP error', () => {
    const errorResponse = {
      error: {
        message: 'Server error occurred'
      }
    };

    userService.registerUser.and.returnValue(throwError(() => errorResponse));

    // Fill form
    component.registrationForm.patchValue({
      userName: 'testuser',
      password: 'password123',
      confirmPassword: 'password123',
      phone: '+1234567890',
      emailId: 'test@example.com',
      address1: '123 Test St',
      city: 'Test City',
      state: 'Test State',
      zipCode: '12345',
      dob: '1990-01-01'
    });

    component.onSubmit();

    expect(component.message).toBe('Server error occurred');
    expect(component.messageType).toBe('error');
  });

  it('should not submit invalid form', () => {
    component.onSubmit();
    
    expect(userService.registerUser).not.toHaveBeenCalled();
    expect(component.message).toBe('');
  });

  it('should mark form as touched on invalid submission', () => {
    const form = component.registrationForm;
    const userNameControl = form.get('userName');
    
    component.onSubmit();
    
    expect(userNameControl?.touched).toBeTruthy();
  });

  it('should clear message when clearMessage is called', () => {
    component.message = 'Test message';
    component.messageType = 'success';
    
    component.clearMessage();
    
    expect(component.message).toBe('');
    expect(component.messageType).toBe('');
  });

  it('should reset form after successful registration', () => {
    const mockResponse = {
      success: true,
      message: 'User registered successfully',
      data: {}
    };

    userService.registerUser.and.returnValue(of(mockResponse));

    // Fill form
    component.registrationForm.patchValue({
      userName: 'testuser',
      password: 'password123',
      confirmPassword: 'password123',
      phone: '+1234567890',
      emailId: 'test@example.com',
      address1: '123 Test St',
      city: 'Test City',
      state: 'Test State',
      zipCode: '12345',
      dob: '1990-01-01'
    });

    component.onSubmit();

    expect(component.registrationForm.get('userName')?.value).toBeNull();
    expect(component.registrationForm.get('role')?.value).toBe(UserRole.CUSTOMER);
    expect(component.registrationForm.get('customerCategory')?.value).toBe(CustomerCategory.REGULAR);
  });

  it('should get correct error messages for different validation errors', () => {
    expect(component.getErrorMessage('userName')).toBe('Username is required');
    expect(component.getErrorMessage('emailId')).toBe('Email is required');
    expect(component.getErrorMessage('phone')).toBe('Phone Number is required');
    expect(component.getErrorMessage('address1')).toBe('Address Line 1 is required');
    expect(component.getErrorMessage('city')).toBe('City is required');
    expect(component.getErrorMessage('state')).toBe('State is required');
    expect(component.getErrorMessage('zipCode')).toBe('ZIP Code is required');
    expect(component.getErrorMessage('dob')).toBe('Date of Birth is required');
  });

  it('should check if field has errors correctly', () => {
    const userNameControl = component.registrationForm.get('userName');
    userNameControl?.markAsTouched();
    
    expect(component.hasError('userName')).toBeTruthy();
    expect(component.hasError('emailId')).toBeFalsy();
  });
});
