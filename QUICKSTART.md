# 🎉 Migration Complete - Quick Start Guide

## ✅ What Has Been Modernized

### Architecture Changes
- ❌ **Old**: SOAP Web Services (JAX-WS)
- ✅ **New**: RESTful API (Spring Boot 3)

- ❌ **Old**: Java Swing Desktop GUI
- ✅ **New**: React Web Application

- ❌ **Old**: Ant Build System
- ✅ **New**: Maven Multi-Module Project

- ❌ **Old**: Raw JDBC Connection
- ✅ **New**: Spring Data JPA + HikariCP

- ❌ **Old**: Plain Text Passwords
- ✅ **New**: BCrypt + JWT Authentication

- ❌ **Old**: Java 8
- ✅ **New**: Java 21 LTS

### What's Preserved
- ✅ **TCP Server on Port 6002** (for your monitoring assignment)
- ✅ **MySQL Database Schema** (compatible with original)
- ✅ **Core Business Logic** (tickets, transactions, notifications)

---

## 🚀 How to Run (3 Steps)

### Step 1: Start MySQL Database
```bash
# Make sure MySQL is running on port 3306
# Database: uasdisprog
# Username: root
# Password: (empty)
```

### Step 2: Start Backend Services

**Terminal 1 - Backend API:**
```bash
cd Disprog_UAS_Modern/backend
mvn clean install
mvn spring-boot:run
```
✅ Running on http://localhost:8080

**Terminal 2 - TCP Server:**
```bash
cd Disprog_UAS_Modern/tcp-server
mvn clean install
mvn spring-boot:run
```
✅ Running on port 6002

### Step 3: Start Frontend

**Terminal 3 - React App:**
```bash
cd Disprog_UAS_Modern/frontend
npm install
npm run dev
```
✅ Running on http://localhost:3000

---

## 🔧 IntelliJ IDEA Setup

### 1. Open Project
```
File → Open → Select "Disprog_UAS_Modern" folder
```

### 2. Maven Import
IntelliJ will automatically detect Maven modules. Wait for dependency download.

### 3. Configure Java 21
```
File → Project Structure → Project SDK → Select JDK 21
```

### 4. Run Configurations

**Backend:**
- Main Class: `com.uasdisprog.backend.UASDisprogBackendApplication`
- Module: `backend`

**TCP Server:**
- Main Class: `com.uasdisprog.tcpserver.TCPServerApplication`
- Module: `tcp-server`

**Frontend:**
- Type: npm
- Command: `run dev`
- Working Directory: `frontend`

---

## 📝 Testing the Application

### 1. Register a Customer
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullname": "Test User",
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "dateOfBirth": "2000-01-01",
  "memberSince": "2026"
}
```

### 2. Login
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

### 3. Browse Tickets
Open http://localhost:3000 in browser and explore!

---

## 🐛 Common Issues & Fixes

### Issue 1: Port Already in Use
```bash
# Backend (8080)
lsof -ti:8080 | xargs kill -9

# TCP Server (6002)
lsof -ti:6002 | xargs kill -9

# Frontend (3000)
lsof -ti:3000 | xargs kill -9
```

### Issue 2: MySQL Connection Failed
Check `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/uasdisprog
spring.datasource.username=root
spring.datasource.password=
```

### Issue 3: Frontend Can't Connect to Backend
Check CORS configuration in `SecurityConfig.java`:
```java
configuration.setAllowedOrigins(List.of("http://localhost:3000"));
```

### Issue 4: Maven Build Failed
```bash
# Clear Maven cache
mvn clean
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

---

## 🔑 Default Credentials

### Admin Account
```
Username: b
Password: c
```

### Customer Accounts (from SQL dump)
```
Username: Fi
Password: 1234

Username: Ab
Password: 5678

Username: Dan
Password: abcd
```

---

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication
All protected endpoints require JWT token in header:
```
Authorization: Bearer <token>
```

### Key Endpoints
- `POST /auth/login` - Login
- `POST /auth/register` - Register
- `GET /public/tickets` - Get all tickets
- `GET /customer/balance` - Get balance
- `POST /customer/topup` - Top-up balance
- `POST /customer/transactions` - Book tickets
- `DELETE /customer/transactions/{id}` - Cancel booking

---

## 📊 Project Statistics

- **Total Files**: ~80 files
- **Lines of Code**: ~5,000+ LOC
- **Technologies**: 15+ (Java, Spring, React, MySQL, Maven, etc.)
- **API Endpoints**: 20+
- **React Components**: 10+
- **Database Tables**: 8 tables

---

## 🎓 Assignment Notes

### TCP Server Monitoring
The TCP Server runs on port **6002** as required. It handles:
- Broadcast notifications
- Personal notifications
- Notification count
- Read/delete notifications

You can monitor TCP traffic using:
```bash
telnet localhost 6002
```

Or test with commands like:
```
Test message-broadcast
Personal msg-personal-1
none-hitungnotif-1-0
```

---

## 🚢 Deployment (Docker)

### Quick Deploy All Services
```bash
cd Disprog_UAS_Modern/docker
docker-compose up -d
```

This will start:
- MySQL (port 3306)
- Backend (port 8080)
- TCP Server (port 6002)
- Frontend (port 3000)

---

## 📚 Additional Resources

### Documentation
- Spring Boot: https://spring.io/projects/spring-boot
- React: https://react.dev
- Vite: https://vitejs.dev
- Tailwind CSS: https://tailwindcss.com

### Code Structure
- `backend/` - Spring Boot REST API
- `tcp-server/` - TCP Socket Server
- `frontend/` - React SPA
- `docker/` - Docker configuration

---

## 🤝 Need Help?

### Check Logs
```bash
# Backend logs
cd backend
mvn spring-boot:run

# TCP Server logs
cd tcp-server
mvn spring-boot:run

# Frontend logs
cd frontend
npm run dev
```

### Database Issues
```sql
-- Verify tables exist
USE uasdisprog;
SHOW TABLES;

-- Check sample data
SELECT * FROM users;
SELECT * FROM tickets;
```

---

**Project Status**: ✅ Fully Migrated & Ready to Run  
**Version**: 2.0.0  
**Date**: September 3, 2026  
**Compatibility**: IntelliJ IDEA 2026.2.2 + Java 21 + Node 18+

**Enjoy your modernized application! 🎉**
