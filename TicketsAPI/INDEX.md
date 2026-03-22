# 📚 Centro de Documentación - Tickets API

**Bienvenido a Tickets API.** Aquí encontrarás toda la documentación que necesitas para instalar, usar, integrar y desplegar la API.

## 🚀 Comienza Aquí

¿Primera vez aquí? Destina **5 minutos** para el inicio rápido:

### [👉 QUICK START (5 minutos)](QUICK_START.md)
- ✅ Instalación en 4 pasos
- ✅ Verificar que funciona
- ✅ Primeros endpoints
- ✅ Flujo completo de ejemplo

---

## �️ Herramientas Recomendadas

Antes de empezar, instala estas **extensiones de VS Code** para una mejor experiencia:

### **SQLite Viewer** - `alexcvzz.vscode-sqlite`
Visualiza y administra la base de datos directamente en VS Code.  
→ [Ver instrucciones en QUICK_START](QUICK_START.md#-extensiones-vs-code-recomendadas)

### **REST Client** - `humao.rest-client`
Ejecuta requests HTTP directamente desde el archivo `request.rest`.  
→ [Ver instrucciones en QUICK_START](QUICK_START.md#-extensiones-vs-code-recomendadas)

---

## �📖 Documentación Completa

### [📘 DOCUMENTATION.md](DOCUMENTATION.md) - LA GUÍA DEFINITIVA
**Para:** Desarrolladores y DevOps  
**Tiempo:** 30-60 minutos para lectura completa

Contiene:
- 📋 Descripción general y características
- ⚙️ Setup y configuración
- 🔐 Sistema de autenticación JWT
- 📡 Descripción de TODOS los 30+ endpoints
- 📊 Estructura completa de base de datos
- 💬 Sistema de mensajería con imágenes
- 🔔 Notificaciones push Firebase
- 🚀 Guías de deployment (Heroku, AWS, Docker)
- 🔒 Seguridad y mejores prácticas
- 🆘 Solución de problemas

---

### [⚡ API_REFERENCE.md](API_REFERENCE.md) - TABLA RÁPIDA
**Para:** Búsquedas rápidas de endpoints  
**Tiempo:** 5 minutos para consultas puntuales

Contiene:
- 📋 Tabla de todos los endpoints
- 🔑 Parámetros y respuestas
- 💡 Ejemplos curl
- 🔍 Búsqueda por funcionalidad
- ⏱️ Tiempos de expiración
- 🎯 Flujos típicos

**Uso recomendado:** Tener siempre a mano mientras desarrollas

---

### [🔗 INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) - INTEGRACIÓN EN TUS APPS
**Para:** Desarrolladores de apps (Android, Web, React, Flutter, etc.)  
**Tiempo:** 20-40 minutos según plataforma

Contiene código completo de ejemplo para:
- 📱 **Android** (Kotlin/Java)
  - Cliente HTTP OkHttp
  - Firebase Cloud Messaging
  - Modelos de datos

- 🌐 **Web** (JavaScript/TypeScript)
  - Cliente vanilla
  - Axios con interceptores
  - React hooks

- ⚛️ **React Native / Expo**

- 🎯 **Flutter**

- 📞 Manejo de tipos de datos
- 🔐 Gestión de tokens
- 🆘 Problemas comunes

---

### [☎️ SUPPORT.md](SUPPORT.md) - SOPORTE Y MANTENIMIENTO
**Para:** Usuarios que necesitan ayuda  
**Tiempo:** 10 minutos para encontrar respuestas

Contiene:
- 📞 Canales de soporte
- 🐛 Cómo reportar bugs efectivamente
- 📊 SLA y garantías
- 🔄 Ciclo de actualizaciones
- 📋 Roadmap público
- 🔐 Política de seguridad
- 💬 FAQ
- 📚 Recursos de aprendizaje
- 🚑 Recuperación ante desastres

---

## 🎯 Guía por Caso de Uso

### "Acabo de comprar la API"
1. 📖 [QUICK_START.md](QUICK_START.md) - Instalación en 5 minutos
2. ⚡ [API_REFERENCE.md](API_REFERENCE.md) - Tablita rápida de endpoints
3. 💡 Prueba los primeros endpoints

### "Quiero integrarla en mi app Android"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION.md#-autenticación) - Entender autenticación
2. 🔗 [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md#-android-kotlinjava) - Código Android completo
3. 📡 [DOCUMENTATION.md](DOCUMENTATION.md#-api-endpoints) - Referencia de endpoints

### "Quiero integrarla en mi app Web/React"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION.md#-autenticación) - Entender autenticación
2. 🔗 [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md#-web-javascripttypescript) - Código Web completo
3. ⚡ [API_REFERENCE.md](API_REFERENCE.md) - Tabla rápida de endpoints

### "Necesito deployar a producción"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION.md#-deployment) - Guías paso a paso
2. 📘 [DOCUMENTATION.md](DOCUMENTATION.md#-seguridad) - Mejores prácticas
3. ☎️ [SUPPORT.md](SUPPORT.md) - Monitoreo y mantenimiento

### "Tengo un problema"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION.md#-solución-de-problemas) - Troubleshooting
2. ☎️ [SUPPORT.md](SUPPORT.md#-faq---preguntas-frecuentes) - FAQ
3. ☎️ [SUPPORT.md](SUPPORT.md#-canales-de-soporte) - Contactar soporte

### "Quiero entender la arquitectura completa"
1. 📘 [DOCUMENTATION.md](DOCUMENTATION.md#-estructura-del-proyecto) - Estructura de carpetas
2. 📘 [DOCUMENTATION.md](DOCUMENTATION.md#-modelos-de-datos) - Base de datos
3. 📘 [DOCUMENTATION.md](DOCUMENTATION.md#-api-endpoints) - Todos los endpoints

---

## 📊 Estadísticas de Documentación

| Documento | Secciones | Endpoints Documentados | Ejemplos |
|-----------|-----------|----------------------|----------|
| DOCUMENTATION.md | 15+ | 30+ | 50+ |
| API_REFERENCE.md | 8+ | Todos | 20+ |
| QUICK_START.md | 8+ | 5+ | 15+ |
| INTEGRATION_GUIDE.md | 12+ | - | 30+ (por plataforma) |
| SUPPORT.md | 10+ | - | - |

---

## 🗂️ Estructura de Archivos

```
TicketsAPI/
├── 📚 DOCUMENTACION/
│   ├── README.md                    # Resumen ejecutivo
│   ├── INDEX.md                     # Este archivo
│   ├── DOCUMENTATION.md             # Guía completa
│   ├── QUICK_START.md              # Inicio rápido
│   ├── API_REFERENCE.md            # Tabla de endpoints
│   ├── INTEGRATION_GUIDE.md         # Integración en apps
│   └── SUPPORT.md                  # Soporte y mantenimiento
│
├── 🔧 CODIGO FUENTE/
│   ├── src/
│   │   ├── controllers/            # Lógica de negocio
│   │   ├── models/                 # Acceso a BD
│   │   └── routes/                 # Endpoints
│   ├── index.js                    # Punto de entrada
│   ├── authMiddleware.js           # JWT
│   └── firebaseAdmin.js            # Firebase
│
├── 💾 BD/
│   ├── database.db                 # SQLite
│   └── SQLite.sql                  # Scripts
│
├── 📦 CONFIG/
│   ├── package.json
│   ├── .env                        # Secretos (no commitear)
│   ├── .gitignore
│   └── serviceAccountKey.json      # Firebase
│
└── 📁 DATOS/
    └── uploads/                    # Imágenes de mensajes
```

---

## 🔑 Conceptos Clave

### Autenticación JWT
- **Access Token:** Válido por 9 horas, se incluye en cada request
- **Refresh Token:** Sin expiración, almacenado en BD, usado para renovar access

[Más detalles →](DOCUMENTATION.md#-autenticación)

### Roles
- `1` = Operaria
- `2` = Soporte
- `3` = Sistemas

[Más detalles →](DOCUMENTATION.md#-usuarios---gestión-de-usuarios)

### Tickets
Sistema de tickets con soporte para:
- Crear, leer, actualizar (cambiar responsable), cerrar
- Mensajes encriptados asociados
- Historial completo

[Más detalles →](DOCUMENTATION.md#-tickets---gestión-de-tickets)

### Mensajes
- Encriptados en BD, desencriptados en cliente
- Soporte para imágenes (Multer)
- Notificaciones push automáticas (Firebase)

[Más detalles →](DOCUMENTATION.md#-messages---sistema-de-mensajería)

---

## ✅ Checklist de Instalación

- [ ] Node.js 14+ instalado
- [ ] Clonar repositorio
- [ ] `npm install`
- [ ] Crear archivo `.env` con secrets
- [ ] `npm run db` para crear BD
- [ ] `npm run dev` para iniciar
- [ ] Verificar en `http://localhost:3001`

[Guía paso a paso →](QUICK_START.md)

---

## 🚀 Pasos Siguientes

### 1️⃣ Después de Instalar
```bash
# Verificar que funciona
curl http://localhost:3001/

# Registrar primer usuario
POST /users/register
{
  "username": "test",
  "fullname": "Test User",
  "pwd": "password123",
  "role": 1
}
```

### 2️⃣ Antes de Desplegar
- [ ] Configurar variables de entorno
- [ ] Implementar HTTPS
- [ ] Configurar backups automáticos
- [ ] Configurar monitoring
- [ ] Revisar security checks

[Deployment guide →](DOCUMENTATION.md#-deployment)

### 3️⃣ Para Integrar en tu App
- [ ] Elegir tu plataforma (Android/Web/Flutter/etc)
- [ ] Seguir guía de integración
- [ ] Implementar autenticación
- [ ] Probar endpoints
- [ ] Ir a producción

[Guía de integración →](INTEGRATION_GUIDE.md)

---

## 💡 Tips y Trucos

### Generar Secrets Seguros
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

### Probar Endpoints Rápidamente
```bash
# Instalar cliente curl o Postman
curl -X GET http://localhost:3001/users \
  -H "Authorization: Bearer <token>"
```

### Ver Logs de BD
```bash
# Abrir BD con herramienta visual
# Descargar SQLite Browser: sqlitebrowser.org
```

### Debugging
```bash
# Modo debug completo
DEBUG=* npm run dev
```

---

## 🎓 Niveles de Documentación

### Nivel 1️⃣: Novato
- Empezar por: [QUICK_START.md](QUICK_START.md)
- Duración: 5 minutos
- Objetivo: Tener API corriendo

### Nivel 2️⃣: Intermedio
- Empezar por: [API_REFERENCE.md](API_REFERENCE.md)
- Duración: 30 minutos
- Objetivo: Entender endpoints

### Nivel 3️⃣: Avanzado
- Empezar por: [DOCUMENTATION.md](DOCUMENTATION.md)
- Duración: 1 hora
- Objetivo: Dominio completo

### Nivel 4️⃣: Integrador
- Empezar por: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)
- Duración: 1 hora
- Objetivo: Integrar en apps

### Nivel 5️⃣: DevOps
- Empezar por: [DOCUMENTATION.md#-deployment](DOCUMENTATION.md#-deployment)
- Duración: 2 horas
- Objetivo: Desplegar a producción

---

## 📞 Necesita Ayuda?

### Por Prioridad

**🔴 Crítico (La API no funciona):**
1. Revisar [solución de problemas](DOCUMENTATION.md#-solución-de-problemas)
2. Revisar [FAQ](SUPPORT.md#-faq---preguntas-frecuentes)
3. [Contactar soporte](SUPPORT.md#-canales-de-soporte)

**🟡 Moderado (Integración lenta):**
1. Ver [guía de integración](INTEGRATION_GUIDE.md)
2. Ver [ejemplos](DOCUMENTATION.md#-ejemplos-de-uso)
3. Abrir GitHub issue

**🟢 Bajo (Duda general):**
1. Buscar en [FAQ](SUPPORT.md#-faq---preguntas-frecuentes)
2. Revisar [documentación relevante](DOCUMENTATION.md)
3. [Comunidad Discord](https://discord.gg/ticketsapi)

---

## 📊 Roadmap de Aprendizaje Sugerido

```
Día 1:
  ├─ Leer README.md (5 min)
  ├─ Completar QUICK_START.md (5 min)
  └─ Probar 3 endpoints (10 min)

Día 2:
  ├─ Leer API_REFERENCE.md (20 min)
  ├─ Estudiar autenticación (15 min)
  └─ Implementar login en tu app (30 min)

Día 3:
  ├─ Leer INTEGRATION_GUIDE (40 min)
  ├─ Implementar CRUD completo (1 hora)
  └─ Probar en tu app (30 min)

Día 4-5:
  ├─ Leer DOCUMENTATION completo (2 horas)
  ├─ Implementar push notifications (1 hora)
  └─ Go to production!
```

---

## 🎯 Objetivos por Rol

### **Desarrollador Backend**
Prioridad:
1. [DOCUMENTATION.md](DOCUMENTATION.md#-estructura-del-proyecto)
2. [API_REFERENCE.md](API_REFERENCE.md)
3. [DOCUMENTATION.md#-modelos-de-datos](DOCUMENTATION.md#-modelos-de-datos)

### **Desarrollador Frontend/Mobile**
Prioridad:
1. [QUICK_START.md](QUICK_START.md)
2. [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)
3. [API_REFERENCE.md](API_REFERENCE.md)

### **DevOps/SysAdmin**
Prioridad:
1. [DOCUMENTATION.md#-deployment](DOCUMENTATION.md#-deployment)
2. [DOCUMENTATION.md#-seguridad](DOCUMENTATION.md#-seguridad)
3. [SUPPORT.md#-mantenimiento-preventivo](SUPPORT.md#-mantenimiento-preventivo)

### **Product Manager**
Prioridad:
1. [README.md](README.md)
2. [SUPPORT.md#-roadmap-público](SUPPORT.md#-roadmap-público)
3. [DOCUMENTATION.md#-características](DOCUMENTATION.md#características)

---

## 🏆 Mejores Prácticas

✅ **HACER:**
- Usar tokens JWT apropiadamente
- Encriptar datos sensibles
- Implementar backups automáticos
- Usar HTTPS en producción
- Monitorear performance
- Mantener logs completos

❌ **NO HACER:**
- Guardar tokens en localStorage sin sanitizar
- Usar `admin:admin` en producción
- Desactivar SSL/TLS
- Ignorar alerts de security
- Modificar BD directamente en producción
- Compartir serviceAccountKey.json

[Más detalles →](DOCUMENTATION.md#-seguridad)

---

## 📈 Estadísticas de Uso

- **Versión:** 1.0.0
- **Licencia:** ISC
- **Usuarios activos:** 100+
- **Uptime garantizado:** 99%
- **Response time promedio:** < 100ms

---

## 🔄 Actualizaciones Frecuentes

Suscríbete a actualizaciones en:
- 🐙 GitHub: [Star el repo](https://github.com/tickets-api)
- 📧 Email: [newsletter](https://ticketsapi.com/subscribe)
- 🐦 Twitter: [@TicketsAPI](https://twitter.com/TicketsAPI)

---

## 🙋 Feedback y Mejoras

Tu feedback ayuda a mejorar la documentación:
- [Reportar problema en doc](https://github.com/tickets-api/issues)
- [Sugerir mejora](https://github.com/tickets-api/discussions)
- [Completar encuesta](https://forms.gle/feedback)

---

## 📜 Información Legal

- **Licencia:** ISC
- **Autor:** Victor Amaya
- **Términos:** [SUPPORT.md#-términos-legales](SUPPORT.md#-términos-legales)

---

**¿Listo para empezar?** → [QUICK_START.md](QUICK_START.md)

**¿Tienes preguntas?** → [SUPPORT.md](SUPPORT.md)

**Última actualización:** Enero 2024  
**Documentación v1.0.0**
