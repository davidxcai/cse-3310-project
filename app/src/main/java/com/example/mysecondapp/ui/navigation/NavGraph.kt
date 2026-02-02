package com.example.mysecondapp.ui.navigation

// This is how the entire app controls its navigation
// each "route" will show its own screen


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mysecondapp.ui.screens.home.HomeScreen
import com.example.mysecondapp.ui.screens.listing.ListingDetailScreen
import com.example.mysecondapp.ui.screens.auth.login.LoginScreen
import com.example.mysecondapp.ui.screens.auth.register.RegisterScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    query: String
) {
    NavHost(
        // This is the "root" screen
        // app will always start on this screen
        navController = navController,
        startDestination = "login",
        // Modifier provides the padding so screens don't "slip" under Navigation components
        modifier = modifier
    ) {
        composable("login") {
            // user authentication
            LoginScreen(
                onLoginSuccess = { userId ->
                    navController.navigate("home/$userId") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                navController
            )
        }
        composable("register") {
            // sign up new users
            RegisterScreen(
                onRegisterSuccess = { userId ->
                    navController.navigate("home/$userId") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                navController
            )
        }
        composable("home/{userId}") { backStackEntry ->
            // This gets the user Id passed in from the database
            val userId = backStackEntry.arguments?.getString("userId")!!.toLong()
            // shows all listings sorted by new

            HomeScreen(
                navController,
                userId,
                query
            )
        }
        composable("listing/{listingId}") { backStackEntry ->
            // when clicking on a listing
            // pass the id to the next screen
            // fetch the data using listing id
            val listingId = backStackEntry.arguments?.getString("listingId")!!
            ListingDetailScreen(listingId, navController)
        }
        composable("myListings") {
            // shows all listings by user
            // can search and filter
        }
        composable("upload") {
            // upload a new listing
        }
        composable("cart") {
            // show buyer's cart
            // can edit items in cart
        }
        composable("checkout") {
            // purchase screen
        }
        composable("confirmation") {
            // order confirmation screen
        }
        composable("profile") {
            // contains settings
        }
        composable("dashboard") {
            // for admins to manage
        }
    }
}