# Tickets API - Documentación Completa

## 📋 Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Características](#características)
3. [Requisitos Previos](#requisitos-previos)
4. [Instalación](#instalación)
5. [Configuración](#configuración)
6. [Estructura del Proyecto](#estructura-del-proyecto)
7. [Autenticación](#autenticación)
8. [API Endpoints](#api-endpoints)
9. [Modelos de Datos](#modelos-de-datos)
10. [Ejemplos de Uso](#ejemplos-de-uso)
11. [Manejo de Errores](#manejo-de-errores)
12. [Notificaciones Push](#notificaciones-push)
13. [Seguridad](#seguridad)
14. [Deployment](#deployment)
15. [Solución de Problemas](#solución-de-problemas)

---

## 📱 Descripción General

**Tickets API** es una solución backend completa desarrollada en Node.js para gestionar un sistema de tickets con soporte para mensajería en tiempo real, gestión de usuarios con roles, y notificaciones push integradas con Firebase Cloud Messaging.

La API está diseñada para ser utilizada principalmente por aplicaciones Android, pero es agnóstica del cliente y puede ser consumida desde cualquier plataforma compatible con HTTP/REST.

**Autor:** Victor Amaya  
**Versión:** 1.0.0  
**Licencia:** ISC

---

## ✨ Características

- ✅ **Gestión de Usuarios** - Registro, login, roles y permisos
- ✅ **Sistema de Tickets** - Crear, asignar, reasignar y cerrar tickets
- ✅ **Mensajería** - Sistema de chat completo con soporte para imágenes
- ✅ **Autenticación JWT** - Tokens de acceso y refresh tokens
- ✅ **Notificaciones Push** - Integración con Firebase Cloud Messaging
- ✅ **Encriptación de Mensajes** - Los mensajes se almacenan encriptados
- ✅ **Control de Estado Online** - Tracking de usuarios activos
- ✅ **Base de Datos SQLite** - Persistencia de datos local
- ✅ **Subida de Archivos** - Soporte para imágenes en mensajes (Multer)
- ✅ **Pagination** - Soporte para resultados paginados

---

## 🔧 Requisitos Previos

### Sistema:
- **Node.js** >= 14.0.0
- **npm** >= 6.0.0
- **SQLite3** (incluido en el proyecto)
- Windows, macOS o Linux

### Conocimientos:
- JavaScript/Node.js básico
- REST APIs
- JWT (JSON Web Tokens)
- SQLite/SQL básico

---

## 📦 Instalación

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tuusuario/TicketsAPI.git
cd TicketsAPI
```

### 2. Instalar Dependencias
```bash
npm install
```

Este comando instalará:
- **express** - Framework web
- **sqlite3** - Base de datos
- **bcrypt/bcryptjs** - Encriptación de contraseñas
- **jsonwebtoken** - Autenticación JWT
- **firebase-admin** - Notificaciones push
- **multer** - Manejo de subidas de archivos
- **socket.io** - WebSockets en tiempo real
- **dotenv** - Variables de entorno

### 3. Crear Base de Datos
```bash
npm run db
```

Este comando ejecutará [src/models/sqlite.js](#) que crea las tablas necesarias.

---

## ⚙️ Configuración

### Variables de Entorno (.env)

Crea un archivo `.env` en la raíz del proyecto:

```env
# PUERTO
PORT=3001

# JWT SECRETS (CRUCIAL - Genera strings largos y aleatorios)
ACCESS_TOKEN_SECRET=tu_secret_key_super_largo_y_aleatorio_para_access_tokens
REFRESH_TOKEN_SECRET=tu_secret_key_super_largo_y_aleatorio_para_refresh_tokens

# FIREBASE (Opcional, solo si usas notificaciones push)
FIREBASE_PROJECT_ID=tu-proyecto-id
FIREBASE_PRIVATE_KEY_ID=tu-private-key-id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk@tu-proyecto.iam.gserviceaccount.com
FIREBASE_CLIENT_ID=tu-client-id
```

### Archivos Importantes

#### serviceAccountKey.json
Este archivo contiene las credenciales de Firebase. Se requiere para enviar notificaciones push.

```json
{
  "type": "service_account",
  "project_id": "tu-proyecto",
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

## 🏗️ Estructura del Proyecto

```
TicketsAPI/
├── src/
│   ├── controllers/          # Lógica de negocio
│   │   ├── main.controller.js
│   │   ├── users.controller.js
│   │   ├── tickets.controller.js
│   │   ├── messages.controller.js
│   │   └── cryptoHelper.js    # Encriptación de mensajes
│   │
│   ├── models/               # Capas de acceso a datos
│   │   ├── users.js
│   │   ├── tickets.js
│   │   ├── messages.js
│   │   └── sqlite.js         # Inicialización de BD
│   │
│   └── routes/               # Definición de endpoints
│       ├── main.router.js
│       ├── users.router.js
│       ├── tickets.router.js
│       └── messages.router.js
│
├── uploads/                  # Directorio para imágenes subidas
├── authMiddleware.js         # Middleware de autenticación JWT
├── firebaseAdmin.js          # Configuración de Firebase
├── index.js                  # Punto de entrada
├── package.json              # Dependencias
├── .env                      # Variables de entorno (no commitear)
├── .gitignore
├── SQLite.sql               # Scripts SQL útiles
├── database.sqbpro          # Herramienta SQLite
└── DOCUMENTATION.md         # Este archivo
```

---

## 🔐 Autenticación

### Flujo de Autenticación

```
1. REGISTRO
   POST /users/register
   {username, fullname, pwd, role}
   ↓
   Contraseña encriptada con bcrypt
   ↓
   Usuario guardado en BD
   
2. LOGIN
   POST /users/login
   {username, pwd}
   ↓
   Contraseña validada
   ↓
   JWT Access Token (9 horas)
   JWT Refresh Token (Sin expiración)
   
3. USAR API
   GET /tickets/:id
   Headers: Authorization: Bearer <ACCESS_TOKEN>
   ↓
   Middleware valida token
   ↓
   Acceso permitido
   
4. REFRESCAR TOKEN
   POST /users/token
   {token: REFRESH_TOKEN}
   ↓
   Nuevo Access Token (9 horas)
   
5. LOGOUT
   POST /users/logout
   {token: REFRESH_TOKEN}
   ↓
   Refresh Token eliminado de BD
```

### Tipos de Tokens

#### Access Token
- **Expiración:** 9 horas
- **Uso:** Acceso a endpoints protegidos
- **Ubicación en Headers:** `Authorization: Bearer <token>`
- **Payload:** `{name: username}`

#### Refresh Token
- **Expiración:** Sin límite
- **Uso:** Obtener nuevo access token
- **Almacenamiento:** Base de datos (tabla TOKENS)
- **Validación:** Token debe existir en BD para ser válido

### Middleware de Autenticación

Todos los endpoints excepto `/users/register` y `/users/login` requieren:

```javascript
// Headers necesarios
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

Si el token es inválido o está expirado:
```json
{
  "error": "Token inválido" | "Falta token"
}
```

---

## 📡 API Endpoints

### Base URL
```
http://localhost:3001
```

---

### 👥 USERS - Gestión de Usuarios

#### 1. Registro de Usuario
```http
POST /users/register
Content-Type: application/json

{
  "username": "juan_perez",
  "fullname": "Juan Pérez García",
  "pwd": "MiPassword123!",
  "role": 1
}
```

**Respuesta Exitosa (201):**
```json
{
  "id": 5
}
```

**Errores Posibles:**
| Código | Descripción |
|--------|-------------|
| 500 | Error al hashear contraseña o BD |

**Roles Disponibles:**
- `1` - Operaria
- `2` - Soporte
- `3` - Sistemas

---

#### 2. Login
```http
POST /users/login
Content-Type: application/json

{
  "username": "juan_perez",
  "pwd": "MiPassword123!"
}
```

**Respuesta Exitosa (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Errores Posibles:**
| Código | Descripción |
|--------|-------------|
| 404 | Usuario no encontrado |
| 400 | Contraseña incorrecta |
| 500 | Error en servidor |

---

#### 3. Refrescar Access Token
```http
POST /users/token
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Respuesta Exitosa (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Errores Posibles:**
| Código | Descripción |
|--------|-------------|
| 401 | Token no proporcionado |
| 403 | Token no existe o inválido |
| 500 | Error en servidor |

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

**Respuesta Exitosa (204):**
```
Logout success
```

---

#### 5. Obtener Todos los Usuarios
```http
GET /users
Authorization: Bearer <ACCESS_TOKEN>
```

**Respuesta Exitosa (200):**
```json
[
  {
    "ID": 1,
    "USER_NAME": "admin",
    "FULL_NAME": "Administrador",
    "ONLINE": 1,
    "CREATED_AT": "2024-01-15 10:30:00",
    "ROLE": 3,
    "ROLE_NAME": "Sistemas",
    "ACTIVE": 1
  },
  {
    "ID": 2,
    "USER_NAME": "juan_perez",
    "FULL_NAME": "Juan Pérez García",
    "ONLINE": 0,
    "CREATED_AT": "2024-01-20 14:45:00",
    "ROLE": 1,
    "ROLE_NAME": "Operaria",
    "ACTIVE": 1
  }
]
```

---

#### 6. Obtener Usuario por ID
```http
GET /users/:id
Authorization: Bearer <ACCESS_TOKEN>
```

**Ejemplo:**
```http
GET /users/2
Authorization: Bearer <ACCESS_TOKEN>
```

**Respuesta Exitosa (200):**
```json
{
  "ID": 2,
  "USER_NAME": "juan_perez",
  "FULL_NAME": "Juan Pérez García",
  "ONLINE": 1,
  "CREATED_AT": "2024-01-20 14:45:00",
  "ROLE": 1,
  "ROLE_NAME": "Operaria",
  "ACTIVE": 1
}
```

**Errores Posibles:**
| Código | Descripción |
|--------|-------------|
| 404 | Usuario no encontrado |
| 500 | Error en servidor |

---

#### 7. Actualizar Usuario
```http
POST /users/update/:id
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "username": "nuevo_usuario",
  "fullname": "Nuevo Nombre Completo",
  "pwd": "NuevaPassword123!"
}
```

**Campos Opcionales:** Todos los campos son opcionales. Solo se actualizarán los enviados.

**Respuesta Exitosa (201):**
```json
1
```
(Número de filas actualizadas)

---

#### 8. Actualizar FCM Token (Push Notifications)
```http
POST /users/updateToken
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "userId": 2,
  "fcmToken": "fHkfJK3jk2n3jk2nj3k2nj32kn2kn3..."
}
```

**Respuesta Exitosa (200):**
```json
{
  "message": "Token actualizado correctamente",
  "changes": 1
}
```

---

#### 9. Establecer Estado Online/Offline
```http
POST /users/setOnlineStatus/:id
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "online": 1
}
```

**Parámetros:**
- `online`: `1` = Online, `0` = Offline

**Respuesta Exitosa (200):**
```json
1
```
(Número de filas actualizadas)

---

#### 10. Eliminar Usuario
```http
POST /users/delete/:id
Authorization: Bearer <ACCESS_TOKEN>
```

**Respuesta Exitosa (200):**
```json
1
```

---

### 🎟️ TICKETS - Gestión de Tickets

#### 1. Crear Ticket
```http
POST /tickets/createTicket
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "createdBy": 1,
  "title": "Problema con el sistema de inventario",
  "responsible": 2
}
```

**Campos:**
- `createdBy`: ID del usuario que crea el ticket
- `title`: Título/Descripción del ticket
- `responsible`: ID del usuario asignado

**Respuesta Exitosa (200):**
```json
{
  "ID": 5
}
```

---

#### 2. Obtener Ticket por ID
```http
GET /tickets/:id
Authorization: Bearer <ACCESS_TOKEN>
```

**Ejemplo:**
```http
GET /tickets/5
Authorization: Bearer <ACCESS_TOKEN>
```

**Respuesta Exitosa (200):**
```json
{
  "ID": 5,
  "CREATED_BY": 1,
  "TITLE": "Problema con el sistema de inventario",
  "RESPONSIBLE": 2,
  "STATE": 0,
  "CREATED_AT": "2024-01-21 09:30:00"
}
```

**Estados:**
- `0` - Abierto
- `1` - Cerrado

**Errores Posibles:**
| Código | Descripción |
|--------|-------------|
| 404 | Ticket no encontrado |
| 500 | Error en servidor |

---

#### 3. Concluir Ticket
```http
PUT /tickets/concludeTicket
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "id": 5
}
```

**Respuesta Exitosa (200):**
```json
{
  "message": "Ticket concluido con éxito"
}
```

**Errores Posibles:**
| Código | Descripción |
|--------|-------------|
| 404 | Ticket no encontrado |
| 500 | Error en servidor |

---

#### 4. Reasignar Ticket
```http
PUT /tickets/reassignTicket
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "id": 5,
  "newResponsibleId": 3
}
```

**Respuesta Exitosa (200):**
```json
{
  "message": "Ticket reasignado con éxito"
}
```

**Errores Posibles:**
| Código | Descripción |
|--------|-------------|
| 400 | Faltan datos (id o newResponsibleId) |
| 404 | Ticket no encontrado |
| 500 | Error en servidor |

---

### 💬 MESSAGES - Sistema de Mensajería

#### 1. Enviar Mensaje (con soporte para imágenes)
```http
POST /messages/sendMessage
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: multipart/form-data

Form Data:
  - ticket: 5 (opcional)
  - sender: 1 (ID del remitente)
  - receiver: 2 (ID del destinatario)
  - content: "Hola, ¿cómo estás?"
  - image: <archivo> (opcional, archivo multipart)
```

**Notas:**
- El mensaje puede ser de texto, imagen o ambos
- Si se envía una imagen, se almacena en `/uploads/`
- El contenido se encripta antes de guardarse
- Si el receptor tiene FCM Token registrado, recibe notificación push

**Respuesta Exitosa (200):**

*Con ticket:*
```json
{
  "id": 123,
  "changes": 1
}
```

*Sin ticket:*
```json
{
  "id": 123
}
```

**Errores Posibles:**
| Código | Descripción |
|--------|-------------|
| 400 | El mensaje debe tener texto o imagen |
| 500 | Error al subir archivo o guardar |

---

#### 2. Obtener Chats (Últimos Mensajes)
```http
GET /messages/getChats/:id
Authorization: Bearer <ACCESS_TOKEN>
```

**Parámetros:**
- `:id` - ID del usuario

**Ejemplo:**
```http
GET /messages/getChats/1
Authorization: Bearer <ACCESS_TOKEN>
```

**Respuesta Exitosa (200):**
```json
[
  {
    "Contact": "juan_perez",
    "LastMessage": "Hola, ¿cómo estás?",
    "SendedBy": "maria_garcia",
    "SendedAt": "2024-01-21 14:30:00"
  },
  {
    "Contact": "admin",
    "LastMessage": "El problema está resuelto",
    "SendedBy": "admin",
    "SendedAt": "2024-01-21 13:45:00"
  }
]
```

**Notas:** El `LastMessage` es automáticamente desencriptado

---

#### 3. Obtener Mensajes de un Chat
```http
GET /messages/getMessages?ticket=5&user=1&contact=2
Authorization: Bearer <ACCESS_TOKEN>
```

**Parámetros Query:**
- `ticket` - ID del ticket (optional)
- `user` - ID del usuario actual
- `contact` - ID del otro usuario en el chat

**Respuesta Exitosa (200):**
```json
[
  {
    "ID": 100,
    "SENDER": 1,
    "RECEIVER": 2,
    "CONTENT": "Hola, tengo un problema",
    "CREATED_AT": "2024-01-21 10:00:00",
    "DELIVERED": 0
  },
  {
    "ID": 101,
    "SENDER": 2,
    "RECEIVER": 1,
    "CONTENT": "[image]uploads/1705851600000.jpg",
    "CREATED_AT": "2024-01-21 10:15:00",
    "DELIVERED": 1
  }
]
```

**Notas:**
- El `CONTENT` es desencriptado automáticamente
- Si contiene `[image]` al inicio, es una imagen

---

#### 4. Marcar Mensaje como Entregado
```http
POST /messages/markDelivered/:id
Authorization: Bearer <ACCESS_TOKEN>
```

**Parámetros:**
- `:id` - ID del mensaje

**Respuesta Exitosa (200):**
```json
1
```
(Número de filas actualizadas)

---

#### 5. Marcar Mensajes como Leídos
```http
PUT /messages/markAsRead
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "ticketId": 5,
  "userId": 1
}
```

**Respuesta Exitosa (200):**
```json
{
  "status": "success",
  "updated": 3
}
```

---

#### 6. Obtener Tickets Concluidos (Paginado)
```http
GET /messages/getConcludedTickets?userId=1&page=1&limit=15
Authorization: Bearer <ACCESS_TOKEN>
```

**Parámetros Query:**
- `userId` - ID del usuario
- `page` - Número de página (default: 1)
- `limit` - Resultados por página (default: 15)

**Respuesta Exitosa (200):**
```json
[
  {
    "ID": 1,
    "TITLE": "Problema de conexión",
    "CREATED_BY": 1,
    "RESPONSIBLE": 2,
    "STATE": 1,
    "CREATED_AT": "2024-01-15 10:00:00"
  }
]
```

---

## 📊 Modelos de Datos

### Tabla: USERS
```sql
CREATE TABLE IF NOT EXISTS USERS (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    USER_NAME TEXT UNIQUE NOT NULL,
    FULL_NAME TEXT NOT NULL,
    PWD TEXT NOT NULL,                    -- Hash bcrypt
    ROLE INTEGER NOT NULL,                 -- FK a USER_ROLES
    ONLINE INTEGER DEFAULT 0,              -- 0=offline, 1=online
    FCM_TOKEN TEXT,                        -- Firebase Cloud Messaging
    ACTIVE INTEGER DEFAULT 1,              -- 0=desactivado, 1=activo
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### Tabla: USER_ROLES
```sql
CREATE TABLE IF NOT EXISTS USER_ROLES (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    ROLE_NAME TEXT UNIQUE NOT NULL,
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### Tabla: TOKENS
```sql
CREATE TABLE IF NOT EXISTS TOKENS (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    BELONGS_TO INTEGER NOT NULL,           -- FK a USERS
    TOKEN TEXT UNIQUE NOT NULL,
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (BELONGS_TO) REFERENCES USERS(ID)
);
```

### Tabla: TICKETS
```sql
CREATE TABLE IF NOT EXISTS TICKETS (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    CREATED_BY INTEGER NOT NULL,           -- FK a USERS
    TITLE TEXT NOT NULL,
    RESPONSIBLE INTEGER,                   -- FK a USERS
    STATE INTEGER DEFAULT 0,               -- 0=abierto, 1=cerrado
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CREATED_BY) REFERENCES USERS(ID),
    FOREIGN KEY (RESPONSIBLE) REFERENCES USERS(ID)
);
```

### Tabla: MESSAGES
```sql
CREATE TABLE IF NOT EXISTS MESSAGES (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    SENDER INTEGER NOT NULL,               -- FK a USERS
    RECEIVER INTEGER NOT NULL,             -- FK a USERS
    CONTENT TEXT NOT NULL,                 -- Encriptado
    DELIVERED INTEGER DEFAULT 0,           -- 0=no entregado, 1=entregado
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (SENDER) REFERENCES USERS(ID),
    FOREIGN KEY (RECEIVER) REFERENCES USERS(ID)
);
```

### Tabla: TICKETS_CHATS
```sql
CREATE TABLE IF NOT EXISTS TICKETS_CHATS (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    TICKET INTEGER NOT NULL,               -- FK a TICKETS
    MESSAGE INTEGER NOT NULL,              -- FK a MESSAGES
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (TICKET) REFERENCES TICKETS(ID),
    FOREIGN KEY (MESSAGE) REFERENCES MESSAGES(ID)
);
```

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Flujo Completo de Login y Acceso a API

```bash
# 1. Registro
curl -X POST http://localhost:3001/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan_perez",
    "fullname": "Juan Pérez",
    "pwd": "MiPassword123!",
    "role": 2
  }'
# Respuesta: {"id": 5}

# 2. Login
curl -X POST http://localhost:3001/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan_perez",
    "pwd": "MiPassword123!"
  }'
# Respuesta: 
# {
#   "accessToken": "eyJhbG...",
#   "refreshToken": "eyJhbG..."
# }

# 3. Usar token para acceder a recurso
ACCESS_TOKEN="eyJhbG..."
curl -X GET http://localhost:3001/users \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Ejemplo 2: Crear Ticket y Enviar Mensaje

```bash
# 1. Crear ticket
curl -X POST http://localhost:3001/tickets/createTicket \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "createdBy": 1,
    "title": "Resolver problema de inventario",
    "responsible": 2
  }'
# Respuesta: {"ID": 5}

# 2. Enviar mensaje relacionado al ticket
curl -X POST http://localhost:3001/messages/sendMessage \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F "ticket=5" \
  -F "sender=1" \
  -F "receiver=2" \
  -F "content=He creado un ticket para esto"
# Respuesta: {"id": 123, "changes": 1}
```

### Ejemplo 3: Enviar Mensaje con Imagen

```bash
curl -X POST http://localhost:3001/messages/sendMessage \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -F "ticket=5" \
  -F "sender=1" \
  -F "receiver=2" \
  -F "content=Mira este error" \
  -F "image=@/ruta/a/captura.png"
```

### Ejemplo 4: Obtener Conversación Completa

```bash
# Obtener última conversación
curl -X GET "http://localhost:3001/messages/getChats/1" \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# Obtener todos los mensajes con un contacto
curl -X GET "http://localhost:3001/messages/getMessages?ticket=null&user=1&contact=2" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Ejemplo 5: Refrescar Token Expirado

```bash
# Si el access token expiró (9 horas)
REFRESH_TOKEN="eyJhbG..."
curl -X POST http://localhost:3001/users/token \
  -H "Content-Type: application/json" \
  -d "{ \"token\": \"$REFRESH_TOKEN\" }"
# Respuesta: {"accessToken": "nuevo_token..."}
```

---

## ⚠️ Manejo de Errores

### Códigos HTTP Estándar

| Código | Significado | Causa Común |
|--------|------------|-----------|
| 200 | OK | Solicitud exitosa |
| 201 | Created | Recurso creado exitosamente |
| 204 | No Content | Logout exitoso |
| 400 | Bad Request | Datos inválidos o faltantes |
| 401 | Unauthorized | Falta token o expirado |
| 403 | Forbidden | Token inválido o sin permisos |
| 404 | Not Found | Recurso no existe |
| 500 | Server Error | Error interno del servidor |

### Estructura de Errores

Todos los errores incluyen un mensaje descriptivo:

```json
{
  "error": "Token inválido"
}
```

o

```
Error al subir archivo: Archivo muy grande
```

### Errores Comunes y Soluciones

#### "Falta token"
**Causa:** No enviaste el header `Authorization`

**Solución:**
```bash
curl -H "Authorization: Bearer <token>" ...
```

#### "Token inválido"
**Causa:** El token expiró o está mal formado

**Solución:** Usa el refresh token para obtener uno nuevo:
```bash
curl -X POST http://localhost:3001/users/token \
  -H "Content-Type: application/json" \
  -d "{\"token\": \"$REFRESH_TOKEN\"}"
```

#### "Cannot find user"
**Causa:** El username no existe en la BD

**Solución:** Verifica que el usuario esté registrado o regístralo primero

#### "Usuario no encontrado"
**Causa:** El ID de usuario no existe

**Solución:** Usa `GET /users` para obtener IDs válidos

---

## 🔔 Notificaciones Push

La API integra **Firebase Cloud Messaging** para enviar notificaciones push automáticas cuando:

1. Se recibe un mensaje nuevo
2. Se menciona al usuario
3. Se asigna/reasigna un ticket

### Configuración de FCM

#### 1. En el Cliente (Android)
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
```

Obtener el FCM Token del dispositivo y enviarlo:

```bash
curl -X POST http://localhost:3001/users/updateToken \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "fcmToken": "fHkfJK3jk2n3jk2nj3k2nj32kn2kn3..."
  }'
```

#### 2. En el Servidor
El servidor automáticamente:
- Almacena el FCM Token en la BD
- Envía notificaciones cuando hay mensajes nuevos
- La notificación incluye:
  - `title`: "Nuevo Mensaje"
  - `body`: Contenido del mensaje (o "📷 Foto" si es imagen)
  - `data`: senderId, ticketId

### Formato de Notificación

```json
{
  "notification": {
    "title": "Nuevo Mensaje",
    "body": "Contenido del mensaje o 📷 Foto"
  },
  "data": {
    "senderId": "1",
    "ticketId": "5"
  }
}
```

---

## 🔒 Seguridad

### Mejores Prácticas Implementadas

1. **Encriptación de Contraseñas**
   - Contraseñas hasheadas con bcrypt (10 rounds)
   - Nunca se almacenan en texto plano

2. **Encriptación de Mensajes**
   - Mensajes encriptados antes de almacenarse
   - Desencriptados solo cuando se consultan
   - Ver `cryptoHelper.js` para algoritmo

3. **Autenticación JWT**
   - Access tokens con expiración de 9 horas
   - Refresh tokens sin expiración pero almacenados en BD
   - Invalidación de tokens al logout

4. **Validación de Entrada**
   - Parámetros validados antes de usarse
   - Protección contra inyección SQL (prepared statements)

5. **Control de Acceso**
   - Todos los endpoints excepto login/register protegidos
   - Validación de pertenencia (usuario solo accede sus datos)

### Recomendaciones de Seguridad Adicionales

Para ambiente de producción:

1. **Variables de Entorno**
   ```env
   ACCESS_TOKEN_SECRET=generar_clave_super_larga_aleatoria
   REFRESH_TOKEN_SECRET=generar_clave_super_larga_aleatoria
   ```

2. **HTTPS**
   - Usar certificados SSL válidos
   - Redirigir HTTP a HTTPS

3. **Rate Limiting**
   ```bash
   npm install express-rate-limit
   ```

4. **CORS**
   ```bash
   npm install cors
   ```
   Configurar orígenes permitidos

5. **Validación Robusta**
   ```bash
   npm install joi
   ```
   Validar formato de datos

6. **Logs Auditados**
   Registrar accesos sensibles

---

## 🚀 Deployment

### Opción 1: Heroku

1. **Crear app en Heroku**
```bash
heroku create nombre-app
```

2. **Configurar variables de entorno**
```bash
heroku config:set ACCESS_TOKEN_SECRET=tu_valor
heroku config:set REFRESH_TOKEN_SECRET=tu_valor
heroku config:set PORT=5000
```

3. **Desplegar**
```bash
git push heroku main
```

### Opción 2: AWS EC2

1. **Crear instancia EC2** (Ubuntu 20.04)

2. **Instalar dependencias**
```bash
sudo apt update
sudo apt install nodejs npm
```

3. **Clonar y configurar**
```bash
git clone ...
cd TicketsAPI
npm install
npm run db
```

4. **Iniciar con PM2**
```bash
npm install -g pm2
pm2 start index.js --name "TicketsAPI"
pm2 startup
pm2 save
```

### Opción 3: Docker

1. **Crear Dockerfile**
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ENV PORT=3001
CMD ["npm", "start"]
```

2. **Construir imagen**
```bash
docker build -t tickets-api:1.0.0 .
```

3. **Ejecutar contenedor**
```bash
docker run -d \
  -p 3001:3001 \
  -e ACCESS_TOKEN_SECRET=tu_valor \
  -e REFRESH_TOKEN_SECRET=tu_valor \
  -e FIREBASE_PROJECT_ID=tu_proyecto \
  tickets-api:1.0.0
```

### Opción 4: DigitalOcean App Platform

1. **Push a GitHub**
```bash
git remote add origin https://github.com/tuusuario/TicketsAPI.git
git push origin main
```

2. **Conectar DigitalOcean**
   - Crear app desde DigitalOcean Console
   - Seleccionar repositorio GitHub
   - Configurar variables de entorno
   - Deploy automático

---

## 🔧 Solución de Problemas

### Problema: "Cannot find module 'sqlite3'"

**Solución:**
```bash
npm install sqlite3 --build-from-source
```

### Problema: "Port 3001 already in use"

**Solución:**
```bash
# Cambiar puerto en .env
PORT=3002

# O matar proceso que usa puerto 3001
lsof -ti:3001 | xargs kill -9  # Linux/Mac
netstat -ano | findstr :3001    # Windows
```

### Problema: "FIREBASE_PROJECT_ID is undefined"

**Solución:**
- Verificar archivo `.env` existe
- Verificar variables de entorno están configuradas
- En desarrollo, usar `serviceAccountKey.json`

### Problema: "Contraseña incorrecta" pero contraseña es correcta

**Verificar:**
- El usuario espera minúsculas/mayúsculas exactas
- No hay espacios extras al inicio/final
- Usar herramienta como Postman para verificar

### Problema: Mensajes encriptados ven como basura

**Causa:** Cliente no está desencriptando

**Solución:** Los mensajes se desencriptan automáticamente en:
- `GET /messages/getChats/:id`
- `GET /messages/getMessages`

Si consumes la BD directamente, necesitas desencriptar manualmente.

### Problema: Imágenes no se suben

**Verificar:**
1. Carpeta `/uploads` existe y tiene permisos de escritura
2. Header `Content-Type: multipart/form-data` automático (no especificar en curl)
3. Nombre del campo es `image` exactamente

### Problema: Notificaciones push no llegan

**Verificar:**
1. `serviceAccountKey.json` es válido
2. FCM Token actualizado en BD: `GET /users/:id` y ver `FCM_TOKEN`
3. Firebase Cloud Messaging habilitado en proyecto
4. Dispositivo Android tiene Google Play Services

---

## 📞 Soporte y Contacto

Para reportar bugs, sugerencias o preguntas:

- **GitHub Issues:** [Issues](https://github.com/tuusuario/TicketsAPI/issues)
- **Email:** victor@example.com
- **Documentación:** [Wiki](https://github.com/tuusuario/TicketsAPI/wiki)

---

## 📄 Licencia

Este proyecto está licenciado bajo ISC License. Ver [LICENSE](LICENSE)

---

## 🙏 Agradecimientos

- Express.js por el framework web
- Firebase por notificaciones push
- Bcrypt por encriptación segura
- SQLite por persistencia de datos

---

**Última Actualización:** Enero 2024  
**Versión Documentación:** 1.0.0
