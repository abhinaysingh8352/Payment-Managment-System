# Payment Management System - Technical Documentation

## Table of Contents
1. [System Overview](#system-overview)
2. [Architecture Design](#architecture-design)
3. [Database Design](#database-design)
4. [API Documentation](#api-documentation)
5. [Class Diagrams](#class-diagrams)
6. [Sequence Diagrams](#sequence-diagrams)
7. [Security Implementation](#security-implementation)
8. [Error Handling](#error-handling)
9. [Performance Considerations](#performance-considerations)
10. [Deployment Guide](#deployment-guide)

## System Overview

### Purpose
The Payment Management System is designed to handle financial transactions within an organization, providing secure user management, transaction processing, status tracking, and comprehensive reporting capabilities.

### Scope
- User authentication and authorization
- Payment transaction management
- Financial reporting and analytics
- Audit trail maintenance
- Role-based access control

### Technology Stack
- **Backend**: Java SE 17
- **Database**: PostgreSQL 14+
- **JDBC**: PostgreSQL JDBC Driver 42.6.0
- **Architecture**: Layered Architecture Pattern
- **Design Patterns**: DAO, Factory, Singleton

## Architecture Design

### Layered Architecture Pattern

#### 1. Presentation Layer (Controller)
**Responsibilities:**
- User input handling
- Menu display and navigation
- Input validation
- Session management

**Components:**
- `Main.java` - Application entry point
- `PaymentController.java` - Payment operations UI
- `ReportController.java` - Report generation UI

#### 2. Service Layer (Business Logic)
**Responsibilities:**
- Business rule enforcement
- Transaction management
- Data validation
- Audit logging

**Components:**
- `PaymentService.java` - Payment business logic
- `UserService.java` - User management logic
- `ReportService.java` - Report generation logic

#### 3. Data Access Layer (DAO)
**Responsibilities:**
- Database connection management
- CRUD operations
- SQL query execution
- Data mapping

**Components:**
- Interface: `PaymentDao`, `UserDao`, `AuditTrailDao`
- Implementation: `PaymentDaoImpl`, `UserDaoImpl`, `AuditTrailDaoImpl`

#### 4. Utility Layer
**Responsibilities:**
- Database connection pooling
- Configuration management
- Common utilities

**Components:**
- `DBUtil.java` - Database connection management
- Exception classes for error handling

### Design Patterns Implemented

#### 1. Data Access Object (DAO) Pattern
```java
public interface PaymentDao {
    void addPayment(Payment payment) throws Exception;
    Payment getPayment(int paymentId) throws Exception;
    List<Payment> getAllPayments() throws Exception;
    void updatePaymentStatus(int paymentId, String status) throws Exception;
}
```

#### 2. Service Layer Pattern
```java
public class PaymentService {
    private PaymentDao paymentDao;
    private AuditTrailDao auditTrailDao;
    
    public void addPayment(Payment payment) throws BusinessException {
        // Business validation
        // Audit logging
        // Delegate to DAO
    }
}
```

#### 3. Factory Pattern (Implicit)
```java
// Service instantiation in Main.java
PaymentService paymentService = new PaymentService(
    new PaymentDaoImpl(), 
    new AuditTrailDaoImpl()
);
```

## Database Design

### Entity Relationship Model

#### Entities and Attributes

**User Entity:**
- `id` (Primary Key) - Integer, Auto-increment
- `username` - VARCHAR(50), Unique, Not Null
- `password` - VARCHAR(255), Not Null
- `role` - VARCHAR(30), Not Null

**Payment Entity:**
- `id` (Primary Key) - Integer, Auto-increment
- `amount` - NUMERIC(12,2), Not Null
- `type` - VARCHAR(20), Not Null
- `category` - VARCHAR(30), Not Null
- `status` - VARCHAR(20), Not Null
- `created_at` - TIMESTAMP, Not Null
- `updated_at` - TIMESTAMP, Not Null
- `user_id` (Foreign Key) - Integer, References users(id)
- `remarks` - VARCHAR(255)

**AuditTrail Entity:**
- `id` (Primary Key) - Integer, Auto-increment
- `action` - VARCHAR(100), Not Null
- `user_id` (Foreign Key) - Integer, References users(id)
- `payment_id` (Foreign Key) - Integer, References payment(id)
- `timestamp` - TIMESTAMP, Not Null
- `details` - VARCHAR(255)

#### Relationships

1. **User to Payment (1:M)**
    - One user can create multiple payments
    - Foreign key: `payment.user_id` → `users.id`

2. **User to AuditTrail (1:M)**
    - One user can generate multiple audit entries
    - Foreign key: `audit_trail.user_id` → `users.id`

3. **Payment to AuditTrail (1:M)**
    - One payment can have multiple audit log entries
    - Foreign key: `audit_trail.payment_id` → `payment.id`

### Database Schema SQL

```sql
-- Create Users Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('ADMIN', 'FINANCE_MANAGER', 'VIEWER'))
);

-- Create Payment Table
CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    amount NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOMING', 'OUTGOING')),
    category VARCHAR(30) NOT NULL CHECK (category IN ('SALARY', 'VENDOR_PAYMENT', 'CLIENT_INVOICE')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    remarks VARCHAR(255)
);

-- Create Audit Trail Table
CREATE TABLE audit_trail (
    id SERIAL PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    payment_id INTEGER REFERENCES payment(id) ON DELETE CASCADE,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    details VARCHAR(255)
);

-- Create Indexes for Performance
CREATE INDEX idx_payment_user_id ON payment(user_id);
CREATE INDEX idx_payment_status ON payment(status);
CREATE INDEX idx_payment_created_at ON payment(created_at);
CREATE INDEX idx_audit_trail_user_id ON audit_trail(user_id);
CREATE INDEX idx_audit_trail_payment_id ON audit_trail(payment_id);
CREATE INDEX idx_audit_trail_timestamp ON audit_trail(timestamp);
```

## API Documentation

### Service Layer APIs

#### PaymentService

**addPayment(Payment payment)**
- **Purpose**: Creates a new payment transaction
- **Parameters**: Payment object with required fields
- **Returns**: void
- **Exceptions**: BusinessException, Exception
- **Business Rules**:
    - Amount must be positive
    - User must exist
    - Automatic audit logging

**updatePaymentStatus(int paymentId, PaymentStatus newStatus, int userId)**
- **Purpose**: Updates payment status
- **Parameters**: Payment ID, new status, user ID
- **Returns**: void
- **Exceptions**: NotFoundException, Exception
- **Business Rules**:
    - Payment must exist
    - Status transition validation
    - Automatic audit logging

**getPayment(int paymentId)**
- **Purpose**: Retrieves payment by ID
- **Parameters**: Payment ID
- **Returns**: Payment object or null
- **Exceptions**: Exception

#### UserService

**addUser(User user)**
- **Purpose**: Creates new user account
- **Parameters**: User object
- **Returns**: void
- **Exceptions**: Exception
- **Business Rules**:
    - Username must be unique
    - Password requirements
    - Valid role assignment

**getUserByUsername(String username)**
- **Purpose**: Retrieves user by username
- **Parameters**: Username string
- **Returns**: User object or null
- **Exceptions**: Exception

#### ReportService

**getMonthlyReport(int month, int year)**
- **Purpose**: Generates monthly financial report
- **Parameters**: Month (1-12), Year
- **Returns**: Map<String, Double> (category -> amount)
- **Exceptions**: Exception
- **Business Rules**:
    - Only completed payments included
    - Grouped by category

**getQuarterlyReport(int quarter, int year)**
- **Purpose**: Generates quarterly financial report
- **Parameters**: Quarter (1-4), Year
- **Returns**: Map<String, Double> (category -> amount)
- **Exceptions**: Exception

### DAO Layer APIs

#### PaymentDao Interface

```java
public interface PaymentDao {
    void addPayment(Payment payment) throws Exception;
    void updatePaymentStatus(int paymentId, String status) throws Exception;
    Payment getPayment(int paymentId) throws Exception;
    List<Payment> getAllPayments() throws Exception;
    List<Payment> getPaymentsByUser(int userId) throws Exception;
}
```

#### UserDao Interface

```java
public interface UserDao {
    User getUserById(int id) throws Exception;
    User getUserByUsername(String username) throws Exception;
    void addUser(User user) throws Exception;
}
```

#### AuditTrailDao Interface

```java
public interface AuditTrailDao {
    void logAction(AuditTrail auditTrail) throws Exception;
}
```

## Security Implementation

### Authentication
- Username/password based authentication
- Session management through static variables
- No external authentication providers (future enhancement)

### Authorization (Role-Based Access Control)

| Operation | ADMIN | FINANCE_MANAGER | VIEWER |
|-----------|-------|-----------------|--------|
| Add Payment | ✅ | ✅ | ❌ |
| Update Payment Status | ✅ | ✅ | ❌ |
| View Reports | ✅ | ✅ | ✅ |
| Create Users | ✅ | ❌ | ❌ |

### Data Security
- Database credentials externalized in `db.properties`
- SQL injection prevention through PreparedStatements
- Input validation at service layer

### Future Security Enhancements
- Password hashing (BCrypt)
- JWT token-based authentication
- HTTPS/TLS encryption
- Rate limiting
- Session timeout

## Error Handling

### Exception Hierarchy

```java
Exception
├── BusinessException (Custom)
├── NotFoundException (Custom)
└── SQLException (Standard)
```

### Exception Handling Strategy

#### Custom Exceptions

**BusinessException**
- Used for business rule violations
- Examples: Negative amount, invalid status transition

**NotFoundException**
- Used when requested resources don't exist
- Examples: Payment not found, User not found

#### Error Handling Flow

1. **DAO Layer**: Catches and re-throws SQLException
2. **Service Layer**: Catches DAO exceptions, applies business logic, throws custom exceptions
3. **Controller Layer**: Catches all exceptions, displays user-friendly messages

### Logging Strategy

```java
// Current: System.out.println (Development)
System.out.println("✅ Payment added successfully!");
System.out.println("❌ Error: " + exception.getMessage());

// Future: Proper logging framework
logger.info("Payment {} added by user {}", paymentId, userId);
logger.error("Failed to add payment", exception);
```



## Deployment Guide

### Development Environment Setup

1. **Prerequisites**
   ```bash
   Java JDK 17+
   PostgreSQL 14+
   Maven 3.6+
   IntelliJ IDEA (recommended)
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE payments;
   -- Run schema creation scripts
   -- Insert initial data
   ```

3. **Application Configuration**
   ```properties
   # src/main/resources/db.properties
   db.url=jdbc:postgresql://localhost:5432/payments
   db.username=postgres
   db.password=your_password
   ```

4. **Dependencies**
    - Add PostgreSQL JDBC driver to project libraries
    - Ensure all packages compile without errors
   
### Integration Testing
- Database connectivity tests
- End-to-end workflow testing
- Role-based access control testing

### Manual Testing Checklist
- [ ] User registration/login flows
- [ ] Payment CRUD operations
- [ ] Report generation accuracy
- [ ] Role-based access restrictions
- [ ] Audit trail completeness
- [ ] Error handling scenarios



---

**Document Version**: 1.0  
**Last Updated**: August 2025  
