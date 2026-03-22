# 🚀 Guía de Inicio Rápido

Si estás aquí es porque acabas de adquirir **Tickets API**. ¡Genial! En 5 minutos tendrás la API funcionando.

## ⏱️ 5 Minutos de Setup

### Paso 1: Instalar Node.js (si no lo tienes)
Descarga de [nodejs.org](https://nodejs.org) - Versión LTS (18+)

### Paso 2: Instalar dependencias
```bash
npm install
```

### Paso 3: Crear archivo .env
En la raíz del proyecto, crear archivo `.env`:

```env
PORT=3001
ACCESS_TOKEN_SECRET=MiSecretSuperLargoYAleatorioParaAccesToken123456789
REFRESH_TOKEN_SECRET=MiSecretSuperLargoYAleatorioParaRefreshToken123456789
```

⚠️ **IMPORTANTE:** Los secrets deben ser valores largos y aleatorios. Puedes generar uno aquí:
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

### Paso 4: Crear base de datos
```bash
npm run db
```

### Paso 5: Iniciar servidor
```bash
npm run dev
```

Deberías ver:
```
http://localhost:3001
```

✅ **¡Listo!** Tu API ya está corriendo.

---

## 🧪 Verificar que Funciona

### Opción A: Con cURL (Terminal)

```bash
# Prueba básica
curl http://localhost:3001/
# Deberías ver: "Hola Mundo!!!"
```

### Opción B: Con Postman

1. Descargar [Postman](https://postman.com)
2. Crear nueva solicitud
3. URL: `http://localhost:3001/`
4. Click "Send"

### Opción C: Con nuestro archivo request.rest

Si usas VS Code con extensión REST Client:
1. Abre `request.rest`
2. Click en "Send Request"

---

## � Extensiones VS Code Recomendadas

Para una experiencia óptima de desarrollo, instala estas extensiones en VS Code:

### **1. SQLite Viewer** 📊
- **ID:** `alexcvzz.vscode-sqlite`
- **Para qué:** Ver y explorar la base de datos directamente en VS Code
- **Instalación:**
  - Abre VS Code → Extensiones (Ctrl + Shift + X)
  - Busca "SQLite Viewer"
  - Click "Install"
- **Cómo usar:** Click derecho en `database.db` → "Open with SQLite Viewer"
- **Ventajas:** Ves todas las tablas, datos, relaciones en tiempo real sin necesidad de DB Browser

### **2. REST Client** 🧪
- **ID:** `humao.rest-client`
- **Para qué:** Hacer requests HTTP directamente desde `request.rest`
- **Instalación:**
  - Abre VS Code → Extensiones (Ctrl + Shift + X)
  - Busca "REST Client"
  - Click "Install"
- **Cómo usar:** Abre `request.rest` y verás botones "▶ Send Request" sobre cada endpoint
- **Ventajas:** Prueba endpoints sin salir de VS Code, respuestas formateadas

**¡Con estas dos extensiones tienes todo lo que necesitas para desarrollar completo!**

---

## �📝 Primeros Pasos: Registrarse y Loguearse

### 1️⃣ Registrar usuario

```bash
curl -X POST http://localhost:3001/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "mi_usuario",
    "fullname": "Mi Nombre Completo",
    "pwd": "MiPassword123!",
    "role": 1
  }'
```

**Respuesta esperada:**
```json
{
  "id": 1
}
```

### 2️⃣ Loguearse

```bash
curl -X POST http://localhost:3001/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "mi_usuario",
    "pwd": "MiPassword123!"
  }'
```

**Respuesta esperada:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Guarda estos tokens**, los necesitarás para todas las siguientes solicitudes.

### 3️⃣ Usar el Token para acceder a API protegida

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:3001/users \
  -H "Authorization: Bearer $TOKEN"
```

**Deberías ver la lista de usuarios.**

---

## 📦 Roles de Usuario Disponibles

| ID | Nombre | Descripción |
|----|--------|------------|
| 1 | Operaria | Usuario estándar |
| 2 | Soporte | Equipo de soporte |
| 3 | Sistemas | Administrador |

---

## 🎟️ Crear tu Primer Ticket

```bash
TOKEN="tu_token_aqui"

curl -X POST http://localhost:3001/tickets/createTicket \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "createdBy": 1,
    "title": "Mi primer ticket",
    "responsible": 2
  }'
```

**Respuesta:**
```json
{
  "ID": 1
}
```

---

## 💬 Enviar tu Primer Mensaje

### Mensaje de Texto
```bash
TOKEN="tu_token_aqui"

curl -X POST http://localhost:3001/messages/sendMessage \
  -H "Authorization: Bearer $TOKEN" \
  -F "ticket=1" \
  -F "sender=1" \
  -F "receiver=2" \
  -F "content=Hola, necesito ayuda con este ticket"
```

### Mensaje con Imagen
```bash
TOKEN="tu_token_aqui"

curl -X POST http://localhost:3001/messages/sendMessage \
  -H "Authorization: Bearer $TOKEN" \
  -F "ticket=1" \
  -F "sender=1" \
  -F "receiver=2" \
  -F "content=Mira el error en esta imagen" \
  -F "image=@/ruta/a/tu/imagen.png"
```

---

## 🔄 Flujo Completo de Ejemplo

```bash
# 1. Registrar Usuario A
curl -X POST http://localhost:3001/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","fullname":"Alice","pwd":"password123","role":1}'
# Respuesta: {"id": 1}

# 2. Registrar Usuario B
curl -X POST http://localhost:3001/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"bob","fullname":"Bob","pwd":"password123","role":2}'
# Respuesta: {"id": 2}

# 3. Alice se loguea
curl -X POST http://localhost:3001/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","pwd":"password123"}'
# Respuesta: {"accessToken":"TOKEN_A","refreshToken":"REFRESH_A"}

# 4. Alice crea un ticket asignado a Bob
TOKEN_A="TOKEN_AQUI"
curl -X POST http://localhost:3001/tickets/createTicket \
  -H "Authorization: Bearer $TOKEN_A" \
  -H "Content-Type: application/json" \
  -d '{"createdBy":1,"title":"Bug en login","responsible":2}'
# Respuesta: {"ID": 1}

# 5. Alice le envía mensaje a Bob
curl -X POST http://localhost:3001/messages/sendMessage \
  -H "Authorization: Bearer $TOKEN_A" \
  -F "ticket=1" \
  -F "sender=1" \
  -F "receiver=2" \
  -F "content=Por favor revisa este ticket"

# 6. Bob se loguea
curl -X POST http://localhost:3001/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"bob","pwd":"password123"}'
# Respuesta: {"accessToken":"TOKEN_B","refreshToken":"REFRESH_B"}

# 7. Bob obtiene su lista de chats
TOKEN_B="TOKEN_AQUI"
curl -X GET http://localhost:3001/messages/getChats/2 \
  -H "Authorization: Bearer $TOKEN_B"
# Respuesta: muestra mensaje de Alice

# 8. Bob obtiene los mensajes del ticket
curl -X GET "http://localhost:3001/messages/getMessages?ticket=1&user=2&contact=1" \
  -H "Authorization: Bearer $TOKEN_B"

# 9. Bob marca el ticket como concluido
curl -X PUT http://localhost:3001/tickets/concludeTicket \
  -H "Authorization: Bearer $TOKEN_B" \
  -H "Content-Type: application/json" \
  -d '{"id":1}'
```

---

## 🆘 Problemas Comunes

### "Cannot GET /"
**Problema:** La API no inició correctamente
**Solución:**
```bash
npm install
npm run db
npm run dev
```

### "nodemon: command not found"
**Problema:** nodemon no está instalado globalmente
**Solución:**
```bash
npm install -g nodemon
# O usa directamente:
npm run dev
```

### "Error: ENOENT: no such file or directory, open '.env'"
**Problema:** No creaste el archivo `.env`
**Solución:** Crea un archivo `.env` en la raíz con el contenido mostrado arriba

### "Port 3001 already in use"
**Problema:** Otro proceso usa el puerto 3001
**Solución:** Cambia en `.env`:
```env
PORT=3002
```

### "Token inválido"
**Problema:** El token expiró (9 horas de duración)
**Solución:** Usa el refresh token para obtener uno nuevo:
```bash
curl -X POST http://localhost:3001/users/token \
  -H "Content-Type: application/json" \
  -d '{"token":"tu_refresh_token"}'
```

---

## 📚 Documentación Completa

Para información detallada sobre:
- ✅ Todos los 30+ endpoints
- ✅ Estructura completa de base de datos
- ✅ Notificaciones push con Firebase
- ✅ Deployment en producción
- ✅ Seguridad y mejores prácticas

**→ [VER DOCUMENTACIÓN COMPLETA](DOCUMENTATION.md)**

---

## 🎯 Próximos Pasos

### Para Desarrollo Local
1. ✅ API funcionando
2. ⏭️ Conecta tu app Android/Web a la API
3. ⏭️ Implementa autenticación (guardar tokens)
4. ⏭️ Crea interfaz de usuarios

### Para Producción
1. ✅ API funcionando localmente
2. ⏭️ Configura Firebase para notificaciones push
3. ⏭️ Elige plataforma de hosting (Heroku, AWS, Docker)
4. ⏭️ Lee [guía de deployment](DOCUMENTATION.md#-deployment)
5. ⏭️ Configura HTTPS y dominio

### Para Personalización
1. Modifica campos en base de datos (`SQLite.sql`)
2. Actualiza modelos en `src/models/`
3. Agrega nuevas rutas en `src/routes/`
4. Implementa lógica en `src/controllers/`

---

## 📞 ¿Tienes Dudas?

- 📖 [Ver documentación completa](DOCUMENTATION.md)
- 🐛 [Solución de problemas](DOCUMENTATION.md#-solución-de-problemas)
- 💡 [Ejemplos de uso](DOCUMENTATION.md#-ejemplos-de-uso)

---

**¡Que disfrutes la API! 🎉**
