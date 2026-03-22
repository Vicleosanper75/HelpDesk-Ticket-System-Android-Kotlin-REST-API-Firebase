# 🎟️ Tickets API

A complete backend solution in **Node.js/Express** for managing a ticket system with real-time messaging, JWT authentication, and push notifications integrated with Firebase.

![Node.js](https://img.shields.io/badge/Node.js-18+-green)
![Express](https://img.shields.io/badge/Express-5.1+-lightblue)
![SQLite](https://img.shields.io/badge/SQLite-3-blue)
![License](https://img.shields.io/badge/License-ISC-yellow)

## 📋 Main Features

✅ **User Management** - Registration, login, roles with JWT  
✅ **Ticket System** - Complete CRUD with responsible assignment  
✅ **Messaging** - Chat with encrypted image support  
✅ **Push Notifications** - Firebase Cloud Messaging integrated  
✅ **SQLite Database** - Relational persistence  
✅ **JWT Authentication** - Access tokens + Refresh tokens  
✅ **Status Control** - Online/offline user tracking  
✅ **File Upload** - Multer for image management  

## 🚀 Quick Start

### 1. Installation
```bash
# Clone repository
git clone <your-repo>
cd TicketsAPI

# Install dependencies
npm install

# Create database
npm run db
```

### 2. Configuration
Create `.env` file:
```env
PORT=3001
ACCESS_TOKEN_SECRET=your_super_long_random_secret
REFRESH_TOKEN_SECRET=your_refresh_super_long_random_secret
```

### 3. Run
```bash
# Development mode (with nodemon)
npm run dev

# Production mode
npm start
```

The API will be available at `http://localhost:3001`

## �️ Recommended VS Code Extensions

For an optimal development experience, install these extensions:

### **SQLite Viewer**
- **ID:** `alexcvzz.vscode-sqlite`
- **Function:** View and manage SQLite database directly in VS Code
- **Installation:** 
  1. Open VS Code
  2. Go to Extensions (Ctrl + Shift + X)
  3. Search for "SQLite Viewer"
  4. Click "Install"
- **Usage:** Right-click on `database.db` → "Open with SQLite Viewer"

### **REST Client**
- **ID:** `humao.rest-client`
- **Function:** Execute HTTP requests directly from the `request.rest` file
- **Installation:**
  1. Open VS Code
  2. Go to Extensions (Ctrl + Shift + X)
  3. Search for "REST Client"
  4. Click "Install"
- **Usage:** Open `request.rest` and you'll see "Send Request" ▶ buttons above each endpoint

**Benefits:**
- 📊 View table structure in real-time
- 🧪 Test requests without external tools
- ⚡ Autocompletion and syntax highlighting
- 🔍 View formatted JSON responses

## �📚 Full Documentation

**→ [VIEW COMPLETE DOCUMENTATION](DOCUMENTATION_EN.md)**

The complete documentation includes:
- ✅ Authentication and token flows
- ✅ **Description of all 30+ endpoints** with examples
- ✅ Complete database structure
- ✅ cURL and JSON examples
- ✅ Error handling
- ✅ Push notification configuration
- ✅ Deployment guide (Heroku, AWS, Docker)
- ✅ Security best practices
- ✅ Common troubleshooting

## 🔐 Main Endpoints (summary)

### Users
- `POST /users/register` - Register new user
- `POST /users/login` - Login (get tokens)
- `POST /users/token` - Refresh access token
- `GET /users` - List all users
- `GET /users/:id` - Get user by ID

### Tickets
- `POST /tickets/createTicket` - Create new ticket
- `GET /tickets/:id` - Get ticket
- `PUT /tickets/concludeTicket` - Close ticket
- `PUT /tickets/reassignTicket` - Reassign responsible

### Messages
- `POST /messages/sendMessage` - Send message (with images)
- `GET /messages/getChats/:id` - Get recent conversations
- `GET /messages/getMessages` - Get chat history
- `PUT /messages/markAsRead` - Mark as read

[**View all endpoints →**](DOCUMENTATION_EN.md#-api-endpoints)

## 📦 Tech Stack

| Module | Version | Purpose |
|--------|---------|---------|
| express | 5.1.0 | Web framework |
| sqlite3 | 5.1.7 | Database |
| bcryptjs | 3.0.2 | Password encryption |
| jsonwebtoken | 9.0.2 | JWT authentication |
| firebase-admin | 13.6.0 | Push notifications |
| multer | 2.0.2 | File upload |
| socket.io | 4.8.1 | WebSockets (optional) |
| dotenv | 17.0.1 | Environment variables |

## 🏗️ Project Structure

```
TicketsAPI/
├── src/
│   ├── controllers/       # Business logic
│   ├── models/            # Database access
│   └── routes/            # Endpoint definitions
├── uploads/               # Uploaded images
├── authMiddleware.js      # JWT authentication
├── firebaseAdmin.js       # Firebase config
├── index.js              # Entry point
├── DOCUMENTATION_EN.md   # Complete documentation
├── .env                  # Environment variables
└── package.json
```

## 🛠️ Development

```bash
# Development mode (restarts with nodemon)
npm run dev

# Create/recreate database
npm run db

# Production mode
npm start
```

## 🚀 Deployment

Complete guides for:
- **Heroku** - Automatic deployment from GitHub
- **AWS EC2** - Instance with PM2
- **Docker** - Containerization
- **DigitalOcean** - App Platform

[View deployment guide →](DOCUMENTATION_EN.md#-deployment)

## ⚙️ Firebase Configuration (Push Notifications)

1. Get `serviceAccountKey.json` from Firebase Console
2. Place in project root
3. Environment variables automatically configured

[Detailed setup →](DOCUMENTATION_EN.md#-push-notifications)

## 🔒 Security

- ✅ Passwords hashed with bcrypt
- ✅ Encrypted messages in database
- ✅ JWT with expiration
- ✅ SQL prepared statements (no injection)
- ✅ Input validation

Additional recommendations:
[View best practices →](DOCUMENTATION_EN.md#-security)

## 🆘 Troubleshooting

**"Cannot find module 'sqlite3'"**
```bash
npm install sqlite3 --build-from-source
```

**"Port 3001 already in use"**
```bash
# Change in .env: PORT=3002
```

**More issues:**
[View complete troubleshooting →](DOCUMENTATION_EN.md#-solution-of-problems)

## 📖 Available Documentation

| Document | Content | Target Audience |
|----------|---------|-----------------|
| [README_EN.md](README_EN.md) | Executive summary | Everyone |
| [DOCUMENTATION_EN.md](DOCUMENTATION_EN.md) | Complete reference | Developers |
| [QUICK_START_EN.md](QUICK_START_EN.md) | 5-minute setup | New users |
| [API_REFERENCE_EN.md](API_REFERENCE_EN.md) | Quick endpoints | Developers |
| [INTEGRATION_GUIDE_EN.md](INTEGRATION_GUIDE_EN.md) | App integration | Integrators |
| [SUPPORT_EN.md](SUPPORT_EN.md) | Support and maintenance | Everyone |
| [INDEX_EN.md](INDEX_EN.md) | Documentation hub | Everyone |

## 📞 Support

- 📖 [View complete documentation](DOCUMENTATION_EN.md)
- 🐛 Report bugs using GitHub Issues
- 💬 Questions in Discussions
- ☎️ [Support channels](SUPPORT_EN.md)

## 📄 Author and License

**Author:** Victor Amaya  
**License:** ISC  
**Version:** 1.0.0

---

**First time here?** → [Start here](QUICK_START_EN.md)  
**Already have tokens?** → [Quick reference](API_REFERENCE_EN.md)  
**Want to deploy?** → [Deployment guide](DOCUMENTATION_EN.md#-deployment)
