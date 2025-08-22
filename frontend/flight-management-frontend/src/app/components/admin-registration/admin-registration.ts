import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User, UserRole } from '../../models/user.model';

@Component({
  selector: 'app-admin-registration',
  templateUrl: './admin-registration.html',
  styleUrls: ['./admin-registration.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class AdminRegistrationComponent implements OnInit {
  registrationForm: FormGroup;
  isLoading = false;
  message = '';
  messageType = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserService
  ) {
    this.registrationForm = this.fb.group({
      userName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      phone: ['', [Validators.required, Validators.pattern(/^\+?[1-9]\d{1,14}$/)]],
      emailId: ['', [Validators.required, Validators.email]],
      address1: ['', Validators.required],
      address2: [''],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', [Validators.required, Validators.pattern(/^\d{5}(-\d{4})?$/)]],
      dob: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    // Form is already set up without role and customer category fields
    // Role will be automatically set to ADMIN on the backend
  }

  /**
   * Custom validator to check if passwords match
   */
  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    if (confirmPassword && confirmPassword.errors) {
      delete confirmPassword.errors['passwordMismatch'];
      if (Object.keys(confirmPassword.errors).length === 0) {
        confirmPassword.setErrors(null);
      }
    }
    
    return null;
  }

  /**
   * Handle form submission
   */
  onSubmit() {
    if (this.registrationForm.valid) {
      this.isLoading = true;
      this.message = '';
      
      const formValue = this.registrationForm.value;
      const user: User = {
        userName: formValue.userName,
        password: formValue.password,
        role: UserRole.ADMIN, // Explicitly set role to ADMIN
        phone: formValue.phone,
        emailId: formValue.emailId,
        address1: formValue.address1,
        address2: formValue.address2,
        city: formValue.city,
        state: formValue.state,
        zipCode: formValue.zipCode,
        dob: formValue.dob
      };

      this.userService.registerAdmin(user).subscribe({
        next: (response) => {
          if (response.success) {
            this.message = 'Administrator registered successfully!';
            this.messageType = 'success';
            this.registrationForm.reset();
          } else {
            this.message = response.message || 'Registration failed';
            this.messageType = 'error';
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Registration error:', error);
          this.message = error.error?.message || 'An error occurred during registration';
          this.messageType = 'error';
          this.isLoading = false;
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  /**
   * Mark all form controls as touched to trigger validation display
   */
  markFormGroupTouched() {
    Object.keys(this.registrationForm.controls).forEach(key => {
      const control = this.registrationForm.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Check if a field has validation errors
   */
  hasError(fieldName: string): boolean {
    const field = this.registrationForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  /**
   * Get error message for a field
   */
  getErrorMessage(fieldName: string): string {
    const field = this.registrationForm.get(fieldName);
    if (field && field.errors) {
      if (field.errors['required']) {
        return `${this.getFieldDisplayName(fieldName)} is required`;
      }
      if (field.errors['minlength']) {
        return `${this.getFieldDisplayName(fieldName)} must be at least ${field.errors['minlength'].requiredLength} characters`;
      }
      if (field.errors['maxlength']) {
        return `${this.getFieldDisplayName(fieldName)} must be at most ${field.errors['maxlength'].requiredLength} characters`;
      }
      if (field.errors['email']) {
        return 'Please enter a valid email address';
      }
      if (field.errors['pattern']) {
        if (fieldName === 'phone') {
          return 'Please enter a valid phone number';
        }
        if (fieldName === 'zipCode') {
          return 'Please enter a valid ZIP code (e.g., 12345 or 12345-6789)';
        }
      }
      if (field.errors['passwordMismatch']) {
        return 'Passwords do not match';
      }
    }
    return '';
  }

  /**
   * Get display name for form fields
   */
  getFieldDisplayName(fieldName: string): string {
    const displayNames: { [key: string]: string } = {
      userName: 'Username',
      password: 'Password',
      confirmPassword: 'Confirm Password',
      phone: 'Phone Number',
      emailId: 'Email',
      address1: 'Address Line 1',
      address2: 'Address Line 2',
      city: 'City',
      state: 'State',
      zipCode: 'ZIP Code',
      dob: 'Date of Birth'
    };
    return displayNames[fieldName] || fieldName;
  }

  /**
   * Clear message
   */
  clearMessage() {
    this.message = '';
    this.messageType = '';
  }
}
