package com.example.mysecondapp.ui.screens.listing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.dummy.dummyListings
import kotlinx.coroutines.launch

@Composable
fun ListingDetailScreen(
    listingId: Long,
    userId: Long,
    navController: NavController,
    repository: MarketplaceRepository
) {
    var listing by remember { mutableStateOf<ListingEntity?>(null) }
    // Initialize the scope here!
    val scope = rememberCoroutineScope()

    LaunchedEffect(listingId) {
        listing = repository.getListingById(listingId)
    }

    if (listing == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val item = listing!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // BACK BUTTON (Optional but highly recommended)
            Button(onClick = { navController.popBackStack() }) {
                Text("< Back")
            }

            Text(text = item.name, style = MaterialTheme.typography.headlineMedium)

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )

            Text(text = "$${item.price}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Condition: ${item.condition}")
            Text(text = "Posted on: ${item.dateAdded}")

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !item.isSold, // Don't let them buy sold items
                onClick = {
                    scope.launch {
                        repository.addItemToCart(userId, item.id)
                        // This takes them straight to the cart to see their addition
                        navController.navigate("cart/$userId")
                    }
                }
            ) {
                Text(if (item.isSold) "Sold Out" else "Add to Cart")
            }
        }
    }
}