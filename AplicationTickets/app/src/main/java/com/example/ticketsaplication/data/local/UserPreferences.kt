package com.example.ticketsaplication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para acceder al DataStore desde cualquier Context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

// Modelo simple para devolver los datos juntos
data class UserSession(val token: String, val userId: Int, val username: String)

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_USER_ID = intPreferencesKey("user_id")
        val KEY_USERNAME = stringPreferencesKey("username")
    }

    // Flujo que emite la sesión actual (o null si no hay nadie logueado)
    val session: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val token = prefs[KEY_TOKEN]
        val userId = prefs[KEY_USER_ID]
        val username = prefs[KEY_USERNAME]

        if (token != null && userId != null && username != null) {
            UserSession(token, userId, username)
        } else {
            null
        }
    }

    // Guardar sesión (Login)
    suspend fun saveSession(token: String, userId: Int, username: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
        }
    }

    // Borrar sesión (Logout)
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
