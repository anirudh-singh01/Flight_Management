import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FlightService } from '../../services/flight.service';
import { Flight, FlightResponse } from '../../models/flight.model';

@Component({
  selector: 'app-view-flights',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './view-flights.component.html',
  styleUrls: ['./view-flights.component.css']
})
export class ViewFlightsComponent implements OnInit {
  flights: Flight[] = [];
  filteredFlights: Flight[] = [];
  searchCarrierName: string = '';
  loading: boolean = false;
  error: string = '';

  constructor(private flightService: FlightService, private router: Router) {}

  ngOnInit(): void {
    this.loadAllFlights();
  }

  loadAllFlights(): void {
    this.loading = true;
    this.error = '';
    
    console.log('Loading flights...');
    
    // Add timeout to prevent infinite loading
    const timeout = setTimeout(() => {
      if (this.loading) {
        console.log('Request timeout - setting loading to false');
        this.loading = false;
        this.error = 'Request timeout - please check if the backend is running';
      }
    }, 10000); // 10 second timeout
    
    this.flightService.getAllFlights().subscribe({
      next: (response: FlightResponse) => {
        clearTimeout(timeout);
        console.log('API Response:', response);
        if (response.success && response.data) {
          this.flights = Array.isArray(response.data) ? response.data : [response.data];
          this.filteredFlights = [...this.flights];
          console.log('Flights loaded:', this.flights.length);
        } else {
          this.error = response.message || 'Failed to load flights';
          console.log('API returned error:', this.error);
        }
        this.loading = false;
        console.log('Loading state set to:', this.loading);
      },
      error: (error) => {
        clearTimeout(timeout);
        console.error('API Error:', error);
        this.error = 'Error loading flights: ' + (error.message || 'Unknown error');
        this.loading = false;
        console.log('Loading state set to:', this.loading);
      }
    });
  }

  searchByCarrier(): void {
    if (!this.searchCarrierName.trim()) {
      this.filteredFlights = [...this.flights];
      return;
    }

    this.loading = true;
    this.error = '';
    
    console.log('Searching for carrier:', this.searchCarrierName);

    // Add timeout to prevent infinite loading
    const timeout = setTimeout(() => {
      if (this.loading) {
        console.log('Search request timeout - setting loading to false');
        this.loading = false;
        this.error = 'Search request timeout - please check if the backend is running';
      }
    }, 10000); // 10 second timeout

    this.flightService.getFlightsByCarrierName(this.searchCarrierName.trim()).subscribe({
      next: (response: FlightResponse) => {
        clearTimeout(timeout);
        console.log('Search API Response:', response);
        if (response.success && response.data) {
          this.filteredFlights = Array.isArray(response.data) ? response.data : [response.data];
          console.log('Search results:', this.filteredFlights.length);
        } else {
          this.filteredFlights = [];
          this.error = response.message || 'No flights found for this carrier';
          console.log('Search error:', this.error);
        }
        this.loading = false;
        console.log('Search loading state set to:', this.loading);
      },
      error: (error) => {
        clearTimeout(timeout);
        console.error('Search API Error:', error);
        this.error = 'Error searching flights: ' + (error.message || 'Unknown error');
        this.loading = false;
        this.filteredFlights = [];
        console.log('Search loading state set to:', this.loading);
      }
    });
  }

  clearSearch(): void {
    this.searchCarrierName = '';
    this.filteredFlights = [...this.flights];
    this.error = '';
  }

  // Method to manually reset loading state if it gets stuck
  resetLoadingState(): void {
    console.log('Manually resetting loading state');
    this.loading = false;
    this.error = 'Loading state was reset manually. Please try again.';
  }

  // Method to test API connection
  testApiConnection(): void {
    console.log('Testing API connection...');
    this.loading = true;
    this.error = '';
    
    // Simple test to see if the backend is reachable
    fetch('http://localhost:8080/flight-management/api/flights')
      .then(response => {
        console.log('API Response Status:', response.status);
        if (response.ok) {
          return response.json();
        } else {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
      })
      .then(data => {
        console.log('API Test Response:', data);
        this.error = 'API is reachable. Response: ' + JSON.stringify(data);
        this.loading = false;
      })
      .catch(error => {
        console.error('API Test Error:', error);
        this.error = 'API connection failed: ' + error.message;
        this.loading = false;
      });
  }

  // Method to provide user guidance
  getNoFlightsMessage(): string {
    if (this.searchCarrierName) {
      return `No flights found for carrier "${this.searchCarrierName}". Try a different carrier name or clear the search to see all flights.`;
    } else {
      return 'No flights are currently available in the database. You may need to register some flights first.';
    }
  }

  // Method to check backend health
  checkBackendHealth(): void {
    console.log('Checking backend health...');
    this.error = 'Checking backend health...';
    
    fetch('http://localhost:8080/flight-management/actuator/health')
      .then(response => {
        if (response.ok) {
          this.error = 'Backend is healthy and running!';
        } else {
          this.error = `Backend health check failed: HTTP ${response.status}`;
        }
      })
      .catch(error => {
        this.error = 'Backend health check failed: ' + error.message;
      });
  }

  // Method to show setup guidance
  showSetupGuidance(): void {
    const guidance = `
      To view flights, you need to:

      1. **Register a Carrier** first:
         - Go to "Carrier Registration" 
         - Create a carrier (e.g., "Delta Airlines")

      2. **Register Flights** for that carrier:
         - Go to "Flight Registration"
         - Select the carrier you created
         - Fill in flight details (origin, destination, etc.)

      3. **Then return here** to view the flights

      Current Status:
      - Backend: ${this.error.includes('healthy') ? '✅ Running' : '❌ Check connection'}
      - Flights in DB: ${this.flights.length}
    `;
    
    alert(guidance);
  }

  getTotalSeats(flight: Flight): number {
    return (flight.seatCapacityBusiness || 0) + 
           (flight.seatCapacityEconomy || 0) + 
           (flight.seatCapacityExecutive || 0);
  }

  editFlight(flightId: number): void {
    this.router.navigate(['/update-flight', flightId]);
  }

  bookFlight(flightId: number): void {
    this.router.navigate(['/book-flight', flightId]);
  }
}
