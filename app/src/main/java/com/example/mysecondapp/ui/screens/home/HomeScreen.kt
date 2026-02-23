package com.example.mysecondapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.data.dummy.dummyListings
import com.example.mysecondapp.ui.components.ListingCard
import androidx.compose.foundation.lazy.items

// Sejal
// TODO:
// add search functionality
// filter/sort by name and price

@Composable
fun HomeScreen(
    navController: NavController,
    userId: Long,
    query: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "All Listings")
        }

        items(dummyListings) { listing ->
            ListingCard(
                listing = listing,
                onClick = { navController.navigate("listing/${listing.id}") }
            )
        }
    }
}

private fun Nothing?.navigate(string: String) {}
