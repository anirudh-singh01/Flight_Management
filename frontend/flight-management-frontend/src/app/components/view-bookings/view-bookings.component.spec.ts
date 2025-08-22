import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ViewBookingsComponent } from './view-bookings.component';
import { BookFlightService } from '../../services/book-flight.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

describe('ViewBookingsComponent', () => {
  let component: ViewBookingsComponent;
  let fixture: ComponentFixture<ViewBookingsComponent>;
  let mockBookingService: jasmine.SpyObj<BookFlightService>;
  let mockRouter: jasmine.SpyObj<Router>;

  const mockBookings = [
    {
      bookingId: 1,
      flightId: 1,
      userId: 1,
      noOfSeats: 2,
      seatCategory: 'ECONOMY',
      dateOfTravel: '2024-06-15',
      bookingAmount: 509.98,
      discountAmount: 89.98,
      discountReason: 'Advance booking (30+ days): 15%, Customer category (GOLD): 15%',
      bookingStatus: 'BOOKED',
      bookingDate: '2024-01-15T10:30:00',
      origin: 'New York',
      destination: 'Los Angeles',
      carrierName: 'Test Airlines',
      originalAirFare: 299.99
    },
    {
      bookingId: 2,
      flightId: 2,
      userId: 1,
      noOfSeats: 1,
      seatCategory: 'BUSINESS',
      dateOfTravel: '2024-07-20',
      bookingAmount: 399.99,
      discountAmount: 0,
      discountReason: 'No discounts applied',
      bookingStatus: 'BOOKED',
      bookingDate: '2024-01-20T14:45:00',
      origin: 'Chicago',
      destination: 'Miami',
      carrierName: 'Another Airlines',
      originalAirFare: 399.99
    }
  ];

  beforeEach(async () => {
    mockBookingService = jasmine.createSpyObj('BookFlightService', ['getUserBookings']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [CommonModule, FormsModule],
      providers: [
        { provide: BookFlightService, useValue: mockBookingService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ViewBookingsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user bookings on init', () => {
    // Arrange
    mockBookingService.getUserBookings.and.returnValue(of({
      success: true,
      message: 'User bookings retrieved successfully',
      data: mockBookings
    }));

    // Act
    fixture.detectChanges();

    // Assert
    expect(mockBookingService.getUserBookings).toHaveBeenCalledWith(1);
    expect(component.bookings).toEqual(mockBookings);
    expect(component.loading).toBeFalse();
    expect(component.error).toBe('');
  });

  it('should handle successful response with empty bookings', () => {
    // Arrange
    mockBookingService.getUserBookings.and.returnValue(of({
      success: true,
      message: 'User bookings retrieved successfully',
      data: []
    }));

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.bookings).toEqual([]);
    expect(component.loading).toBeFalse();
    expect(component.error).toBe('');
  });

  it('should handle error response', () => {
    // Arrange
    mockBookingService.getUserBookings.and.returnValue(of({
      success: false,
      message: 'Failed to load bookings',
      data: null
    }));

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.error).toBe('Failed to load bookings');
    expect(component.loading).toBeFalse();
  });

  it('should handle HTTP error', () => {
    // Arrange
    mockBookingService.getUserBookings.and.returnValue(throwError(() => new Error('Network error')));

    // Act
    fixture.detectChanges();

    // Assert
    expect(component.error).toBe('Error loading bookings. Please try again.');
    expect(component.loading).toBeFalse();
  });

  it('should reload bookings when user ID changes', () => {
    // Arrange
    component.userId = 2;
    mockBookingService.getUserBookings.and.returnValue(of({
      success: true,
      message: 'User bookings retrieved successfully',
      data: mockBookings
    }));

    // Act
    component.onUserIdChange();

    // Assert
    expect(mockBookingService.getUserBookings).toHaveBeenCalledWith(2);
  });

  it('should navigate to book flight page', () => {
    // Act
    component.goToBookFlight();

    // Assert
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/book-flight']);
  });

  it('should return correct status class for different statuses', () => {
    // Assert
    expect(component.getStatusClass('BOOKED')).toBe('status-booked');
    expect(component.getStatusClass('CANCELLED')).toBe('status-cancelled');
    expect(component.getStatusClass('COMPLETED')).toBe('status-completed');
    expect(component.getStatusClass('PENDING')).toBe('status-pending');
    expect(component.getStatusClass('UNKNOWN')).toBe('status-default');
  });

  it('should format date correctly', () => {
    // Arrange
    const dateString = '2024-06-15';

    // Act
    const result = component.formatDate(dateString);

    // Assert
    expect(result).toMatch(/\d{1,2}\/\d{1,2}\/\d{4}/);
  });

  it('should format date time correctly', () => {
    // Arrange
    const dateTimeString = '2024-01-15T10:30:00';

    // Act
    const result = component.formatDateTime(dateTimeString);

    // Assert
    expect(result).toMatch(/\d{1,2}\/\d{1,2}\/\d{4}, \d{1,2}:\d{2}:\d{2}/);
  });

  it('should format amount correctly', () => {
    // Arrange
    const amount = 509.98;

    // Act
    const result = component.formatAmount(amount);

    // Assert
    expect(result).toBe('$509.98');
  });

  it('should handle empty date strings', () => {
    // Act & Assert
    expect(component.formatDate('')).toBe('');
    expect(component.formatDateTime('')).toBe('');
  });

  it('should handle null/undefined date strings', () => {
    // Act & Assert
    expect(component.formatDate(null as any)).toBe('');
    expect(component.formatDateTime(undefined as any)).toBe('');
  });

  it('should show loading state initially', () => {
    // Arrange
    mockBookingService.getUserBookings.and.returnValue(of({
      success: true,
      message: 'User bookings retrieved successfully',
      data: mockBookings
    }));

    // Act
    component.loadUserBookings();
    expect(component.loading).toBeTrue();

    // Complete the loading
    fixture.detectChanges();
    expect(component.loading).toBeFalse();
  });

  it('should clear error when loading new bookings', () => {
    // Arrange
    component.error = 'Previous error';
    mockBookingService.getUserBookings.and.returnValue(of({
      success: true,
      message: 'User bookings retrieved successfully',
      data: mockBookings
    }));

    // Act
    component.loadUserBookings();

    // Assert
    expect(component.error).toBe('');
  });

  it('should cancel booking successfully and update local data', () => {
    // Arrange
    const mockCancelResponse = {
      success: true,
      message: 'Booking cancelled successfully',
      data: {
        bookingId: 1,
        flightId: 1,
        userId: 1,
        noOfSeats: 2,
        seatCategory: 'ECONOMY',
        dateOfTravel: '2024-06-15',
        originalAmount: 509.98,
        refundAmount: 407.98,
        refundPercentage: 80.00,
        bookingStatus: 'CANCELLED',
        origin: 'New York',
        destination: 'Los Angeles',
        carrierName: 'Test Airlines'
      }
    };

    // Mock the cancelBooking method
    mockBookingService.cancelBooking = jasmine.createSpy('cancelBooking').and.returnValue(of(mockCancelResponse));

    // Set up initial bookings
    component.bookings = [...mockBookings];
    fixture.detectChanges();

    // Act
    component.cancelBooking(1);

    // Assert
    expect(mockBookingService.cancelBooking).toHaveBeenCalledWith(1);
    expect(component.bookings[0].bookingStatus).toBe('CANCELLED');
    expect(component.bookings[0].refundAmount).toBe(407.98);
    expect(component.bookings[0].refundPercentage).toBe(80.00);
    expect(component.cancellingBooking).toBeNull();
  });

  it('should handle error when cancelling booking', () => {
    // Arrange
    const mockErrorResponse = {
      success: false,
      message: 'Booking cannot be cancelled'
    };

    mockBookingService.cancelBooking = jasmine.createSpy('cancelBooking').and.returnValue(of(mockErrorResponse));

    // Set up initial bookings
    component.bookings = [...mockBookings];
    fixture.detectChanges();

    // Act
    component.cancelBooking(1);

    // Assert
    expect(mockBookingService.cancelBooking).toHaveBeenCalledWith(1);
    expect(component.error).toBe('Booking cannot be cancelled');
    expect(component.cancellingBooking).toBeNull();
  });

  it('should handle network error when cancelling booking', () => {
    // Arrange
    mockBookingService.cancelBooking = jasmine.createSpy('cancelBooking').and.returnValue(
      throwError(() => new Error('Network error'))
    );

    // Set up initial bookings
    component.bookings = [...mockBookings];
    fixture.detectChanges();

    // Act
    component.cancelBooking(1);

    // Assert
    expect(mockBookingService.cancelBooking).toHaveBeenCalledWith(1);
    expect(component.error).toBe('Error cancelling booking. Please try again.');
    expect(component.cancellingBooking).toBeNull();
  });

  it('should set cancelling state during cancellation process', () => {
    // Arrange
    const mockCancelResponse = {
      success: true,
      message: 'Booking cancelled successfully',
      data: { /* mock data */ }
    };

    mockBookingService.cancelBooking = jasmine.createSpy('cancelBooking').and.returnValue(of(mockCancelResponse));

    // Set up initial bookings
    component.bookings = [...mockBookings];
    fixture.detectChanges();

    // Act
    component.cancelBooking(1);

    // Assert
    expect(component.cancellingBooking).toBe(1);
  });
});
