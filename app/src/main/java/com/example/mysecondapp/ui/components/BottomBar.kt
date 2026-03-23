package com.example.mysecondapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavController,
    userId: Long, // Add this!
    onLogout: () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        // --- HOME ---
        NavigationBarItem(
            selected = currentRoute?.startsWith("home") == true,
            onClick = {
                navController.navigate("home/$userId") {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )

        // --- PROFILE ---
        NavigationBarItem(
            selected = currentRoute?.startsWith("profile") == true,
            onClick = {
                navController.navigate("profile/$userId")
            },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile") }
        )

        // --- CART ---
        NavigationBarItem(
            selected = currentRoute?.startsWith("cart") == true,
            onClick = {
                navController.navigate("cart/$userId")
            },
            icon = { Icon(Icons.Default.ShoppingCart, null) },
            label = { Text("Cart") }
        )

        // --- LOGOUT ---
        NavigationBarItem(
            selected = false,
            onClick = { onLogout() },
            icon = { Icon(Icons.Default.Logout, null) }, // Default name is ExitToApp
            label = { Text("Logout") }
        )
    }
}

//        NavigationBarItem(
//            selected = true,
//            onClick = {
//                // if user is in buy mode, they can add new item
//            },
//            icon = { Icon(Icons.Default.AddCircle, null) },
//            label = { Text("Add") }
//        )