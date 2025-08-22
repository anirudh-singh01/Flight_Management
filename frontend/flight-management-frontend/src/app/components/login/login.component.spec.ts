import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { UserService } from '../../services/user.service';
import { LoginRequest, LoginResponse, ApiResponse } from '../../models/user.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockUserService: jasmine.SpyObj<UserService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['login']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    mockUserService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    mockRouter = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form', () => {
    expect(component.loginForm.get('userName')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
  });

  it('should validate required fields', () => {
    const form = component.loginForm;
    
    // Test empty form
    expect(form.valid).toBeFalsy();
    expect(form.get('userName')?.errors?.['required']).toBeTruthy();
    expect(form.get('password')?.errors?.['required']).toBeTruthy();
    
    // Test with valid values
    form.patchValue({
      userName: 'testuser',
      password: 'password123'
    });
    
    expect(form.valid).toBeTruthy();
  });

  it('should validate username length constraints', () => {
    const userNameControl = component.loginForm.get('userName');
    
    // Test minimum length
    userNameControl?.setValue('ab');
    expect(userNameControl?.errors?.['minlength']).toBeTruthy();
    
    // Test maximum length
    userNameControl?.setValue('a'.repeat(51));
    expect(userNameControl?.errors?.['maxlength']).toBeTruthy();
    
    // Test valid length
    userNameControl?.setValue('testuser');
    expect(userNameControl?.errors).toBeNull();
  });

  it('should validate password minimum length', () => {
    const passwordControl = component.loginForm.get('password');
    
    // Test minimum length
    passwordControl?.setValue('12345');
    expect(passwordControl?.errors?.['minlength']).toBeTruthy();
    
    // Test valid length
    passwordControl?.setValue('password123');
    expect(passwordControl?.errors).toBeNull();
  });

  it('should show error message for required fields', () => {
    const userNameError = component.getErrorMessage('userName');
    const passwordError = component.getErrorMessage('password');
    
    expect(userNameError).toBe('Username is required');
    expect(passwordError).toBe('Password is required');
  });

  it('should show error message for length constraints', () => {
    const userNameControl = component.loginForm.get('userName');
    const passwordControl = component.loginForm.get('password');
    
    userNameControl?.setValue('ab');
    userNameControl?.markAsTouched();
    
    passwordControl?.setValue('12345');
    passwordControl?.markAsTouched();
    
    expect(component.getErrorMessage('userName')).toBe('Username must be at least 3 characters');
    expect(component.getErrorMessage('password')).toBe('Password must be at least 6 characters');
  });

  it('should call login service on valid form submission', () => {
    const mockResponse: ApiResponse = {
      success: true,
      message: 'Login successful',
      data: {
        userId: 1,
        userName: 'testuser',
        role: 'CUSTOMER',
        message: 'Login successful'
      } as LoginResponse
    };

    mockUserService.login.and.returnValue(of(mockResponse));
    
    component.loginForm.patchValue({
      userName: 'testuser',
      password: 'password123'
    });
    
    component.onSubmit();
    
    expect(mockUserService.login).toHaveBeenCalledWith('testuser', 'password123');
  });

  it('should handle successful login response', () => {
    const mockResponse: ApiResponse = {
      success: true,
      message: 'Login successful',
      data: {
        userId: 1,
        userName: 'testuser',
        role: 'CUSTOMER',
        message: 'Login successful'
      } as LoginResponse
    };

    mockUserService.login.and.returnValue(of(mockResponse));
    
    component.loginForm.patchValue({
      userName: 'testuser',
      password: 'password123'
    });
    
    component.onSubmit();
    
    expect(component.successMessage).toBe('Login successful!');
    expect(component.errorMessage).toBe('');
    expect(component.isLoading).toBeFalse();
  });

  it('should handle login error response', () => {
    const mockResponse: ApiResponse = {
      success: false,
      message: 'Invalid credentials',
      data: null
    };

    mockUserService.login.and.returnValue(of(mockResponse));
    
    component.loginForm.patchValue({
      userName: 'testuser',
      password: 'wrongpassword'
    });
    
    component.onSubmit();
    
    expect(component.errorMessage).toBe('Invalid credentials');
    expect(component.successMessage).toBe('');
    expect(component.isLoading).toBeFalse();
  });

  it('should handle service error', () => {
    const errorResponse = {
      error: {
        message: 'User not found'
      }
    };

    mockUserService.login.and.returnValue(throwError(() => errorResponse));
    
    component.loginForm.patchValue({
      userName: 'nonexistent',
      password: 'password123'
    });
    
    component.onSubmit();
    
    expect(component.errorMessage).toBe('User not found');
    expect(component.isLoading).toBeFalse();
  });

  it('should navigate to customer dashboard for customer role', () => {
    const mockResponse: ApiResponse = {
      success: true,
      message: 'Login successful',
      data: {
        userId: 1,
        userName: 'testuser',
        role: 'CUSTOMER',
        message: 'Login successful'
      } as LoginResponse
    };

    mockUserService.login.and.returnValue(of(mockResponse));
    
    component.loginForm.patchValue({
      userName: 'testuser',
      password: 'password123'
    });
    
    component.onSubmit();
    
    // Wait for the timeout to complete
    setTimeout(() => {
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/customer-dashboard']);
    }, 1100);
  });

  it('should navigate to admin dashboard for admin role', () => {
    const mockResponse: ApiResponse = {
      success: true,
      message: 'Login successful',
      data: {
        userId: 1,
        userName: 'adminuser',
        role: 'ADMIN',
        message: 'Login successful'
      } as LoginResponse
    };

    mockUserService.login.and.returnValue(of(mockResponse));
    
    component.loginForm.patchValue({
      userName: 'adminuser',
      password: 'password123'
    });
    
    component.onSubmit();
    
    // Wait for the timeout to complete
    setTimeout(() => {
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin-dashboard']);
    }, 1100);
  });

  it('should not submit form when invalid', () => {
    component.onSubmit();
    
    expect(mockUserService.login).not.toHaveBeenCalled();
  });

  it('should navigate to register page', () => {
    component.goToRegister();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/register']);
  });

  it('should navigate to admin register page', () => {
    component.goToAdminRegister();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/admin-register']);
  });
});
