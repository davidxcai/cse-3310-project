package com.example.mysecondapp.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.data.db.entity.UserEntity
import com.example.mysecondapp.data.db.MarketplaceRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    navController: NavController,
    adminId: Long,
    query: String,
    repository: MarketplaceRepository
) {
    // Use the search method which returns all users if query is empty
    val users by repository.searchUsersByName(query).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        // The TopBar is handled in MainActivity, so we don't need a TopAppBar here
        // unless we want to override it. But the requirement is to use the existing TopBar's search.
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users) { user ->
                UserCard(
                    user = user,
                    isAdminSelf = user.id == adminId,
                    onDelete = {
                        scope.launch {
                            repository.deleteUser(user)
                            snackbarHostState.showSnackbar("User ${user.name} deleted")
                        }
                    }
                )
            }
            
            if (users.isEmpty() && query.isNotEmpty()) {
                item {
                    Text(
                        text = "No users found matching '$query'.",
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: UserEntity,
    isAdminSelf: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                Text(text = user.email, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Role: ${if (user.isAdmin) "ADMIN" else user.accountType}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (user.isAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
            
            if (!isAdminSelf) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete User",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
