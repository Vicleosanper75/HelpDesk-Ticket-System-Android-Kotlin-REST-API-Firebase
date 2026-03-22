# 📋 Quick Endpoint Reference

Quick table of all available endpoints in Tickets API.

## 🔐 Authentication Required

✅ All endpoints EXCEPT these require:
```
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

**Endpoints WITHOUT authentication:**
- `POST /users/register`
- `POST /users/login`

---

## 👥 USERS

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| POST | `/users/register` | Create user | ❌ No |
| POST | `/users/login` | Login (get tokens) | ❌ No |
| POST | `/users/token` | Refresh access token | ❌ No |
| POST | `/users/logout` | Logout (delete refresh token) | ✅ Yes |
| POST | `/users/clearTokens` | Clear all tokens | ✅ Yes |
| GET | `/users/roles` | Get available roles | ✅ Yes |
| GET | `/users` | List all users | ✅ Yes |
| GET | `/users/:id` | Get user by ID | ✅ Yes |
| POST | `/users/update/:id` | Update user | ✅ Yes |
| POST | `/users/updateToken` | Update FCM token | ✅ Yes |
| POST | `/users/setOnlineStatus/:id` | Change online status | ✅ Yes |
| POST | `/users/delete/:id` | Delete user | ✅ Yes |
| POST | `/users/reset-password` | Reset password | ✅ Yes |

### 📝 Quick Examples

```bash
# Register
POST /users/register
{
  "username": "juan",
  "fullname": "Juan Pérez",
  "pwd": "password123",
  "role": 1
}

# Login
POST /users/login
{
  "username": "juan",
  "pwd": "password123"
}

# Get all
GET /users

# Get by ID
GET /users/5

# Update
POST /users/update/5
{
  "fullname": "Juan Pérez García"
}

# FCM Token (for push notifications)
POST /users/updateToken
{
  "userId": 5,
  "fcmToken": "fHkfJK3jk2n3jk2nj3k2nj32kn2kn3..."
}

# Online status
POST /users/setOnlineStatus/5
{
  "online": 1
}
```

---

## 🎟️ TICKETS

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| POST | `/tickets/createTicket` | Create new ticket | ✅ Yes |
| GET | `/tickets/:id` | Get ticket by ID | ✅ Yes |
| PUT | `/tickets/concludeTicket` | Close/conclude ticket | ✅ Yes |
| PUT | `/tickets/reassignTicket` | Reassign responsible | ✅ Yes |

### 📝 Quick Examples

```bash
# Create ticket
POST /tickets/createTicket
{
  "createdBy": 1,
  "title": "Resolve inventory problem",
  "responsible": 2
}

# Get by ID
GET /tickets/5

# Conclude (close)
PUT /tickets/concludeTicket
{
  "id": 5
}

# Reassign to another user
PUT /tickets/reassignTicket
{
  "id": 5,
  "newResponsibleId": 3
}
```

---

## 💬 MESSAGES

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| POST | `/messages/sendMessage` | Send message (+ images) | ✅ Yes |
| POST | `/messages/markDelivered/:id` | Mark message delivered | ✅ Yes |
| GET | `/messages/getChats/:id` | Get last conversations | ✅ Yes |
| GET | `/messages/getMessages` | Get chat history | ✅ Yes |
| PUT | `/messages/markAsRead` | Mark messages as read | ✅ Yes |
| GET | `/messages/getConcludedTickets` | Get concluded tickets | ✅ Yes |

### 📝 Quick Examples

```bash
# Send text message
POST /messages/sendMessage
{
  "ticket": 5,
  "sender": 1,
  "receiver": 2,
  "content": "Hello, how are you?"
}

# Send message with image (multipart)
POST /messages/sendMessage
Form Data:
  - ticket: 5
  - sender: 1
  - receiver: 2
  - content: "Look at this error"
  - image: <file>

# Get chats (latest conversations)
GET /messages/getChats/1

# Get messages from a chat
GET /messages/getMessages?ticket=5&user=1&contact=2

# Mark as read
PUT /messages/markAsRead
{
  "ticketId": 5,
  "userId": 1
}

# Mark as delivered
POST /messages/markDelivered/123

