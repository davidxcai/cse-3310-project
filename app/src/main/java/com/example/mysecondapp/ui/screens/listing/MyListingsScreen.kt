package com.example.mysecondapp.ui.screens.listing

import ListingsViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.ui.components.ListingCard

@Composable
fun MyListingsScreen(
    userId: Long,
    navController: NavController,
    viewModel: ListingsViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Live", "Sold")

    LaunchedEffect(userId) {
        viewModel.fetchListings(userId)
    }

    val listings by viewModel.userListings.collectAsState()
    
    val filteredListings = remember(listings, selectedTab) {
        if (selectedTab == 0) {
            listings.filter { !it.isSold }
        } else {
            listings.filter { it.isSold }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Listings",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = { navController.navigate("upload/$userId") }) {
                Text("Add New")
            }
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredListings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(if (selectedTab == 0) "No live listings" else "No sold listings")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredListings) { listing ->
                    ListingCard(
                        listing = listing,
                        currentUserId = userId,
                        isCurrentUserSeller = true,
                        sellerName = "You",
                        onRemoveFromCart = {},
                        onAddToCart = {},
                        onClick = { navController.navigate("listing/${listing.id}/$userId") },
                        onEdit = {
                            navController.navigate("editListing/${listing.id}")
                        }
                    )
                }
            }
        }
    }
}
