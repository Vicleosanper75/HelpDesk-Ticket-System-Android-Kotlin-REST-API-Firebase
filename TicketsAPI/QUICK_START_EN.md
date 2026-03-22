# 🚀 Quick Start Guide

Just acquired **Tickets API**? Great! You'll have the API running in 5 minutes.

## ⏱️ 5 Minutes of Setup

### Step 1: Install Node.js (if you don't have it)
Download from [nodejs.org](https://nodejs.org) - LTS Version (18+)

### Step 2: Install dependencies
```bash
npm install
```

### Step 3: Create .env file
In the project root, create a `.env` file:

```env
PORT=3001
ACCESS_TOKEN_SECRET=MyVeryLongAndRandomSecretForAccessToken123456789
REFRESH_TOKEN_SECRET=MyVeryLongAndRandomSecretForRefreshToken123456789
```

⚠️ **IMPORTANT:** Secrets must be long and random values. You can generate one here:
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

### Step 4: Create database
```bash
npm run db
```

### Step 5: Start server
```bash
npm run dev
```

You should see:
```
http://localhost:3001
```

✅ **Done!** Your API is now running.

---

## 🧪 Verify It Works

### Option A: With cURL (Terminal)

```bash
# Basic test
curl http://localhost:3001/
# You should see: "Hello World!!!"
```

### Option B: With Postman

1. Download [Postman](https://postman.com)
2. Create new request
3. URL: `http://localhost:3001/`
4. Click "Send"

### Option C: With our request.rest file

If you use VS Code with REST Client extension:
1. Open `request.rest`
2. Click "Send Request"

---

## � Recommended VS Code Extensions

For an optimal development experience, install these extensions in VS Code:

### **1. SQLite Viewer** 📊
- **ID:** `alexcvzz.vscode-sqlite`
- **Purpose:** View and explore the database directly in VS Code
- **Installation:**
  - Open VS Code → Extensions (Ctrl + Shift + X)
  - Search for "SQLite Viewer"
  - Click "Install"
- **How to use:** Right-click on `database.db` → "Open with SQLite Viewer"
- **Benefits:** See all tables, data, and relationships in real-time without external tools

### **2. REST Client** 🧪
- **ID:** `humao.rest-client`
- **Purpose:** Make HTTP requests directly from `request.rest`
- **Installation:**
  - Open VS Code → Extensions (Ctrl + Shift + X)
  - Search for "REST Client"
  - Click "Install"
- **How to use:** Open `request.rest` and you'll see "▶ Send Request" buttons above each endpoint
- **Benefits:** Test endpoints without leaving VS Code, formatted responses

**With these two extensions you have everything you need for complete development!**

---

## �📝 First Steps: Register and Login

### 1️⃣ Register a user

```bash
curl -X POST http://localhost:3001/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "my_user",
    "fullname": "My Full Name",
    "pwd": "MyPassword123!",
    "role": 1
  }'
```

**Expected response:**
```json
{
  "id": 1
}
```

### 2️⃣ Login

```bash
curl -X POST http://localhost:3001/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "my_user",
    "pwd": "MyPassword123!"
  }'
```

**Expected response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Save these tokens**, you'll need them for all following requests.

### 3️⃣ Use the token to access protected API

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:3001/users \
  -H "Authorization: Bearer $TOKEN"
```

**You should see the list of users.**

---

## 📦 Available User Roles

| ID | Name | Description |
|----|------|------------|
| 1 | Operator | Standard user |
| 2 | Support | Support team |
| 3 | Systems | Administrator |

---

## 🎟️ Create Your First Ticket

```bash
TOKEN="your_token_here"

curl -X POST http://localhost:3001/tickets/createTicket \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "createdBy": 1,
    "title": "My first ticket",
    "responsible": 2
  }'
```

**Response:**
```json
{
  "ID": 1
}
```

---

## 💬 Send Your First Message

### Text Message
```bash
TOKEN="your_token_here"

curl -X POST http://localhost:3001/messages/sendMessage \
  -H "Authorization: Bearer $TOKEN" \
  -F "ticket=1" \
  -F "sender=1" \
  -F "receiver=2" \
  -F "content=Hello, I need help with this ticket"
```

### Message with Image
```bash
TOKEN="your_token_here"

curl -X POST http://localhost:3001/messages/sendMessage \
  -H "Authorization: Bearer $TOKEN" \
  -F "ticket=1" \
  -F "sender=1" \
  -F "receiver=2" \
  -F "content=Look at the error in this image" \
  -F "image=@/path/to/your/image.png"
```

---

## 🔄 Complete Flow Example

```bash
# 1. Register User A
curl -X POST http://localhost:3001/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","fullname":"Alice","pwd":"password123","role":1}'
# Response: {"id": 1}

# 2. Register User B
curl -X POST http://localhost:3001/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"bob","fullname":"Bob","pwd":"password123","role":2}'
# Response: {"id": 2}

# 3. Alice logs in
curl -X POST http://localhost:3001/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","pwd":"password123"}'
# Response: {"accessToken":"TOKEN_A","refreshToken":"REFRESH_A"}

# 4. Alice creates a ticket assigned to Bob
TOKEN_A="TOKEN_HERE"
curl -X POST http://localhost:3001/tickets/createTicket \
  -H "Authorization: Bearer $TOKEN_A" \
  -H "Content-Type: application/json" \
  -d '{"createdBy":1,"title":"Bug in login","responsible":2}'
# Response: {"ID": 1}

# 5. Alice sends a message to Bob
curl -X POST http://localhost:3001/messages/sendMessage \
  -H "Authorization: Bearer $TOKEN_A" \
  -F "ticket=1" \
  -F "sender=1" \
  -F "receiver=2" \
  -F "content=Please review this ticket"

# 6. Bob logs in
curl -X POST http://localhost:3001/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"bob","pwd":"password123"}'
# Response: {"accessToken":"TOKEN_B","refreshToken":"REFRESH_B"}

# 7. Bob gets his chat list
TOKEN_B="TOKEN_HERE"
curl -X GET http://localhost:3001/messages/getChats/2 \
  -H "Authorization: Bearer $TOKEN_B"
# Response: shows Alice's message

# 8. Bob gets the messages from the ticket
curl -X GET "http://localhost:3001/messages/getMessages?ticket=1&user=2&contact=1" \
  -H "Authorization: Bearer $TOKEN_B"

# 9. Bob marks the ticket as concluded
curl -X PUT http://localhost:3001/tickets/concludeTicket \
  -H "Authorization: Bearer $TOKEN_B" \
  -H "Content-Type: application/json" \
  -d '{"id":1}'
```

---

## 🆘 Common Issues

### "Cannot GET /"
**Problem:** The API didn't start correctly  
**Solution:**
```bash
npm install
npm run db
npm run dev
```

### "nodemon: command not found"
**Problem:** nodemon is not installed globally  
**Solution:**
```bash
npm install -g nodemon
# Or use directly:
npm run dev
```

### "Error: ENOENT: no such file or directory, open '.env'"
**Problem:** You didn't create the .env file  
**Solution:** Create a .env file in the root with the content shown above

### "Port 3001 already in use"
**Problem:** Another process is using port 3001  
**Solution:** Change in .env:
```env
PORT=3002
```

### "Invalid token"
**Problem:** The token expired (9 hours duration)  
**Solution:** Use the refresh token to get a new one:
```bash
curl -X POST http://localhost:3001/users/token \
  -H "Content-Type: application/json" \
  -d '{"token":"your_refresh_token"}'
```

---

## 📚 Complete Documentation

For detailed information about:
- ✅ All 30+ endpoints
- ✅ Complete database structure
- ✅ Push notifications with Firebase
- ✅ Production deployment
- ✅ Security best practices

**→ [VIEW COMPLETE DOCUMENTATION](DOCUMENTATION_EN.md)**

---

## 🎯 Next Steps

### For Local Development
1. ✅ API running
2. ⏭️ Connect your Android/Web app to the API
3. ⏭️ Implement authentication (save tokens)
4. ⏭️ Create user interface

### For Production
1. ✅ API running locally
2. ⏭️ Configure Firebase for push notifications
3. ⏭️ Choose hosting platform (Heroku, AWS, Docker)
4. ⏭️ Read [deployment guide](DOCUMENTATION_EN.md#-deployment)
5. ⏭️ Configure HTTPS and domain

### For Customization
1. Modify fields in database (`SQLite.sql`)
2. Update models in `src/models/`
3. Add new routes in `src/routes/`
4. Implement logic in `src/controllers/`

---

## 📞 Need Help?

- 📖 [See complete documentation](DOCUMENTATION_EN.md)
- 🐛 [Troubleshooting](DOCUMENTATION_EN.md#-troubleshooting)
- 💡 [Usage examples](DOCUMENTATION_EN.md#-usage-examples)

---

**Enjoy the API! 🎉**
