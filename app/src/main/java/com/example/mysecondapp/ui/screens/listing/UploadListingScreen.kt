package com.example.mysecondapp.ui.screens.listing

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mysecondapp.data.db.MarketplaceRepository
import com.example.mysecondapp.data.db.entity.ListingEntity
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadListingScreen(
    userId: Long,
    navController: NavController,
    repository: MarketplaceRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var condition by remember { mutableStateOf("New") }
    
    var showImageSourceModal by remember { mutableStateOf(false) }
    var showUrlInput by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    val conditions = listOf("Mint", "Excellent", "Good", "Fair", "Poor")

    // Camera Support
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempPhotoUri != null) {
            localImageUri = tempPhotoUri
            imageUrl = ""
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            localImageUri = uri
            imageUrl = ""
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = createTempImageUri(context)
            tempPhotoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add New Listing") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Image Preview Section at the Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                val displayUri = if (imageUrl.isNotBlank()) imageUrl else localImageUri
                
                if (displayUri != null && (displayUri.toString().isNotBlank())) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(displayUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Button to change/remove image
                    FilledTonalButton(
                        onClick = { showImageSourceModal = true },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                    ) {
                        Text("Change Image")
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = { showImageSourceModal = true }) {
                            Text("Add Photo")
                        }
                    }
                }
            }

            // 2. Form Fields
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
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            if (showUrlInput) {
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { 
                        imageUrl = it
                        if (it.isNotBlank()) localImageUri = null
                    },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showUrlInput = false; imageUrl = "" }) {
                            Icon(Icons.Default.Link, contentDescription = "Remove URL")
                        }
                    }
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = condition,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Condition") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    conditions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                condition = selectionOption
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isFormValid = name.isNotBlank() && 
                             price.isNotBlank() && 
                             (imageUrl.isNotBlank() || localImageUri != null)

            Button(
                onClick = {
                    val priceFloat = price.toFloatOrNull()
                    if (isFormValid && priceFloat != null) {
                        scope.launch {
                            val newListing = ListingEntity(
                                name = name,
                                price = priceFloat,
                                sellerId = userId,
                                dateAdded = System.currentTimeMillis(),
                                imageUrl = if (imageUrl.isBlank()) null else imageUrl,
                                localImagePath = localImageUri?.toString(),
                                condition = condition
                            )
                            repository.createListing(newListing)
                            navController.navigate("uploadConfirmation/$userId") {
                                popUpTo("home/$userId")
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please fill in all fields correctly.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                enabled = isFormValid
            ) {
                Text("Post Listing")
            }
        }

        // Image Source Modal (BottomSheet)
        if (showImageSourceModal) {
            ModalBottomSheet(
                onDismissRequest = { showImageSourceModal = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Select Image Source", style = MaterialTheme.typography.titleLarge)
                    
                    ListItem(
                        headlineContent = { Text("Take Photo") },
                        leadingContent = { Icon(Icons.Default.AddAPhoto, null) },
                        modifier = Modifier.fillMaxWidth().background(Color.Transparent).padding(0.dp),
                        trailingContent = null,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp,
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    ).let {
                        Surface(onClick = {
                            showImageSourceModal = false
                            val permission = Manifest.permission.CAMERA
                            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                val uri = createTempImageUri(context)
                                tempPhotoUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(permission)
                            }
                        }) {
                           Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                               Icon(Icons.Default.AddAPhoto, null)
                               Spacer(Modifier.width(16.dp))
                               Text("Take Photo")
                           }
                        }
                    }

                    Surface(onClick = {
                        showImageSourceModal = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddPhotoAlternate, null)
                            Spacer(Modifier.width(16.dp))
                            Text("Choose from Gallery")
                        }
                    }

                    Surface(onClick = {
                        showImageSourceModal = false
                        showUrlInput = true
                    }) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, null)
                            Spacer(Modifier.width(16.dp))
                            Text("Use Image URL")
                        }
                    }
                }
            }
        }
    }
}

fun createTempImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    if (storageDir != null && !storageDir.exists()) {
        storageDir.mkdirs()
    }
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    return FileProvider.getUriForFile(context, "com.example.mysecondapp.fileprovider", file)
}
