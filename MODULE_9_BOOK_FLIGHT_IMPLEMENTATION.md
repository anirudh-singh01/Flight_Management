# Module 9: Book Flight (BookFlight) Implementation

## Overview
This module implements the complete flight booking functionality allowing customers to book flights with automatic discount calculations and seat availability management.

## Features Implemented

### Backend Features
- ✅ `/bookFlight` API endpoint
- ✅ Auto-generated booking ID
- ✅ Auto-filled user ID and booking status
- ✅ Automatic discount calculation (advance booking, customer type, bulk booking)
- ✅ Flight schedule management with booked seat tracking
- ✅ Overbooking prevention
- ✅ Comprehensive validation and error handling

### Frontend Features
- ✅ Angular booking form with validation
- ✅ Real-time booking summary
- ✅ Success/error message handling
- ✅ Responsive design with modern UI
- ✅ Integration with existing flight viewing system

### Testing
- ✅ Backend unit tests for all scenarios
- ✅ Frontend component tests with form validation
- ✅ Error handling and edge case testing

## Backend Implementation

### 1. Entity Classes

#### Booking Entity (`Booking.java`)
```java
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;
    
    private Long flightId;
    private Long userId;
    private Integer noOfSeats;
    private SeatCategory seatCategory;
    private LocalDate dateOfTravel;
    private BigDecimal bookingAmount;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDate;
    private BigDecimal discountAmount;
    private String discountReason;
}
```

#### FlightSchedule Entity (`FlightSchedule.java`)
```java
@Entity
@Table(name = "flight_schedules")
public class FlightSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;
    
    private Long flightId;
    private LocalDate dateOfTravel;
    private Integer bookedCountEconomy;
    private Integer bookedCountBusiness;
    private Integer bookedCountExecutive;
    private Integer totalCapacityEconomy;
    private Integer totalCapacityBusiness;
    private Integer totalCapacityExecutive;
}
```

#### Enums
- **SeatCategory**: ECONOMY, BUSINESS, EXECUTIVE
- **BookingStatus**: BOOKED, CANCELLED, COMPLETED, PENDING

### 2. DTOs

#### BookFlightRequest
```java
public class BookFlightRequest {
    private Long flightId;
    private Integer noOfSeats;
    private SeatCategory seatCategory;
    private LocalDate dateOfTravel;
}
```

#### BookFlightResponse
```java
public class BookFlightResponse {
    private Long bookingId;
    private Long flightId;
    private Long userId;
    private Integer noOfSeats;
    private SeatCategory seatCategory;
    private LocalDate dateOfTravel;
    private BigDecimal bookingAmount;
    private BigDecimal discountAmount;
    private String discountReason;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDate;
    private String origin;
    private String destination;
    private String carrierName;
    private BigDecimal originalAirFare;
}
```

### 3. Service Layer (`BookingService.java`)

#### Key Methods
- `bookFlight()` - Main booking logic with discount calculation
- `checkSeatAvailability()` - Prevents overbooking
- `calculateDiscounts()` - Applies multiple discount types
- `getOrCreateFlightSchedule()` - Manages flight schedules

#### Discount Calculation Logic
```java
// 1. Advance booking discount
if (daysInAdvance >= 30) discount += 15%;
else if (daysInAdvance >= 14) discount += 10%;
else if (daysInAdvance >= 7) discount += 5%;

// 2. Customer category discount
switch (user.getCustomerCategory()) {
    case PLATINUM: discount += 20%; break;
    case GOLD: discount += 15%; break;
    case SILVER: discount += 10%; break;
    case PREMIUM: discount += 25%; break;
}

// 3. Bulk booking discount
if (noOfSeats >= 5) discount += 10%;
```

### 4. Controller (`BookingController.java`)

#### API Endpoints
- `POST /api/bookings/bookFlight` - Book a flight
- `GET /api/bookings/{bookingId}` - Get booking details
- `GET /api/bookings/user/{userId}` - Get user bookings
- `GET /api/bookings/availability` - Check seat availability
- `PUT /api/bookings/{bookingId}/cancel` - Cancel booking
- `GET /api/bookings/stats` - Get booking statistics

### 5. Repository Layer
- `BookingRepository` - CRUD operations for bookings
- `FlightScheduleRepository` - Flight schedule management

## Frontend Implementation

### 1. Component (`BookFlightComponent`)

#### Features
- Reactive form with comprehensive validation
- Real-time cost estimation
- Flight selection dropdown
- Seat category selection
- Date picker with future date validation
- Booking summary display
- Success/error message handling

#### Form Validation
```typescript
this.bookingForm = this.fb.group({
  flightId: ['', Validators.required],
  noOfSeats: ['', [Validators.required, Validators.min(1), Validators.max(10)]],
  seatCategory: ['', Validators.required],
  dateOfTravel: ['', [Validators.required, this.futureDateValidator()]]
});
```

### 2. Service (`BookFlightService`)

