package com.example.mundopelota.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.mundopelota.viewmodel.CatalogoViewModel
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.model.categoriaDesdeDeporte
import com.example.mundopelota.ui.theme.Purple40
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
fun CatalogoScreen(
    navController: NavController,
    catalogoViewModel: CatalogoViewModel,
    onAgregarAlCarrito: (Pelota) -> Unit = {}
) {
    val productosServidor by catalogoViewModel.pelotasServidor.observeAsState()
    val isLoading by catalogoViewModel.isLoading.observeAsState(false)
    val error by catalogoViewModel.error.observeAsState()
    val pelotas = catalogoViewModel.pelotas

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    LaunchedEffect(Unit) {
        catalogoViewModel.obtenerPelotasServidor()
    }

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
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
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
                            "Catálogo de Pelotas",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        IconButton(
                            onClick = { navController.navigate("carrito") }
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Contenido
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Loading
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Purple40)
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    // Error
                    if (error != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Color(0xFFC71C1C),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Error: $error",
                                    color = Color(0xFFC71C1C),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Lista de productos
                    if (pelotas.isEmpty() && !isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = Purple40,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        "No hay productos disponibles",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Purple40
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pelotas) { pelota ->
                                ProductoCard(
                                    pelota = pelota,
                                    onAgregarAlCarrito = {
                                        onAgregarAlCarrito(pelota)
                                        catalogoViewModel.decrementarStockAlComprar(pelota.id) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "${pelota.nombre} agregado al carrito ✅",
                                                    duration = SnackbarDuration.Short
                                                )
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
    onAgregarAlCarrito: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 🖼️ AGREGAR IMAGEN AQUÍ (NUEVO)
            AsyncImage(
                model = pelota.imageUrl,
                contentDescription = pelota.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_menu_report_image)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                pelota.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2121),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categoria = categoriaDesdeDeporte(pelota.deporte)
                Badge(
                    containerColor = Color(0xFFE1F5FE),
                    contentColor = Color(0xFF01579B)
                ) {
                    Text(
                        categoria?.name?.replace('_', ' ') ?: pelota.deporte,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

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

                Spacer(Modifier.width(8.dp))

                Badge(
                    containerColor = if (pelota.stock > 0) Color(0xFF2196F3) else Color(0xFFFF6B6B),
                    contentColor = Color.White
                ) {
                    Text(
                        "Stock: ${pelota.stock}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                pelota.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onAgregarAlCarrito,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF32B8C6)),
                shape = RoundedCornerShape(8.dp),
                enabled = pelota.stock > 0
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Agregar al Carrito", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
