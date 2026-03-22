package com.example.ticketsaplication.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.SubcomposeAsyncImage
import com.example.ticketsaplication.data.remote.Repository
import com.example.ticketsaplication.data.remote.RoleDto
import com.example.ticketsaplication.data.remote.UserDto
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTicketScreen(
    repo: Repository,
    currentUserId: Int,
    onTicketCreated: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Roles (Areas)
    var roles by remember { mutableStateOf<List<RoleDto>>(emptyList()) }
    var selectedRole by remember { mutableStateOf<RoleDto?>(null) }
    var expandedRoles by remember { mutableStateOf(false) }

    // Usuarios
    var allUsers by remember { mutableStateOf<List<UserDto>>(emptyList()) }
    var filteredUsers by remember { mutableStateOf<List<UserDto>>(emptyList()) }
    var selectedUser by remember { mutableStateOf<UserDto?>(null) }
    var expandedUsers by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) } // For initial data
    var isCreating by remember { mutableStateOf(false) } // For ticket creation
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Foto obligatoria
    var selectedImage by remember { mutableStateOf<File?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImage = uriToFile(context, it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            selectedImage = uriToFile(context, tempCameraUri!!)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val (uri, _) = createImageFile(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            // Solo permitimos Soporte y Sistemas
            val allowedRoles = listOf("Soporte", "Sistemas", "Support", "Systems")
            roles = repo.getRoles().filter { role ->
                allowedRoles.any { it.equals(role.roleName, ignoreCase = true) }
            }
            allUsers = repo.getAllUsers()
        } catch (e: Exception) {
            error = "Error loading initial data"
        }
        isLoading = false
    }

    LaunchedEffect(selectedRole, allUsers) {
        if (selectedRole != null) {
            // Filtramos por rol y que el usuario esté activo (ACTIVE != 0)
            filteredUsers = allUsers.filter { it.role == selectedRole!!.id && it.active != 0 }
        } else {
            filteredUsers = emptyList()
        }
        // Reset user selection when role changes
        selectedUser = null
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Ticket") },
                navigationIcon = {
                    IconButton(onClick = { if (!isCreating) onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Ticket title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = isCreating
            )

            // Dropdown for Roles (Areas)
            ExposedDropdownMenuBox(
                expanded = expandedRoles,
                onExpandedChange = { if (!isCreating) expandedRoles = !expandedRoles }
            ) {
                OutlinedTextField(
                    value = selectedRole?.roleName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Area") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoles)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    enabled = !isCreating
                )
                ExposedDropdownMenu(
                    expanded = expandedRoles,
                    onDismissRequest = { expandedRoles = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.roleName) },
                            onClick = {
                                selectedRole = role
                                expandedRoles = false
                            },
                            enabled = !isCreating
                        )
                    }
                }
            }


            // Dropdown for Users (now shows filteredUsers)
            ExposedDropdownMenuBox(
                expanded = expandedUsers,
                onExpandedChange = { if (selectedRole != null && !isCreating) expandedUsers = !expandedUsers }
            ) {
                OutlinedTextField(
                    value = selectedUser?.fullName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Assign to responsible") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUsers)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    enabled = selectedRole != null && !isCreating
                )
                ExposedDropdownMenu(
                    expanded = expandedUsers,
                    onDismissRequest = { expandedUsers = false }
                ) {
                    filteredUsers.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.fullName.toString()) },
                            onClick = {
                                selectedUser = user
                                expandedUsers = false
                            },
                            enabled = !isCreating
                        )
                    }
                }
            }

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Description / First message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                singleLine = false,
                readOnly = isCreating
            )

            // Sección de adjuntar foto (Obligatoria)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Photo evidence (Required)", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            val permission = Manifest.permission.CAMERA
                            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                val (uri, _) = createImageFile(context)
                                tempCameraUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(permission)
                            }
                        },
                        enabled = !isCreating,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CameraAlt, null)
                        Spacer(Modifier.size(8.dp))
                        Text("Camera")
                    }
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        enabled = !isCreating,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, null)
                        Spacer(Modifier.size(8.dp))
                        Text("Gallery")
                    }
                }

                if (selectedImage != null) {
                    Spacer(Modifier.height(8.dp))
                    Box(modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally)) {
                        SubcomposeAsyncImage(
                            model = selectedImage,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { selectedImage = null },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.Black.copy(0.5f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            if (isLoading) {
                Text("Loading areas and users...")
            }

            error?.let {
                Text(it, color = Color.Red)
            }


            Button(
                onClick = {
                    scope.launch {
                        isCreating = true
                        error = null
                        try {
                            // Paso 1: Crear el ticket
                            val newTicket = repo.createTicket(
                                createdBy = currentUserId,
                                title = title,
                                responsible = selectedUser!!.id
                            )

                            // Paso 2: Enviar el primer mensaje con la DESCRIPCIÓN (Solo texto)
                            repo.sendMessage(
                                ticketId = newTicket.id,
                                senderId = currentUserId,
                                receiverId = selectedUser!!.id,
                                content = message,
                                imageFile = null
                            )

                            // Paso 3: Enviar el segundo mensaje con la IMAGEN
                            repo.sendMessage(
                                ticketId = newTicket.id,
                                senderId = currentUserId,
                                receiverId = selectedUser!!.id,
                                content = "", // Contenido vacío para el mensaje de imagen
                                imageFile = selectedImage
                            )

                            // Paso 4: Si todo fue exitoso, notificar y navegar
                            onTicketCreated()

                        } catch (e: Exception) {
                            error = e.message ?: "Error creating ticket"
                        } finally {
                            isCreating = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && selectedUser != null && message.isNotBlank() && selectedImage != null && !isCreating
            ) {
                if (isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Create Ticket")
                }
            }
        }
    }
}
