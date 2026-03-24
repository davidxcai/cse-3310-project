package com.example.mysecondapp.ui.navigation

// This is how the entire app controls its navigation
// each "route" will show its own screen


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.ui.screens.home.HomeScreen
import com.example.mysecondapp.ui.screens.listing.ListingDetailScreen
import com.example.mysecondapp.ui.screens.auth.login.LoginScreen
import com.example.mysecondapp.ui.screens.auth.register.RegisterScreen
import com.example.mysecondapp.ui.screens.cart.CartScreen
import com.example.mysecondapp.ui.viewmodel.CartViewModel
import com.example.mysecondapp.ui.viewmodel.ViewModelFactory // Your custom factory
import com.example.mysecondapp.ui.viewmodel.MarketplaceViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    query: String,
    repository: MarketplaceRepository,
    onUserAuthenticated: (Long) -> Unit
) {
    val factory = ViewModelFactory(repository)
    NavHost(
        // This is the "root" screen
        // app will always start on this screen
        navController = navController,
        startDestination = "login",
        // Modifier provides the padding so screens don't "slip" under Navigation components
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userId ->
                    onUserAuthenticated(userId) // Tell MainActivity
                    navController.navigate("home/$userId") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                navController
            )
        }
        composable("home/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")!!.toLong()

            val marketplaceViewModel: MarketplaceViewModel = viewModel(factory = factory)
            val cartViewModel: CartViewModel = viewModel(factory = factory) // Get the cart VM

            HomeScreen(
                navController = navController,
                userId = userId,
                query = query,
                viewModel = marketplaceViewModel,
                cartViewModel = cartViewModel // Pass it in
            )
        }
        composable("home/{userId}") { backStackEntry ->
            // This gets the user Id passed in from the database
            val userId = backStackEntry.arguments?.getString("userId")!!.toLong()
            val marketplaceViewModel: MarketplaceViewModel = viewModel(factory = factory)
            val cartViewModel: CartViewModel = viewModel(factory = factory)
            // shows all listings sorted by new

            HomeScreen(
                navController,
                userId,
                query,
                viewModel = marketplaceViewModel,
                cartViewModel = cartViewModel
            )
        }
        composable(
            route = "listing/{listingId}/{userId}", // Add userId to the route
            arguments = listOf(
                navArgument("listingId") { type = NavType.LongType },
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            // Use the specific NavBundle getters for LongType
            val lId = backStackEntry.arguments?.getLong("listingId") ?: -1L
            val uId = backStackEntry.arguments?.getLong("userId") ?: -1L

            ListingDetailScreen(
                listingId = lId,
                userId = uId, // Pass the second Long here
                navController = navController,
                repository = repository
            )
        }
        composable("listing/{listingId}/{userId}") { backStackEntry ->
            val lId = backStackEntry.arguments?.getString("listingId")?.toLongOrNull() ?: 0L
            val uId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0L

            ListingDetailScreen(
                listingId = lId,
                userId = uId, // Add this parameter
                navController = navController,
                repository = repository
            )
        }
        composable("myListings/{userId}") {
            // shows all listings by user
            // can search and filter
        }
        composable("upload") {
            // upload a new listing
        }
        composable("cart/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong() ?: 0L

            // Use the same factory we used for the MarketplaceViewModel
            val cartViewModel: CartViewModel = viewModel(factory = factory)

            CartScreen(
                navController = navController,
                userId = userId,
                viewModel = cartViewModel
            )
        }
        composable("checkout") {
            // purchase screen
        }
        composable("confirmation") {
            // order confirmation screen
        }
        composable("profile/{userId}") {
            // contains settings
        }
        composable("dashboard/{userId}") {
            // for admins to manage
        }
    }
}