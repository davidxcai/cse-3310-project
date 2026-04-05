package com.example.mysecondapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.People
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
    userId: Long,
    accountType: String?,
    isAdmin: Boolean = false,
    onLogout: () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isSeller = accountType?.uppercase() == "SELLER"

    NavigationBar {
        // --- HOME ---
        NavigationBarItem(
            selected = currentRoute?.startsWith("home") == true,
            onClick = {
                navController.navigate("home/$userId") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
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

        if (isAdmin) {
            // --- USERS (Admin only) ---
            NavigationBarItem(
                selected = currentRoute?.startsWith("manageUsers") == true,
                onClick = {
                    navController.navigate("manageUsers/$userId")
                },
                icon = { Icon(Icons.Default.People, null) },
                label = { Text("Users") }
            )
        } else if (isSeller) {
            // --- MY LISTINGS (Seller only) ---
            NavigationBarItem(
                selected = currentRoute?.startsWith("myListings") == true,
                onClick = {
                    navController.navigate("myListings/$userId")
                },
                icon = { Icon(Icons.Default.List, null) },
                label = { Text("Listings") }
            )
        } else {
            // --- CART (Buyer only) ---
            NavigationBarItem(
                selected = currentRoute?.startsWith("cart") == true,
                onClick = {
                    navController.navigate("cart/$userId")
                },
                icon = { Icon(Icons.Default.ShoppingCart, null) },
                label = { Text("Cart") }
            )
        }

        // --- LOGOUT ---
        NavigationBarItem(
            selected = false,
            onClick = { onLogout() },
            icon = { Icon(Icons.Default.Logout, null) },
            label = { Text("Logout") }
        )
    }
}
