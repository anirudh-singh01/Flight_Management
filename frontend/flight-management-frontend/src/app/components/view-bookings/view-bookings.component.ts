import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookFlightService, BookFlightResponse } from '../../services/book-flight.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './view-bookings.component.html',
  styleUrls: ['./view-bookings.component.css']
})
export class ViewBookingsComponent implements OnInit {
  bookings: BookFlightResponse[] = [];
  loading = false;
  error = '';
  userId: number = 1; // Default user ID, in real app this would come from auth service
  cancellingBooking: number | null = null;

  constructor(
    private bookingService: BookFlightService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserBookings();
  }

  loadUserBookings(): void {
    this.loading = true;
    this.error = '';
    
    this.bookingService.getUserBookings(this.userId).subscribe({
      next: (response: any) => {
        if (response.success) {
          this.bookings = response.data;
        } else {
          this.error = response.message || 'Failed to load bookings';
        }
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error loading bookings. Please try again.';
        this.loading = false;
        console.error('Error loading bookings:', error);
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'BOOKED':
        return 'status-booked';
      case 'CANCELLED':
        return 'status-cancelled';
      case 'COMPLETED':
        return 'status-completed';
      case 'PENDING':
        return 'status-pending';
      default:
        return 'status-default';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }

  formatDateTime(dateTimeString: string): string {
    if (!dateTimeString) return '';
    const date = new Date(dateTimeString);
    return date.toLocaleString();
  }

  formatAmount(amount: number): string {
    return `$${amount.toFixed(2)}`;
  }

  onUserIdChange(): void {
    this.loadUserBookings();
  }

  goToBookFlight(): void {
    this.router.navigate(['/book-flight']);
  }
  
  cancelBooking(bookingId: number): void {
    if (confirm('Are you sure you want to cancel this booking? This action cannot be undone.')) {
      this.cancellingBooking = bookingId;
      
      this.bookingService.cancelBooking(bookingId).subscribe({
        next: (response: any) => {
          if (response.success) {
            // Update the local booking data with cancellation details
            const cancelledBooking = response.data;
            const index = this.bookings.findIndex(b => b.bookingId === bookingId);
            if (index !== -1) {
              this.bookings[index] = {
                ...this.bookings[index],
                bookingStatus: cancelledBooking.bookingStatus,
                refundAmount: cancelledBooking.refundAmount,
                refundPercentage: cancelledBooking.refundPercentage
              };
            }
            
            // Show success message
            alert(`Booking cancelled successfully! Refund amount: $${cancelledBooking.refundAmount.toFixed(2)} (${cancelledBooking.refundPercentage}%)`);
          } else {
            this.error = response.message || 'Failed to cancel booking';
            alert('Error cancelling booking: ' + this.error);
          }
          this.cancellingBooking = null;
        },
        error: (error) => {
          this.error = 'Error cancelling booking. Please try again.';
          this.cancellingBooking = null;
          alert('Error cancelling booking: ' + this.error);
          console.error('Error cancelling booking:', error);
        }
      });
    }
  }
}
