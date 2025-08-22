import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
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
      
      // Redirect to login if no user info or not admin
      if (!this.userName || !this.userRole || this.userRole !== 'ADMIN') {
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
