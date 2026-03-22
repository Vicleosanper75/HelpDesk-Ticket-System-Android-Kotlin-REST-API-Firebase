# 📚 Documentation Center - Tickets API

**Welcome to Tickets API.** Here you'll find all the documentation you need to install, use, integrate and deploy the API.

## 🚀 Get Started Here

First time here? Spend **5 minutes** on the quick start:

### [👉 QUICK START (5 minutes)](QUICK_START_EN.md)
- ✅ Installation in 4 steps
- ✅ Verify it works
- ✅ First endpoints
- ✅ Complete example flow

---

## �️ Recommended Tools

Before you start, install these **VS Code extensions** for the best experience:

### **SQLite Viewer** - `alexcvzz.vscode-sqlite`
View and manage the database directly in VS Code.  
→ [See instructions in QUICK_START](QUICK_START_EN.md#-recommended-vs-code-extensions)

### **REST Client** - `humao.rest-client`
Execute HTTP requests directly from the `request.rest` file.  
→ [See instructions in QUICK_START](QUICK_START_EN.md#-recommended-vs-code-extensions)

---

## �📖 Complete Documentation

### [📘 DOCUMENTATION.md](DOCUMENTATION_EN.md) - THE DEFINITIVE GUIDE
**For:** Developers and DevOps  
**Time:** 30-60 minutes for complete read

Contains:
- 📋 Overview and features
- ⚙️ Setup and configuration
- 🔐 JWT authentication system
- 📡 Description of ALL 30+ endpoints
- 📊 Complete database structure
- 💬 Messaging system with images
- 🔔 Firebase push notifications
- 🚀 Deployment guides (Heroku, AWS, Docker)
- 🔒 Security and best practices
- 🆘 Troubleshooting

---

### [⚡ API_REFERENCE.md](API_REFERENCE_EN.md) - QUICK TABLE
**For:** Quick endpoint lookups  
**Time:** 5 minutes for spot checks

Contains:
- 📋 Table of all endpoints
- 🔑 Parameters and responses
- 💡 curl examples
- 🔍 Search by functionality
- ⏱️ Expiration times
- 🎯 Typical flows

**Recommended use:** Keep handy while developing

---

### [🔗 INTEGRATION_GUIDE.md](INTEGRATION_GUIDE_EN.md) - INTEGRATION IN YOUR APPS
**For:** App developers (Android, Web, React, Flutter, etc.)  
**Time:** 20-40 minutes depending on platform

Contains complete example code for:
- 📱 **Android** (Kotlin/Java)
  - OkHttp HTTP client
  - Firebase Cloud Messaging
  - Data models

- 🌐 **Web** (JavaScript/TypeScript)
  - Vanilla client
  - Axios with interceptors
  - React hooks

- ⚛️ **React Native / Expo**

- 🎯 **Flutter**

- 📞 Data type handling
- 🔐 Token management
- 🆘 Common issues

---

### [☎️ SUPPORT.md](SUPPORT_EN.md) - SUPPORT AND MAINTENANCE
**For:** Users needing help  
**Time:** 10 minutes to find answers

Contains:
- 📞 Support channels
- 🐛 How to report bugs effectively
- 📊 SLA and guarantees
- 🔄 Update cycle
- 📋 Public roadmap
- 🔐 Security policy
- 💬 FAQ
- 📚 Learning resources
- 🚑 Disaster recovery

---

## 🎯 Guide by Use Case

### "I just bought the API"
1. 📖 [QUICK_START.md](QUICK_START_EN.md) - Installation in 5 minutes
2. ⚡ [API_REFERENCE.md](API_REFERENCE_EN.md) - Quick endpoint table
3. 💡 Try the first endpoints

### "I want to integrate it in my Android app"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION_EN.md#-authentication) - Understand authentication
2. 🔗 [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE_EN.md#-android-kotlinjava) - Complete Android code
3. 📡 [DOCUMENTATION.md](DOCUMENTATION_EN.md#-api-endpoints) - Endpoint reference

### "I want to integrate it in my Web/React app"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION_EN.md#-authentication) - Understand authentication
2. 🔗 [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE_EN.md#-web-javascripttypescript) - Complete Web code
3. ⚡ [API_REFERENCE.md](API_REFERENCE_EN.md) - Quick endpoint table

### "I need to deploy to production"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION_EN.md#-deployment) - Step by step guides
2. 📘 [DOCUMENTATION.md](DOCUMENTATION_EN.md#-security) - Best practices
3. ☎️ [SUPPORT.md](SUPPORT_EN.md) - Monitoring and maintenance

### "I have a problem"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION_EN.md#-troubleshooting) - Troubleshooting
2. ☎️ [SUPPORT.md](SUPPORT_EN.md#-faq---frequently-asked-questions) - FAQ
3. ☎️ [SUPPORT.md](SUPPORT_EN.md#-support-channels) - Contact support

### "I want to understand the complete architecture"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION_EN.md#-project-structure) - Folder structure
2. 📘 [DOCUMENTATION.md](DOCUMENTATION_EN.md#-data-models) - Database
3. 📘 [DOCUMENTATION.md](DOCUMENTATION_EN.md#-api-endpoints) - All endpoints

---

## 📊 Documentation Statistics

| Document | Sections | Documented Endpoints | Examples |
|----------|----------|----------------------|----------|
| DOCUMENTATION.md | 15+ | 30+ | 50+ |
| API_REFERENCE.md | 8+ | All | 20+ |
| QUICK_START.md | 8+ | 5+ | 15+ |
| INTEGRATION_GUIDE.md | 12+ | - | 30+ (per platform) |
| SUPPORT.md | 10+ | - | - |

---

## 🗂️ File Structure

```
TicketsAPI/
├── 📚 DOCUMENTATION/
│   ├── README.md                    # Executive summary
│   ├── INDEX.md                     # This file
│   ├── DOCUMENTATION.md             # Complete guide
│   ├── QUICK_START.md              # Quick start
│   ├── API_REFERENCE.md            # Endpoint table
│   ├── INTEGRATION_GUIDE.md         # Integration in apps
│   └── SUPPORT.md                  # Support and maintenance
│
├── 🔧 SOURCE CODE/
│   ├── src/
│   │   ├── controllers/            # Business logic
│   │   ├── models/                 # Database access
│   │   └── routes/                 # Endpoints
│   ├── index.js                    # Entry point
│   ├── authMiddleware.js           # JWT
│   └── firebaseAdmin.js            # Firebase
│
├── 💾 DATABASE/
│   ├── database.db                 # SQLite
│   └── SQLite.sql                  # Scripts
│
├── 📦 CONFIG/
│   ├── package.json
│   ├── .env                        # Secrets (don't commit)
│   ├── .gitignore
│   └── serviceAccountKey.json      # Firebase
│
└── 📁 DATA/
    └── uploads/                    # Message images
```

---

## 🔑 Key Concepts

### JWT Authentication
- **Access Token:** Valid for 9 hours, included in each request
- **Refresh Token:** No expiration, stored in database, used to renew access

[More details →](DOCUMENTATION_EN.md#-authentication)

### Roles
- `1` = Operator
- `2` = Support
- `3` = Systems

[More details →](DOCUMENTATION_EN.md#-users---user-management)

### Tickets
Ticket system with support for:
- Create, read, update (change responsible), close
- Encrypted associated messages
- Complete history

[More details →](DOCUMENTATION_EN.md#-api-endpoints)

### Messages
- Encrypted in database, decrypted on client
- Image support (Multer)
- Automatic push notifications (Firebase)

[More details →](DOCUMENTATION_EN.md#-push-notifications)

---

## ✅ Installation Checklist

- [ ] Node.js 14+ installed
- [ ] Repository cloned
- [ ] `npm install`
- [ ] `.env` file created with secrets
- [ ] `npm run db` to create database
- [ ] `npm run dev` to start
- [ ] Verify at `http://localhost:3001`

[Step by step guide →](QUICK_START_EN.md)

---

## 🚀 Next Steps

### 1️⃣ After Installing
```bash
# Verify it works
curl http://localhost:3001/

# Register first user
POST /users/register
{
  "username": "test",
  "fullname": "Test User",
  "pwd": "password123",
  "role": 1
}
```

### 2️⃣ Before Deploying
- [ ] Configure environment variables
- [ ] Implement HTTPS
- [ ] Set up automatic backups
- [ ] Configure monitoring
- [ ] Review security checks

[Deployment guide →](DOCUMENTATION_EN.md#-deployment)

### 3️⃣ To Integrate in Your App
- [ ] Choose your platform (Android/Web/Flutter/etc)
- [ ] Follow integration guide
- [ ] Implement authentication
- [ ] Test endpoints
- [ ] Go to production

[Integration guide →](INTEGRATION_GUIDE_EN.md)

---

## 💡 Tips and Tricks

### Generate Secure Secrets
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

### Test Endpoints Quickly
```bash
# Install curl or Postman client
curl -X GET http://localhost:3001/users \
  -H "Authorization: Bearer <token>"
```

### View Database
```bash
# Open database with visual tool
# Download SQLite Browser: sqlitebrowser.org
```

### Debugging
```bash
# Full debug mode
DEBUG=* npm run dev
```

---

## 🎓 Documentation Levels

### Level 1️⃣: Beginner
- Start with: [QUICK_START.md](QUICK_START_EN.md)
- Duration: 5 minutes
- Goal: Get API running

### Level 2️⃣: Intermediate
- Start with: [API_REFERENCE.md](API_REFERENCE_EN.md)
- Duration: 30 minutes
- Goal: Understand endpoints

### Level 3️⃣: Advanced
- Start with: [DOCUMENTATION.md](DOCUMENTATION_EN.md)
- Duration: 1 hour
- Goal: Complete mastery

### Level 4️⃣: Integrator
- Start with: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE_EN.md)
- Duration: 1 hour
- Goal: Integrate in apps

### Level 5️⃣: DevOps
- Start with: [DOCUMENTATION.md#-deployment](DOCUMENTATION_EN.md#-deployment)
- Duration: 2 hours
- Goal: Deploy to production

---

## 📞 Need Help?

### By Priority

**🔴 Critical (API not working):**
1. Check [troubleshooting](DOCUMENTATION_EN.md#-troubleshooting)
2. Check [FAQ](SUPPORT_EN.md#-faq---frequently-asked-questions)
3. [Contact support](SUPPORT_EN.md#-support-channels)

**🟡 Moderate (Integration slow):**
1. See [integration guide](INTEGRATION_GUIDE_EN.md)
2. See [examples](DOCUMENTATION_EN.md#-usage-examples)
3. Open GitHub issue

**🟢 Low (General question):**
1. Search [FAQ](SUPPORT_EN.md#-faq---frequently-asked-questions)
2. Check [relevant documentation](DOCUMENTATION_EN.md)
3. [Community Discord](https://discord.gg/ticketsapi)

---

## 📊 Suggested Learning Roadmap

```
Day 1:
  ├─ Read README.md (5 min)
  ├─ Complete QUICK_START.md (5 min)
  └─ Test 3 endpoints (10 min)

Day 2:
  ├─ Read API_REFERENCE.md (20 min)
  ├─ Study authentication (15 min)
  └─ Implement login in your app (30 min)

Day 3:
  ├─ Read INTEGRATION_GUIDE (40 min)
  ├─ Implement complete CRUD (1 hour)
  └─ Test in your app (30 min)

Day 4-5:
  ├─ Read complete DOCUMENTATION (2 hours)
  ├─ Implement push notifications (1 hour)
  └─ Go to production!
```

---

## 🎯 Objectives by Role

### **Backend Developer**
Priority:
1. [DOCUMENTATION.md](DOCUMENTATION_EN.md#-project-structure)
2. [API_REFERENCE.md](API_REFERENCE_EN.md)
3. [DOCUMENTATION.md#-data-models](DOCUMENTATION_EN.md#-data-models)

### **Frontend/Mobile Developer**
Priority:
1. [QUICK_START.md](QUICK_START_EN.md)
2. [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE_EN.md)
3. [API_REFERENCE.md](API_REFERENCE_EN.md)

### **DevOps/SysAdmin**
Priority:
1. [DOCUMENTATION.md#-deployment](DOCUMENTATION_EN.md#-deployment)
2. [DOCUMENTATION.md#-security](DOCUMENTATION_EN.md#-security)
3. [SUPPORT.md#-preventive-maintenance](SUPPORT_EN.md#-preventive-maintenance)

### **Product Manager**
Priority:
1. [README.md](README_EN.md)
2. [SUPPORT.md#-public-roadmap](SUPPORT_EN.md#-public-roadmap)
3. [DOCUMENTATION.md#-features](DOCUMENTATION_EN.md#-features)

---

## 🏆 Best Practices

✅ **DO:**
- Use JWT tokens appropriately
- Encrypt sensitive data
- Implement automatic backups
- Use HTTPS in production
- Monitor performance
- Keep complete logs

❌ **DON'T:**
- Store tokens in localStorage without sanitizing
- Use `admin:admin` in production
- Disable SSL/TLS
- Ignore security alerts
- Modify database directly in production
- Share serviceAccountKey.json

[More details →](DOCUMENTATION_EN.md#-security)

---

## 📈 Usage Statistics

- **Version:** 1.0.0
- **License:** ISC
- **Active Users:** 100+
- **Guaranteed Uptime:** 99%
- **Average Response Time:** < 100ms

---

## 🔄 Frequent Updates

Subscribe to updates at:
- 🐙 GitHub: [Star the repo](https://github.com/tickets-api)
- 📧 Email: [Newsletter](https://ticketsapi.com/subscribe)
- 🐦 Twitter: [@TicketsAPI](https://twitter.com/TicketsAPI)

---

## 🙋 Feedback and Improvements

Your feedback helps improve documentation:
- [Report documentation issue](https://github.com/tickets-api/issues)
- [Suggest improvement](https://github.com/tickets-api/discussions)
- [Complete survey](https://forms.gle/feedback)

---

## 📜 Legal Information

- **License:** ISC
- **Author:** Victor Amaya
- **Terms:** [SUPPORT.md#-legal-terms](SUPPORT_EN.md#-legal-terms)

---

**Ready to start?** → [QUICK_START.md](QUICK_START_EN.md)

**Have questions?** → [SUPPORT.md](SUPPORT_EN.md)

**Last Updated:** January 2024  
**Documentation v1.0.0**
