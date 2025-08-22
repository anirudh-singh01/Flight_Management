import { Routes } from '@angular/router';
import { CustomerRegistrationComponent } from './components/customer-registration/customer-registration';
import { AdminRegistrationComponent } from './components/admin-registration/admin-registration';
import { LoginComponent } from './components/login/login.component';
import { CarrierRegistrationComponent } from './components/carrier-registration/carrier-registration';
import { FlightRegistrationComponent } from './components/flight-registration/flight-registration.component';
import { ViewFlightsComponent } from './components/view-flights/view-flights.component';
import { UpdateFlightComponent } from './components/update-flight/update-flight.component';
import { UpdateCarrierComponent } from './components/update-carrier/update-carrier.component';
import { BookFlightComponent } from './components/book-flight/book-flight.component';
import { ViewBookingsComponent } from './components/view-bookings/view-bookings.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: CustomerRegistrationComponent },
  { path: 'admin-register', component: AdminRegistrationComponent },
  { path: 'carrier-register', component: CarrierRegistrationComponent },
  { path: 'flight-register', component: FlightRegistrationComponent },
  { path: 'view-flights', component: ViewFlightsComponent },
  { path: 'update-flight/:id', component: UpdateFlightComponent },
  { path: 'update-carrier/:id', component: UpdateCarrierComponent },
  { path: 'book-flight', component: BookFlightComponent },
  { path: 'book-flight/:flightId', component: BookFlightComponent },
  { path: 'view-bookings', component: ViewBookingsComponent },
  { path: '**', redirectTo: '/login' }
];
