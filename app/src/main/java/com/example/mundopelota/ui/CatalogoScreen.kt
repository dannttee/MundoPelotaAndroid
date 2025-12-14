package com.example.mundopelota.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.ui.theme.Purple40
import com.example.mundopelota.viewmodel.CatalogoViewModel
import kotlinx.coroutines.launch

// Enum dummy para UI
enum class CategoriaDeporte { FUTBOL, BASKETBALL, TENNIS, OTRO }

fun categoriaDesdeDeporte(deporte: String): CategoriaDeporte? {
    return try {
        CategoriaDeporte.valueOf(deporte.uppercase())
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController,
    catalogoViewModel: CatalogoViewModel,
    onAgregarAlCarrito: (Pelota) -> Unit = {}
) {
    // 1. Observamos datos del servidor y estados
    val productosServidor by catalogoViewModel.pelotasServidor.observeAsState()
    val isLoading by catalogoViewModel.isLoading.observeAsState(false)
    val error by catalogoViewModel.error.observeAsState()

    // 2. Observamos el valor del Dólar (State en ViewModel)
    // Nota: Como 'valorDolar' es un MutableState en el VM, lo leemos directo
    val valorDolar = catalogoViewModel.valorDolar

    // Usamos productosServidor si existe, si no, la lista local
    val pelotas = if (!productosServidor.isNullOrEmpty()) {
        catalogoViewModel.pelotas
    } else {
        catalogoViewModel.pelotas
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    LaunchedEffect(Unit) {
        catalogoViewModel.obtenerPelotasServidor()
        // El dólar ya se pide en el init del ViewModel, pero no hace daño refrescar si quieres
        // catalogoViewModel.obtenerValorDolarDia()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.8f),
                drawerContainerColor = Color.White
            ) {
                // Header Drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Purple40)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Menú",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (valorDolar > 0) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Dólar hoy: $${valorDolar}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Catálogo", fontWeight = FontWeight.Bold) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Mi Carrito", fontWeight = FontWeight.Bold) },
                    selected = false,
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("carrito")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", fontWeight = FontWeight.Bold) },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFFF6B6B)) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("login") {
                            popUpTo("catalogo") { inclusive = true }
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
                // Header App
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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White, modifier = Modifier.size(32.dp))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "MundoPelota",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (valorDolar > 0) {
                                Text(
                                    "USD: $${valorDolar}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        IconButton(onClick = { navController.navigate("carrito") }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                }

                // Contenido
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (isLoading) {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Purple40)
                        }
                    }

                    if (error != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = Color(0xFFC71C1C))
                                Spacer(Modifier.width(12.dp))
                                Text("Error: $error", color = Color(0xFFC71C1C))
                            }
                        }
                    }

                    if (pelotas.isEmpty() && !isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay productos disponibles", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pelotas) { pelota ->
                                ProductoCard(
                                    pelota = pelota,
                                    valorDolar = valorDolar, // <--- PASAMOS EL DÓLAR
                                    onAgregarAlCarrito = {
                                        onAgregarAlCarrito(pelota)
                                        catalogoViewModel.decrementarStockAlComprar(pelota.id) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("${pelota.nombre} agregado ✅")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    pelota: Pelota,
    valorDolar: Double, // <--- NUEVO PARÁMETRO
    onAgregarAlCarrito: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            AsyncImage(
                model = pelota.imageUrl,
                contentDescription = pelota.nombre,
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(12.dp))

            Text(pelota.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Categoría
                Surface(color = Color(0xFFE1F5FE), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        pelota.deporte,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF01579B),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // PRECIO CON DÓLAR
                Surface(color = Color(0xFF4CAF50), shape = RoundedCornerShape(4.dp)) {
                    val precioCLP = pelota.precio.toInt()
                    val textoPrecio = if (valorDolar > 0) {
                        val precioUSD = String.format("%.2f", pelota.precio / valorDolar)
                        "$$precioCLP / USD $precioUSD"
                    } else {
                        "$$precioCLP CLP"
                    }

                    Text(
                        textoPrecio,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Stock
                Surface(color = if (pelota.stock > 0) Color(0xFF2196F3) else Color(0xFFFF6B6B), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        "Stock: ${pelota.stock}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(pelota.descripcion, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onAgregarAlCarrito,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF32B8C6)),
                enabled = pelota.stock > 0
            ) {
                Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Agregar al Carrito")
            }
        }
    }
}



