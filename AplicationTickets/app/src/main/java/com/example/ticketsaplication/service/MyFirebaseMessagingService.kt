package com.example.ticketsaplication.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ticketsaplication.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

// Objeto Singleton para comunicar eventos de notificación a la app en tiempo real.
// Se puede mover a su propio archivo (p.ej. "NotificationEvents.kt") para mayor orden.
object NotificationEvents {
    val messages = MutableSharedFlow<Map<String, String>>()
}

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // Aquí puedes añadir la lógica para enviar el nuevo token a tu servidor si es necesario.
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "Mensaje recibido. Datos: ${remoteMessage.data}")

        // --- Lógica para refrescar la UI en tiempo real ---
        // Emitimos los datos del mensaje a un Flow para que los ViewModels puedan escucharlo.
        if (remoteMessage.data.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                NotificationEvents.messages.emit(remoteMessage.data)
            }
        }

        // --- Lógica para mostrar la notificación (cuando la app está en segundo plano) ---
        // Construimos una notificación personalizada para añadir el Intent con los datos del chat.
        remoteMessage.notification?.let { notification ->
            val senderId = remoteMessage.data["senderId"]
            val ticketId = remoteMessage.data["ticketId"]
            showNotification(notification.title, notification.body, senderId, ticketId)
        }
    }

    private fun showNotification(title: String?, body: String?, senderId: String?, ticketId: String?) {
        // 1. Intent para abrir MainActivity al tocar la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Añadir datos para que MainActivity sepa qué chat abrir.
            senderId?.toIntOrNull()?.let { putExtra("CHAT_CONTACT_ID", it) }
            if (ticketId != null && ticketId != "null") {
                ticketId.toIntOrNull()?.let { putExtra("CHAT_TICKET_ID", it) }
            }
        }

        // El requestCode debe ser único para que los extras de cada notificación no se sobreescriban.
        val pendingIntent = PendingIntent.getActivity(
            this,
            Random.nextInt(), // ID aleatorio para el PendingIntent
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Construcción de la notificación
        val channelId = "tickets_notification_channel"
        val channelName = "Notificaciones de Tickets"
        val defaultSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title ?: "Nuevo Mensaje")
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Para que aparezca como Heads-Up

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH // Para que aparezca la notificación flotante
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 4. Mostrar la notificación
        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }
}
