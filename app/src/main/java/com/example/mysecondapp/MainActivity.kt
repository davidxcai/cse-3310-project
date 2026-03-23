package com.example.mysecondapp

import com.example.mysecondapp.data.db.MarketplaceRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mysecondapp.data.db.AppDatabase
import com.example.mysecondapp.data.db.DatabaseSeeder
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

        // 1. Initialize data layer once per Activity lifecycle
        val db = AppDatabase.getInstance(this)
        val repo = MarketplaceRepository(db) // Use the correct name you chose

        setContent {
            val navController = rememberNavController()
            var currentUserId by rememberSaveable { mutableLongStateOf(-1L) }
            // 2. Database Seeding
            LaunchedEffect(Unit) {
                // Using getAllListings (or your equivalent) to check if empty
                val listings = repo.getAllListings()
                if (listings.isEmpty()) {
                    DatabaseSeeder.seedDatabase(repo)
                }
            }

            // --- State Management ---
            var query by rememberSaveable { mutableStateOf("") }
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            val hideNavScreens = setOf("login", "register")

            MysecondappTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (currentRoute !in hideNavScreens) {
                            TopBar(
                                navController = navController,
                                query = query,
                                onQueryChange = { query = it }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentRoute !in hideNavScreens) {
                            BottomBar(
                                navController = navController,
                                userId = currentUserId, // Pass it here
                                onLogout = { /* logout logic */ }
                            )
                        }
                    }
                ) { innerPadding ->
                    // 3. Passing the padding correctly
                    AppNavGraph(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding), // innerPadding already handles scaffold bars
                        navController = navController,
                        query = query,
                        repository = repo, // Pass repo to the graph so it can feed ViewModels
                        onUserAuthenticated = { id ->
                            currentUserId = id // Now BottomBar knows the ID!
                        }
                    )
                }
            }
        }
    }
}

