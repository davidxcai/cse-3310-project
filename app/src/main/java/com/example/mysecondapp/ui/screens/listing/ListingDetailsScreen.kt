package com.example.mysecondapp.ui.screens.listing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.UserEntity
import com.example.mysecondapp.ui.components.SkeletonLoader
import com.example.mysecondapp.ui.viewmodel.CartViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ListingDetailScreen(
    listingId: Long,
    userId: Long,
    navController: NavController,
    repository: MarketplaceRepository,
    cartViewModel: CartViewModel
) {
    var listing by remember { mutableStateOf<ListingEntity?>(null) }
    var user by remember { mutableStateOf<UserEntity?>(null) }
    var retryKey by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val cartItems by cartViewModel.cartItems.collectAsState()
    val isInCart = cartItems.any { it.listing.id == listingId }

    LaunchedEffect(listingId) {
        listing = repository.getListingById(listingId)
    }

    LaunchedEffect(userId) {
        user = repository.getUser(userId)
        cartViewModel.loadCart(userId)
    }

    if (listing == null || user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val item = listing!!
        val currentUser = user!!
        val isAdmin = currentUser.isAdmin
        val isSeller = currentUser.accountType.uppercase() == "SELLER"
        val isOwner = item.sellerId == currentUser.id
        
        // Format the timestamp into "Month day, year"
        val formattedDate = remember(item.dateAdded) {
            val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            sdf.format(Date(item.dateAdded))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = item.name, style = MaterialTheme.typography.headlineMedium)

            val imageData = item.localImagePath ?: item.imageUrl
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageData)
                    .crossfade(true)
                    .memoryCacheKey("${imageData}_$retryKey")
                    .build(),
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop,
                loading = {
                    SkeletonLoader()
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { retryKey++ }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )

            Text(text = "$${String.format(Locale.getDefault(), "%.2f", item.price)}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Condition: ${item.condition}")
            Text(text = "Posted on: $formattedDate")

            if ((isOwner || isAdmin) && !item.isSold) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("editListing/${item.id}")
                    }
                ) {
                    Text("Edit Listing")
                }
            } else if (!isSeller && !isAdmin) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !item.isSold,
                    onClick = {
                        scope.launch {
                            if (isInCart) {
                                cartViewModel.removeFromCart(userId, item.id)
                            } else {
                                repository.addItemToCart(userId, item.id)
                                cartViewModel.loadCart(userId)
                            }
                        }
                    },
                    colors = if (isInCart) androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    else androidx.compose.material3.ButtonDefaults.buttonColors()
                ) {
                    Text(
                        text = when {
                            item.isSold -> "Sold Out"
                            isInCart -> "Remove from Cart"
                            else -> "Add to Cart"
                        }
                    )
                }
            }
        }
    }
}