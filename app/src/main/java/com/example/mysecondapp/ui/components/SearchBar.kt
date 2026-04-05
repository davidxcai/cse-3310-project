package com.example.mysecondapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.mysecondapp.ui.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    viewModel: MarketplaceViewModel,
    userId: Long,
    isManageUsersScreen: Boolean = false, // New flag to switch behavior
    modifier: Modifier = Modifier // Added modifier parameter
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val focusManager = LocalFocusManager.current
    var isPopupVisible by remember { mutableStateOf(false) }

    // Debounce logic for AI suggestions (only for items, not users)
    LaunchedEffect(query) {
        if (!isManageUsersScreen && query.length >= 2) {
            delay(2000) // 2-second debounce
            viewModel.fetchAiSuggestions(query)
            isPopupVisible = true
        } else {
            viewModel.clearSuggestions()
            isPopupVisible = false
        }
    }

    Box(modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                // If we are on ManageUsers, the query change immediately filters the list
                // because ManageUsersScreen collects repository.searchUsersByName(query)
            },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search, 
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { 
                        onQueryChange("")
                        if (!isManageUsersScreen) {
                            viewModel.loadMarketplace(userId, "")
                        }
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            placeholder = { 
                Text(
                    if (isManageUsersScreen) "Search users" else "Search items...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                ) 
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (!isManageUsersScreen) {
                        viewModel.loadMarketplace(userId, query)
                    }
                    isPopupVisible = false
                    focusManager.clearFocus()
                }
            )
        )

        if (isPopupVisible && suggestions.isNotEmpty() && !isManageUsersScreen) {
            Popup(
                onDismissRequest = { isPopupVisible = false },
                properties = PopupProperties(focusable = false)
            ) {
                Surface(
                    modifier = Modifier
                        .padding(top = 56.dp, start = 8.dp, end = 8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(suggestions) { suggestion ->
                            ListItem(
                                headlineContent = { 
                                    Text(
                                        suggestion.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    ) 
                                },
                                leadingContent = {
                                    Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp))
                                },
                                modifier = Modifier.clickable {
                                    onQueryChange(suggestion.name)
                                    viewModel.loadMarketplace(userId, suggestion.name)
                                    isPopupVisible = false
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
