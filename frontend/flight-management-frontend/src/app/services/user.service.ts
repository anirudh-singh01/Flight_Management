import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, ApiResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:8080/flight-management/api/users';

  constructor(private http: HttpClient) { }

  /**
   * Register a new user
   */
  registerUser(user: User): Observable<ApiResponse> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<ApiResponse>(`${this.baseUrl}/registerUser`, user, { headers });
  }

  /**
   * Register a new administrator
   */
  registerAdmin(user: User): Observable<ApiResponse> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<ApiResponse>(`${this.baseUrl}/registerAdmin`, user, { headers });
  }

  /**
   * Get user by ID
   */
  getUserById(userId: number): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/${userId}`);
  }

  /**
   * Get user by username
   */
  getUserByUsername(userName: string): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/username/${userName}`);
  }

  /**
   * Get all users
   */
  getAllUsers(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}`);
  }

  /**
   * Update user
   */
  updateUser(userId: number, user: User): Observable<ApiResponse> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<ApiResponse>(`${this.baseUrl}/${userId}`, user, { headers });
  }

  /**
   * Delete user
   */
  deleteUser(userId: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${this.baseUrl}/${userId}`);
  }

  /**
   * User login
   */
  login(userName: string, password: string): Observable<ApiResponse> {
    const loginData = { userName, password };
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<ApiResponse>(`${this.baseUrl}/login`, loginData, { headers });
  }
}
