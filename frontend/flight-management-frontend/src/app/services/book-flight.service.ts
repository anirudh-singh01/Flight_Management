import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface BookFlightRequest {
  flightId: number;
  noOfSeats: number;
  seatCategory: string;
  dateOfTravel: string;
}

export interface BookFlightResponse {
  bookingId: number;
  flightId: number;
  userId: number;
  noOfSeats: number;
  seatCategory: string;
  dateOfTravel: string;
  bookingAmount: number;
  discountAmount: number;
  discountReason: string;
  bookingStatus: string;
  bookingDate: string;
  origin: string;
  destination: string;
  carrierName: string;
  originalAirFare: number;
  refundAmount?: number;
  refundPercentage?: number;
}

@Injectable({
  providedIn: 'root'
})
export class BookFlightService {
  private apiUrl = `${environment.apiUrl}/api/bookings`;

  constructor(private http: HttpClient) { }

  /**
   * Book a flight
   */
  bookFlight(bookingData: BookFlightRequest, userId: number): Observable<any> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.post(`${this.apiUrl}/bookFlight`, bookingData, { params });
  }

  /**
   * Get booking by ID
   */
  getBookingById(bookingId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${bookingId}`);
  }

  /**
   * Get all bookings for a user
   */
  getUserBookings(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/${userId}`);
  }

  /**
   * Get seat availability for a flight on a specific date
   */
  getSeatAvailability(flightId: number, dateOfTravel: string): Observable<any> {
    const params = new HttpParams()
      .set('flightId', flightId.toString())
      .set('dateOfTravel', dateOfTravel);
    return this.http.get(`${this.apiUrl}/availability`, { params });
  }

  /**
   * Cancel a booking
   */
  cancelBooking(bookingId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${bookingId}/cancel`, {});
  }

  /**
   * Get booking statistics
   */
  getBookingStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/stats`);
  }
}
