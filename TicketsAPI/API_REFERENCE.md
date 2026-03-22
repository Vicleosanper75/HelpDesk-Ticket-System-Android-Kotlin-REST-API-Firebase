# 📋 Referencia Rápida de Endpoints

Tabla rápida de todos los endpoints disponibles en Tickets API.

## 🔐 Autenticación Requerida

✅ Todos los endpoints EXCEPTO estos requieren:
```
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

**Endpoints SIN autenticación:**
- `POST /users/register`
- `POST /users/login`

---

## 👥 USUARIOS

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|----------------|
| POST | `/users/register` | Crear usuario | ❌ No |
| POST | `/users/login` | Login (obtener tokens) | ❌ No |
| POST | `/users/token` | Refrescar access token | ❌ No |
| POST | `/users/logout` | Logout (eliminar refresh token) | ✅ Sí |
| POST | `/users/clearTokens` | Limpiar todos los tokens | ✅ Sí |
| GET | `/users/roles` | Obtener roles disponibles | ✅ Sí |
| GET | `/users` | Listar todos usuarios | ✅ Sí |
| GET | `/users/:id` | Obtener usuario por ID | ✅ Sí |
| POST | `/users/update/:id` | Actualizar usuario | ✅ Sí |
| POST | `/users/updateToken` | Actualizar FCM token | ✅ Sí |
| POST | `/users/setOnlineStatus/:id` | Cambiar estado online | ✅ Sí |
| POST | `/users/delete/:id` | Eliminar usuario | ✅ Sí |
| POST | `/users/reset-password` | Resetear contraseña | ✅ Sí |

### 📝 Ejemplos Rápidos

```bash
# Registrar
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

# Obtener todos
GET /users

# Obtener por ID
GET /users/5

# Actualizar
POST /users/update/5
{
  "fullname": "Juan Pérez García"
}

# FCM Token (para push notifications)
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

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|----------------|
| POST | `/tickets/createTicket` | Crear nuevo ticket | ✅ Sí |
| GET | `/tickets/:id` | Obtener ticket por ID | ✅ Sí |
| PUT | `/tickets/concludeTicket` | Cerrar/concluir ticket | ✅ Sí |
| PUT | `/tickets/reassignTicket` | Reasignar responsable | ✅ Sí |

### 📝 Ejemplos Rápidos

```bash
# Crear ticket
POST /tickets/createTicket
{
  "createdBy": 1,
  "title": "Resolver problema de inventario",
  "responsible": 2
}

# Obtener por ID
GET /tickets/5

# Concluir (cerrar)
PUT /tickets/concludeTicket
{
  "id": 5
}

# Reasignar a otro usuario
PUT /tickets/reassignTicket
{
  "id": 5,
  "newResponsibleId": 3
}
```

---

## 💬 MENSAJES

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|----------------|
| POST | `/messages/sendMessage` | Enviar mensaje (+ imágenes) | ✅ Sí |
| POST | `/messages/markDelivered/:id` | Marcar mensaje entregado | ✅ Sí |
| GET | `/messages/getChats/:id` | Obtener últimas conversaciones | ✅ Sí |
| GET | `/messages/getMessages` | Obtener historial de chat | ✅ Sí |
| PUT | `/messages/markAsRead` | Marcar mensajes como leídos | ✅ Sí |
| GET | `/messages/getConcludedTickets` | Obtener tickets concluidos | ✅ Sí |

### 📝 Ejemplos Rápidos

```bash
# Enviar mensaje de texto
POST /messages/sendMessage
{
  "ticket": 5,
  "sender": 1,
  "receiver": 2,
  "content": "Hola, ¿cómo estás?"
}

# Enviar mensaje con imagen (multipart)
POST /messages/sendMessage
Form Data:
  - ticket: 5
  - sender: 1
  - receiver: 2
  - content: "Mira este error"
  - image: <archivo>

# Obtener chats (últimas conversaciones)
GET /messages/getChats/1

# Obtener mensajes de un chat
GET /messages/getMessages?ticket=5&user=1&contact=2

# Marcar como leído
PUT /messages/markAsRead
{
  "ticketId": 5,
  "userId": 1
}

# Marcar como entregado
POST /messages/markDelivered/123

# Obtener tickets concluidos (paginado)
GET /messages/getConcludedTickets?userId=1&page=1&limit=15
```

---

## 🔑 ESTADOS DE RESPUESTA

