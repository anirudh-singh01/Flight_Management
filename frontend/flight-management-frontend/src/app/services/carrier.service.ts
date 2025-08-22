import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Carrier } from '../models/carrier.model';

export interface ApiResponse {
  success: boolean;
  message: string;
  data: any;
}

@Injectable({
  providedIn: 'root'
})
export class CarrierService {
  private baseUrl = 'http://localhost:8080/api/carriers';

  constructor(private http: HttpClient) { }

  /**
   * Register a new carrier
   */
  registerCarrier(carrier: Carrier): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/registerCarrier`, carrier);
  }

  /**
   * Get carrier by ID
   */
  getCarrierById(carrierId: number): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/${carrierId}`);
  }

  /**
   * Get carrier by name
   */
  getCarrierByName(carrierName: string): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/name/${carrierName}`);
  }

  /**
   * Get all carriers
   */
  getAllCarriers(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}`);
  }

  /**
   * Get active carriers
   */
  getActiveCarriers(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/active`);
  }

  /**
   * Update carrier
   */
  updateCarrier(carrierId: number, carrier: Carrier): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.baseUrl}/${carrierId}`, carrier);
  }

  /**
   * Delete carrier
   */
  deleteCarrier(carrierId: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${this.baseUrl}/${carrierId}`);
  }
}
