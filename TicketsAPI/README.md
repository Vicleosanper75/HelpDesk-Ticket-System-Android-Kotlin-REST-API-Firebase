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

## 🚀 Inicio Rápido

### 1. Instalación
```bash
# Clonar repositorio
git clone <tu-repo>
cd TicketsAPI

# Instalar dependencias
npm install

# Crear base de datos
npm run db
```

### 2. Configuración
Crear archivo `.env`:
```env
PORT=3001
ACCESS_TOKEN_SECRET=tu_secret_super_largo_y_aleatorio
REFRESH_TOKEN_SECRET=tu_refresh_secret_super_largo_y_aleatorio
```

### 3. Ejecutar
```bash
# Modo desarrollo (con nodemon)
npm run dev

# Modo producción
npm start
```

La API estará disponible en `http://localhost:3001`

## �️ Extensiones Recomendadas para VS Code

Para una experiencia de desarrollo óptima, instala estas extensiones:

### **SQLite Viewer**
- **ID:** `alexcvzz.vscode-sqlite`
- **Función:** Visualizar y gestionar la base de datos SQLite directamente en VS Code
- **Instalación:** 
  1. Abre VS Code
  2. Ve a Extensiones (Ctrl + Shift + X)
  3. Busca "SQLite Viewer"
  4. Click en "Install"
- **Uso:** Click derecho en `database.db` → "Open with SQLite Viewer"

### **REST Client**
- **ID:** `humao.rest-client`
- **Función:** Ejecutar requests HTTP directamente desde el archivo `request.rest`
- **Instalación:**
  1. Abre VS Code
  2. Ve a Extensiones (Ctrl + Shift + X)
  3. Busca "REST Client"
  4. Click en "Install"
- **Uso:** Abre `request.rest` y verás botones "Send Request" ▶ sobre cada endpoint

**Ventajas:**
- 📊 Ver estructura de tablas en tiempo real
- 🧪 Hacer requests de prueba sin herramientas externas
- ⚡ Autocompletado y sintaxis coloreada
- 🔍 Visualizar respuestas JSON formateadas

## �📚 Documentación Completa

**→ [VER DOCUMENTACIÓN EXHAUSTIVA](DOCUMENTATION.md)**

La documentación completa incluye:
- ✅ Auténticación y flujos de token
- ✅ **Descripción de todos los 30+ endpoints** con ejemplos
- ✅ Estructura completa de la base de datos
- ✅ Ejemplos curl y JSON
- ✅ Manejo de errores
- ✅ Configuración de notificaciones push
- ✅ Guía de deployment (Heroku, AWS, Docker)
- ✅ Mejores prácticas de seguridad
- ✅ Solución de problemas comunes

## 🔐 Endpoints principales (resumen)

### Usuarios
- `POST /users/register` - Registro de nuevo usuario
- `POST /users/login` - Login (obtiene tokens)
- `POST /users/token` - Refrescar access token
- `GET /users` - Listar todos los usuarios
- `GET /users/:id` - Obtener usuario por ID

### Tickets
- `POST /tickets/createTicket` - Crear nuevo ticket
- `GET /tickets/:id` - Obtener ticket
- `PUT /tickets/concludeTicket` - Cerrar ticket
- `PUT /tickets/reassignTicket` - Reasignar responsable

### Mensajes
- `POST /messages/sendMessage` - Enviar mensaje (con imágenes)
- `GET /messages/getChats/:id` - Obtener últimas conversaciones
- `GET /messages/getMessages` - Obtener historial de chat
- `PUT /messages/markAsRead` - Marcar como leído

[**Ver todos los endpoints →**](DOCUMENTATION.md#-api-endpoints)

## 📦 Tech Stack

| Module | Version | Purpose |
|--------|---------|----------|
| express | 5.1.0 | Web framework |
| sqlite3 | 5.1.7 | Database |
| bcryptjs | 3.0.2 | Password encryption |
| jsonwebtoken | 9.0.2 | JWT authentication |
| firebase-admin | 13.6.0 | Push notifications |
| multer | 2.0.2 | File upload |
| socket.io | 4.8.1 | WebSockets (optional) |
| dotenv | 17.0.1 | Environment variables |

## 🏗️ Estructura del Proyecto

```
TicketsAPI/
├── src/
│   ├── controllers/       # Lógica de negocio
│   ├── models/            # Acceso a base de datos
│   └── routes/            # Definición de endpoints
├── uploads/               # Imágenes subidas
├── authMiddleware.js      # Autenticación JWT
├── firebaseAdmin.js       # Config Firebase
├── index.js              # Punto de entrada
├── DOCUMENTATION.md      # Documentación completa
├── .env                  # Variables de entorno
└── package.json
```

## 🛠️ Desarrollo

```bash
# Modo desarrollo (reinicia con nodemon)
npm run dev

# Crear/recriar base de datos
npm run db

# De producción
npm start
```

## 🚀 Deployment

Guías completas para:
- **Heroku** - Deploy automático desde GitHub
- **AWS EC2** - Instancia con PM2
- **Docker** - Containerización
- **DigitalOcean** - App Platform

[Ver guía de deployment →](DOCUMENTATION.md#-deployment)

## ⚙️ Configuración Firebase (Push Notifications)

1. Obtener `serviceAccountKey.json` de Firebase Console
2. Colocar en raíz del proyecto
3. Variables de entorno configuradas automáticamente

[Ver setup detallado →](DOCUMENTATION.md#-notificaciones-push)

## ⚠️ Solución de Problemas

**"Cannot find module 'sqlite3'"**
```bash
npm install sqlite3 --build-from-source
```

**"Port 3001 already in use"**
```bash
# Cambiar en .env: PORT=3002
```

**Más problemas:**
[Ver troubleshooting completo →](DOCUMENTATION.md#-solución-de-problemas)

## 🔒 Seguridad

- ✅ Contraseñas hasheadas con bcrypt
- ✅ Mensajes encriptados en BD
- ✅ JWT con expiración
- ✅ SQL prepared statements (sin inyección)
- ✅ Validación de entrada

Recomendaciones adicionales:
[Ver mejores prácticas →](DOCUMENTATION.md#-seguridad)

## 📞 Soporte

- 📖 [Ver documentación completa](DOCUMENTATION.md)
- 🐛 Reportar bugs en Issues
- 💬 Preguntas en Discussions

## 📄 Autor y Licencia

**Autor:** Victor Amaya  
**Licencia:** ISC  
**Versión:** 1.0.0

---

**¿Primera vez aquí?** → [Inicia aquí](DOCUMENTATION.md)  
**¿Ya tienes los tokens?** → [Referencia rápida de endpoints](DOCUMENTATION.md#-api-endpoints)  
**¿Quieres deployar?** → [Deployment guide](DOCUMENTATION.md#-deployment)