package com.example.ticketsaplication.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ticketsaplication.data.remote.Repository
import com.example.ticketsaplication.ui.viewmodels.ContactUi
import com.example.ticketsaplication.ui.viewmodels.ContactsUiState
import com.example.ticketsaplication.ui.viewmodels.ContactsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactsScreen(
    username: String,
    currentUserId: Int,
    repo: Repository,
    onOpenChat: (String, String, Int) -> Unit,
    onNewTicket: (Int) -> Unit,
    onLogout: () -> Unit,
    onNavigateToChangePassword: () -> Unit
) {
    val factory = remember { ContactsViewModel.Factory(repo) }
    val vm: ContactsViewModel = viewModel(factory = factory)
    val state by vm.state.collectAsStateWithLifecycle()
    var showNewTicketMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showTopBarMenu by remember { mutableStateOf(false) }

    LaunchedEffect(currentUserId) {
        if (currentUserId != -1) {
            vm.loadContacts(currentUserId)
        }
    }

    LaunchedEffect(vm) {
        vm.logoutEvent.collect {
            onLogout()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Do you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    vm.logout()
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("No") }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tickets") },
                actions = {
                    Box {
                        IconButton(onClick = { showTopBarMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                        DropdownMenu(
                            expanded = showTopBarMenu,
                            onDismissRequest = { showTopBarMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Change Password") },
                                onClick = {
                                    showTopBarMenu = false
                                    onNavigateToChangePassword()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showTopBarMenu = false
                                    showLogoutDialog = true
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(onClick = { showNewTicketMenu = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "New Ticket")
                }
                DropdownMenu(
                    expanded = showNewTicketMenu,
                    onDismissRequest = { showNewTicketMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Create ticket") },
                        onClick = {
                            showNewTicketMenu = false
                            onNewTicket(currentUserId)
                        }
                    )
                }
            }
        }
    ) { inner ->
        when (val s = state) {
            is ContactsUiState.Loading -> {
                Box(
                    modifier = Modifier.padding(inner).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is ContactsUiState.Error -> {
                Box(
                    modifier = Modifier.padding(inner).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(s.message) }
            }

            is ContactsUiState.Ready -> {
                PullToRefreshBox(
                    isRefreshing = s.isRefreshing,
                    onRefresh = { vm.refresh(currentUserId) },
                    modifier = Modifier.padding(inner).fillMaxSize()
                ) {
                    // Definir pestañas dinámicamente según el rol
                    val tabTitles = remember(s.isSupportOrSystems) {
                        if (s.isSupportOrSystems) {
                            listOf("Assigned", "Created", "Completed")
                        } else {
                            listOf("Created", "Completed")
                        }
                    }
                    
                    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
                    val coroutineScope = rememberCoroutineScope()

                    Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(selectedTabIndex = pagerState.currentPage) {
                            tabTitles.forEachIndexed { index, title ->
                                Tab(
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                    },
                                    text = { Text(title) }
                                )
                            }
                        }

                        HorizontalPager(state = pagerState) { page ->
                            val currentTabTitle = tabTitles[page]
                            
                            when (currentTabTitle) {
                                "Assigned" -> {
                                    if (s.assignedToMeDirectly.isEmpty() && s.assignedToMyArea.isEmpty()) {
                                        EmptyState()
                                    } else {
                                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                                            if (s.assignedToMeDirectly.isNotEmpty()) {
                                                stickyHeader { SectionHeader("My Tickets") }
                                                items(s.assignedToMeDirectly, key = { it.id }) { ticket ->
                                                    ContactRow(ticket) {
                                                        ticket.contactId?.let { onOpenChat(ticket.id, ticket.contactName, it) }
                                                    }
                                                    HorizontalDivider()
                                                }
                                            }
                                            if (s.assignedToMyArea.isNotEmpty()) {
                                                stickyHeader { SectionHeader("Assigned to area") }
                                                items(s.assignedToMyArea, key = { it.id }) { ticket ->
                                                    ContactRow(ticket, showUnreadCount = false) {
                                                        ticket.contactId?.let { onOpenChat(ticket.id, ticket.contactName, it) }
                                                    }
                                                    HorizontalDivider()
                                                }
                                            }
                                        }
                                    }
                                }
                                "Created" -> {
                                    if (s.createdByMe.isEmpty()) EmptyState() else {
                                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                                            items(s.createdByMe, key = { it.id }) { ticket ->
                                                // Ocultamos la línea de responsable aquí
                                                ContactRow(ticket, showResponsible = false) {
                                                    ticket.contactId?.let { onOpenChat(ticket.id, ticket.contactName, it) }
                                                }
                                                HorizontalDivider()
                                            }
                                        }
                                    }
                                }
                                "Completed" -> {
                                    if (s.concluded.isEmpty()) EmptyState() else {
                                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                                            items(s.concluded, key = { it.id }) { ticket ->
                                                ContactRow(ticket, showUnreadCount = false) {
                                                    ticket.contactId?.let { onOpenChat(ticket.id, ticket.contactName, it) }
                                                }
                                                HorizontalDivider()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No tickets in this category.")
    }
}

@Composable
private fun ContactRow(
    contact: ContactUi,
    showResponsible: Boolean = true,
    showUnreadCount: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = "Ticket: #${contact.ticketId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "With: ${contact.contactName}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showResponsible) {
                Text(
                    text = "Responsible: ${contact.responsibleName}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = contact.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (contact.lastMessage.isNotBlank()) {
                Text(
                    text = contact.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (showUnreadCount && contact.unreadCount > 0) {
            Spacer(Modifier.width(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (contact.unreadCount > 99) "+99" else contact.unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
