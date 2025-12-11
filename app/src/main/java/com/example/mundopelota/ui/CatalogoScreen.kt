package com.example.mundopelota.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.mundopelota.viewmodel.CatalogoViewModel
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.viewmodel.UserAdminViewModel
import com.example.mundopelota.model.categoriaDesdeDeporte


@Composable
fun CatalogoScreen(
    navController: NavController,
    catalogoViewModel: CatalogoViewModel,
    onAgregarAlCarrito: (Pelota) -> Unit = {},
    userAdminViewModel: UserAdminViewModel
) {
    // Observar datos del servidor
    val productosServidor by catalogoViewModel.pelotasServidor.observeAsState()
    val isLoading by catalogoViewModel.isLoading.observeAsState(false)
    val error by catalogoViewModel.error.observeAsState()

    // Lista local del ViewModel
    val pelotas = catalogoViewModel.pelotas

    // Cargar productos al abrir
    LaunchedEffect(Unit) {
        catalogoViewModel.obtenerPelotasServidor() // 🆕 CARGAR DEL SERVIDOR
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Catálogo de pelotas", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        // Mostrar loading
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
        }

        // Mostrar error
        if (error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    "Error: $error",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // Lista de productos
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(pelotas) { pelota ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(pelota.nombre, style = MaterialTheme.typography.bodyLarge)
                        val categoria = categoriaDesdeDeporte(pelota.deporte)
                        Text(
                            "Precio: ${pelota.precio} | ${categoria?.name?.replace('_', ' ') ?: pelota.deporte}",
                            style = MaterialTheme.typography.bodySmall
                        )


                        Text(pelota.descripcion, style = MaterialTheme.typography.bodySmall)
                    }
                    Button(
                        onClick = { onAgregarAlCarrito(pelota) },
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text("Agregar")
                    }
                }
                Divider()
            }

            // Mostrar si no hay productos
            if (pelotas.isEmpty() && !isLoading) {
                item {
                    Text(
                        "No hay productos disponibles",
                        modifier = Modifier.padding(32.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Volver")
        }
    }
}





