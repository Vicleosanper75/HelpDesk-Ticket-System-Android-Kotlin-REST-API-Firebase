# 🔗 Integration Guide - Tickets API

How to integrate **Tickets API** in your applications (Android, Web, Flutter, etc.)

---

## 📱 Android (Kotlin/Java)

### 1. Add Dependencies (build.gradle)

```gradle
dependencies {
    // HTTP Client
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    
    // JSON parsing
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // JWT (optional)
    implementation 'com.auth0:java-jwt:4.4.0'
    
    // Firebase (for push notifications)
    implementation 'com.google.firebase:firebase-messaging:23.2.1'
}
```

### 2. Create HTTP Client

```kotlin
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson

class TicketsAPIClient {
    private val baseUrl = "http://IP:3001" // IP of your server
    private val client = OkHttpClient()
    private val gson = Gson()
    private var accessToken: String? = null
    private var refreshToken: String? = null
    
    // Login
    fun login(username: String, password: String, callback: (String?, String?) -> Unit) {
        val json = """{"username":"$username","pwd":"$password"}"""
        val body = json.toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url("$baseUrl/users/login")
            .post(body)
            .build()
        
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, null)
            }
            
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val json = gson.fromJson(responseBody, Map::class.java)
                
                accessToken = json["accessToken"] as? String
                refreshToken = json["refreshToken"] as? String
                
                callback(accessToken, refreshToken)
            }
        })
    }
    
    // Get users
    fun getUsers(callback: (List<User>?) -> Unit) {
        val request = Request.Builder()
            .url("$baseUrl/users")
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()
        
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
            
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val users = gson.fromJson(responseBody, Array<User>::class.java).toList()
                callback(users)
            }
        })
    }
    
    // Send message
    fun sendMessage(
        ticket: Int?,
        sender: Int,
        receiver: Int,
        content: String,
        imageFile: File?,
        callback: (Int?) -> Unit
    ) {
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("ticket", ticket?.toString() ?: "null")
            .addFormDataPart("sender", sender.toString())
            .addFormDataPart("receiver", receiver.toString())
            .addFormDataPart("content", content)
        
        imageFile?.let {
            multipartBody.addFormDataPart(
                "image",
                it.name,
                it.asRequestBody("image/*".toMediaType())
            )
        }
        
        val request = Request.Builder()
            .url("$baseUrl/messages/sendMessage")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(multipartBody.build())
            .build()
        
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }
            
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val json = gson.fromJson(responseBody, Map::class.java)
                val messageId = (json["id"] as? Double)?.toInt()
                callback(messageId)
            }
        })
    }
}

// Models
data class User(
    val ID: Int,
    val USER_NAME: String,
    val FULL_NAME: String,
    val ONLINE: Int,
    val ROLE: Int,
    val ROLE_NAME: String
)

data class Ticket(
    val ID: Int,
    val CREATED_BY: Int,
    val TITLE: String,
    val RESPONSIBLE: Int,
    val STATE: Int,
    val CREATED_AT: String
)

data class Message(
    val ID: Int,
    val SENDER: Int,
    val RECEIVER: Int,
    val CONTENT: String,
    val DELIVERED: Int,
    val CREATED_AT: String
)
```

### 3. Use in Activity

```kotlin
class MainActivity : AppCompatActivity() {
    private val apiClient = TicketsAPIClient()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Login
        apiClient.login("user", "password") { access, refresh ->
            if (access != null) {
                // Save tokens in SharedPreferences
                val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("access_token", access)
                    putString("refresh_token", refresh)
                    apply()
                }
                
                // Get users
                apiClient.getUsers { users ->
                    users?.forEach { user ->
                        println("${user.USER_NAME} - ${user.FULL_NAME}")
                    }
                }
            }
        }
    }
}
```

### 4. Firebase Cloud Messaging (Push Notifications)

```kotlin
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // Save token and send it to the API
        val apiClient = TicketsAPIClient()
        // apiClient.updateFCMToken(userId, token)
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title ?: "New Message"
        val body = remoteMessage.notification?.body ?: ""
        val senderId = remoteMessage.data["senderId"]
        val ticketId = remoteMessage.data["ticketId"]
        
        // Show notification
        showNotification(title, body)
    }
    
    private fun showNotification(title: String, body: String) {
        val notificationId = System.currentTimeMillis().toInt()
        
        val notification = NotificationCompat.Builder(this, "messages_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }
}
```

---

## 🌐 Web (JavaScript/TypeScript)

### 1. HTTP Client (Vanilla JS)

```javascript
class TicketsAPIClient {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
        this.accessToken = null;
        this.refreshToken = null;
    }
    
    // Login
    async login(username, password) {
        const response = await fetch(`${this.baseUrl}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, pwd: password })
        });
        
        const data = await response.json();
        this.accessToken = data.accessToken;
        this.refreshToken = data.refreshToken;
        
        // Save in localStorage
        localStorage.setItem('accessToken', this.accessToken);
        localStorage.setItem('refreshToken', this.refreshToken);
        
        return data;
    }
    
    // Get users
    async getUsers() {
        const response = await fetch(`${this.baseUrl}/users`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${this.accessToken}`
            }
        });
        return await response.json();
    }
    
    // Create ticket
    async createTicket(createdBy, title, responsible) {
        const response = await fetch(`${this.baseUrl}/tickets/createTicket`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${this.accessToken}`
            },
            body: JSON.stringify({ createdBy, title, responsible })
        });
        return await response.json();
    }
    
    // Send message
    async sendMessage(ticket, sender, receiver, content, imageFile = null) {
        const formData = new FormData();
        formData.append('ticket', ticket || 'null');
        formData.append('sender', sender);
        formData.append('receiver', receiver);
        formData.append('content', content);
        
        if (imageFile) {
            formData.append('image', imageFile);
        }
        
        const response = await fetch(`${this.baseUrl}/messages/sendMessage`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${this.accessToken}`
            },
            body: formData
        });
        return await response.json();
    }
    
    // Get chats
    async getChats(userId) {
        const response = await fetch(
            `${this.baseUrl}/messages/getChats/${userId}`,
            {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${this.accessToken}`
                }
            }
        );
        return await response.json();
    }
}

