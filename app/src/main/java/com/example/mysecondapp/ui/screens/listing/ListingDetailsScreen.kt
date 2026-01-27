package com.example.mysecondapp.ui.screens.listing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun ListingDetailScreen(listingId: String, navController: NavController) {
    // Id is passed from the home screen
    // Search the listings by that id
    // If not found, return the error
    // Else, display its info
    val listing = dummyListings.find { it.id == listingId }
    if (listing == null) {
        Text("Listing not found")
        return
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = listing.name)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(listing.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = listing.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
        Text(text = "$${listing.price}")
        Text(text = "Sold by: ${listing.seller}")
        Text(text = "Posted on: ${listing.dateAdded}")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                println("added to cart")
            }
        ) {
            Text(text = "Add to cart")
        }

    }
}