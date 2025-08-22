# Module 6: View Flights Implementation

## Overview
This module implements the View Flights functionality with both backend and frontend components, allowing users to view all flights and search flights by carrier name.

## Backend Implementation

### 1. FlightService Updates
- **File**: `backend/src/main/java/com/airline/flightmanagement/service/FlightService.java`
- **New Method**: `getFlightsByCarrierName(String carrierName)`
- **Functionality**: Retrieves flights filtered by carrier name using the existing repository method

### 2. FlightController Updates
- **File**: `backend/src/main/java/com/airline/flightmanagement/controller/FlightController.java`
- **New Endpoint**: `GET /api/flights/carrierName/{carrierName}`
- **Functionality**: Returns flights filtered by carrier name
- **Response Format**: Standard API response with success status, message, and data

### 3. Repository Layer
- **File**: `backend/src/main/java/com/airline/flightmanagement/repository/FlightRepository.java`
- **Existing Method**: `findByCarrierName(String carrierName)` - Already implemented with JPA query

## Frontend Implementation

### 1. View Flights Component
- **File**: `frontend/flight-management-frontend/src/app/components/view-flights/view-flights.component.ts`
- **Features**:
  - Loads all flights on component initialization
  - Provides search functionality by carrier name
  - Displays flights in a responsive table format
  - Handles loading states and error messages
  - Calculates total seats for each flight

### 2. Component Template
- **File**: `frontend/flight-management-frontend/src/app/components/view-flights/view-flights.component.html`
- **Features**:
  - Modern, responsive design with gradient headers
  - Search input with search and clear buttons
  - Comprehensive flight table with all relevant columns
  - Loading spinner and error message handling
  - Empty state handling with user-friendly messages

### 3. Component Styling
- **File**: `frontend/flight-management-frontend/src/app/components/view-flights/view-flights.component.css`
- **Features**:
  - Modern gradient design with smooth transitions
  - Responsive layout for mobile and desktop
  - Hover effects and interactive elements
  - Professional table styling with proper spacing

### 4. Flight Service Updates
- **File**: `frontend/flight-management-frontend/src/app/services/flight.service.ts`
- **New Method**: `getFlightsByCarrierName(carrierName: string)`
- **Functionality**: Calls the new backend endpoint to search flights by carrier

### 5. Navigation Updates
- **File**: `frontend/flight-management-frontend/src/app/components/navigation/navigation.component.html`
- **New Link**: Added "View Flights" navigation link

### 6. Routing Updates
- **File**: `frontend/flight-management-frontend/src/app/app.routes.ts`
- **New Route**: `/view-flights` mapped to ViewFlightsComponent

## API Endpoints

### 1. View All Flights
- **Endpoint**: `GET /api/flights`
- **Description**: Retrieves all available flights
- **Response**: List of FlightDTO objects with success status

### 2. View Flights by Carrier Name
- **Endpoint**: `GET /api/flights/carrierName/{carrierName}`
- **Description**: Retrieves flights filtered by carrier name
- **Parameters**: `carrierName` (path variable)
- **Response**: List of FlightDTO objects for the specified carrier

## Testing

### 1. Backend Tests
- **File**: `backend/src/test/java/com/airline/flightmanagement/service/FlightServiceTest.java`
- **New Tests**:
  - `testGetFlightsByCarrierName_Success()`: Tests successful retrieval of flights by carrier name
  - `testGetFlightsByCarrierName_NoFlightsFound()`: Tests behavior when no flights are found

### 2. Frontend Tests
- **File**: `frontend/flight-management-frontend/src/app/components/view-flights/view-flights.component.spec.ts`
- **Test Coverage**:
  - Component creation and initialization
  - Loading all flights on init
  - Error handling for failed API calls
  - Search functionality by carrier name
  - Empty search handling
  - Search with no results
  - Search error handling
  - Clear search functionality
  - Total seats calculation
  - Null value handling

## Features

### 1. Flight Display
- **Flight ID**: Unique identifier for each flight
- **Carrier Information**: Carrier name with distinct styling
- **Route Details**: Origin and destination cities
- **Pricing**: Air fare with currency formatting
- **Seat Information**: Business, Economy, and Executive class capacities
- **Total Seats**: Calculated sum of all seat categories

### 2. Search Functionality
- **Carrier Name Search**: Real-time search by carrier name
- **Search Button**: Triggers API call to filter flights
- **Clear Button**: Resets search and shows all flights
- **Enter Key Support**: Search on Enter key press

### 3. User Experience
- **Loading States**: Visual feedback during API calls
- **Error Handling**: User-friendly error messages
- **Empty States**: Helpful messages when no flights are found
- **Responsive Design**: Works on all device sizes
- **Modern UI**: Professional appearance with gradients and shadows

## Technical Details

### 1. Data Flow
1. Component initializes and calls `getAllFlights()`
2. Service makes HTTP GET request to `/api/flights`
3. Backend processes request and returns flight data
4. Frontend displays flights in table format
5. User can search by carrier name using search input
6. Search triggers `getFlightsByCarrierName()` API call
7. Results are filtered and displayed accordingly

### 2. Error Handling
- **Network Errors**: Graceful fallback with user-friendly messages
- **Empty Results**: Clear indication when no flights match search criteria
- **API Failures**: Proper error messages for failed requests

### 3. Performance Considerations
- **Lazy Loading**: Flights are loaded only when component is accessed
- **Efficient Filtering**: Backend filtering reduces data transfer
- **Responsive Updates**: UI updates immediately after API responses

## Usage Instructions

### 1. Accessing View Flights
1. Navigate to the application
2. Click on "View Flights" in the navigation menu
3. All flights will be displayed in a table format

### 2. Searching by Carrier
1. Enter a carrier name in the search input field
2. Click the "Search" button or press Enter
3. Results will be filtered to show only flights from that carrier
4. Use the "Clear" button to reset the search and view all flights

### 3. Understanding the Table
- **Flight ID**: Unique identifier for the flight
- **Carrier**: Airline company name
- **Origin**: Departure city
- **Destination**: Arrival city
- **Air Fare**: Ticket price in USD
- **Seat Capacities**: Available seats in each class
- **Total Seats**: Sum of all available seats

## Future Enhancements

### 1. Additional Search Criteria
- Search by origin/destination cities
- Filter by price range
- Search by date of travel
- Filter by seat availability

### 2. Advanced Features
- Sorting by various columns
- Pagination for large datasets
- Export functionality (CSV, PDF)
- Real-time flight status updates

### 3. User Preferences
- Save favorite search criteria
- Customizable table columns
- Theme preferences
- Language localization

## Conclusion

Module 6: View Flights has been successfully implemented with:
- ✅ Complete backend API endpoints
- ✅ Modern, responsive frontend interface
- ✅ Comprehensive search functionality
- ✅ Professional UI/UX design
- ✅ Thorough testing coverage
- ✅ Error handling and user feedback
- ✅ Responsive design for all devices

The implementation follows best practices for both backend and frontend development, providing a robust and user-friendly flight viewing experience.
