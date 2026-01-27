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
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    NavigationBar() {

        NavigationBarItem(
            selected = currentRoute == "home/{userId}",
            onClick = {
                // shows all listings on screen
                // do nothing if currently on home
            },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "profile/{userId}",
            onClick = {
                // navigate to the profile page
            },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile") }
        )
//        NavigationBarItem(
//            selected = true,
//            onClick = {
//                // if user is in buy mode, they can add new item
//            },
//            icon = { Icon(Icons.Default.AddCircle, null) },
//            label = { Text("Add") }
//        )
        NavigationBarItem(
            selected = currentRoute == "cart/{userId}",
            onClick = {
                // navigate to the cart screen
            },
            icon = { Icon(Icons.Default.ShoppingCart, null) },
            label = { Text("Cart") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {
                // log user out and navigate back to login screen
            },
            icon = { Icon(Icons.Default.Logout, null) },
            label = { Text("Logout") }
        )
    }
}