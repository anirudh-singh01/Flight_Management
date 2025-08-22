# Airline Management System

A comprehensive airline management system built using Spring Boot (backend microservices + H2 SQL) and Angular (frontend). This system provides complete functionality for managing airlines, flights, bookings, and user administration.

## 🚀 Project Overview

The Airline Management System is a full-stack application designed to handle all aspects of airline operations including:

- **User Management**: Customer and administrator registration and authentication
- **Carrier Management**: Airline carrier registration and management
- **Flight Management**: Flight scheduling, updates, and availability tracking
- **Booking System**: Flight booking with automatic discount calculation and seat management
- **Booking Operations**: View booking history and cancel bookings
- **Real-time Features**: Seat availability checking and dynamic pricing

## 🛠️ Tech Stack

### Backend
- **Java 17** - Core programming language
- **Spring Boot 3.2.0** - Main framework for microservices
- **Spring Data JPA** - Data persistence and database operations
- **H2 Database** - In-memory SQL database for development
- **Maven** - Dependency management and build tool
- **JUnit** - Unit testing framework

### Frontend
- **Angular 20.2.0** - Modern frontend framework
- **TypeScript 5.9.2** - Type-safe JavaScript
- **HTML/CSS** - Markup and styling
- **Jasmine/Karma** - Testing framework

## 📋 Prerequisites

- Java 17 or higher
- Node.js 18+ and npm
- Angular CLI (`npm install -g @angular/cli`)
- Maven 3.6+

## 🆕 Complete Setup Process for New System

### Step 1: Install Java 17+
1. **Download Java:**
   - Visit [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
   - Download Java 17 or higher for your operating system
   
2. **Install Java:**
   - **Windows**: Run the downloaded `.exe` file and follow the installer
   - **macOS**: Run the downloaded `.dmg` file and follow the installer
   - **Linux**: Use package manager or download and extract the tar.gz file

3. **Verify Installation:**
   ```bash
   java -version
   # Should show Java 17 or higher
   ```

### Step 2: Install Maven 3.6+
1. **Download Maven:**
   - Visit [Apache Maven](https://maven.apache.org/download.cgi)
   - Download the latest binary zip archive

2. **Install Maven:**
   - **Windows**: Extract to `C:\Program Files\Apache\maven` and add to PATH
   - **macOS/Linux**: Extract to `/opt/apache-maven` and add to PATH
   
3. **Set Environment Variables:**
   - **Windows**: Add `C:\Program Files\Apache\maven\bin` to PATH
   - **macOS/Linux**: Add `/opt/apache-maven/bin` to PATH
   
4. **Verify Installation:**
   ```bash
   mvn -version
   # Should show Maven 3.6+ and Java version
   ```

### Step 3: Install Node.js 18+
1. **Download Node.js:**
   - Visit [nodejs.org](https://nodejs.org/)
   - Download the LTS version (18+)

2. **Install Node.js:**
   - **Windows**: Run the downloaded `.msi` file
   - **macOS**: Run the downloaded `.pkg` file
   - **Linux**: Use package manager or download and install

3. **Verify Installation:**
   ```bash
   node --version
   npm --version
   # Should show Node.js 18+ and npm version
   ```

### Step 4: Install Angular CLI
```bash
npm install -g @angular/cli
```

**Verify Installation:**
```bash
ng version
# Should show Angular CLI version
```

### Step 5: Clone and Setup Project
```bash
# Clone your repository
git clone <your-repository-url>
cd flight_management

# Verify all prerequisites are working
java -version
mvn -version
node --version
ng version
```

## 🚀 Setup Instructions

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

   The backend will start on **http://localhost:8080**

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend/flight-management-frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   ng serve
   ```

   The frontend will be available at **http://localhost:4200**

## 🗄️ Database Information

- **Database**: H2 In-Memory Database
- **Console Access**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa` (default)
- **Password**: ` ` (empty by default)

## 🔌 API Endpoints

### User Management APIs
- `POST /api/users/registerUser` - Register a new customer
- `POST /api/users/registerAdmin` - Register a new administrator
- `POST /api/users/login` - User authentication
- `GET /api/users/{userId}` - Get user by ID
- `GET /api/users/username/{userName}` - Get user by username

### Carrier Management APIs
- `POST /api/carriers/registerCarrier` - Register a new airline carrier
- `GET /api/carriers/{carrierId}` - Get carrier by ID
- `GET /api/carriers/name/{carrierName}` - Get carrier by name
- `GET /api/carriers` - Get all carriers
- `GET /api/carriers/active` - Get active carriers
- `PUT /api/carriers/{carrierId}` - Update carrier information
- `DELETE /api/carriers/{carrierId}` - Delete carrier

### Flight Management APIs
- `POST /api/flights/registerFlight` - Register a new flight
- `GET /api/flights` - Get all flights
- `GET /api/flights/{flightId}` - Get flight by ID
- `PUT /api/flights/{flightId}` - Update flight information
- `DELETE /api/flights/{flightId}` - Delete flight

### Booking Management APIs
- `POST /api/bookings/bookFlight` - Book a flight
- `GET /api/bookings/{bookingId}` - Get booking by ID
- `GET /api/bookings/user/{userId}` - Get all bookings for a user
- `GET /api/bookings/availability` - Check seat availability
- `PUT /api/bookings/{bookingId}/cancel` - Cancel a booking

## ✨ Frontend Features

### User Interface Components
- **Registration Forms**: User, Admin, Carrier, and Flight registration
- **Login System**: Secure authentication interface
- **Flight Management**: Comprehensive flight listing and search functionality
- **Booking System**: Advanced booking with real-time seat availability and discount calculation
- **Booking History**: Complete booking management and cancellation system
- **Responsive Design**: Modern, mobile-friendly interface

### Key Features
- **Automatic Discount Calculation**: System calculates discounts based on various factors
- **Real-time Seat Management**: Live seat availability tracking
- **Dynamic Pricing**: Flexible fare management system
- **User Role Management**: Separate interfaces for customers and administrators

## 🧪 Testing Instructions

### Backend Testing
Run the following command from the backend directory:
```bash
mvn test
```

### Frontend Testing
Run the following command from the frontend directory:
```bash
ng test
```

## 📁 Project Structure

```
flight_management/
├── backend/                          # Spring Boot backend
│   ├── src/main/java/com/airline/flightmanagement/
│   │   ├── controller/              # REST API controllers
│   │   ├── service/                 # Business logic services
│   │   ├── repository/              # Data access layer
│   │   ├── entity/                  # JPA entities
│   │   └── dto/                     # Data transfer objects
│   ├── src/main/resources/          # Configuration files
│   └── pom.xml                      # Maven configuration
├── frontend/                         # Angular frontend
│   └── flight-management-frontend/
│       ├── src/app/components/      # Angular components
│       ├── src/app/services/        # Frontend services
│       ├── src/app/models/          # TypeScript models
│       └── package.json             # Node.js dependencies
└── README.md                         # This file
```

## 🔮 Future Improvements

- **JWT Authentication**: Implement secure token-based authentication for production
- **External Database**: Migrate from H2 to PostgreSQL or MySQL for production use
- **Enhanced UI**: Integrate Bootstrap or Material UI for better styling
- **Real-time Updates**: Implement WebSocket connections for live updates
- **Payment Integration**: Add payment gateway integration for bookings
- **Email Notifications**: Send booking confirmations and updates via email
- **Mobile App**: Develop native mobile applications for iOS and Android
- **Analytics Dashboard**: Add comprehensive reporting and analytics features

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions, please contact the development team or create an issue in the repository.

---

**Happy Flying! ✈️**
