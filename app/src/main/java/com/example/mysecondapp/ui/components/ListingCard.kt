package com.example.mysecondapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mysecondapp.data.db.entity.ListingEntity // Use the Entity now

@Composable
fun ListingCard(
    listing: ListingEntity, // Swapped from Listing to ListingEntity
    sellerName: String = "Unknown Seller", // Added this to handle the ID-to-Name gap
    isInCart: Boolean = false,
    onClick: () -> Unit,
    onAddToCart: () -> Unit, // Better to pass the action up than just println
    onRemoveFromCart: () -> Unit,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // Fixed height to keep the UI consistent
            .clickable { onClick() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(listing.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = listing.name,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween // Pushes the button to the bottom
        ) {
            Column {
                Text(
                    text = listing.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${listing.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Seller: $sellerName",
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