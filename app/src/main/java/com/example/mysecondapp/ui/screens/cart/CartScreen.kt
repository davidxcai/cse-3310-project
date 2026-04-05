package com.example.mysecondapp.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.ui.components.ListingCard
import com.example.mysecondapp.ui.viewmodel.CartViewModel

@Composable
fun CartScreen(
    navController: NavController,
    userId: Long,
    viewModel: CartViewModel
) {
    // 1. Fetch data on load
    LaunchedEffect(userId) {
        viewModel.loadCart(userId)
    }

    val cartItems by viewModel.cartItems.collectAsState()

    // 2. Main Layout
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "My Shopping Cart", style = MaterialTheme.typography.headlineMedium)
        }

        if (cartItems.isEmpty()) {
            item {
                Text(text = "Your cart is empty.", modifier = Modifier.padding(top = 20.dp))
            }
        }

        items(cartItems) { listing ->
            ListingCard(
                listing = listing,
                isInCart = true, // Force the button to show "Remove"
                onRemoveFromCart = {
                    // Call the ViewModel to delete from DB
                    viewModel.removeFromCart(userId, listing.id)
                },
                onClick = { navController.navigate("listing/${listing.id}/$userId") },
                onAddToCart = {}, // Not needed here since it's already in the cart
                onEdit = {}
            )
        }

        if (cartItems.isNotEmpty()) {
            item {
                Button(
                    onClick = {
                        // Navigate to PaymentScreen instead of immediate checkout
                        navController.navigate("checkout/$userId")
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    val total = cartItems.sumOf { it.price.toDouble() }
                    Text("Checkout ($${String.format(java.util.Locale.getDefault(), "%.2f", total)})")
                }
            }
        }
    }
}
