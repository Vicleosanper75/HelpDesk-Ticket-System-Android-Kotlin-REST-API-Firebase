package com.example.ticketsaplication.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.ticketsaplication.data.remote.Repository
import com.example.ticketsaplication.data.remote.RoleDto
import com.example.ticketsaplication.data.remote.UserDto
import com.example.ticketsaplication.ui.viewmodels.ChatUiState
import com.example.ticketsaplication.ui.viewmodels.ChatViewModel
import com.example.ticketsaplication.ui.viewmodels.ReassignmentState
import com.example.ticketsaplication.ui.viewmodels.UiMessage
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Base URL for images in chat. Should be same as Retrofit BASE_URL or CDN.
private const val BASE_URL = "https://your-api-url.com/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    repo: Repository,
    conversationId: String,
    contactId: Int,
    name: String,
    onBack: () -> Unit
) {
    val vm: ChatViewModel = viewModel(
        key = "chat_vm_$conversationId",
        factory = viewModelFactory {
            initializer { ChatViewModel(repo, conversationId, contactId) }
        }
    )
    val state by vm.state.collectAsStateWithLifecycle()
    val isCurrentUserResponsible by vm.isCurrentUserResponsible.collectAsStateWithLifecycle()
    val isActuallyResponsible by vm.isActuallyResponsible.collectAsStateWithLifecycle()
    val isTechnicalUser by vm.isTechnicalUser.collectAsStateWithLifecycle()
    val isTicketConcluded by vm.isTicketConcluded.collectAsStateWithLifecycle()
    val isCreator by vm.isCreator.collectAsStateWithLifecycle()
    val responsibleName by vm.responsibleName.collectAsStateWithLifecycle()
    val reassignmentState by vm.reassignmentState.collectAsStateWithLifecycle()

    var text by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<File>>(emptyList()) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showTakeTicketDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(vm) {
        vm.navigationEvent.collect {
            onBack()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val files = uris.mapNotNull { uri -> uriToFile(context, uri) }
        selectedImages = selectedImages + files
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            val file = uriToFile(context, tempCameraUri!!)
            if (file != null) {
                selectedImages = selectedImages + file
            }
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
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Action") },
            text = { Text("Are you sure you want to complete this ticket?") },
            confirmButton = {
                Button(onClick = {
                    vm.concludeTicket()
                    showConfirmDialog = false
                    onBack()
                }) { Text("Confirm") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showTakeTicketDialog) {
        AlertDialog(
            onDismissRequest = { showTakeTicketDialog = false },
            title = { Text("Take Ticket") },
            text = { Text("Do you want to reassign the ticket to yourself?") },
            confirmButton = {
                Button(onClick = {
                    vm.takeTicket()
                    showTakeTicketDialog = false
                }) { Text("Yes, take") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showTakeTicketDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (reassignmentState.showDialog) {
        ReassignmentDialog(
            state = reassignmentState,
            onRoleSelected = vm::onRoleSelected,
            onUserSelected = vm::onUserSelected,
            onConfirm = vm::confirmReassignment,
            onDismiss = vm::cancelReassignment
        )
    }

    if (selectedImageUrl != null) {
        FullScreenImageViewer(
            imageUrl = selectedImageUrl!!,
            onDismiss = { selectedImageUrl = null }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isTicketConcluded) {
                        if (isActuallyResponsible) {
                            IconButton(onClick = { showConfirmDialog = true }) {
                                Icon(Icons.AutoMirrored.Filled.PlaylistAddCheck, contentDescription = "Complete")
                            }
                            IconButton(onClick = { vm.startReassignment() }) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Reassign")
                            }
                        } else if (isTechnicalUser && !isCreator) {
                            // Botón para tomar el ticket (reasignar a uno mismo)
                            IconButton(onClick = { showTakeTicketDialog = true }) {
                                Icon(Icons.Default.AssignmentInd, contentDescription = "Take ticket")
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val canChat = !isTicketConcluded && isCurrentUserResponsible
        val placeholderText = when {
            isTicketConcluded -> "Ticket completed - Read only"
            !isCurrentUserResponsible -> "Only the responsible person can reply"
            else -> "Message..."
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val s = state) {
                    is ChatUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is ChatUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(s.message)
                        }
                    }
                    is ChatUiState.Ready -> {
                        LaunchedEffect(s.messages.size) {
                            if (s.messages.isNotEmpty()) listState.animateScrollToItem(0)
                        }
                        LazyColumn(
                            state = listState,
                            reverseLayout = true,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(s.messages.reversed()) { msg ->
                                MessageBubble(message = msg, onImageClick = { selectedImageUrl = it })
                            }
                        }
                    }
                }
            }

            Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                    responsibleName?.let {
                        Text(
                            text = "Responsible: $it",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (selectedImages.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().height(80.dp).padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedImages) { file ->
                                Box {
                                    SubcomposeAsyncImage(
                                        model = file,
                                        contentDescription = null,
                                        modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { selectedImages = selectedImages - file },
                                        modifier = Modifier.align(Alignment.TopEnd).size(20.dp).background(Color.Black.copy(0.5f), RoundedCornerShape(10.dp))
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            enabled = canChat,
                            onClick = {
                                val permission = Manifest.permission.CAMERA
                                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                    val (uri, _) = createImageFile(context)
                                    tempCameraUri = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    permissionLauncher.launch(permission)
                                }
                            }
                        ) { Icon(Icons.Default.CameraAlt, "Camera") }

                        IconButton(
                            enabled = canChat,
                            onClick = { galleryLauncher.launch("image/*") }
                        ) { Icon(Icons.Default.Image, "Gallery") }

                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(placeholderText) },
                            maxLines = 3,
                            enabled = canChat
                        )

                        IconButton(
                            enabled = canChat && (text.isNotBlank() || selectedImages.isNotEmpty()) && state is ChatUiState.Ready,
                            onClick = {
                                scope.launch {
                                    vm.sendMessage(text, selectedImages)
                                    text = ""
                                    selectedImages = emptyList()
                                }
                            }
                        ) { Icon(Icons.Filled.Send, "Send") }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: UiMessage, onImageClick: (String) -> Unit) {
    val isMyMessage = message.isSentByMe
    val alignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMyMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val isImage = message.content.startsWith("[image]")

    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = alignment) {
        Column(
            modifier = Modifier.fillMaxWidth(0.75f).clip(RoundedCornerShape(12.dp)).background(bubbleColor).padding(8.dp),
            horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start
        ) {
            Text(
                text = message.senderName,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isMyMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            if (isImage) {
                val imageUrl = "$BASE_URL${message.content.removePrefix("[image]")}"
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp, max = 300.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(imageUrl) },
                    contentScale = ContentScale.Crop,
                    loading = { Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(Modifier.size(24.dp)) } },
                    error = { Icon(Icons.Default.Warning, "Error", tint = MaterialTheme.colorScheme.error) }
                )
            } else {
                Text(message.content, fontSize = 16.sp)
            }
            Text(message.date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun FullScreenImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        scale = scale.coerceIn(1f, 5f)
                        if (scale > 1f) {
                            offset += pan
                        } else {
                            offset = Offset.Zero
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    ),
                contentScale = ContentScale.Fit,
                loading = { CircularProgressIndicator(color = Color.White) }
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReassignmentDialog(
    state: ReassignmentState,
    onRoleSelected: (RoleDto) -> Unit,
    onUserSelected: (UserDto) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reassign Ticket") },
        text = {
            Column(Modifier.fillMaxWidth(), horizontalAlignment =  Alignment.CenterHorizontally) {
                if (state.isLoading) CircularProgressIndicator() else {
                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    DropdownSelector("Destination Area", state.roles, state.selectedRole, onRoleSelected, { it.roleName })
                    if (state.selectedRole != null) {
                        Spacer(Modifier.height(16.dp))
                        if (state.isDifferentArea) {
                            Text("Automatic assignment to the area manager.", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            DropdownSelector("New Responsible", state.usersInSelectedRole, state.selectedUser, onUserSelected, { it.fullName.toString() })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !state.isLoading && state.selectedRole != null && (state.isDifferentArea || state.selectedUser != null)) {
                Text("Confirm")
            }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownSelector(label: String, items: List<T>, selectedItem: T?, onItemSelected: (T) -> Unit, itemToString: (T) -> String) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedItem?.let(itemToString) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded, { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(text = { Text(itemToString(item)) }, onClick = { onItemSelected(item); expanded = false })
            }
        }
    }
}
