<<<<<<< HEAD
package com.example.mysecondapp.ui.screens.cart

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
fun CartScreen(
//    navController: NavController,
//    userId: Long,
//    query: String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "Cart Screen")
        }


    }
}

private fun Nothing?.navigate(string: String) {}
=======
package com.example.mysecondapp.ui.screens.listing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.mysecondapp.ui.components.ListingCard

@Composable
fun CartScreen(navController: NavController, userId: Long) {
    // get all items from cart
    // display them once they load

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
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
}
>>>>>>> 37b4802d54016c2fd744bdc426c5d5eb6bca18fd
