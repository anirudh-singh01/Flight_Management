import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { FlightService } from '../../services/flight.service';
import { CarrierService } from '../../services/carrier.service';
import { Flight } from '../../models/flight.model';
import { Carrier } from '../../models/carrier.model';

@Component({
  selector: 'app-update-flight',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './update-flight.component.html',
  styleUrls: ['./update-flight.component.css']
})
export class UpdateFlightComponent implements OnInit {
  flightForm: FormGroup;
  carriers: Carrier[] = [];
  loading = false;
  errorMessage = '';
  successMessage = '';
  flightId: number | null = null;
  currentFlight: Flight | null = null;

  constructor(
    private fb: FormBuilder,
    private flightService: FlightService,
    private carrierService: CarrierService,
    private router: Router,
    private route: ActivatedRoute
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
    this.route.params.subscribe(params => {
      this.flightId = +params['id'];
      if (this.flightId) {
        this.loadFlightDetails();
      }
    });
    this.loadCarriers();
  }

  loadFlightDetails(): void {
    if (!this.flightId) return;
    
    this.loading = true;
    this.errorMessage = '';
    
    this.flightService.getFlightById(this.flightId).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.currentFlight = response.data as Flight;
          this.populateForm();
        } else {
          this.errorMessage = response.message || 'Failed to load flight details';
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading flight details:', error);
        this.errorMessage = 'An error occurred while loading flight details';
        this.loading = false;
      }
    });
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

  populateForm(): void {
    if (this.currentFlight) {
      this.flightForm.patchValue({
        carrierId: this.currentFlight.carrierId,
        origin: this.currentFlight.origin,
        destination: this.currentFlight.destination,
        airFare: this.currentFlight.airFare,
        seatCapacityBusiness: this.currentFlight.seatCapacityBusiness,
        seatCapacityEconomy: this.currentFlight.seatCapacityEconomy,
        seatCapacityExecutive: this.currentFlight.seatCapacityExecutive
      });
    }
  }

  onSubmit(): void {
    if (this.flightForm.valid && this.flightId) {
      this.loading = true;
      this.errorMessage = '';
      this.successMessage = '';

      const flightData: Flight = this.flightForm.value;

      this.flightService.updateFlight(this.flightId, flightData).subscribe({
        next: (response) => {
          if (response.success) {
            this.successMessage = 'Flight updated successfully!';
            this.loading = false;
            // Redirect to view flights after a short delay
            setTimeout(() => {
              this.router.navigate(['/view-flights']);
            }, 2000);
          } else {
            this.errorMessage = response.message || 'Failed to update flight';
            this.loading = false;
          }
        },
        error: (error) => {
          console.error('Error updating flight:', error);
          this.errorMessage = 'An error occurred while updating the flight';
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
    this.router.navigate(['/view-flights']);
  }

  cancelUpdate(): void {
    this.router.navigate(['/view-flights']);
  }
}
