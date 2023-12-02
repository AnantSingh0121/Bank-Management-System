# Bank Management System

A secure, scalable banking platform built with **Java Spring Boot**, **Vue.js** and **Apache Cassandra**.

## Features

### Customer Features
- Secure registration and JWT authentication
- Create multiple accounts (Savings/Current)
- Deposit and withdraw money
- Transfer funds between accounts
- View transaction history
- Manage profile
- Receive email notifications for:
  - KYC status updates (Approved / Pending / Rejected)  
  - Loan requests and approval/rejection

### Admin Features
- Dashboard with statistics
- View all customers
- Approve/Reject KYC
- Monitor all transactions

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Data Cassandra
- Spring Security
- JWT Authentication
- Maven
- Spring Mail for notifications

### Frontend
- Vue 3
- Vite
- Pinia (State Management)
- Vue Router
- Axios

### Database
- Apache Cassandra (Local or AWS Keyspaces)

## Project Structure

```
Bank Management System/
├── backend/
│   ├── src/main/java/com/bankmanagement/
│   │   ├── config/          # Security configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data transfer objects
│   │   ├── model/           # Cassandra entities
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # JWT components
│   │   └── service/         # Business logic
│   ├── pom.xml
│   └── schema.cql           # Cassandra schema
└── frontend/
    ├── src/
    │   ├── api/             # API service
    │   ├── components/      # Vue components
    │   ├── router/          # Vue Router
    │   ├── stores/          # Pinia stores
    │   ├── views/           # Page components
    │   └── main.js
    └── package.json
```

## Setup Instructions

### Prerequisites
- Java 17+
- Maven
- Node.js 18+
- Apache Cassandra 4.x

### Database Setup

1. Start Cassandra:
```bash
cassandra
```

2. Create schema:
```bash
cqlsh -f backend/schema.cql
```

### Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Install dependencies and run:
```bash
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### Database Setup (AWS Keyspaces)

1. **Create a Keyspace** in AWS Keyspaces with the same schema.  
2. **Download the Amazon Keyspaces truststore** (`cassandra-truststore.jks`).  
3. **Place the truststore** in `backend/src/main/resources/`.  
4. **Update `application.properties`** with AWS Keyspaces credentials and SSL configuration:

```properties
spring.cassandra.keyspace-name=bankmgmt
spring.cassandra.contact-points=cassandra.eu-north-1.amazonaws.com
spring.cassandra.port=9142
spring.cassandra.username=<AWS_ACCESS_KEY>
spring.cassandra.password=<AWS_SECRET_KEY>
spring.cassandra.ssl.enabled=true
spring.cassandra.ssl.bundle.jks.trust-store.location=classpath:cassandra-truststore.jks
spring.cassandra.ssl.bundle.jks.trust-store.password=amazon
spring.cassandra.local-datacenter=eu-north-1
spring.cassandra.schema-action=create_if_not_exists
spring.cassandra.request.timeout=10s
spring.cassandra.connection.connect-timeout=10s
spring.cassandra.connection.init-query-timeout=10s

Note: Replace <AWS_ACCESS_KEY> and <AWS_SECRET_KEY> with your AWS credentials.
---

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Run development server:
```bash
npm run dev
```

Frontend will start on `http://localhost:5173`

## Default Admin Credentials

- **Username:** `admin`
- **Password:** `admin123`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new customer
- `POST /api/auth/login` - Login

### Customer
- `GET /api/customer/profile` - Get profile
- `PUT /api/customer/profile` - Update profile

### Account
- `POST /api/account/create` - Create account
- `GET /api/account/{accountNo}` - Get account details
- `GET /api/account/balance/{accountNo}` - Get balance
- `GET /api/account/customer/{customerId}` - Get customer accounts

### Transaction
- `POST /api/transaction/deposit` - Deposit money
- `POST /api/transaction/withdraw` - Withdraw money
- `POST /api/transaction/transfer` - Transfer funds
- `GET /api/transaction/history/{accountNo}` - Get transaction history

### Admin
- `GET /api/admin/customers` - Get all customers
- `PUT /api/admin/kyc/approve/{customerId}` - Approve/Reject KYC
- `GET /api/admin/transactions/all` - Get all transactions
- `GET /api/admin/dashboard/stats` - Get dashboard statistics

## Security Features

- JWT-based authentication
- BCrypt password hashing
- Role-based access control (CUSTOMER/ADMIN)
- CORS protection
- Session management

### Notifications via Email
The system sends email notifications for important events:  

- **KYC status:** Approved / Pending / Rejected  
- **Loan requests:** Submitted  
- **Loan approval/rejection**  

> Requires valid SMTP configuration in `application.properties`.

## Performance

- Cassandra provides horizontal scalability
- Optimized time-series queries for transactions
- API response time < 50ms for most operations

## License

MIT License
