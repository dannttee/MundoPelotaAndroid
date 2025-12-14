package com.example.mundopelota.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.ui.theme.Purple40
import com.example.mundopelota.viewmodel.CatalogoViewModel
import com.example.mundopelota.viewmodel.UserAdminViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userAdminViewModel: UserAdminViewModel,
    catalogoViewModel: CatalogoViewModel
) {
    // LEEMOS EL ESTADO DIRECTAMENTE DEL VIEWMODEL (Esto asegura reactividad)
    val isAdmin = userAdminViewModel.isAdmin

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val pelotas = catalogoViewModel.pelotas
    val pelotasDestacadas = pelotas.take(4)

    LaunchedEffect(Unit) {
        catalogoViewModel.obtenerPelotasServidor()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.75f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Purple40)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "MundoPelota", tint = Color.White, modifier = Modifier.size(50.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("MundoPelota", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Divider()

                NavigationDrawerItem(
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } }
                )

                NavigationDrawerItem(
                    label = { Text("Catálogo") },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("catalogo")
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Carrito") },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("carrito")
                    }
                )

                if (isAdmin) {
                    Divider()
                    NavigationDrawerItem(
                        label = { Text("Panel Admin") },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() } }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Divider()

                NavigationDrawerItem(
                    label = { Text("Logout", color = Color.Red) },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        userAdminViewModel.logout()
                        navController.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isAdmin) "Panel Admin" else "MundoPelota", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Purple40,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (isAdmin) {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Panel de Administración", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Bienvenido, Administrador.", color = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Desde aquí podrás gestionar usuarios y productos.", style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(24.dp))

                            // --- BOTÓN AGREGADO AQUÍ ---
                            Button(
                                onClick = { navController.navigate("admin_catalogo") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gestionar Catálogo de Productos")
                            }
                            // ---------------------------
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("¡Bienvenido a MundoPelota!", fontWeight = FontWeight.Bold, color = Purple40)
                            Text("Tu tienda online de pelotas deportivas", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Acciones Rápidas", fontWeight = FontWeight.Bold)

                    Button(
                        onClick = { navController.navigate("catalogo") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Catálogo", color = Color.White)
                    }

                    Button(
                        onClick = { navController.navigate("carrito") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mi Carrito", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Productos Destacados", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (pelotasDestacadas.isNotEmpty()) {
                        pelotasDestacadas.chunked(2).forEach { fila ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                fila.forEach { pelota ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        ProductoCardMini(pelota = pelota, navController = navController)
                                    }
                                }
                                if (fila.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Purple40)
                        }
                    }
                } // FIN ELSE
            }
        }
    }
}

// FUNCION ProductoCardMini (Debe estar FUERA de HomeScreen, al mismo nivel)
@Composable
fun ProductoCardMini(pelota: Pelota, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = pelota.imageUrl,
                contentDescription = pelota.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_menu_report_image)
            )

            Text(
                pelota.nombre,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2121),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Surface(
                color = Color(0xFF4CAF50),
                contentColor = Color.White,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "$${pelota.precio}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }

            Button(
                onClick = { navController.navigate("catalogo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                contentPadding = PaddingValues(4.dp)
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Ver", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}




