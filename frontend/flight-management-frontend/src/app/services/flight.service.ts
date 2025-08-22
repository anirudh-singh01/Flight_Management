import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Flight, FlightResponse } from '../models/flight.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FlightService {
  private apiUrl = environment.apiUrl + '/api/flights';

  constructor(private http: HttpClient) { }

  registerFlight(flight: Flight): Observable<FlightResponse> {
    return this.http.post<FlightResponse>(`${this.apiUrl}/registerFlight`, flight);
  }

  getAllFlights(): Observable<FlightResponse> {
    return this.http.get<FlightResponse>(this.apiUrl);
  }

  getFlightById(flightId: number): Observable<FlightResponse> {
    return this.http.get<FlightResponse>(`${this.apiUrl}/${flightId}`);
  }

  updateFlight(flightId: number, flight: Flight): Observable<FlightResponse> {
    return this.http.put<FlightResponse>(`${this.apiUrl}/${flightId}`, flight);
  }

  deleteFlight(flightId: number): Observable<FlightResponse> {
    return this.http.delete<FlightResponse>(`${this.apiUrl}/${flightId}`);
  }

  searchFlights(origin: string, destination: string): Observable<FlightResponse> {
    return this.http.get<FlightResponse>(`${this.apiUrl}/search?origin=${origin}&destination=${destination}`);
  }

  getFlightsByCarrier(carrierId: number): Observable<FlightResponse> {
    return this.http.get<FlightResponse>(`${this.apiUrl}/carrier/${carrierId}`);
  }

  getFlightsByCarrierName(carrierName: string): Observable<FlightResponse> {
    return this.http.get<FlightResponse>(`${this.apiUrl}/carrierName/${carrierName}`);
  }
}
