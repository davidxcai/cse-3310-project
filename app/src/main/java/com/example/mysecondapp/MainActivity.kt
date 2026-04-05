package com.example.mysecondapp

import com.example.mysecondapp.data.db.MarketplaceRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mysecondapp.data.db.AppDatabase
import com.example.mysecondapp.data.db.DatabaseSeeder
import com.example.mysecondapp.ui.components.BottomBar
import com.example.mysecondapp.ui.components.TopBar
import com.example.mysecondapp.ui.navigation.AppNavGraph
import com.example.mysecondapp.ui.theme.MysecondappTheme
import com.example.mysecondapp.ui.viewmodel.MarketplaceViewModel
import com.example.mysecondapp.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(this)
        val repo = MarketplaceRepository(db)

        setContent {
            val navController = rememberNavController()
            var currentUserId by rememberSaveable { mutableLongStateOf(-1L) }
            val factory = ViewModelFactory(repo)
            
            // Initialize ViewModels here to avoid repeated initialization in Scaffold slots
            val marketplaceViewModel: MarketplaceViewModel = viewModel(factory = factory)
            val user by marketplaceViewModel.currentUser.collectAsState()

            LaunchedEffect(currentUserId) {
                if (currentUserId != -1L) {
                    marketplaceViewModel.loadUser(currentUserId)
                }
            }

            LaunchedEffect(Unit) {
                val listings = repo.getAllListings()
                if (listings.isEmpty()) {
                    DatabaseSeeder.seedDatabase(repo)
                }
            }

            var query by rememberSaveable { mutableStateOf("") }
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            val hideNavScreens = setOf("login", "register")

            // Determine if dark mode should be enabled
            val darkTheme = when {
                user != null -> user!!.preferDarkMode
                else -> isSystemInDarkTheme()
            }

            MysecondappTheme(darkTheme = darkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (currentRoute !in hideNavScreens && currentRoute != null) {
                            TopBar(
                                navController = navController,
                                query = query,
                                onQueryChange = { query = it },
                                viewModel = marketplaceViewModel,
                                userId = currentUserId
                            )
                        }
                    },
                    bottomBar = {
                        if (currentRoute !in hideNavScreens && currentRoute != null) {
                            BottomBar(
                                navController = navController,
                                userId = currentUserId,
                                accountType = user?.accountType,
                                isAdmin = user?.isAdmin == true,
                                onLogout = {
                                    currentUserId = -1L
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNavGraph(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        navController = navController,
                        query = query,
                        onClearQuery = { query = "" },
                        repository = repo,
                        onUserAuthenticated = { id ->
                            currentUserId = id
                        }
                    )
                }
            }
        }
    }
}
