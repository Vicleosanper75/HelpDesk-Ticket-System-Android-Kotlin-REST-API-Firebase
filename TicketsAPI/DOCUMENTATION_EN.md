# Tickets API - Complete Documentation

## 📋 Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Prerequisites](#prerequisites)
4. [Installation](#installation)
5. [Configuration](#configuration)
6. [Project Structure](#project-structure)
7. [Authentication](#authentication)
8. [API Endpoints](#api-endpoints)
9. [Data Models](#data-models)
10. [Usage Examples](#usage-examples)
11. [Error Handling](#error-handling)
12. [Push Notifications](#push-notifications)
13. [Security](#security)
14. [Deployment](#deployment)
15. [Troubleshooting](#troubleshooting)

---

## 📱 Overview

**Tickets API** is a complete backend solution developed in Node.js to manage a ticket system with support for real-time messaging, user management with roles, and push notifications integrated with Firebase Cloud Messaging.

The API is designed to be used primarily by Android applications, but is client-agnostic and can be consumed from any platform compatible with HTTP/REST.

**Author:** Victor Amaya  
**Version:** 1.0.0  
**License:** ISC

---

## ✨ Features

- ✅ **User Management** - Registration, login, roles and permissions
- ✅ **Ticket System** - Create, assign, reassign and close tickets
- ✅ **Messaging** - Complete chat system with image support
- ✅ **JWT Authentication** - Access tokens and refresh tokens
- ✅ **Push Notifications** - Firebase Cloud Messaging integration
- ✅ **Message Encryption** - Messages stored encrypted
- ✅ **Online Status Control** - Tracking of active users
- ✅ **SQLite Database** - Local data persistence
- ✅ **File Upload** - Image support in messages (Multer)
- ✅ **Pagination** - Support for paginated results

---

## 🔧 Prerequisites

### System:
- **Node.js** >= 14.0.0
- **npm** >= 6.0.0
- **SQLite3** (included in project)
- Windows, macOS or Linux

### Knowledge:
- Basic JavaScript/Node.js
- REST APIs
- JWT (JSON Web Tokens)
- Basic SQLite/SQL

---

## 📦 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/TicketsAPI.git
cd TicketsAPI
```

### 2. Install Dependencies
```bash
npm install
```

This command will install:
- **express** - Web framework
- **sqlite3** - Database
- **bcrypt/bcryptjs** - Password encryption
- **jsonwebtoken** - JWT authentication
- **firebase-admin** - Push notifications
- **multer** - File upload handling
- **socket.io** - Real-time WebSockets
- **dotenv** - Environment variables

### 3. Create Database
```bash
npm run db
```

This command will execute [src/models/sqlite.js](#) which creates the necessary tables.

---

## ⚙️ Configuration

### Environment Variables (.env)

Create a `.env` file in the project root:

```env
# PORT
PORT=3001

# JWT SECRETS (CRUCIAL - Generate long random strings)
ACCESS_TOKEN_SECRET=your_super_long_and_random_secret_key_for_access_tokens
REFRESH_TOKEN_SECRET=your_super_long_and_random_secret_key_for_refresh_tokens

# FIREBASE (Optional, only if using push notifications)
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_PRIVATE_KEY_ID=your-private-key-id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk@your-project.iam.gserviceaccount.com
FIREBASE_CLIENT_ID=your-client-id
```

### Important Files

#### serviceAccountKey.json
This file contains Firebase credentials. Required to send push notifications.

```json
{
  "type": "service_account",
  "project_id": "your-project",
  "private_key_id": "...",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...",
  "client_email": "firebase-adminsdk-...",
  "client_id": "...",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "..."
}
```

---

## 🏗️ Project Structure

```
TicketsAPI/
├── src/
│   ├── controllers/          # Business logic
│   │   ├── main.controller.js
│   │   ├── users.controller.js
│   │   ├── tickets.controller.js
│   │   ├── messages.controller.js
│   │   └── cryptoHelper.js    # Message encryption
│   │
│   ├── models/               # Data access layers
│   │   ├── users.js
│   │   ├── tickets.js
│   │   ├── messages.js
│   │   └── sqlite.js         # Database initialization
│   │
│   └── routes/               # Endpoint definitions
│       ├── main.router.js
│       ├── users.router.js
│       ├── tickets.router.js
│       └── messages.router.js
│
├── uploads/                  # Directory for uploaded images
├── authMiddleware.js         # JWT authentication middleware
├── firebaseAdmin.js          # Firebase configuration
├── index.js                  # Entry point
├── package.json              # Dependencies
├── .env                      # Environment variables (do not commit)
├── .gitignore
├── SQLite.sql               # Useful SQL scripts
├── database.sqbpro          # SQLite tool
└── DOCUMENTATION.md         # This file
```

---

## 🔐 Authentication

### Authentication Flow

```
1. REGISTRATION
   POST /users/register
   {username, fullname, pwd, role}
   ↓
   Password encrypted with bcrypt
   ↓
   User saved to database
   
2. LOGIN
   POST /users/login
   {username, pwd}
   ↓
   Password validated
   ↓
   JWT Access Token (9 hours)
   JWT Refresh Token (No expiration)
   
3. USE API
   GET /tickets/:id
   Headers: Authorization: Bearer <ACCESS_TOKEN>
   ↓
   Middleware validates token
   ↓
   Access granted
   
4. REFRESH TOKEN
   POST /users/token
   {token: REFRESH_TOKEN}
   ↓
   New Access Token (9 hours)
   
5. LOGOUT
   POST /users/logout
   {token: REFRESH_TOKEN}
   ↓
   Refresh Token removed from database
```

### Token Types

#### Access Token
- **Expiration:** 9 hours
- **Usage:** Access to protected endpoints
- **Location in Headers:** `Authorization: Bearer <token>`
- **Payload:** `{name: username}`

#### Refresh Token
- **Expiration:** No limit
- **Usage:** Obtain new access token
- **Storage:** Database (TOKENS table)
- **Validation:** Token must exist in database to be valid

### Authentication Middleware

All endpoints except `/users/register` and `/users/login` require:

```javascript
// Required headers
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

If the token is invalid or expired:
```json
{
  "error": "Invalid token" | "Token missing"
}
```

---

## 📡 API Endpoints

### Base URL
```
http://localhost:3001
```

---

### 👥 USERS - User Management

#### 1. User Registration
```http
POST /users/register
Content-Type: application/json

{
  "username": "juan_perez",
  "fullname": "Juan Pérez García",
  "pwd": "MyPassword123!",
  "role": 1
}
```

**Successful Response (201):**
```json
{
  "id": 5
}
```

**Possible Errors:**
| Code | Description |
|------|-------------|
| 500 | Error hashing password or database |

**Available Roles:**
- `1` - Operator
- `2` - Support
- `3` - Systems

---

#### 2. Login
```http
POST /users/login
Content-Type: application/json

{
  "username": "juan_perez",
  "pwd": "MyPassword123!"
}
```

**Successful Response (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Possible Errors:**
| Code | Description |
|------|-------------|
| 404 | User not found |
| 400 | Incorrect password |
| 500 | Server error |

---

#### 3. Refresh Access Token
```http
POST /users/token
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Successful Response (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Possible Errors:**
| Code | Description |
|------|-------------|
| 401 | Token not provided |
| 403 | Token does not exist or is invalid |
| 500 | Server error |

---

#### 4. Logout
```http
POST /users/logout
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Successful Response (204):**
```
Logout success
```

---

#### 5. Get All Users
```http
GET /users
Authorization: Bearer <ACCESS_TOKEN>
```

**Successful Response (200):**
```json
[
  {
    "ID": 1,
    "USER_NAME": "admin",
    "FULL_NAME": "Administrator",
    "ONLINE": 1,
    "CREATED_AT": "2024-01-15 10:30:00",
    "ROLE": 3,
    "ROLE_NAME": "Systems",
    "ACTIVE": 1
  },
  {
    "ID": 2,
    "USER_NAME": "juan_perez",
    "FULL_NAME": "Juan Pérez García",
    "ONLINE": 0,
    "CREATED_AT": "2024-01-20 14:45:00",
    "ROLE": 1,
    "ROLE_NAME": "Operator",
    "ACTIVE": 1
  }
]
```

---

#### 6. Get User by ID
```http
GET /users/:id
Authorization: Bearer <ACCESS_TOKEN>
```

**Example:**
```http
GET /users/2
Authorization: Bearer <ACCESS_TOKEN>
```

**Successful Response (200):**
```json
{
  "ID": 2,
  "USER_NAME": "juan_perez",
  "FULL_NAME": "Juan Pérez García",
  "ONLINE": 1,
  "CREATED_AT": "2024-01-20 14:45:00",
  "ROLE": 1,
  "ROLE_NAME": "Operator",
  "ACTIVE": 1
}
```

**Possible Errors:**
| Code | Description |
|------|-------------|
| 404 | User not found |
| 500 | Server error |

---

#### 7. Update User
```http
POST /users/update/:id
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "username": "new_user",
  "fullname": "New Full Name",
  "pwd": "NewPassword123!"
}
```

**Optional Fields:** All fields are optional. Only sent fields will be updated.

**Successful Response (201):**
```json
1
```
(Number of rows updated)

---

#### 8. Update FCM Token (Push Notifications)
```http
POST /users/updateToken
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "userId": 2,
  "fcmToken": "fHkfJK3jk2n3jk2nj3k2nj32kn2kn3..."
}
```

**Successful Response (200):**
```json
{
  "message": "Token updated successfully",
  "changes": 1
}
```

---

#### 9. Set Online/Offline Status
```http
POST /users/setOnlineStatus/:id
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "online": 1
}
```

**Parameters:**
- `online`: `1` = Online, `0` = Offline

**Successful Response (200):**
```json
1
```

---

## 📚 Data Models

### USERS Table
```sql
CREATE TABLE USERS (
  ID INTEGER PRIMARY KEY,
  USER_NAME TEXT UNIQUE NOT NULL,
  FULL_NAME TEXT NOT NULL,
  PWD TEXT NOT NULL,
  ONLINE INTEGER DEFAULT 0,
  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  ROLE INTEGER DEFAULT 1,
  ACTIVE INTEGER DEFAULT 1,
  FCM_TOKEN TEXT
)
```

### TICKETS Table
```sql
CREATE TABLE TICKETS (
  ID INTEGER PRIMARY KEY,
  CREATED_BY INTEGER NOT NULL,
  TITLE TEXT NOT NULL,
  RESPONSIBLE INTEGER,
  STATE INTEGER DEFAULT 0,
  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(CREATED_BY) REFERENCES USERS(ID),
  FOREIGN KEY(RESPONSIBLE) REFERENCES USERS(ID)
)
```

### MESSAGES Table
```sql
CREATE TABLE MESSAGES (
  ID INTEGER PRIMARY KEY,
  TICKET INTEGER,
  SENDER INTEGER NOT NULL,
  RECEIVER INTEGER NOT NULL,
  CONTENT TEXT,
  DELIVERED INTEGER DEFAULT 0,
  IMAGE_URL TEXT,
  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(TICKET) REFERENCES TICKETS(ID),
  FOREIGN KEY(SENDER) REFERENCES USERS(ID),
  FOREIGN KEY(RECEIVER) REFERENCES USERS(ID)
)
```

### TOKENS Table
```sql
CREATE TABLE TOKENS (
  ID INTEGER PRIMARY KEY,
  USER_ID INTEGER NOT NULL,
  TOKEN TEXT NOT NULL UNIQUE,
  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY(USER_ID) REFERENCES USERS(ID)
)
```

---

## 🆘 Error Handling

### Standard Error Response Format

```json
{
  "error": "Error message in English"
}
```

### HTTP Status Codes

| Code | Meaning | Common Cause |
|------|---------|-------------|
| 200 | OK | Successful request |
| 201 | Created | Successful creation |
| 204 | No Content | Successful logout |
| 400 | Bad Request | Invalid data |
| 401 | Unauthorized | Missing token |
| 403 | Forbidden | Invalid token |
| 404 | Not Found | Resource not found |
| 500 | Server Error | Server issue |

### Common Errors

#### "Token inválido"
**Cause:** Token expired or invalid  
**Solution:** Use refresh token to get a new access token

#### "Usuario no encontrado"
**Cause:** User does not exist  
**Solution:** Check username spelling

#### "Port 3001 already in use"
**Cause:** Another process using port 3001  
**Solution:** Change PORT in .env or kill the process

---

## 🔔 Push Notifications

### Firebase Cloud Messaging Setup

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project
3. Add Android app (or your platform)
4. Download serviceAccountKey.json
5. Place in project root
6. Update .env with Firebase credentials

### Sending Push Notifications

The API automatically sends push notifications when:
- New message is received
- Ticket is reassigned
- Ticket is closed

### On Client Side (Android Example)

```kotlin
import com.google.firebase.messaging.FirebaseMessaging

FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val token = task.result
        // Send this token to the API
        apiClient.updateFCMToken(userId, token)
    }
}
```

---

## 🔒 Security

### Best Practices

1. **Never commit .env or serviceAccountKey.json**
   ```
   # .gitignore
   .env
   serviceAccountKey.json
   ```

2. **Use strong JWT secrets**
   ```bash
   node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
   ```

3. **HTTPS in Production**
   - Use Let's Encrypt for free SSL certificates
   - Configure nginx or load balancer

4. **Database Security**
   - Backup regularly
   - Restrict access to production DB
   - Use strong credentials

5. **API Rate Limiting**
   ```javascript
   // Recommended: use express-rate-limit
   const rateLimit = require('express-rate-limit');
   const limiter = rateLimit({
     windowMs: 15 * 60 * 1000,
     max: 100
   });
   app.use(limiter);
   ```

6. **CORS Configuration**
   ```javascript
   const cors = require('cors');
   app.use(cors({
     origin: process.env.ALLOWED_ORIGINS
   }));
   ```

7. **Data Validation**
   - Validate all input
   - Sanitize strings
   - Check types

---

## 🚀 Deployment

### Option 1: Heroku

```bash
# Install Heroku CLI
# Create app
heroku create your-app-name

# Set environment variables
heroku config:set PORT=3001
heroku config:set ACCESS_TOKEN_SECRET=your_secret

# Deploy
git push heroku main

# View logs
heroku logs --tail
```

### Option 2: AWS EC2

```bash
# SSH into instance
ssh -i key.pem ubuntu@your-instance-ip

# Install Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Clone and setup
git clone your-repo
cd TicketsAPI
npm install
npm run db

# Start with PM2
npm install -g pm2
pm2 start index.js --name "tickets-api"
pm2 startup
pm2 save
```

### Option 3: Docker

```dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

ENV PORT=3001
EXPOSE 3001

CMD ["npm", "start"]
```

```bash
# Build and run
docker build -t tickets-api .
docker run -p 3001:3001 \
  -e ACCESS_TOKEN_SECRET=your_secret \
  tickets-api
```

### Option 4: DigitalOcean App Platform

1. Connect GitHub repository
2. Create App
3. Set environment variables
4. Deploy

---

## 🆘 Troubleshooting

### "Cannot GET /"
**Problem:** API did not start correctly  
**Solution:**
```bash
npm install
npm run db
npm run dev
```

### "nodemon: command not found"
**Problem:** nodemon not installed globally  
**Solution:**
```bash
npm install -g nodemon
# Or use directly:
npm run dev
```

### "Error: ENOENT: no such file or directory, open '.env'"
**Problem:** .env file not created  
**Solution:** Create .env file in root with credentials shown above

### "SQLITE_CANTOPEN"
**Problem:** Database file cannot be created  
**Solution:**
```bash
npm run db
```

### "Invalid token"
**Problem:** JWT token expired or malformed  
**Solution:** Use refresh token endpoint to get new access token:
```bash
curl -X POST http://localhost:3001/users/token \
  -H "Content-Type: application/json" \
  -d '{"token":"your_refresh_token"}'
```

### Performance Issues

If API is slow:
```bash
# Check CPU/Memory
top

# Add indexes to database
npm run db:index

# Enable caching
npm install redis

# Use load balancer
# nginx configuration
```

---

**Version:** 1.0.0  
**Last Updated:** January 2024  
**Author:** Victor Amaya
