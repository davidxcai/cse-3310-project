package com.example.mysecondapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.ui.components.ListingCard
import com.example.mysecondapp.ui.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SellerHomeScreen(
    navController: NavController,
    userId: Long,
    query: String,
    viewModel: MarketplaceViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(query) {
        viewModel.loadMarketplace(userId, query)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val listings by viewModel.listings.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = if (query.isEmpty()) "My Listings" else "Search Results for '$query'",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            items(listings) { listingWithSeller ->
                val listing = listingWithSeller.listing
                val seller = listingWithSeller.seller

                ListingCard(
                    listing = listing,
                    sellerName = seller?.name ?: "Unknown Seller",
                    isInCart = false, // Sellers don't see cart status for their own items here
                    onClick = {
                        navController.navigate("listing/${listing.id}/$userId")
                    },
                    onAddToCart = { /* Seller shouldn't add their own item to cart */ },
                    onRemoveFromCart = { /* No-op */ },
                    onEdit = {
                        // Navigate to edit screen if implemented
                    }
                )
            }

            if (listings.isEmpty()) {
                item {
                    Text(
                        text = "You have no active listings.",
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }
        }
    }
}
