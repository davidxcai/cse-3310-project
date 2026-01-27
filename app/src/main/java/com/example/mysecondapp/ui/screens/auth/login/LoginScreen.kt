package com.example.mysecondapp.ui.screens.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (userId: Long) -> Unit,
    navController: NavController
) {
    val vm: LoginViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}

    LaunchedEffect(state.successUserId) {
        val id = state.successUserId
        if (id != null) onLoginSuccess(id)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.width(280.dp)
        ) {
            Text(
                text = "Marketplace App",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 32.sp
            )
            TextField(
                value = email,
                onValueChange = {email = it},
                label = { Text("Email")},
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = password,
                onValueChange = {password = it},
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Password")},
                modifier = Modifier.fillMaxWidth()
            )
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = {
                    vm.login(email, password)
                },
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()

            ) {
                Text(text = "Login")
            }
            TextButton(
                onClick = {
                    navController.navigate("register")
                },
                modifier = Modifier.fillMaxWidth()

            ) {
                Text(text = "Create new account")
            }
        }
    }
}