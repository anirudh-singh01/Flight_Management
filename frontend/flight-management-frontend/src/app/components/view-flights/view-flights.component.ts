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
    
    this.flightService.getAllFlights().subscribe({
      next: (response: FlightResponse) => {
        if (response.success && response.data) {
          this.flights = Array.isArray(response.data) ? response.data : [response.data];
          this.filteredFlights = [...this.flights];
        } else {
          this.error = response.message || 'Failed to load flights';
        }
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error loading flights: ' + error.message;
        this.loading = false;
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

    this.flightService.getFlightsByCarrierName(this.searchCarrierName.trim()).subscribe({
      next: (response: FlightResponse) => {
        if (response.success && response.data) {
          this.filteredFlights = Array.isArray(response.data) ? response.data : [response.data];
        } else {
          this.filteredFlights = [];
          this.error = response.message || 'No flights found for this carrier';
        }
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error searching flights: ' + error.message;
        this.loading = false;
        this.filteredFlights = [];
      }
    });
  }

  clearSearch(): void {
    this.searchCarrierName = '';
    this.filteredFlights = [...this.flights];
    this.error = '';
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
