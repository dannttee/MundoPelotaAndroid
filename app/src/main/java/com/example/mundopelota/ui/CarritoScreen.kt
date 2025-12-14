package com.example.mundopelota.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.ui.theme.Purple40
import com.example.mundopelota.viewmodel.CatalogoViewModel
import kotlinx.coroutines.launch
import android.util.Log


@Composable
fun CarritoScreen(
    navController: NavController,
    carritoItems: MutableList<Pelota>,
    catalogoViewModel: CatalogoViewModel
) {
    val carrito = remember { mutableStateOf(carritoItems) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.8f),
                drawerContainerColor = Color.White
            ) {
                // Header del drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Purple40)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Menú",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Divider()

                // Items del menú
                NavigationDrawerItem(
                    label = { Text("Catálogo", fontWeight = FontWeight.Bold) },
                    selected = false,
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("catalogo")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Mi Carrito", fontWeight = FontWeight.Bold) },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Home", fontWeight = FontWeight.Bold) },
                    selected = false,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("home")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", fontWeight = FontWeight.Bold) },
                    selected = false,
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFFF6B6B)) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("login") {
                            popUpTo("carrito") { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(paddingValues)
            ) {
                // Header con menú hamburguesa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Purple40)
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            "Mi Carrito",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            "${carrito.value.size}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }

                // Contenido
                if (carrito.value.isEmpty()) {
                    // Carrito vacío
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .padding(32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(Color(0xFFE8D5FF), RoundedCornerShape(20.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = Purple40,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    "Carrito Vacío",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2121)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Agrega algunos productos para continuar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666)
                                )
                                Spacer(Modifier.height(24.dp))
                                Button(
                                    onClick = { navController.navigate("catalogo") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Continuar Comprando", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Carrito con items
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Lista
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(carrito.value) { pelota ->
                                CarritoItemCard(
                                    pelota = pelota,
                                    onEliminar = {
                                        scope.launch {
                                            try {
                                                Log.d("Carrito", "🔄 Restaurando stock para: ${pelota.nombre}")

                                                val result = catalogoViewModel.restaurarStockAlCancelarSuspend(pelota.id)

                                                if (result.isSuccess) {
                                                    carrito.value.remove(pelota)
                                                    carrito.value = carrito.value.toMutableList()

                                                    snackbarHostState.showSnackbar(
                                                        message = "${pelota.nombre} removido ✅",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                    Log.d("Carrito", "✅ Stock restaurado y eliminado")
                                                } else {
                                                    snackbarHostState.showSnackbar(
                                                        message = "Error al restaurar stock ❌",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                Log.e("Carrito", "Error: ${e.message}")
                                                snackbarHostState.showSnackbar(
                                                    message = "Error: ${e.message}",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Resumen
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                val total = carrito.value.sumOf { it.precio }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Total a Pagar:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF666666)
                                    )
                                    Text(
                                        "\$${String.format("%.2f", total)}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }

                                Divider(modifier = Modifier.padding(bottom = 20.dp))

                                // Proceder al pago
                                Button(
                                    onClick = {
                                        Log.d("Carrito", "✅ Proceediendo al pago...")
                                        carrito.value.clear()
                                        navController.navigateUp()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Proceder al Pago", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                Spacer(Modifier.height(10.dp))

                                // Continuar comprando
                                Button(
                                    onClick = { navController.navigate("catalogo") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF32B8C6)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Continuar Comprando", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                Spacer(Modifier.height(10.dp))

                                // Vaciar carrito
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                Log.d("Carrito", "🔄 Vaciando carrito...")

                                                carrito.value.forEach { pelota ->
                                                    Log.d("Carrito", "   Restaurando: ${pelota.nombre}")
                                                    catalogoViewModel.restaurarStockAlCancelarSuspend(pelota.id).getOrNull()
                                                }

                                                carrito.value.clear()
                                                carrito.value = carrito.value.toMutableList()

                                                snackbarHostState.showSnackbar(
                                                    message = "Carrito vaciado - Stock restaurado ✅",
                                                    duration = SnackbarDuration.Short
                                                )
                                                Log.d("Carrito", "✅ Carrito vaciado")
                                            } catch (e: Exception) {
                                                Log.e("Carrito", "Error: ${e.message}")
                                                snackbarHostState.showSnackbar(
                                                    message = "Error: ${e.message}",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(width = 2.dp, color = Color(0xFFFF6B6B))
                                ) {
                                    Text("Vaciar Carrito", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CarritoItemCard(
    pelota: Pelota,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        pelota.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2121)
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Badge(
                            containerColor = Color(0xFFE1F5FE),
                            contentColor = Color(0xFF01579B)
                        ) {
                            Text(
                                pelota.marca,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Badge(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ) {
                            Text(
                                "\$${pelota.precio}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Subtotal: \$${String.format("%.2f", pelota.precio)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Purple40
                    )
                }

                // Botón eliminar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(10.dp))
                        .clickable { onEliminar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}