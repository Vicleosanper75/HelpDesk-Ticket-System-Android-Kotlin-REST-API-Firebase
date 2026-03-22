# 🔗 Guía de Integración - Tickets API

Cómo integrar **Tickets API** en tus aplicaciones (Android, Web, Flutter, etc.)

---

## 📱 Android (Kotlin/Java)

### 1. Agregar Dependencias (build.gradle)

```gradle
dependencies {
    // HTTP Client
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    
    // JSON parsing
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // JWT (opcional)
    implementation 'com.auth0:java-jwt:4.4.0'
    
    // Firebase (para push notifications)
    implementation 'com.google.firebase:firebase-messaging:23.2.1'
}
```

### 2. Crear Cliente HTTP

```kotlin
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson

class TicketsAPIClient {
    private val baseUrl = "http://IP:3001" // IP de tu servidor
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
    
    // Obtener usuarios
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
    
    // Enviar mensaje
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

// Modelos
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

### 3. Usar en Activity

```kotlin
class MainActivity : AppCompatActivity() {
    private val apiClient = TicketsAPIClient()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Login
        apiClient.login("usuario", "password") { access, refresh ->
            if (access != null) {
                // Guardar tokens en SharedPreferences
                val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("access_token", access)
                    putString("refresh_token", refresh)
                    apply()
                }
                
                // Obtener usuarios
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

### 4. Firebase Cloud Messaging (Notificaciones Push)

```kotlin
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // Guardar token y enviarlo a la API
        val apiClient = TicketsAPIClient()
        // apiClient.updateFCMToken(userId, token)
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title ?: "Nuevo Mensaje"
        val body = remoteMessage.notification?.body ?: ""
        val senderId = remoteMessage.data["senderId"]
        val ticketId = remoteMessage.data["ticketId"]
        
        // Mostrar notificación
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

### 1. Cliente HTTP (Vanilla JS)

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
        
        // Guardar en localStorage
        localStorage.setItem('accessToken', this.accessToken);
        localStorage.setItem('refreshToken', this.refreshToken);
        
        return data;
    }
    
    // Obtener usuarios
    async getUsers() {
        const response = await fetch(`${this.baseUrl}/users`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${this.accessToken}`
            }
        });
        return await response.json();
    }
    
    // Crear ticket
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
    
    // Enviar mensaje
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
    
    // Obtener chats
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

// Uso
const api = new TicketsAPIClient('http://localhost:3001');

// Login
document.getElementById('loginBtn').addEventListener('click', async () => {
    try {
        await api.login('usuario', 'password');
        console.log('Login exitoso');
        
        // Obtener usuarios
        const users = await api.getUsers();
        console.log('Usuarios:', users);
    } catch (error) {
        console.error('Error:', error);
    }
});

// Enviar mensaje
document.getElementById('sendBtn').addEventListener('click', async () => {
    const imageInput = document.getElementById('imageInput');
    const file = imageInput.files[0];
    
    await api.sendMessage(
        5, // ticket
        1, // sender
        2, // receiver
        'Hola! Aquí está la imagen',
        file
    );
});
```

### 2. Cliente HTTP (Axios)

```javascript
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:3001'
});

// Interceptor para agregar token
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

// Interceptor para refrescar token
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
                // Token inválido, redirigir a login
                window.location.href = '/login';
            }
        }
        
        return Promise.reject(error);
    }
);

export default api;
```

### 3. React Hook Hook para Tickets API

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
            // Verificar si el token es válido
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

// Componente de Login
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
            alert('Login fallido: ' + error.message);
        }
    };
    
    return (
        <form onSubmit={handleLogin}>
            <input
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="Usuario"
                required
            />
            <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Contraseña"
                required
            />
            <button type="submit">Login</button>
        </form>
    );
}
```

---

## 📱 React Native / Expo

```javascript
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as DocumentPicker from 'expo-document-picker';

class TicketsAPIClient {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    async login(username, password) {
        const response = await fetch(`${this.baseUrl}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, pwd: password })
        });
        
        const data = await response.json();
        
        await AsyncStorage.setItem('accessToken', data.accessToken);
        await AsyncStorage.setItem('refreshToken', data.refreshToken);
        
        return data;
    }
    
    async getUsers() {
        const token = await AsyncStorage.getItem('accessToken');
        
        const response = await fetch(`${this.baseUrl}/users`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        return await response.json();
    }
    
    async sendMessage(ticket, sender, receiver, content, imageFile = null) {
        const token = await AsyncStorage.getItem('accessToken');
        const formData = new FormData();
        
        formData.append('ticket', ticket || 'null');
        formData.append('sender', sender);
        formData.append('receiver', receiver);
        formData.append('content', content);
        
        if (imageFile) {
            const filename = imageFile.uri.split('/').pop();
            let mimeType = 'image/jpeg';
            
            if (filename.includes('png')) mimeType = 'image/png';
            if (filename.includes('gif')) mimeType = 'image/gif';
            
            formData.append('image', {
                uri: imageFile.uri,
                type: mimeType,
                name: filename
            });
        }
        
        const response = await fetch(`${this.baseUrl}/messages/sendMessage`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });
        
        return await response.json();
    }
}
```

---

## 🏠 Flutter

```dart
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';

