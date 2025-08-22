import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { LoginRequest, LoginResponse, ApiResponse } from '../../models/user.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      userName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      const loginRequest: LoginRequest = {
        userName: this.loginForm.get('userName')?.value,
        password: this.loginForm.get('password')?.value
      };

      this.userService.login(loginRequest.userName, loginRequest.password).subscribe({
        next: (response: ApiResponse) => {
          this.isLoading = false;
          if (response.success) {
            this.successMessage = 'Login successful!';
            const loginResponse = response.data as LoginResponse;
            
            // Store user info in session storage
            sessionStorage.setItem('userId', loginResponse.userId.toString());
            sessionStorage.setItem('userName', loginResponse.userName);
            sessionStorage.setItem('userRole', loginResponse.role);
            
            // Redirect based on role
            setTimeout(() => {
              if (loginResponse.role === 'ADMIN') {
                this.router.navigate(['/admin-dashboard']);
              } else {
                this.router.navigate(['/customer-dashboard']);
              }
            }, 1000);
          } else {
            this.errorMessage = response.message || 'Login failed';
          }
        },
        error: (error) => {
          this.isLoading = false;
          if (error.error && error.error.message) {
            this.errorMessage = error.error.message;
          } else {
            this.errorMessage = 'An error occurred during login. Please try again.';
          }
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched() {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      control?.markAsTouched();
    });
  }

  getErrorMessage(controlName: string): string {
    const control = this.loginForm.get(controlName);
    if (control?.errors && control.touched) {
      if (control.errors['required']) {
        return `${controlName === 'userName' ? 'Username' : 'Password'} is required`;
      }
      if (control.errors['minlength']) {
        return `${controlName === 'userName' ? 'Username' : 'Password'} must be at least ${control.errors['minlength'].requiredLength} characters`;
      }
      if (control.errors['maxlength']) {
        return `Username must not exceed ${control.errors['maxlength'].requiredLength} characters`;
      }
    }
    return '';
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }

  goToAdminRegister() {
    this.router.navigate(['/admin-register']);
  }

  goToCarrierRegister() {
    this.router.navigate(['/carrier-register']);
  }
}