// Usage
const api = new TicketsAPIClient('http://localhost:3001');

// Login
document.getElementById('loginBtn').addEventListener('click', async () => {
    try {
        await api.login('user', 'password');
        console.log('Login successful');
        
        // Get users
        const users = await api.getUsers();
        console.log('Users:', users);
    } catch (error) {
        console.error('Error:', error);
    }
});

// Send message
document.getElementById('sendBtn').addEventListener('click', async () => {
    const imageInput = document.getElementById('imageInput');
    const file = imageInput.files[0];
    
    await api.sendMessage(
        5, // ticket
        1, // sender
        2, // receiver
        'Hello! Here is the image',
        file
    );
});
```

### 2. HTTP Client (Axios)

```javascript
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:3001'
});

// Interceptor to add token
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

// Interceptor to refresh token
api.interceptors.response.use(
    response => response,
    async error => {
        const config = error.config;
        
        if (error.response?.status === 403) {
            const refreshToken = localStorage.getItem('refreshToken');
            
            try {
                const response = await api.post('/users/token', {
                    token: refreshToken
                });
                
                localStorage.setItem('accessToken', response.data.accessToken);
                config.headers.Authorization = `Bearer ${response.data.accessToken}`;
                
                return api(config);
            } catch (err) {
                // Invalid token, redirect to login
                window.location.href = '/login';
            }
        }
        
        return Promise.reject(error);
    }
);

export default api;
```

### 3. React Hook for Tickets API

```javascript
import { useState, useContext, createContext, useEffect } from 'react';
import api from './api';

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            // Verify token is valid
            api.get('/users')
                .then(() => setUser({ token }))
                .catch(() => localStorage.removeItem('accessToken'));
        }
        setLoading(false);
    }, []);
    
    const login = async (username, password) => {
        const response = await api.post('/users/login', {
            username,
            pwd: password
        });
        
        localStorage.setItem('accessToken', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        setUser({ token: response.data.accessToken });
        
        return response.data;
    };
    
    const logout = async () => {
        const refreshToken = localStorage.getItem('refreshToken');
        await api.post('/users/logout', { token: refreshToken });
        
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setUser(null);
    };
    
    return (
        <AuthContext.Provider value={{ user, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);

// Login Component
function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();
    
    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            await login(username, password);
            navigate('/dashboard');
        } catch (error) {
            alert('Login failed: ' + error.message);
        }
    };
    
    return (
        <form onSubmit={handleLogin}>
            <input
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="Username"
                required
            />
            <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Password"
                required
            />
            <button type="submit">Login</button>
        </form>
    );
}
```

---

## 🚀 Deployment Considerations

### Development vs Production

**Development:**
```env
API_URL=http://localhost:3001
```

**Production:**
```env
API_URL=https://your-domain.com/api
```

### HTTPS Configuration

Always use HTTPS in production:
```javascript
const baseUrl = process.env.REACT_APP_API_URL || 'https://api.yourdomain.com';
```

### Token Storage Security

```javascript
// ❌ NOT SAFE for sensitive apps
localStorage.setItem('token', token);

// ✅ SAFER - Use secure HTTP-only cookies
// Configure on backend to set cookie automatically
```

---

## 🔧 Common Integration Issues

### CORS Issues
Add CORS headers on backend:
```javascript
const cors = require('cors');
app.use(cors({
    origin: process.env.ALLOWED_ORIGINS?.split(',')
}));
```

### Token Expiration
Always handle 401/403 responses:
```javascript
api.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 403) {
            // Refresh token
        }
        return Promise.reject(error);
    }
);
```

### Image Upload
Check file size:
```javascript
const MAX_SIZE = 5 * 1024 * 1024; // 5MB

if (file.size > MAX_SIZE) {
    alert('File too large');
}
```

---

## 📱 Flutter Integration

```dart
import 'package:http/http.dart' as http;
import 'dart:convert';

class TicketsAPIClient {
    final String baseUrl;
    String? accessToken;
    String? refreshToken;
    
    TicketsAPIClient({required this.baseUrl});
    
    Future<Map<String, dynamic>> login(String username, String password) async {
        final response = await http.post(
            Uri.parse('$baseUrl/users/login'),
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode({'username': username, 'pwd': password})
        );
        
        if (response.statusCode == 200) {
            final data = jsonDecode(response.body);
            accessToken = data['accessToken'];
            refreshToken = data['refreshToken'];
            return data;
        }
        throw Exception('Login failed');
    }
    
    Future<List<dynamic>> getUsers() async {
        final response = await http.get(
            Uri.parse('$baseUrl/users'),
            headers: {
                'Authorization': 'Bearer $accessToken',
                'Content-Type': 'application/json'
            }
        );
        
        if (response.statusCode == 200) {
            return jsonDecode(response.body);
        }
        throw Exception('Failed to get users');
    }
}
```

---

**Complete integration examples available in each section above.**
