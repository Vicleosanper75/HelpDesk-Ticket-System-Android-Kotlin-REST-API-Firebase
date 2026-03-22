# 🎫 Tickets Application Template - Documentation

Welcome to the **Tickets Application Template**. This project is a robust, scalable, and ready-to-customize solution for managing support and system tickets. Built with modern Android technologies, it follows clean architecture principles and the MVVM pattern.

---

## 🏗️ Technical Architecture

### 1. Project Overview
This is a professional Android template for a Ticket Management System. It features role-based access control, real-time messaging, and multimedia support.

**Technical Stack:**
*   **Language:** Kotlin 1.9+
*   **UI Framework:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture (Data/UI layers)
*   **Networking:** Retrofit 2 + OkHttp + Moshi (JSON)
*   **Local Storage:** Jetpack DataStore (Preferences)
*   **Async Operations:** Kotlin Coroutines & Flow
*   **Image Loading:** Coil
*   **Notifications:** Firebase Cloud Messaging (FCM)

---

### 2. Deep Dive into Architecture

#### 📁 `data/` Layer
*   **`remote/`**: 
    *   `ApiService.kt`: The core interface defining all REST endpoints. Uses Bearer token authentication.
    *   `ApiModels.kt`: Contains all Data Transfer Objects (DTOs). Look here to see the JSON structure expected from your backend.
    *   `Repository.kt`: The single source of truth. It manages API calls, session state (token, userId), and coordinates between the UI and the server.
    *   `RetrofitProvider.kt`: Singleton that configures the Retrofit instance, OkHttpClient (with logging), and the `BASE_URL`.
*   **`local/`**:
    *   `UserPreferences.kt`: Manages persistent session data using **Jetpack DataStore**. It stores the JWT Token, User ID, and Username securely.

#### 📁 `ui/` Layer (Presentation)
*   **`viewmodels/`**: Each screen has its own ViewModel. They handle the UI state (`StateFlow`) and business logic, surviving configuration changes.
*   **`theme/`**: Centralized Material 3 styling (Colors, Typography, Shapes).
*   **Screens**: Built entirely with Compose. Key screens include:
    *   `ContactsScreen.kt`: The main dashboard with dynamic tabs (Assigned, Created, Completed).
    *   `ChatScreen.kt`: A sophisticated chat interface with image support and ticket reassignment logic.
    *   `NewTicketScreen.kt`: Form-based ticket creation with camera/gallery integration.

---

### 3. Key Logic Workflows

#### 🔐 Authentication & Session
1.  **Login:** The `LoginViewModel` calls `repo.login()`. Upon success, the JWT token and user info are saved in `UserPreferences` (DataStore).
2.  **Auto-Login:** In `MainActivity.kt`, the app checks `UserPreferences` during the "loading" state. If a token exists, it restores the session and navigates to the Dashboard.
3.  **FCM Sync:** On every successful login, the app fetches the Firebase token and sends it to the backend via `repo.updateFcmToken()` to enable push notifications for that specific device.

#### 🎫 Ticket Lifecycle
*   **Creation:** Requires a title, a target area (Support/Systems), a responsible person, a text description, and mandatory photo evidence.
*   **Reassignment:** 
    *   *Same Area:* You can pick any active user from the same area.
    *   *Different Area:* The ticket is automatically assigned to the "Area Manager" (defined via `roleBossMap` in `ChatViewModel`).
*   **Conclusion:** Only the assigned responsible person can mark a ticket as "Completed".

#### 💬 Messaging System
*   The chat supports **text** and **multipart images**.
*   Messages are prefixed with `[image]` in the database to distinguish them from text.
*   **Read Receipts:** The app calls `markAsRead` when a user opens a chat where they are either the creator or the responsible party.

---

## 🚀 Customization & Extension

### 📡 Backend Configuration (URL & ngrok)
The backend connection is centralized in `RetrofitProvider.kt`.

**Using ngrok for Development:**
Currently, the `BASE_URL` uses an **ngrok** URL. ngrok is a tool that creates a secure tunnel from the public internet to your local machine. This is extremely useful for:
*   Testing push notifications (FCM) which require a public callback.
*   Running the app on a physical device without a deployed server.
*   Testing the API before buying a domain/hosting.

**To change to your production server:**
Go to `com.example.ticketsaplication.data.remote.RetrofitProvider` and modify:
```kotlin
internal const val BASE_URL = "https://your-api-url.com"
```
*Note: Also update the `BASE_URL` inside `ChatScreen.kt` if you are hosting images on a different CDN.*

---

### 🔥 Firebase Configuration
To use notifications and analytics, you must link your own Firebase project:
1.  **`google-services.json`**: Replace the existing file in the `app/` folder with the one downloaded from your Firebase Console.
2.  **Notification Logic**: Custom handling of incoming messages can be found in `com.example.ticketsaplication.service.MyFirebaseMessagingService`.
3.  **Token Registration**: The code that sends the device token to your server is located in `LoginViewModel.kt` within the login flow.

---

### 🎨 Modifying UI & Roles
*   **New Roles:** 
    1. Add the new role ID to the logic in `ContactsViewModel.kt` (check `isSupportOrSystems`).
    2. Update the `roleBossMap` in `ChatViewModel.kt` if the new role requires a manager for reassignments.
*   **Branding:** 
    *   **Colors:** Edit `ui/theme/Color.kt`.
    *   **App Name:** Change `app_name` in `res/values/strings.xml`.
    *   **Logo:** Replace `res/drawable/logo.png` and update icons via `New > Image Asset`.

---

## 📱 Deployment Checklist
1.  **Firebase:** Replace `google-services.json`.
2.  **Permissions:** Ensure `AndroidManifest.xml` has the correct `FileProvider` authorities if you change the package name.
3.  **Proguard:** If obfuscating code, add rules for Moshi DTOs and Retrofit interfaces to prevent crashes.

---

**This documentation provides everything a developer needs to take this template and turn it into a production-ready application.**
