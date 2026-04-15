package com.example.mysecondapp.ui.screens.listing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.ListingEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditListingScreen(
    listingId: Long,
    navController: NavController,
    repository: MarketplaceRepository
) {
    val scope = rememberCoroutineScope()
    var listing by remember { mutableStateOf<ListingEntity?>(null) }
    
    // Form fields
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var isSold by remember { mutableStateOf(false) }
    var isHidden by remember { mutableStateOf(false) }

    LaunchedEffect(listingId) {
        val fetched = repository.getListingById(listingId)
        fetched?.let {
            listing = it
            name = it.name
            price = it.price.toString()
            imageUrl = it.imageUrl ?: ""
            condition = it.condition
            isSold = it.isSold
            isHidden = it.isHidden
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Listing") }
                // Navigation icon (back button) removed as requested
            )
        }
    ) { paddingValues ->
        if (listing == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price ($)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = condition,
                    onValueChange = { condition = it },
                    label = { Text("Condition (e.g., New, Used)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mark as Sold")
                    Switch(checked = isSold, onCheckedChange = { isSold = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Hide Listing from Marketplace")
                    Switch(checked = isHidden, onCheckedChange = { isHidden = it })
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            val updated = listing!!.copy(
                                name = name,
                                price = price.toFloatOrNull() ?: listing!!.price,
                                imageUrl = imageUrl,
                                condition = condition,
                                isSold = isSold,
                                isHidden = isHidden
                            )
                            repository.updateListing(updated)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            repository.deleteListing(listing!!)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Listing")
                }
            }
        }
    }
}
