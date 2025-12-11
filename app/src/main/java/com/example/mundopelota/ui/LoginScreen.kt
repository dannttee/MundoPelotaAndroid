package com.example.mundopelota.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mundopelota.viewmodel.UserAdminViewModel
import android.util.Log

@Composable
fun LoginScreen(navController: NavController, userAdminViewModel: UserAdminViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Observar estados del ViewModel
    val isLoading by userAdminViewModel.isLoading.observeAsState(false)
    val error by userAdminViewModel.error.observeAsState()
    val loginExitoso by userAdminViewModel.loginExitoso.observeAsState(false)
    val isAdmin by remember { derivedStateOf { userAdminViewModel.isAdmin } }

    // Navegar cuando login es exitoso
    LaunchedEffect(loginExitoso) {
        Log.d("LoginScreen", "LaunchedEffect triggered. loginExitoso=$loginExitoso, isAdmin=$isAdmin")
        if (loginExitoso) {
            keyboardController?.hide()
            Log.d("LoginScreen", "Login exitoso, navegando...")

            // Reset para próximo login
            userAdminViewModel.resetLoginExitoso()

            // Navega según el rol
            if (isAdmin) {
                Log.d("LoginScreen", "Navegando a: admin")
                navController.navigate("admin") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                Log.d("LoginScreen", "Navegando a: home")
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    userAdminViewModel.login(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Ingresar")
            }
        }

        // Mostrar error
        if (error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = error ?: "Error desconocido",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Link para registro
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { /* navController.navigate("register") */ }) {
            Text("¿No tienes cuenta? Registrate")
        }
    }
}



