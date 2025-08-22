# Administrator Registration Module Implementation

## Overview
This document outlines the implementation of Module 2: Administrator Registration (registerAdmin) for the Flight Management System.

## Backend Implementation

### 1. API Endpoint
- **Endpoint**: `POST /api/users/registerAdmin`
- **Controller**: `UserController.registerAdmin()`
- **Service**: `UserService.registerAdmin()`

### 2. Key Features
- **Role Enforcement**: Automatically sets `role = "ADMIN"` regardless of input
- **Customer Category**: Sets `customerCategory = null` (not applicable for administrators)
- **Validation**: Same validation as user registration (username uniqueness, email uniqueness)
- **Database Storage**: Saves to the same `User` table with admin privileges

### 3. Code Changes

#### UserController.java
```java
@PostMapping("/registerAdmin")
public ResponseEntity<?> registerAdmin(@Valid @RequestBody UserDTO userDTO) {
    try {
        UserDTO registeredAdmin = userService.registerAdmin(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Administrator registered successfully", registeredAdmin));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "An error occurred while registering administrator", null));
    }
}
```

#### UserService.java
```java
public UserDTO registerAdmin(UserDTO userDTO) {
    // Check if username already exists
    if (userRepository.existsByUserName(userDTO.getUserName())) {
        throw new RuntimeException("Username already exists: " + userDTO.getUserName());
    }
    
    // Check if email already exists
    if (userRepository.existsByEmailId(userDTO.getEmailId())) {
        throw new RuntimeException("Email already exists: " + userDTO.getEmailId());
    }
    
    // Force role to ADMIN for administrator registration
    userDTO.setRole(UserRole.ADMIN);
    
    // Set customer category to null for administrators (not applicable)
    userDTO.setCustomerCategory(null);
    
    // Create and save User entity
    User user = new User();
    // ... set all fields ...
    User savedUser = userRepository.save(user);
    
    return convertToDTO(savedUser);
}
```

## Frontend Implementation

### 1. Component Structure
- **Component**: `AdminRegistrationComponent`
- **Template**: `admin-registration.html`
- **Styles**: `admin-registration.css`
- **Route**: `/admin-register`

### 2. Key Features
- **Form Reuse**: Based on customer registration form but simplified
- **Role Pre-set**: Role is automatically set to `ADMIN` on submission
- **No Customer Category**: Form doesn't include customer category field
- **Navigation**: Links between customer and admin registration forms

### 3. Code Changes

#### AdminRegistrationComponent
```typescript
export class AdminRegistrationComponent implements OnInit {
  registrationForm: FormGroup;
  
  constructor(private fb: FormBuilder, private userService: UserService) {
    this.registrationForm = this.fb.group({
      userName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      phone: ['', [Validators.required, Validators.pattern(/^\+?[1-9]\d{1,14}$/)]],
      emailId: ['', [Validators.required, Validators.email]],
      address1: ['', Validators.required],
      address2: [''],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: ['', [Validators.required, Validators.pattern(/^\d{5}(-\d{4})?$/)]],
      dob: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }
  
  onSubmit() {
    // ... form validation ...
    const user: User = {
      userName: formValue.userName,
      password: formValue.password,
      role: UserRole.ADMIN, // Explicitly set role to ADMIN
      // ... other fields ...
    };
    
    this.userService.registerAdmin(user).subscribe({
      // ... handle response ...
    });
  }
}
```

#### UserService
```typescript
registerAdmin(user: User): Observable<ApiResponse> {
  const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
  return this.http.post<ApiResponse>(`${this.baseUrl}/registerAdmin`, user, { headers });
}
```

#### App Routes
```typescript
export const routes: Routes = [
  { path: '', redirectTo: '/register', pathMatch: 'full' },
  { path: 'register', component: CustomerRegistrationComponent },
  { path: 'admin-register', component: AdminRegistrationComponent },
  { path: '**', redirectTo: '/register' }
];
```

## Testing Implementation

### 1. Backend Tests (UserServiceTest.java)
- **testRegisterAdmin_Success**: Tests successful admin registration
- **testRegisterAdmin_UsernameAlreadyExists**: Tests username conflict handling
- **testRegisterAdmin_EmailAlreadyExists**: Tests email conflict handling

### 2. Frontend Tests (admin-registration.spec.ts)
- **Component Creation**: Verifies component initializes correctly
- **Form Validation**: Tests required field validation
- **Role Setting**: Verifies role is set to ADMIN on submission
- **Service Integration**: Tests registerAdmin service call
- **Success/Error Handling**: Tests response handling

## Key Differences from Customer Registration

| Aspect | Customer Registration | Admin Registration |
|--------|----------------------|-------------------|
| **Role** | `CUSTOMER` (default) | `ADMIN` (forced) |
| **Customer Category** | Required field | Not applicable (null) |
| **Form Fields** | Includes role & category | Excludes role & category |
| **API Endpoint** | `/registerUser` | `/registerAdmin` |
| **Service Method** | `registerUser()` | `registerAdmin()` |

## Security Considerations

1. **Role Enforcement**: Backend ensures role is always set to ADMIN
2. **Input Validation**: Same validation as user registration
3. **Data Integrity**: Customer category is explicitly set to null for admins
4. **API Separation**: Different endpoints for different user types

## Usage

### 1. Navigate to Admin Registration
- From customer registration: Click "Register as Administrator"
- Direct URL: `/admin-register`

### 2. Fill Out Form
- All required fields must be completed
- Password confirmation must match
- Email and username must be unique

### 3. Submit
- Form automatically sets role to ADMIN
- Backend validates and stores with admin privileges
- Success/error messages displayed

## Future Enhancements

1. **Admin Authentication**: Add admin-only access controls
2. **Role-based UI**: Different interfaces for different user roles
3. **Audit Logging**: Track admin account creation
4. **Email Verification**: Admin email verification workflow
5. **Approval Process**: Admin account approval by existing admins

## Testing Commands

### Backend
```bash
cd backend
mvn test -Dtest=UserServiceTest
```

### Frontend
```bash
cd frontend/flight-management-frontend
npm test
```

## Conclusion

The Administrator Registration module has been successfully implemented with:
- ✅ Backend API endpoint (`/registerAdmin`)
- ✅ Service layer with role enforcement
- ✅ Frontend component with simplified form
- ✅ Navigation between registration forms
- ✅ Comprehensive testing coverage
- ✅ Proper role assignment and storage

The implementation follows the existing codebase patterns and ensures that administrators are properly registered with the correct role and privileges.
