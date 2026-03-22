package com.example.ticketsaplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ticketsaplication.data.local.UserPreferences
import com.example.ticketsaplication.data.remote.Repository
import com.example.ticketsaplication.ui.ChangePasswordScreen
import com.example.ticketsaplication.ui.ChatScreen
import com.example.ticketsaplication.ui.ContactsScreen
import com.example.ticketsaplication.LoginScreen
import com.example.ticketsaplication.ui.NewTicketScreen
import com.example.ticketsaplication.ui.theme.TicketsAplicationTheme
import com.example.ticketsaplication.ui.viewmodels.ChangePasswordViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("PERMISSIONS", "Notification permission GRANTED")
        } else {
            Log.d("PERMISSIONS", "Notification permission DENIED")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        enableEdgeToEdge()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM_TOKEN", "Current token: ${task.result}")
            }
        }

        setContent {
            TicketsAplicationTheme {
                Surface {
                    AppNav(intent = intent)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Preview
@Composable
private fun AppNav(intent: Intent? = null) {
    val context = LocalContext.current
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()

    val userPreferences = remember { UserPreferences(context) }
    val repo = remember { Repository() }

    NavHost(navController = nav, startDestination = "loading") {

        composable("loading") {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            LaunchedEffect(Unit) {
                val session = userPreferences.session.first()
                val startRoute: String

                if (session != null) {
                    repo.restoreSession(session.token, session.userId, session.username)

                    val contactId = intent?.getIntExtra("CHAT_CONTACT_ID", -1) ?: -1
                    val ticketId = intent?.getIntExtra("CHAT_TICKET_ID", -1) ?: -1

                    startRoute = if (contactId != -1) {
                        val contact = repo.getAllUsers().find { it.id == contactId }
                        val contactName = contact?.userName ?: "Chat"
                        val conversationId = if (ticketId != -1) "ticket_$ticketId" else contactId.toString()
                        intent?.removeExtra("CHAT_CONTACT_ID")
                        "chat/$conversationId/$contactName/${repo.userId}?contactId=$contactId"
                    } else {
                        val safeUser = URLEncoder.encode(session.username, StandardCharsets.UTF_8.toString())
                        "contacts/${session.userId}?username=$safeUser"
                    }
                } else {
                    startRoute = "login"
                }

                nav.navigate(startRoute) {
                    popUpTo("loading") { inclusive = true }
                }
            }
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = { username, userId, r ->
                    val token = repo.token ?: ""
                    scope.launch {
                        userPreferences.saveSession(token, userId, username)
                    }
                    val safeUser = URLEncoder.encode(username, StandardCharsets.UTF_8.toString())
                    nav.navigate("contacts/$userId?username=$safeUser") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                existingRepo = repo
            )
        }

        composable(
            route = "contacts/{userId}?username={username}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("username") { type = NavType.StringType; defaultValue = ""; nullable = true }
            )
        ) { backStack ->
            val encodedUsername = backStack.arguments?.getString("username").orEmpty()
            val username = URLDecoder.decode(encodedUsername, StandardCharsets.UTF_8.toString())
            val userId = backStack.arguments?.getInt("userId") ?: 0

            ContactsScreen(
                username = username,
                currentUserId = userId,
                repo = repo,
                onOpenChat = { id, name, contactId ->
                    nav.navigate("chat/$id/$name/$userId?contactId=$contactId")
                },
                onNewTicket = { idUsuario -> nav.navigate("newTicket/$idUsuario") },
                onNavigateToChangePassword = { nav.navigate("change_password") },
                onLogout = {
                    scope.launch {
                        userPreferences.clearSession()
                        repo.logout()
                        nav.navigate("login") { popUpTo(0) }
                    }
                }
            )
        }

        composable(
            route = "chat/{conversationId}/{name}/{userId}?contactId={contactId}",
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("userId") { type = NavType.IntType },
                navArgument("contactId") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStack ->
            val conversationId = backStack.arguments?.getString("conversationId").orEmpty()
            val name = backStack.arguments?.getString("name").orEmpty()
            val contactId = backStack.arguments?.getInt("contactId") ?: 0

            ChatScreen(
                repo = repo,
                conversationId = conversationId,
                contactId = contactId,
                name = name,
                onBack = { nav.popBackStack() }
            )
        }

        composable(
            "newTicket/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStack ->
            val userId = backStack.arguments?.getInt("userId") ?: 0

            NewTicketScreen(
                repo = repo,
                currentUserId = userId,
                onTicketCreated = { nav.popBackStack() },
                onNavigateBack = { nav.popBackStack() }
            )
        }

        composable("change_password") {
            val username = repo.username ?: ""
            val viewModel: ChangePasswordViewModel = viewModel(
                factory = ChangePasswordViewModel.factory(repo, username)
            )
            ChangePasswordScreen(
                viewModel = viewModel,
                onNavigateBack = { nav.popBackStack() }
            )
        }
    }
}
