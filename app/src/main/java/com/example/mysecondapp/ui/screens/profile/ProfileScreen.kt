package com.example.mysecondapp.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.data.model.PurchasedListing
import com.example.mysecondapp.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavController,
    userId: Long,
    viewModel: ProfileViewModel
) {
    val user by viewModel.user.collectAsState()
    val purchaseHistory by viewModel.purchaseHistory.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Logic to hide Order History for admins
    val isAdmin = user?.isAdmin == true
    val tabs = if (isAdmin) listOf("Settings") else listOf("Settings", "Order History")

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (tabs.size > 1) {
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            } else {
                // Just a header if there's only one tab (Admin case)
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Profile Settings", style = MaterialTheme.typography.headlineSmall)
                }
            }

            when (selectedTab) {
                0 -> SettingsTab(
                    user = user,
                    userId = userId,
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )
                1 -> if (!isAdmin) {
                    OrderHistoryTab(
                        purchaseHistory = purchaseHistory
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsTab(
    user: com.example.mysecondapp.data.db.entity.UserEntity?,
    userId: Long,
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var accountType by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isAdmin = user?.isAdmin == true

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            email = it.email
            password = it.password
            accountType = it.accountType
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (user != null) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hide account type toggle for admins
            if (!isAdmin) {
                Text(
                    text = "Account Type",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val types = listOf("BUYER", "SELLER")
                    types.forEach { type ->
                        val isSelected = accountType.uppercase() == type
                        if (isSelected) {
                            Button(
                                onClick = { accountType = type },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(type)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { accountType = type },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(type)
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Account Type: ADMIN",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Start),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Preferences", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Dark Mode")
                        Switch(
                            checked = user.preferDarkMode,
                            onCheckedChange = { isDark ->
                                viewModel.updateDarkMode(userId, isDark)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            viewModel.updateName(userId, name)
                            viewModel.updateEmail(userId, email)
                            viewModel.updatePassword(userId, password)
                            if (!isAdmin) {
                                viewModel.updateAccountType(userId, accountType)
                            }
                            snackbarHostState.showSnackbar("Changes saved successfully!")
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Failed to save changes: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        } else {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun OrderHistoryTab(
    purchaseHistory: List<PurchasedListing>
) {
    if (purchaseHistory.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No purchase history found.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(purchaseHistory) { purchase ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = purchase.name, fontWeight = FontWeight.Bold)
                            Text(text = "Seller: ${purchase.sellerName}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Condition: ${purchase.condition}", style = MaterialTheme.typography.bodySmall)
                        }
                        val formattedPrice = String.format(Locale.getDefault(), "%.2f", purchase.price)
                        Text(text = "$$formattedPrice", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
