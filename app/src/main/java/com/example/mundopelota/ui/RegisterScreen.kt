package com.example.mundopelota.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mundopelota.viewmodel.UserAdminViewModel
import android.util.Patterns

@Composable
fun RegisterScreen(navController: NavController, userAdminViewModel: UserAdminViewModel) {
    var email by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // VALIDACIONES EMAIL
    val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val emailError = when {
        email.isEmpty() -> null
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
        else -> null
    }

    // VALIDACIONES NOMBRE
    val isNombreValid = nombre.isNotEmpty() && nombre.length >= 3
    val nombreError = when {
        nombre.isEmpty() -> null
        nombre.length < 3 -> "Mínimo 3 caracteres"
        else -> null
    }

    // VALIDACIONES CONTRASEÑA
    val isPasswordValid = password.isNotEmpty() && password.length >= 8
    val passwordError = when {
        password.isEmpty() -> null
        password.length < 8 -> "Mínimo 8 caracteres"
        else -> null
    }

    // Validación general del formulario
    val isFormValid = isEmailValid && isNombreValid && isPasswordValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registrarse", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // EMAIL FIELD CON VALIDACIÓN
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                isError = emailError != null,
                trailingIcon = {
                    if (email.isNotEmpty()) {
                        Icon(
                            imageVector = if (isEmailValid) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = "Validación email",
                            tint = if (isEmailValid) Color.Green else Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
            if (emailError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = emailError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // NOMBRE FIELD CON VALIDACIÓN
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                isError = nombreError != null,
                trailingIcon = {
                    if (nombre.isNotEmpty()) {
                        Icon(
                            imageVector = if (isNombreValid) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = "Validación nombre",
                            tint = if (isNombreValid) Color.Green else Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
            if (nombreError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nombreError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD FIELD CON VALIDACIÓN
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                enabled = !isLoading,
                isError = passwordError != null,
                trailingIcon = {
                    if (password.isNotEmpty()) {
                        Icon(
                            imageVector = if (isPasswordValid) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = "Validación contraseña",
                            tint = if (isPasswordValid) Color.Green else Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
            if (passwordError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = passwordError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BOTÓN (solo habilitado si TODO es válido)
        Button(
            onClick = {
                if (isFormValid) {
                    userAdminViewModel.register(email, nombre, password)
                    // Después navega al login
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                } else {
                    error = "Completa todos los campos correctamente"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && isFormValid
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Registrarse")
            }
        }

        // Mostrar error general
        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

