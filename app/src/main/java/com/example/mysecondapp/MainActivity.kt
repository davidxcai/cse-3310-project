package com.example.mysecondapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mysecondapp.ui.components.BottomBar
import com.example.mysecondapp.ui.components.TopBar
import com.example.mysecondapp.ui.navigation.AppNavGraph
import com.example.mysecondapp.ui.theme.MysecondappTheme

// David Cai
//Sejal Lamsal
//Nariman Jahangiri
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Sets padding for each screen throughout entire app
            val screenPadding = 16.dp

            // search query
            var query by rememberSaveable { mutableStateOf("") }

            // Allows navigation throughout the app
            val navController = rememberNavController()

            // Tells the app where which screen we're currently on
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            // List of screens that shouldn't display the top/bottom navigation bars
            val hideNavScreens = setOf("login", "register")
            MysecondappTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (currentRoute !in hideNavScreens) {
                            TopBar(
                                navController,
                                query,
                                onQueryChange = {query = it})
                        }
                    },
                    bottomBar = {
                        if (currentRoute !in hideNavScreens) {
                            BottomBar(
                                navController,
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }

                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNavGraph(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(screenPadding),
                        navController = navController,
                        query
                    )
                }
            }
        }
    }
}

