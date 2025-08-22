import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { FlightService } from '../../services/flight.service';
import { CarrierService } from '../../services/carrier.service';
import { Flight } from '../../models/flight.model';
import { Carrier } from '../../models/carrier.model';

@Component({
  selector: 'app-flight-registration',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './flight-registration.component.html',
  styleUrls: ['./flight-registration.component.css']
})
export class FlightRegistrationComponent implements OnInit {
  flightForm: FormGroup;
  carriers: Carrier[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private flightService: FlightService,
    private carrierService: CarrierService,
    private router: Router
  ) {
    this.flightForm = this.fb.group({
      carrierId: ['', [Validators.required]],
      origin: ['', [Validators.required, Validators.minLength(2)]],
      destination: ['', [Validators.required, Validators.minLength(2)]],
      airFare: ['', [Validators.required, Validators.min(0.01)]],
      seatCapacityBusiness: ['', [Validators.required, Validators.min(1)]],
      seatCapacityEconomy: ['', [Validators.required, Validators.min(1)]],
      seatCapacityExecutive: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.loadCarriers();
  }

  loadCarriers(): void {
    this.carrierService.getAllCarriers().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.carriers = response.data as Carrier[];
        }
      },
      error: (error) => {
        console.error('Error loading carriers:', error);
        this.errorMessage = 'Failed to load carriers';
      }
    });
  }

  onSubmit(): void {
    if (this.flightForm.valid) {
      this.loading = true;
      this.errorMessage = '';
      this.successMessage = '';

      const flightData: Flight = this.flightForm.value;

      this.flightService.registerFlight(flightData).subscribe({
        next: (response) => {
          if (response.success) {
            this.successMessage = 'Flight registered successfully!';
            this.flightForm.reset();
            this.loading = false;
          } else {
            this.errorMessage = response.message || 'Failed to register flight';
            this.loading = false;
          }
        },
        error: (error) => {
          console.error('Error registering flight:', error);
          this.errorMessage = 'An error occurred while registering the flight';
          this.loading = false;
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  markFormGroupTouched(): void {
    Object.keys(this.flightForm.controls).forEach(key => {
      const control = this.flightForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string {
    const field = this.flightForm.get(fieldName);
    if (field?.errors && field?.touched) {
      if (field.errors['required']) {
        return `${this.getFieldDisplayName(fieldName)} is required`;
      }
      if (field.errors['minlength']) {
        return `${this.getFieldDisplayName(fieldName)} must be at least ${field.errors['minlength'].requiredLength} characters`;
      }
      if (field.errors['min']) {
        return `${this.getFieldDisplayName(fieldName)} must be at least ${field.errors['min'].min}`;
      }
    }
    return '';
  }

  getFieldDisplayName(fieldName: string): string {
    const displayNames: { [key: string]: string } = {
      carrierId: 'Carrier',
      origin: 'Origin',
      destination: 'Destination',
      airFare: 'Air Fare',
      seatCapacityBusiness: 'Business Seat Capacity',
      seatCapacityEconomy: 'Economy Seat Capacity',
      seatCapacityExecutive: 'Executive Seat Capacity'
    };
    return displayNames[fieldName] || fieldName;
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.flightForm.get(fieldName);
    return !!(field?.invalid && field?.touched);
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
