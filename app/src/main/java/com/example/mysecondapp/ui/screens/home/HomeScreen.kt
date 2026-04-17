package com.example.mysecondapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.ui.components.ListingCard
import com.example.mysecondapp.ui.viewmodel.CartViewModel
import com.example.mysecondapp.ui.viewmodel.MarketplaceViewModel
import com.example.mysecondapp.ui.viewmodel.SortOption
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    navController: NavController,
    userId: Long,
    query: String,
    onClearQuery: () -> Unit,
    viewModel: MarketplaceViewModel,
    cartViewModel: CartViewModel
) {
    val user by viewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSortMenu by remember { mutableStateOf(false) }
    val currentSort by viewModel.sortOption.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
        cartViewModel.loadCart(userId)
    }

    LaunchedEffect(query) {
        viewModel.loadMarketplace(userId, query)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
            cartViewModel.loadCart(userId)
        }
    }

    val listings by viewModel.listings.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val isSeller = user?.accountType?.uppercase() == "SELLER"
        val isAdmin = user?.isAdmin == true

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Only pad for Scaffold components (like Snackbar)
                contentPadding = PaddingValues(16.dp), // This provides the side/vertical "margin" for items
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (query.isEmpty()) "All Listings" else "Search Results for '$query'",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (query.isNotEmpty()) {
                                    TextButton(onClick = onClearQuery) {
                                        Text("Clear")
                                    }
                                }
                                Box {
                                    IconButton(onClick = { showSortMenu = true }) {
                                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                                    }
                                    DropdownMenu(
                                        expanded = showSortMenu,
                                        onDismissRequest = { showSortMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Price: Low to High") },
                                            onClick = {
                                                viewModel.setSortOption(SortOption.PRICE_LOW_TO_HIGH)
                                                showSortMenu = false
                                            },
                                            trailingIcon = { if (currentSort == SortOption.PRICE_LOW_TO_HIGH) Text("✓") }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Price: High to Low") },
                                            onClick = {
                                                viewModel.setSortOption(SortOption.PRICE_HIGH_TO_LOW)
                                                showSortMenu = false
                                            },
                                            trailingIcon = { if (currentSort == SortOption.PRICE_HIGH_TO_LOW) Text("✓") }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Newest") },
                                            onClick = {
                                                viewModel.setSortOption(SortOption.NEWEST)
                                                showSortMenu = false
                                            },
                                            trailingIcon = { if (currentSort == SortOption.NEWEST) Text("✓") }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Oldest") },
                                            onClick = {
                                                viewModel.setSortOption(SortOption.OLDEST)
                                                showSortMenu = false
                                            },
                                            trailingIcon = { if (currentSort == SortOption.OLDEST) Text("✓") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                items(listings) { listingWithSeller ->
                    val listing = listingWithSeller.listing
                    val seller = listingWithSeller.seller ?: return@items
                    val alreadyInCart = cartItems.any { it.listing.id == listing.id }

                    ListingCard(
                        listing = listing,
                        currentUserId = userId,
                        isCurrentUserSeller = isSeller,
                        isAdmin = isAdmin,
                        sellerName = seller.name,
                        isInCart = alreadyInCart,
                        onClick = {
                            navController.navigate("listing/${listing.id}/$userId")
                        },
                        onAddToCart = { 
                            viewModel.addToCart(userId, listing.id)
                        },
                        onRemoveFromCart = { 
                            cartViewModel.removeFromCart(userId, listing.id) 
                        },
                        onEdit = {
                            navController.navigate("editListing/${listing.id}")
                        }
                    )
                }

                if (listings.isEmpty()) {
                    item {
                        Text(
                            text = "No items found matching your search.",
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                }
            }
        }
    }
}
