package com.example.mysecondapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.ui.components.ListingCard
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mysecondapp.ui.viewmodel.CartViewModel
import com.example.mysecondapp.ui.viewmodel.MarketplaceViewModel

// Sejal
// TODO:
// add search functionality
// filter/sort by name and price

@Composable
fun HomeScreen(
    navController: NavController,
    userId: Long,
    query: String,
    viewModel: MarketplaceViewModel, // Pass the VM in
    cartViewModel: CartViewModel
) {
    // 1. Refresh data whenever the search query changes
    LaunchedEffect(query) {
        viewModel.loadMarketplace(query)
    }

    // 2. Observe the database results
    val listings by viewModel.listings.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (query.isEmpty()) "All Listings" else "Search Results for '$query'",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // 3. Loop through the real ListingEntities

        items(listings) { listing ->
            val alreadyInCart = cartItems.any { it.id == listing.id }

            ListingCard(
                listing = listing,
                isInCart = alreadyInCart,
                // In HomeScreen.kt
                onClick = {
                    navController.navigate("listing/${listing.id}/$userId")
                },
                onAddToCart = { viewModel.addToCart(userId, listing.id) },
                onRemoveFromCart = { cartViewModel.removeFromCart(userId, listing.id) }, // You'll need this in Repo
                onEdit = {}
            )
        }

        // 4. Handle the "No Results" case
        if (listings.isEmpty()) {
            item {
                Text(
                    text = "No items found matching your search.",
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    }
}

private fun Nothing?.navigate(string: String) {}
