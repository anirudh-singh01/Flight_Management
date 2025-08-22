import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.css']
})
export class CustomerDashboardComponent implements OnInit {
  userName: string = '';
  userRole: string = '';

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    // Only access sessionStorage in browser environment
    if (isPlatformBrowser(this.platformId)) {
      // Get user info from session storage
      this.userName = sessionStorage.getItem('userName') || '';
      this.userRole = sessionStorage.getItem('userRole') || '';
      
      // Redirect to login if no user info
      if (!this.userName || !this.userRole) {
        this.router.navigate(['/login']);
      }
    }
  }

  logout(): void {
    // Only access sessionStorage in browser environment
    if (isPlatformBrowser(this.platformId)) {
      // Clear session storage
      sessionStorage.clear();
    }
    // Redirect to login
    this.router.navigate(['/login']);
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}
