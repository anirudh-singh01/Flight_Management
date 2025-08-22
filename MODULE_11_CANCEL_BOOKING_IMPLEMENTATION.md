# Module 11: Cancel Booking Implementation

## Overview
This module implements the cancel booking functionality for the Flight Management System, allowing users to cancel their flight bookings and receive refunds based on carrier refund policies.

## Features Implemented

### Backend Features
1. **Cancel Booking API Endpoints**
   - `PUT /api/bookings/{bookingId}/cancel` - Standard cancel endpoint
   - `DELETE /api/bookings/cancelBooking/{bookingId}` - Alternative cancel endpoint as per requirements

2. **Refund Calculation**
   - Refund amount = bookingAmount × refund% (from Carrier table)
   - Supports decimal precision for refund percentages
   - Automatic rounding to 2 decimal places

3. **Flight Schedule Updates**
   - Updates `bookedCount` in Flight Schedule table
   - Decrements seat counts for the cancelled booking
   - Prevents negative seat counts

4. **Business Logic**
   - Validates booking exists and can be cancelled
   - Updates booking status to CANCELLED
   - Retrieves carrier refund policy
   - Updates flight schedule seat availability

### Frontend Features
1. **Cancel Booking UI**
   - Cancel button for each active booking
   - Loading states during cancellation
   - Confirmation dialog before cancellation

2. **Refund Information Display**
   - Shows refund amount after cancellation
   - Displays refund percentage from carrier
   - Visual indicators for cancelled bookings

3. **Real-time Updates**
   - Updates booking status immediately
   - Refreshes refund information
   - Maintains UI consistency

## Technical Implementation

### Backend Components

#### 1. CancelBookingResponse DTO
```java
public class CancelBookingResponse {
    private Long bookingId;
    private Long flightId;
    private Long userId;
    private Integer noOfSeats;
    private SeatCategory seatCategory;
    private LocalDate dateOfTravel;
    private BigDecimal originalAmount;
    private BigDecimal refundAmount;
    private BigDecimal refundPercentage;
    private BookingStatus bookingStatus;
    private String origin;
    private String destination;
    private String carrierName;
    // ... getters, setters, constructors
}
```

#### 2. BookingService.cancelBooking() Method
```java
@Transactional
public CancelBookingResponse cancelBooking(Long bookingId) {
    // Find and validate booking
    // Calculate refund based on carrier policy
    // Update booking status
    // Update flight schedule
    // Return cancellation details
}
```

#### 3. FlightSchedule.decrementBookedCount() Method
```java
public void decrementBookedCount(SeatCategory seatCategory, Integer seats) {
    // Decrement booked count for specific seat category
    // Ensure count doesn't go below 0
}
```

### Frontend Components

#### 1. View Bookings Component Updates
- Added Actions column with Cancel button
- Integrated refund information display
- Real-time status updates

#### 2. BookFlightService Updates
- Added refund fields to BookFlightResponse interface
- Enhanced cancelBooking method

#### 3. UI Enhancements
- Responsive cancel button design
- Loading spinners during operations
- Success/error message handling

## API Endpoints

### Cancel Booking
```
PUT /api/bookings/{bookingId}/cancel
DELETE /api/bookings/cancelBooking/{bookingId}
```

**Request:** No body required
**Response:**
```json
{
  "success": true,
  "message": "Booking cancelled successfully",
  "data": {
    "bookingId": 1,
    "flightId": 1,
    "userId": 1,
    "noOfSeats": 2,
    "seatCategory": "ECONOMY",
    "dateOfTravel": "2024-06-15",
    "originalAmount": 200.00,
    "refundAmount": 160.00,
    "refundPercentage": 80.00,
    "bookingStatus": "CANCELLED",
    "origin": "New York",
    "destination": "Los Angeles",
    "carrierName": "Test Airlines"
  }
}
```

## Database Changes

### FlightSchedule Entity Updates
- Added `decrementBookedCount()` method
- Automatic validation to prevent negative counts
- Transactional updates for data consistency

### Booking Entity
- Status updates to CANCELLED
- No structural changes required

### Carrier Entity
- Utilizes existing `refundPercentage` field
- Supports decimal precision for accurate calculations

## Testing

### Backend Tests

#### 1. BookingService Tests
- `testCancelBooking()` - Basic cancellation flow
- `testCancelBooking_RefundCalculation()` - Refund calculation accuracy
- `testCancelBooking_AlreadyCancelled()` - Business rule validation
- `testCancelBooking_BookingNotFound()` - Error handling

#### 2. BookingController Tests
- `testCancelBooking_Success()` - API endpoint success
- `testCancelBookingAlternative_Success()` - Alternative endpoint
- `testCancelBooking_BookingNotFound()` - Error responses
- `testCancelBooking_InternalServerError()` - Exception handling

### Frontend Tests

#### 1. ViewBookingsComponent Tests
- `testCancelBooking()` - Component cancellation flow
- `testCancelBooking_RefundCalculation()` - UI updates
- `testCancelBooking_ErrorHandling()` - Error scenarios
- `testCancelBooking_LoadingStates()` - User experience

## Business Rules

1. **Cancellation Eligibility**
   - Only BOOKED status bookings can be cancelled
   - CANCELLED, COMPLETED, or PENDING bookings cannot be cancelled

2. **Refund Calculation**
   - Refund = Original Amount × Carrier Refund Percentage
   - Refund percentage is configurable per carrier
   - Supports decimal percentages (e.g., 75.5%)

3. **Seat Management**
   - Cancelled seats are immediately available for re-booking
   - Seat counts are automatically adjusted
   - Negative seat counts are prevented

4. **Data Consistency**
   - All operations are transactional
   - Rollback on any failure
   - Atomic updates for booking and schedule

## Error Handling

### Backend Errors
- **Booking Not Found**: 400 Bad Request
- **Already Cancelled**: 400 Bad Request
- **Service Errors**: 400 Bad Request
- **System Errors**: 500 Internal Server Error

### Frontend Errors
- User-friendly error messages
- Retry mechanisms
- Loading state management
- Confirmation dialogs

## Security Considerations

1. **Input Validation**
   - Booking ID validation
   - Status validation before cancellation
   - Transactional boundaries

2. **Data Integrity**
   - No direct database manipulation
   - Service layer validation
   - Audit trail through status changes

## Performance Considerations

1. **Database Operations**
   - Optimized queries with proper indexing
   - Transactional updates for consistency
   - Minimal database round trips

2. **Frontend Performance**
   - Real-time UI updates
   - Efficient state management
   - Minimal API calls

## Future Enhancements

1. **Advanced Refund Policies**
   - Time-based refund percentages
   - Partial cancellation support
   - Refund processing workflows

2. **Notification System**
   - Email confirmations
   - SMS notifications
   - Refund status tracking

3. **Analytics**
   - Cancellation rate tracking
   - Refund amount analytics
   - User behavior insights

## Deployment Notes

1. **Database Migration**
   - No schema changes required
   - Existing data remains intact
   - Backward compatible

2. **API Versioning**
   - New endpoints added
   - Existing endpoints unchanged
   - No breaking changes

3. **Configuration**
   - Carrier refund percentages configurable
   - Environment-specific settings
   - Feature flags support

## Conclusion

Module 11 successfully implements a comprehensive cancel booking system with:
- Robust backend business logic
- User-friendly frontend interface
- Comprehensive error handling
- Extensive testing coverage
- Scalable architecture

The implementation follows best practices for transaction management, data consistency, and user experience, providing a solid foundation for future enhancements.
