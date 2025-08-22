import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { BookFlightService } from '../../services/book-flight.service';
import { FlightService } from '../../services/flight.service';

@Component({
  selector: 'app-book-flight',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './book-flight.component.html',
  styleUrls: ['./book-flight.component.css']
})
export class BookFlightComponent implements OnInit {
  bookingForm: FormGroup;
  availableFlights: any[] = [];
  selectedFlight: any = null;
  bookingSummary: any = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  
  seatCategories = [
    { value: 'ECONOMY', label: 'Economy' },
    { value: 'BUSINESS', label: 'Business' },
    { value: 'EXECUTIVE', label: 'Executive' }
  ];

  constructor(
    private fb: FormBuilder,
    private bookFlightService: BookFlightService,
    private flightService: FlightService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.bookingForm = this.fb.group({
      flightId: ['', Validators.required],
      noOfSeats: ['', [Validators.required, Validators.min(1), Validators.max(10)]],
      seatCategory: ['', Validators.required],
      dateOfTravel: ['', [Validators.required, this.futureDateValidator()]]
    });
  }

  ngOnInit(): void {
    this.loadAvailableFlights();
    
    // Get flight ID from route if available
    this.route.params.subscribe(params => {
      if (params['flightId']) {
        this.bookingForm.patchValue({
          flightId: params['flightId']
        });
        this.onFlightSelection(params['flightId']);
      }
    });
  }

  loadAvailableFlights(): void {
    this.isLoading = true;
    this.flightService.getAllFlights().subscribe({
      next: (response: any) => {
        if (response.success) {
          this.availableFlights = response.data;
        } else {
          this.errorMessage = 'Failed to load flights';
        }
      },
      error: (error) => {
        this.errorMessage = 'Error loading flights: ' + error.message;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  onFlightSelection(flightId: string): void {
    this.selectedFlight = this.availableFlights.find(f => f.flightId == flightId);
    this.calculateEstimatedCost();
  }

  calculateEstimatedCost(): void {
    if (this.selectedFlight && this.bookingForm.valid) {
      const noOfSeats = this.bookingForm.get('noOfSeats')?.value;
      const baseFare = this.selectedFlight.airFare;
      const estimatedTotal = baseFare * noOfSeats;
      
      // Simple estimation - actual discounts will be calculated on the backend
      this.bookingSummary = {
        flightDetails: this.selectedFlight,
        noOfSeats: noOfSeats,
        seatCategory: this.bookingForm.get('seatCategory')?.value,
        dateOfTravel: this.bookingForm.get('dateOfTravel')?.value,
        baseFare: baseFare,
        estimatedTotal: estimatedTotal,
        estimatedDiscount: 0,
        finalAmount: estimatedTotal
      };
    }
  }

  onSubmit(): void {
    if (this.bookingForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      const bookingData = this.bookingForm.value;
      
      // Get user ID from localStorage or service (assuming user is logged in)
      const userId = this.getCurrentUserId();
      if (!userId) {
        this.errorMessage = 'User not logged in. Please login first.';
        this.isLoading = false;
        return;
      }

      this.bookFlightService.bookFlight(bookingData, userId).subscribe({
        next: (response: any) => {
          if (response.success) {
            this.successMessage = 'Flight booked successfully!';
            this.bookingSummary = response.data;
            this.bookingForm.reset();
            this.selectedFlight = null;
          } else {
            this.errorMessage = response.message || 'Booking failed';
          }
        },
        error: (error) => {
          this.errorMessage = 'Error booking flight: ' + error.message;
        },
        complete: () => {
          this.isLoading = false;
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  getCurrentUserId(): number | null {
    // This should be implemented based on your authentication service
    // For now, returning a mock user ID
    const userStr = localStorage.getItem('currentUser');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        return user.userId || 1; // Default to 1 if not found
      } catch {
        return 1; // Default user ID
      }
    }
    return 1; // Default user ID for testing
  }

  markFormGroupTouched(): void {
    Object.keys(this.bookingForm.controls).forEach(key => {
      const control = this.bookingForm.get(key);
      control?.markAsTouched();
    });
  }

  futureDateValidator() {
    return (control: any) => {
      const selectedDate = new Date(control.value);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      
      if (selectedDate <= today) {
        return { pastDate: true };
      }
      return null;
    };
  }

  getErrorMessage(controlName: string): string {
    const control = this.bookingForm.get(controlName);
    if (control?.errors && control.touched) {
      if (control.errors['required']) {
        return `${this.getFieldLabel(controlName)} is required`;
      }
      if (control.errors['min']) {
        return `${this.getFieldLabel(controlName)} must be at least ${control.errors['min'].min}`;
      }
      if (control.errors['max']) {
        return `${this.getFieldLabel(controlName)} must be at most ${control.errors['max'].max}`;
      }
      if (control.errors['pastDate']) {
        return 'Date of travel must be in the future';
      }
    }
    return '';
  }

  getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      flightId: 'Flight',
      noOfSeats: 'Number of seats',
      seatCategory: 'Seat category',
      dateOfTravel: 'Date of travel'
    };
    return labels[fieldName] || fieldName;
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.bookingForm.get(fieldName);
    return !!(control?.invalid && control?.touched);
  }

  onFormChange(): void {
    if (this.bookingForm.valid) {
      this.calculateEstimatedCost();
    }
  }

  getMinDate(): string {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  }

  viewMyBookings(): void {
    this.router.navigate(['/my-bookings']);
  }

  goBack(): void {
    this.router.navigate(['/view-flights']);
  }
}