| Código | Significado |
|--------|------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado |
| 204 | No Content - Sin contenido (logout) |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Falta token |
| 403 | Forbidden - Token inválido |
| 404 | Not Found - Recurso no existe |
| 500 | Server Error - Error en servidor |

---

## 🔄 PARÁMETROS COMUNES

### Bearer Token (Header)
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Roles
- `1` = Operaria
- `2` = Soporte  
- `3` = Sistemas

### Estados de Ticket
- `0` = Abierto
- `1` = Cerrado

### Estado Online
- `0` = Offline
- `1` = Online

### DELIVERED (Mensaje)
- `0` = No entregado
- `1` = Entregado

---

## 📤 ENCABEZADOS (Headers)

### Requests JSON
```
Content-Type: application/json
Authorization: Bearer <TOKEN>
```

### Requests Multipart (Subida de archivos)
```
Authorization: Bearer <TOKEN>
Content-Type: multipart/form-data
```
(Se configura automáticamente con -F en curl)

---

## 💡 SECRETS

### Generar nuevo SECRET
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

### Variables de entorno (.env)
```env
PORT=3001
ACCESS_TOKEN_SECRET=<secret_largo_y_aleatorio>
REFRESH_TOKEN_SECRET=<secret_largo_y_aleatorio>
```

---

## 🗂️ ESTRUCTURA DE DIRECTORIO

```
/uploads/          # Imágenes subidas de mensajes
.env               # Variables de entorno
DOCUMENTATION.md   # Documentación completa
QUICK_START.md     # Guía de inicio rápido
API_REFERENCE.md   # Este archivo
```

---

## ⏱️ TIEMPOS DE EXPIRACIÓN

| Token | Expiración |
|-------|------------|
| Access Token | 9 horas |
| Refresh Token | Sin límite (pero guardado en BD) |

### Refrescar Access Token
```bash
POST /users/token
{
  "token": "<REFRESH_TOKEN>"
}
```

---

## 🎯 FLUJO TÍPICO

```
1. POST /users/register
   └─ Crear usuario nuevo

2. POST /users/login
   └─ Obtener accessToken + refreshToken

3. POST /tickets/createTicket
   ├─ Crear ticket
   └─ Guardar ID del ticket

4. POST /messages/sendMessage
   ├─ Enviar mensaje relacionado al ticket
   └─ Puede incluir imagen

5. GET /messages/getChats/:id
   └─ Ver conversaciones recientes

6. PUT /tickets/concludeTicket
   └─ Cerrar ticket cuando esté resuelto

7. POST /users/logout
   └─ Logout (elimina refresh token)
```

---

## 🔍 BÚSQUEDA RÁPIDA

### "Quiero..."

#### ...registrar un usuario
`POST /users/register` - [Ver ejemplo](#-ejemplos-rápidos)

#### ...logearme
`POST /users/login` - [Ver ejemplo](#-ejemplos-rápidos)

#### ...crear un ticket
`POST /tickets/createTicket` - [Ver ejemplo](#-ejemplos-rápidos)

#### ...enviar un mensaje
`POST /messages/sendMessage` - [Ver ejemplo](#-ejemplos-rápidos)

#### ...enviar una imagen
`POST /messages/sendMessage` (multipart) - [Ver ejemplo](#-ejemplos-rápidos)

#### ...cerrar un ticket
`PUT /tickets/concludeTicket` - [Ver ejemplo](#-ejemplos-rápidos)

#### ...reasignar un ticket
`PUT /tickets/reassignTicket` - [Ver ejemplo](#-ejemplos-rápidos)

#### ...obtener mis chats
`GET /messages/getChats/:id` - [Ver ejemplo](#-ejemplos-rápidos)

#### ...ver historial de un chat
`GET /messages/getMessages?ticket=X&user=Y&contact=Z` - [Ver ejemplo](#-ejemplos-rápidos)

#### ...refrescar mi token
`POST /users/token` - [Ver sección de tokens](#-tiempos-de-expiración)

#### ...cerrar sesión
`POST /users/logout` - [Ver ejemplo](#-ejemplos-rápidos)

---

## 📖 DOCUMENTACIÓN COMPLETA

→ [DOCUMENTATION.md](DOCUMENTATION.md) - Guía exhaustiva con:
- Setup y configuración
- Autenticación JWT detallada
- Todos los endpoints documentados
- Ejemplos curl
- Base de datos
- Notificaciones push
- Deployment
- Seguridad

→ [QUICK_START.md](QUICK_START.md) - Inicio rápido en 5 minutos

---

**Último actualizado:** Enero 2024
