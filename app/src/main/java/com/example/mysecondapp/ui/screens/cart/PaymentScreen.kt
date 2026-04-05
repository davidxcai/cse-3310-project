package com.example.mysecondapp.ui.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mysecondapp.ui.viewmodel.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(
    navController: NavController,
    userId: Long,
    viewModel: CartViewModel
) {
    var ccNumber by remember { mutableStateOf("") }
    var ccName by remember { mutableStateOf("") }
    var ccExpiry by remember { mutableStateOf("") }
    var ccCvv by remember { mutableStateOf("") }
    var ccZip by remember { mutableStateOf("") }

    var shipStreet by remember { mutableStateOf("") }
    var shipCity by remember { mutableStateOf("") }
    var shipState by remember { mutableStateOf("") }
    var shipZip by remember { mutableStateOf("") }

    var billStreet by remember { mutableStateOf("") }
    var billCity by remember { mutableStateOf("") }
    var billState by remember { mutableStateOf("") }
    var billZip by remember { mutableStateOf("") }

    var sameAsShipping by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val isFormValid = ccNumber.isNotBlank() && ccName.isNotBlank() && 
            ccCvv.isNotBlank() && ccZip.isNotBlank() &&
            shipStreet.isNotBlank() && shipCity.isNotBlank() && 
            shipState.isNotBlank() && shipZip.isNotBlank() &&
            (sameAsShipping || (billStreet.isNotBlank() && billCity.isNotBlank() && billState.isNotBlank() && billZip.isNotBlank()))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Payment Information", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = ccNumber,
            onValueChange = { ccNumber = it },
            label = { Text("Credit Card Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = ccName,
            onValueChange = { ccName = it },
            label = { Text("Name on Card") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = ccCvv,
                onValueChange = { ccCvv = it },
                label = { Text("CVV") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = ccZip,
                onValueChange = { ccZip = it },
                label = { Text("Zip Code") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        HorizontalDivider()
        Text(text = "Shipping Address", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(value = shipStreet, onValueChange = { shipStreet = it }, label = { Text("Street") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = shipCity, onValueChange = { shipCity = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = shipState, onValueChange = { shipState = it }, label = { Text("State") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = shipZip, onValueChange = { shipZip = it }, label = { Text("Zip") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = sameAsShipping, onCheckedChange = { sameAsShipping = it })
            Text(text = "Billing address same as shipping")
        }

        if (!sameAsShipping) {
            Text(text = "Billing Address", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = billStreet, onValueChange = { billStreet = it }, label = { Text("Street") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = billCity, onValueChange = { billCity = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = billState, onValueChange = { billState = it }, label = { Text("State") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = billZip, onValueChange = { billZip = it }, label = { Text("Zip") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    delay(3000) // Simulate 3 second timeout
                    viewModel.checkout(userId) {
                        navController.navigate("confirmation/$userId") {
                            // Pop up to home to clear the entire checkout flow from stack
                            popUpTo("home/$userId") { inclusive = false }
                        }
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Submit the order")
            }
        }
    }
}
