import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CarrierService } from '../../services/carrier.service';
import { Carrier, DiscountType, RefundType, DISCOUNT_TYPE_OPTIONS, REFUND_TYPE_OPTIONS } from '../../models/carrier.model';

@Component({
  selector: 'app-update-carrier',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './update-carrier.component.html',
  styleUrls: ['./update-carrier.component.css']
})
export class UpdateCarrierComponent implements OnInit {
  updateForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';
  carrierId: number | null = null;
  currentCarrier: Carrier | null = null;
  
  discountTypeOptions = DISCOUNT_TYPE_OPTIONS;
  refundTypeOptions = REFUND_TYPE_OPTIONS;

  constructor(
    private fb: FormBuilder,
    private carrierService: CarrierService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.updateForm = this.fb.group({
      carrierName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      discountPercentage: ['', [Validators.required, Validators.min(0.01), Validators.max(99.99)]],
      refundPercentage: ['', [Validators.required, Validators.min(0.01), Validators.max(99.99)]],
      discountType: ['', Validators.required],
      refundType: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.carrierId = +params['id'];
      if (this.carrierId) {
        this.loadCarrierDetails();
      }
    });
  }

  loadCarrierDetails(): void {
    if (!this.carrierId) return;
    
    this.loading = true;
    this.errorMessage = '';
    
    this.carrierService.getCarrierById(this.carrierId).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.currentCarrier = response.data as Carrier;
          this.populateForm();
        } else {
          this.errorMessage = response.message || 'Failed to load carrier details';
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading carrier details:', error);
        this.errorMessage = 'Failed to load carrier details. Please try again.';
        this.loading = false;
      }
    });
  }

  populateForm(): void {
    if (this.currentCarrier) {
      this.updateForm.patchValue({
        carrierName: this.currentCarrier.carrierName,
        discountPercentage: this.currentCarrier.discountPercentage,
        refundPercentage: this.currentCarrier.refundPercentage,
        discountType: this.currentCarrier.discountType,
        refundType: this.currentCarrier.refundType,
        description: this.currentCarrier.description || ''
      });
    }
  }

  onSubmit(): void {
    if (this.updateForm.valid && this.carrierId) {
      this.loading = true;
      this.errorMessage = '';
      this.successMessage = '';
      
      const formValue = this.updateForm.value;
      const carrier: Carrier = {
        carrierId: this.carrierId,
        carrierName: formValue.carrierName,
        discountPercentage: formValue.discountPercentage,
        refundPercentage: formValue.refundPercentage,
        discountType: formValue.discountType,
        refundType: formValue.refundType,
        description: formValue.description || undefined
      };

      this.carrierService.updateCarrier(this.carrierId, carrier).subscribe({
        next: (response) => {
          if (response.success) {
            this.successMessage = 'Carrier updated successfully!';
            this.currentCarrier = response.data as Carrier;
            this.populateForm();
          } else {
            this.errorMessage = response.message || 'Update failed';
          }
          this.loading = false;
        },
        error: (error) => {
          console.error('Error updating carrier:', error);
          this.errorMessage = 'An error occurred while updating the carrier. Please try again.';
          this.loading = false;
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel(): void {
    this.router.navigate(['/carrier-register']); // Navigate back to carrier registration
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.updateForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  getFieldError(fieldName: string): string {
    const field = this.updateForm.get(fieldName);
    if (field && field.errors) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['minlength']) return `${fieldName} must be at least ${field.errors['minlength'].requiredLength} characters`;
      if (field.errors['maxlength']) return `${fieldName} must not exceed ${field.errors['maxlength'].requiredLength} characters`;
      if (field.errors['min']) return `${fieldName} must be at least ${field.errors['min'].min}`;
      if (field.errors['max']) return `${fieldName} must not exceed ${field.errors['max'].max}`;
    }
    return '';
  }

  private markFormGroupTouched(): void {
    Object.keys(this.updateForm.controls).forEach(key => {
      const control = this.updateForm.get(key);
      control?.markAsTouched();
    });
  }
}
