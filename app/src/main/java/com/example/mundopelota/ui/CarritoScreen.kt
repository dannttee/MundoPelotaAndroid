package com.example.mundopelota.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mundopelota.viewmodel.CartViewModel
import com.example.mundopelota.viewmodel.UserAdminViewModel


@Composable
fun CarritoScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    userAdminViewModel: UserAdminViewModel
) {
    val cartBalls = cartViewModel.carrito
    var mensaje by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Carrito de compras", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        if (cartBalls.isNotEmpty()) {
            cartBalls.forEach { pelota ->
                Card(
                    Modifier
                        .padding(vertical = 6.dp)
                        .fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Text("${pelota.nombre} - \$${pelota.precio}",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    val uid = userAdminViewModel.usuarioId.value ?: return@Button
                    cartViewModel.checkoutServidor(uid)
                    mensaje = "Compra realizada con éxito"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar compra")
            }


            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { cartViewModel.clearCart() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Vaciar carrito")
            }
        } else {
            Text("Tu carrito está vacío.", style = MaterialTheme.typography.bodyMedium)
        }

        mensaje?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}


