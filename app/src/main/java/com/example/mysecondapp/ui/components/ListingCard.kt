package com.example.mysecondapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mysecondapp.data.db.entity.ListingEntity
import java.util.Locale

@Composable
fun ListingCard(
    listing: ListingEntity,
    currentUserId: Long = -1L,
    isCurrentUserSeller: Boolean = false,
    isAdmin: Boolean = false,
    sellerName: String = "Unknown Seller",
    isInCart: Boolean = false,
    showCartButton: Boolean = true,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
    onEdit: () -> Unit,
) {
    val isOwner = listing.sellerId == currentUserId
    var retryKey by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(listing.imageUrl)
                    .crossfade(true)
                    .memoryCacheKey("${listing.imageUrl}_$retryKey") // Forces a new request on key change
                    .build(),
                contentDescription = listing.name,
                modifier = Modifier.fillMaxSize(),
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
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = listing.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                val formattedPrice = String.format(Locale.getDefault(), "%.2f", listing.price)
                Text(
                    text = "$$formattedPrice",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Sold by: ${if (isOwner) "You" else sellerName}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (listing.isSold) {
                    Text(
                        text = "SOLD",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            if (isOwner || isAdmin) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Listing")
                }
            } else if (showCartButton && !isCurrentUserSeller) {
                Button(
                    onClick = { if (isInCart) onRemoveFromCart() else onAddToCart() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !listing.isSold,
                    colors = if (isInCart) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    else ButtonDefaults.buttonColors()
                ) {
                    Text(
                        text = when {
                            listing.isSold -> "Unavailable"
                            isInCart -> "Remove from Cart"
                            else -> "Add to Cart"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SkeletonLoader() {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = alpha))
    )
}
