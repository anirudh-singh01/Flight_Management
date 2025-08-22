import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AdminRegistrationComponent } from './admin-registration';
import { UserService } from '../../services/user.service';
import { User, UserRole, ApiResponse } from '../../models/user.model';

describe('AdminRegistrationComponent', () => {
  let component: AdminRegistrationComponent;
  let fixture: ComponentFixture<AdminRegistrationComponent>;
  let userService: jasmine.SpyObj<UserService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('UserService', ['registerAdmin']);
    
    await TestBed.configureTestingModule({
      imports: [AdminRegistrationComponent, ReactiveFormsModule, RouterTestingModule],
      providers: [
        { provide: UserService, useValue: spy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AdminRegistrationComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form without role and customer category fields', () => {
    expect(component.registrationForm.contains('role')).toBeFalse();
    expect(component.registrationForm.contains('customerCategory')).toBeFalse();
    expect(component.registrationForm.contains('userName')).toBeTrue();
    expect(component.registrationForm.contains('password')).toBeTrue();
  });

  it('should set role to ADMIN when submitting form', () => {
    const mockResponse: ApiResponse = {
      success: true,
      message: 'Administrator registered successfully',
      data: null
    };

    userService.registerAdmin.and.returnValue(of(mockResponse));

    // Fill out the form
    component.registrationForm.patchValue({
      userName: 'adminuser',
      password: 'adminpass123',
      confirmPassword: 'adminpass123',
      phone: '+1234567890',
      emailId: 'admin@example.com',
      address1: '123 Admin St',
      city: 'Admin City',
      state: 'Admin State',
      zipCode: '12345',
      dob: '1990-01-01'
    });

    component.onSubmit();

    // Verify that registerAdmin was called with ADMIN role
    expect(userService.registerAdmin).toHaveBeenCalledWith(jasmine.objectContaining({
      userName: 'adminuser',
      password: 'adminpass123',
      role: UserRole.ADMIN
    }));
  });

  it('should show success message on successful registration', () => {
    const mockResponse: ApiResponse = {
      success: true,
      message: 'Administrator registered successfully',
      data: null
    };

    userService.registerAdmin.and.returnValue(of(mockResponse));

    // Fill out the form
    component.registrationForm.patchValue({
      userName: 'adminuser',
      password: 'adminpass123',
      confirmPassword: 'adminpass123',
      phone: '+1234567890',
      emailId: 'admin@example.com',
      address1: '123 Admin St',
      city: 'Admin City',
      state: 'Admin State',
      zipCode: '12345',
      dob: '1990-01-01'
    });

    component.onSubmit();

    expect(component.message).toBe('Administrator registered successfully!');
    expect(component.messageType).toBe('success');
  });

  it('should show error message on registration failure', () => {
    const mockResponse: ApiResponse = {
      success: false,
      message: 'Username already exists',
      data: null
    };

    userService.registerAdmin.and.returnValue(of(mockResponse));

    // Fill out the form
    component.registrationForm.patchValue({
      userName: 'existingadmin',
      password: 'adminpass123',
      confirmPassword: 'adminpass123',
      phone: '+1234567890',
      emailId: 'admin@example.com',
      address1: '123 Admin St',
      city: 'Admin City',
      state: 'Admin State',
      zipCode: '12345',
      dob: '1990-01-01'
    });

    component.onSubmit();

    expect(component.message).toBe('Username already exists');
    expect(component.messageType).toBe('error');
  });

  it('should handle service errors', () => {
    const errorResponse: ApiResponse = {
      success: false,
      message: 'Service unavailable',
      data: null
    };

    userService.registerAdmin.and.returnValue(of(errorResponse));

    // Fill out the form
    component.registrationForm.patchValue({
      userName: 'adminuser',
      password: 'adminpass123',
      confirmPassword: 'adminpass123',
      phone: '+1234567890',
      emailId: 'admin@example.com',
      address1: '123 Admin St',
      city: 'Admin City',
      state: 'Admin State',
      zipCode: '12345',
      dob: '1990-01-01'
    });

    component.onSubmit();

    expect(component.message).toBe('Service unavailable');
    expect(component.messageType).toBe('error');
  });

  it('should validate required fields', () => {
    component.onSubmit();
    
    expect(component.registrationForm.valid).toBeFalse();
    expect(component.hasError('userName')).toBeTrue();
    expect(component.hasError('password')).toBeTrue();
    expect(component.hasError('emailId')).toBeTrue();
  });

  it('should validate password confirmation', () => {
    component.registrationForm.patchValue({
      password: 'password123',
      confirmPassword: 'differentpassword'
    });

    expect(component.registrationForm.hasError('passwordMismatch')).toBeTrue();
  });

  it('should clear message when clearMessage is called', () => {
    component.message = 'Test message';
    component.messageType = 'success';
    
    component.clearMessage();
    
    expect(component.message).toBe('');
    expect(component.messageType).toBe('');
  });

  it('should reset form after successful registration', () => {
    const mockResponse: ApiResponse = {
      success: true,
      message: 'Administrator registered successfully',
      data: null
    };

    userService.registerAdmin.and.returnValue(of(mockResponse));

    // Fill out the form
    component.registrationForm.patchValue({
      userName: 'adminuser',
      password: 'adminpass123',
      confirmPassword: 'adminpass123',
      phone: '+1234567890',
      emailId: 'admin@example.com',
      address1: '123 Admin St',
      city: 'Admin City',
      state: 'Admin State',
      zipCode: '12345',
      dob: '1990-01-01'
    });

    component.onSubmit();

    // Form should be reset
    expect(component.registrationForm.get('userName')?.value).toBeNull();
    expect(component.registrationForm.get('password')?.value).toBeNull();
  });
});