# Get concluded tickets (paginated)
GET /messages/getConcludedTickets?userId=1&page=1&limit=15
```

---

## 🔑 RESPONSE STATES

| Code | Meaning |
|------|---------|
| 200 | OK - Successful request |
| 201 | Created - Resource created |
| 204 | No Content - No content (logout) |
| 400 | Bad Request - Invalid data |
| 401 | Unauthorized - Missing token |
| 403 | Forbidden - Invalid token |
| 404 | Not Found - Resource doesn't exist |
| 500 | Server Error - Server issue |

---

## 🔄 COMMON PARAMETERS

### Bearer Token (Header)
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Roles
- `1` = Operator
- `2` = Support  
- `3` = Systems

### Ticket States
- `0` = Open
- `1` = Closed

### Online Status
- `0` = Offline
- `1` = Online

### DELIVERED (Message)
- `0` = Not delivered
- `1` = Delivered

---

## 📤 HEADERS

### JSON Requests
```
Content-Type: application/json
Authorization: Bearer <TOKEN>
```

### Multipart Requests (File upload)
```
Authorization: Bearer <TOKEN>
Content-Type: multipart/form-data
```
(Automatically configured with -F in curl)

---

## 💡 SECRETS

### Generate new SECRET
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

### Environment variables (.env)
```env
PORT=3001
ACCESS_TOKEN_SECRET=<long_and_random_secret>
REFRESH_TOKEN_SECRET=<long_and_random_secret>
```

---

## 🗂️ DIRECTORY STRUCTURE

```
/uploads/          # Uploaded message images
.env               # Environment variables
DOCUMENTATION.md   # Complete documentation
QUICK_START.md     # Quick start guide
API_REFERENCE.md   # This file
```

---

## ⏱️ EXPIRATION TIMES

| Token | Expiration |
|-------|------------|
| Access Token | 9 hours |
| Refresh Token | No limit (but stored in database) |

### Refresh Access Token
```bash
POST /users/token
{
  "token": "<REFRESH_TOKEN>"
}
```

---

## 🎯 TYPICAL FLOW

```
1. POST /users/register
   └─ Create new user

2. POST /users/login
   └─ Get accessToken + refreshToken

3. POST /tickets/createTicket
   ├─ Create ticket
   └─ Save ticket ID

4. POST /messages/sendMessage
   ├─ Send message related to ticket
   └─ Can include image

5. GET /messages/getChats/:id
   └─ See recent conversations

6. PUT /tickets/concludeTicket
   └─ Close ticket when resolved

7. POST /users/logout
   └─ Logout (removes refresh token)
```

---

## 🔍 QUICK SEARCH

### "I want to..."

#### ...register a user
`POST /users/register` - [See example](#-quick-examples)

#### ...login
`POST /users/login` - [See example](#-quick-examples)

#### ...create a ticket
`POST /tickets/createTicket` - [See example](#-quick-examples)

#### ...send a message
`POST /messages/sendMessage` - [See example](#-quick-examples)

#### ...send an image
`POST /messages/sendMessage` (multipart) - [See example](#-quick-examples)

#### ...close a ticket
`PUT /tickets/concludeTicket` - [See example](#-quick-examples)

#### ...reassign a ticket
`PUT /tickets/reassignTicket` - [See example](#-quick-examples)

#### ...get my chats
`GET /messages/getChats/:id` - [See example](#-quick-examples)

#### ...see chat history
`GET /messages/getMessages?ticket=X&user=Y&contact=Z` - [See example](#-quick-examples)

#### ...refresh my token
`POST /users/token` - [See expiration section](#-expiration-times)

#### ...logout
`POST /users/logout` - [See example](#-quick-examples)

---

## 📖 COMPLETE DOCUMENTATION

→ [DOCUMENTATION.md](DOCUMENTATION_EN.md) - Comprehensive guide with:
- Full setup and configuration
- Detailed JWT authentication
- All endpoints fully documented
- curl examples
- Database structure
- Push notifications
- Deployment
- Security

→ [QUICK_START.md](QUICK_START_EN.md) - Quick start in 5 minutes

---

**Last Updated:** January 2024
