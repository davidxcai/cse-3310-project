package com.example.mysecondapp.ui.navigation

// This is how the entire app controls its navigation
// each "route" will show its own screen


import ListingsViewModel
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
import com.example.mysecondapp.ui.screens.cart.ConfirmationScreen
import com.example.mysecondapp.ui.screens.cart.PaymentScreen
import com.example.mysecondapp.ui.screens.listing.EditListingScreen
import com.example.mysecondapp.ui.screens.listing.MyListingsScreen
import com.example.mysecondapp.ui.screens.listing.UploadConfirmationScreen
import com.example.mysecondapp.ui.screens.listing.UploadListingScreen
import com.example.mysecondapp.ui.screens.profile.ProfileScreen
import com.example.mysecondapp.ui.viewmodel.CartViewModel
import com.example.mysecondapp.ui.viewmodel.ViewModelFactory // Your custom factory
import com.example.mysecondapp.ui.viewmodel.MarketplaceViewModel
import com.example.mysecondapp.ui.viewmodel.ProfileViewModel
import com.example.mysecondapp.ui.screens.admin.ManageUsersScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    query: String,
    onClearQuery: () -> Unit,
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
                onClearQuery = onClearQuery,
                viewModel = marketplaceViewModel,
                cartViewModel = cartViewModel // Pass it in
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
        composable("myListings/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong() ?: 0L
            val listingsViewModel: ListingsViewModel = viewModel(factory = factory)
            MyListingsScreen(
                userId = userId,
                navController = navController,
                viewModel = listingsViewModel
            )
        }
        composable("upload/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong() ?: 0L
            UploadListingScreen(
                userId = userId,
                navController = navController,
                repository = repository
            )
        }
        composable("uploadConfirmation/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong() ?: 0L
            UploadConfirmationScreen(
                userId = userId,
                navController = navController
            )
        }
        composable(
            route = "editListing/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.LongType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getLong("listingId") ?: -1L
            EditListingScreen(
                listingId = listingId,
                navController = navController,
                repository = repository
            )
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
        composable("checkout/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong() ?: 0L
            val cartViewModel: CartViewModel = viewModel(factory = factory)
            PaymentScreen(navController = navController, userId = userId, viewModel = cartViewModel)
        }
        composable("confirmation/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong() ?: 0L
            ConfirmationScreen(navController = navController, userId = userId)
        }
        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong() ?: 0L
            val profileViewModel: ProfileViewModel = viewModel(factory = factory)
            
            ProfileScreen(
                navController = navController,
                userId = userId,
                viewModel = profileViewModel
            )
        }
        composable("manageUsers/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLong() ?: 0L
            ManageUsersScreen(
                navController = navController,
                adminId = userId,
                query = query,
                repository = repository
            )
        }
        composable("dashboard/{userId}") {
            // for admins to manage
        }
    }
}
