package com.example.mysecondapp.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mysecondapp.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    query: String,
    onQueryChange: (String) -> Unit,
    viewModel: MarketplaceViewModel,
    userId: Long
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    
    // Observe the current back stack entry to determine the current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Check the current screen
    val isHomeScreen = currentRoute?.startsWith("home") == true
    val isManageUsersScreen = currentRoute?.startsWith("manageUsers") == true

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            SearchBar(
                query = query,
                onQueryChange = onQueryChange,
                viewModel = viewModel,
                userId = userId,
                isManageUsersScreen = isManageUsersScreen,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            if (!isHomeScreen) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } else {
                // Symmetrical spacer for the left side when no back button is present
                Spacer(modifier = Modifier.width(16.dp))
            }
        },
        actions = {
            if (isHomeScreen) {
                // Symmetrical spacer for the right side to keep the SearchBar centered
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                // Balance the back button's width (approx 48dp) on other screens
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        scrollBehavior = scrollBehavior
    )
}