class TicketsAPIClient {
    final String baseUrl;
    final storage = const FlutterSecureStorage();
    String? accessToken;
    
    TicketsAPIClient({required this.baseUrl});
    
    Future<bool> login(String username, String password) async {
        final response = await http.post(
            Uri.parse('$baseUrl/users/login'),
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode({
                'username': username,
                'pwd': password
            })
        );
        
        if (response.statusCode == 200) {
            final data = jsonDecode(response.body);
            accessToken = data['accessToken'];
            
            await storage.write(
                key: 'accessToken',
                value: data['accessToken']
            );
            await storage.write(
                key: 'refreshToken',
                value: data['refreshToken']
            );
            
            return true;
        }
        
        return false;
    }
    
    Future<List<dynamic>> getUsers() async {
        final response = await http.get(
            Uri.parse('$baseUrl/users'),
            headers: {
                'Authorization': 'Bearer $accessToken'
            }
        );
        
        if (response.statusCode == 200) {
            return jsonDecode(response.body);
        }
        
        throw Exception('Error loading users');
    }
    
    Future<dynamic> sendMessage(
        int? ticket,
        int sender,
        int receiver,
        String content,
        File? imageFile
    ) async {
        var request = http.MultipartRequest(
            'POST',
            Uri.parse('$baseUrl/messages/sendMessage')
        );
        
        request.headers['Authorization'] = 'Bearer $accessToken';
        request.fields['ticket'] = ticket?.toString() ?? 'null';
        request.fields['sender'] = sender.toString();
        request.fields['receiver'] = receiver.toString();
        request.fields['content'] = content;
        
        if (imageFile != null) {
            request.files.add(
                await http.MultipartFile.fromPath('image', imageFile.path)
            );
        }
        
        var response = await request.send();
        
        if (response.statusCode == 200) {
            return jsonDecode(await response.stream.bytesToString());
        }
        
        throw Exception('Error sending message');
    }
}
```

---

## 📚 Referencia de Conversión de Tipos

| API (JSON) | Android (Kotlin) | Web (JS) | Flutter (Dart) |
|-----------|-----------------|---------|---------------|
| `string` | `String` | `string` | `String` |
| `number` | `Int/Double` | `number` | `int/double` |
| `boolean` | `Boolean` | `boolean` | `bool` |
| `null` | `null` | `null` | `null` |
| `array` | `List<T>` | `Array` | `List<T>` |
| `object` | `data class` | `Object` | `class` |

---

## 🔐 Manejo de Tokens

### Guardar Tokens

**Android:**
```kotlin
val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
prefs.edit().putString("accessToken", token).apply()
```

**Web:**
```javascript
localStorage.setItem('accessToken', token);
```

**Flutter:**
```dart
final storage = const FlutterSecureStorage();
await storage.write(key: 'accessToken', value: token);
```

### Refrescar Token

Implementación de ejemplo para manejar tokens expirados:

```javascript
// Cualquier cliente HTTP
async function makeRequest(method, endpoint, data = null) {
    let headers = {
        'Authorization': `Bearer ${accessToken}`
    };
    
    let response = await fetch(
        `${baseUrl}${endpoint}`,
        { method, headers, body: data ? JSON.stringify(data) : null }
    );
    
    // Si token expirado (403)
    if (response.status === 403) {
        const newTokenResponse = await fetch(`${baseUrl}/users/token`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ token: refreshToken })
        });
        
        const newData = await newTokenResponse.json();
        accessToken = newData.accessToken;
        localStorage.setItem('accessToken', accessToken);
        
        // Reintentar request original
        headers['Authorization'] = `Bearer ${accessToken}`;
        response = await fetch(`${baseUrl}${endpoint}`, {
            method,
            headers,
            body: data ? JSON.stringify(data) : null
        });
    }
    
    return response.json();
}
```

---

## 🆘 Problemas Comunes de Integración

### "CORS Error"

**Causa:** El servidor rechaza requests desde tu dominio

**Solución:**
1. Contacta al propietario del servidor
2. Agregar CORS middleware en servidor:
```javascript
const cors = require('cors');
app.use(cors());
```

### "403 Forbidden"

**Causa:** Token expirado o inválido

**Solución:**
1. Refrescar token con refresh token
2. Si falla, pedir login nuevamente

### "401 Unauthorized"

**Causa:** Falta el header Authorization

**Solución:**
```javascript
headers: {
    'Authorization': `Bearer ${accessToken}`
}
```

### "Timeout"

**Causa:** Servidor tardando mucho o no responde

**Solución:**
1. Verificar conexión de red
2. Aumentar timeout:
```javascript
fetch(url, { 
    signal: AbortSignal.timeout(30000) // 30 segundos
})
```

---

## 📞 Detalles de Conexión

- **Servidor en deben reemplazar:** `http://192.168.1.100:3001`
- **En producción:** Usa tu dominio: `https://api.tudominio.com`
- **HTTPS:** Es obligatorio en producción

---

## 📖 Documentación Completa

→ [DOCUMENTATION.md](DOCUMENTATION.md)  
→ [API_REFERENCE.md](API_REFERENCE.md)  
→ [QUICK_START.md](QUICK_START.md)

---

**Últimamente actualizado:** Enero 2024