#### Methods
- `bookFlight()` - Submit booking request
- `getBookingById()` - Retrieve booking details
- `getUserBookings()` - Get user's booking history
- `getSeatAvailability()` - Check seat availability
- `cancelBooking()` - Cancel existing booking

### 3. UI Features

#### Booking Form
- Clean, intuitive interface
- Real-time validation feedback
- Responsive design for mobile/desktop
- Loading states and progress indicators

#### Booking Summary
- Flight details display
- Pricing breakdown
- Discount information
- Total amount calculation

#### Navigation
- Integration with view-flights component
- Book Flight button added to flight listings
- Breadcrumb navigation

## Database Schema

### Tables Created
1. **bookings** - Stores all booking information
2. **flight_schedules** - Tracks seat availability per flight/date

### Key Relationships
- `bookings.flight_id` → `flights.flight_id`
- `bookings.user_id` → `users.user_id`
- `flight_schedules.flight_id` → `flights.flight_id`

## Testing

### Backend Tests (`BookingServiceTest.java`)
- ✅ Successful booking with discounts
- ✅ Overbooking prevention
- ✅ Customer category discount validation
- ✅ Advance booking discount validation
- ✅ Bulk booking discount validation
- ✅ Error handling scenarios
- ✅ Flight schedule management

### Frontend Tests (`BookFlightComponent.spec.ts`)
- ✅ Component creation and initialization
- ✅ Form validation (required fields, min/max values, future dates)
- ✅ Flight selection and cost calculation
- ✅ Successful booking submission
- ✅ Error handling and user feedback
- ✅ Navigation and routing
- ✅ Service integration

## API Usage Examples

### Book a Flight
```bash
POST /api/bookings/bookFlight?userId=1
Content-Type: application/json

{
  "flightId": 1,
  "noOfSeats": 2,
  "seatCategory": "ECONOMY",
  "dateOfTravel": "2024-02-15"
}
```

### Response
```json
{
  "success": true,
  "message": "Flight booked successfully",
  "data": {
    "bookingId": 1,
    "flightId": 1,
    "userId": 1,
    "noOfSeats": 2,
    "seatCategory": "ECONOMY",
    "dateOfTravel": "2024-02-15",
    "bookingAmount": 509.98,
    "discountAmount": 89.98,
    "discountReason": "Advance booking (30+ days): 15%, Customer category (GOLD): 15%",
    "bookingStatus": "BOOKED",
    "origin": "New York",
    "destination": "Los Angeles",
    "carrierName": "Test Airlines",
    "originalAirFare": 299.99
  }
}
```

## Security Features

### Input Validation
- Flight ID validation
- Seat count limits (1-10 seats)
- Future date validation
- Seat category validation

### Business Logic Protection
- Overbooking prevention
- User authentication check
- Transaction management
- Data integrity validation

## Performance Considerations

### Database Optimization
- Indexed foreign keys
- Efficient seat availability queries
- Transaction management for concurrent bookings

### Frontend Optimization
- Lazy loading of flight data
- Form validation optimization
- Responsive design for mobile devices

## Error Handling

### Backend Errors
- Flight not found
- User not found
- Insufficient seats
- Invalid booking data
- Database connection issues

### Frontend Errors
- Network connectivity issues
- Form validation errors
- Service unavailable errors
- User authentication errors

## Future Enhancements

### Potential Features
1. **Payment Integration** - Credit card processing
2. **Email Confirmations** - Booking confirmations and reminders
3. **Seat Selection** - Individual seat picking
4. **Booking Modifications** - Change dates or seats
5. **Loyalty Program** - Points and rewards system
6. **Multi-city Bookings** - Complex itinerary support
7. **Group Bookings** - Corporate and group reservations
8. **Mobile App** - Native mobile application

### Technical Improvements
1. **Caching** - Redis for flight availability
2. **Async Processing** - Queue-based booking processing
3. **Real-time Updates** - WebSocket for seat availability
4. **Analytics** - Booking patterns and trends
5. **A/B Testing** - UI/UX optimization

## Deployment Notes

### Backend Requirements
- Java 17+
- Spring Boot 3.x
- PostgreSQL/MySQL database
- Maven build system

### Frontend Requirements
- Node.js 18+
- Angular 17+
- Modern web browser support
- HTTPS for production

### Environment Variables
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/flight_management
spring.datasource.username=postgres
spring.datasource.password=password

# API Configuration
server.port=8080
spring.jpa.hibernate.ddl-auto=update
```

## Conclusion

Module 9 successfully implements a comprehensive flight booking system with:

- **Robust Backend**: Complete booking logic with discount calculations and seat management
- **User-Friendly Frontend**: Intuitive booking interface with real-time feedback
- **Comprehensive Testing**: Both backend and frontend test coverage
- **Scalable Architecture**: Clean separation of concerns and extensible design
- **Security Features**: Input validation and business logic protection

The implementation follows best practices for both Spring Boot backend and Angular frontend development, providing a solid foundation for future enhancements and production deployment.
