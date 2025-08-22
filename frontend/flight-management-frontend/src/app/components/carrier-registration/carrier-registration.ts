import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CarrierService } from '../../services/carrier.service';
import { Carrier, DiscountType, RefundType, DISCOUNT_TYPE_OPTIONS, REFUND_TYPE_OPTIONS } from '../../models/carrier.model';

@Component({
  selector: 'app-carrier-registration',
  templateUrl: './carrier-registration.html',
  styleUrls: ['./carrier-registration.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class CarrierRegistrationComponent implements OnInit {
  registrationForm: FormGroup;
  isLoading = false;
  message = '';
  messageType = '';
  
  discountTypeOptions = DISCOUNT_TYPE_OPTIONS;
  refundTypeOptions = REFUND_TYPE_OPTIONS;

  constructor(
    private fb: FormBuilder,
    private carrierService: CarrierService
  ) {
    this.registrationForm = this.fb.group({
      carrierName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      discountPercentage: ['', [Validators.required, Validators.min(0.01), Validators.max(99.99)]],
      refundPercentage: ['', [Validators.required, Validators.min(0.01), Validators.max(99.99)]],
      discountType: ['', Validators.required],
      refundType: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    // Form is already set up
  }

  /**
   * Handle form submission
   */
  onSubmit() {
    if (this.registrationForm.valid) {
      this.isLoading = true;
      this.message = '';
      
      const formValue = this.registrationForm.value;
      const carrier: Carrier = {
        carrierName: formValue.carrierName,
        discountPercentage: formValue.discountPercentage,
        refundPercentage: formValue.refundPercentage,
        discountType: formValue.discountType,
        refundType: formValue.refundType,
        description: formValue.description || undefined
      };

      this.carrierService.registerCarrier(carrier).subscribe({
        next: (response) => {
          if (response.success) {
            this.message = 'Carrier registered successfully!';
            this.messageType = 'success';
            this.registrationForm.reset();
          } else {
            this.message = response.message || 'Registration failed';
            this.messageType = 'error';
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error registering carrier:', error);
          this.message = 'An error occurred while registering the carrier. Please try again.';
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
   * Check if a form field is invalid and has been touched
   */
  isFieldInvalid(fieldName: string): boolean {
    const field = this.registrationForm.get(fieldName);
    return field ? field.invalid && field.touched : false;
  }

  /**
   * Get error message for a form field
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
        return `${this.getFieldDisplayName(fieldName)} must not exceed ${field.errors['maxlength'].requiredLength} characters`;
      }
      if (field.errors['min']) {
        return `${this.getFieldDisplayName(fieldName)} must be at least ${field.errors['min'].min}`;
      }
      if (field.errors['max']) {
        return `${this.getFieldDisplayName(fieldName)} must not exceed ${field.errors['max'].max}`;
      }
    }
    return '';
  }

  /**
   * Get display name for form fields
   */
  getFieldDisplayName(fieldName: string): string {
    const fieldNames: { [key: string]: string } = {
      carrierName: 'Carrier Name',
      discountPercentage: 'Discount Percentage',
      refundPercentage: 'Refund Percentage',
      discountType: 'Discount Type',
      refundType: 'Refund Type',
      description: 'Description'
    };
    return fieldNames[fieldName] || fieldName;
  }

  /**
   * Clear form and messages
   */
  clearForm() {
    this.registrationForm.reset();
    this.message = '';
    this.messageType = '';
  }
}
