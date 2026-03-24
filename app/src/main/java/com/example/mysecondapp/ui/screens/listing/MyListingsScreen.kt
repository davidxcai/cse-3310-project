package com.example.mysecondapp.ui.screens.listing

import ListingsViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mysecondapp.data.dummy.dummyListings
import com.example.mysecondapp.ui.components.ListingCard

@Composable
fun MyListingsScreen(
    userId: Long,
    navController: NavController,
    viewModel: ListingsViewModel // Pass the ViewModel here
) {
    // 1. Tell the ViewModel to fetch data when the screen first loads
    LaunchedEffect(userId) {
        viewModel.fetchListings(userId)
    }

    // 2. Collect the state from the ViewModel
    val listings by viewModel.userListings.collectAsState()

    // 3. Handle the empty state
    if (listings.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No listings uploaded")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "My Listings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // 4. items() now receives a List<ListingEntity>, so no mismatch!
        items(listings) { listing ->
            ListingCard(
                listing = listing,
                sellerName = "You", // Since this is the "My Listings" screen
                onRemoveFromCart = {}, // Required parameter: leave empty
                onAddToCart = {},      // Required parameter: leave empty
                onClick = { navController.navigate("listing/${listing.id}") },
                onEdit = {
                    // This is where the user would edit their own listing
                    navController.navigate("editListing/${listing.id}")
                }
            )
        }
    }
}