package com.example.mundopelota.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.ui.theme.Purple40
import com.example.mundopelota.viewmodel.CatalogoViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoAdminScreen(
    navController: NavController,
    catalogViewModel: CatalogoViewModel
) {
    var resultado by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editPelota by remember { mutableStateOf<Pelota?>(null) }

    // Estados para el formulario de edición
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var deporte by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    // Estado para la URI temporal de la foto
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            // Si la foto fue exitosa, guardamos la URI en el campo de texto
            imageUrl = tempPhotoUri.toString()
        }
    }

    // Función auxiliar para crear el archivo de imagen
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            context.getExternalFilesDir(null)
        )
    }

    // Usamos LaunchedEffect para cargar datos al entrar si la lista está vacía
    LaunchedEffect(Unit) {
        if (catalogViewModel.pelotas.isEmpty()) {
            catalogViewModel.obtenerPelotasServidor()
        }
    }

    fun limpiarCampos() {
        nombre = ""
        precio = ""
        descripcion = ""
        deporte = ""
        marca = ""
        stock = ""
        imageUrl = ""
        editPelota = null
        tempPhotoUri = null
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.8f),
                drawerContainerColor = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Purple40)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Menú Admin",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Home", fontWeight = FontWeight.Bold) },
                    selected = false,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("home_admin")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Gestionar Productos", fontWeight = FontWeight.Bold) },
                    selected = true,
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Volver", fontWeight = FontWeight.Bold) },
                    selected = false,
                    icon = { Icon(Icons.Default.ArrowBack, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Catálogo", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Purple40)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        limpiarCampos()
                        showEditDialog = true
                    },
                    containerColor = Purple40,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (resultado.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (resultado.contains("Error")) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = resultado,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Text(
                    "Listado de Productos (${catalogViewModel.pelotas.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (catalogViewModel.pelotas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    catalogViewModel.pelotas.forEach { pelota ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // IMAGEN DEL PRODUCTO
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(pelota.imageUrl)
                                                .crossfade(true)
                                                .error(android.R.drawable.ic_menu_report_image)
                                                .build(),
                                            contentDescription = pelota.nombre,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // INFO DEL PRODUCTO
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = pelota.nombre,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Badge(containerColor = Purple40) {
                                                Text("ID: ${pelota.id}", color = Color.White)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Marca: ${pelota.marca}", style = MaterialTheme.typography.bodySmall)
                                        Text("Stock: ${pelota.stock}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                        Text("$${pelota.precio}", style = MaterialTheme.typography.titleSmall, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                    }
                                }

                                // BOTONES DE ACCIÓN
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            editPelota = pelota
                                            nombre = pelota.nombre
                                            precio = pelota.precio.toString()
                                            descripcion = pelota.descripcion
                                            deporte = pelota.deporte
                                            marca = pelota.marca
                                            stock = pelota.stock.toString()
                                            imageUrl = pelota.imageUrl
                                            showEditDialog = true
                                        },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Editar")
                                    }

                                    Button(
                                        onClick = {
                                            catalogViewModel.eliminarPelota(pelota.id)
                                            resultado = "Producto eliminado"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                limpiarCampos()
            },
            title = { Text(if(editPelota != null) "Modificar Producto" else "Crear Producto") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- SECCIÓN IMAGEN Y CÁMARA ---
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("URL de Imagen") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            try {
                                val photoFile = createImageFile(context)
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    photoFile
                                )
                                tempPhotoUri = uri
                                cameraLauncher.launch(uri)
                            } catch (e: Exception) {
                                resultado = "Error Cámara: ${e.message}"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                    ) {
                        // Icono Settings es seguro si no tienen Extended Icons
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar Foto (Cámara)")
                    }

                    // --- PREVISUALIZACIÓN ---
                    if (imageUrl.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Vista previa:", style = MaterialTheme.typography.bodySmall)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Foto tomada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    // --- FIN SECCIÓN CÁMARA ---

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = marca,
                        onValueChange = { marca = it },
                        label = { Text("Marca") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = deporte,
                        onValueChange = { deporte = it },
                        label = { Text("Deporte") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) precio = it },
                        label = { Text("Precio") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { if (it.all { char -> char.isDigit() }) stock = it },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val precioDouble = precio.toDoubleOrNull()
                        val stockInt = stock.toIntOrNull()

                        if (nombre.isBlank() || precioDouble == null || stockInt == null) {
                            resultado = "Error: Verifica los campos obligatorios"
                        } else {
                            // AQUÍ SOLUCIONAMOS EL ERROR DE TIPOS
                            // Creamos un objeto Pelota, pero con ID dinámico
                            val idParaGuardar = editPelota?.id ?: 0L

                            // El ViewModel anterior (sin modificar) pedía Pelota para .actualizarPelota()
                            // y .agregarPelota(). Asumiré que quieres usar .crearPelota con request.

                            // Si mantuviste el VM antiguo que usaba Pelota para todo:
                            val objetoPelota = Pelota(
                                id = idParaGuardar,
                                nombre = nombre,
                                precio = precioDouble,
                                descripcion = descripcion,
                                imageUrl = imageUrl,
                                deporte = deporte,
                                marca = marca,
                                stock = stockInt
                            )

                            // Para arreglarlo SIN TOCAR el VM:
                            // Usaremos un truco: Si es nuevo (id=0), usamos crearPelota que pide Request.
                            // Si es viejo, usamos actualizarPelota que pide Pelota.

                            if (editPelota != null) {
                                // MODO EDICIÓN -> ViewModel espera Pelota
                                catalogViewModel.actualizarPelota(objetoPelota)
                                resultado = "Producto actualizado correctamente"
                            } else {
                                // MODO CREACIÓN -> ViewModel espera Request
                                // Aquí hacemos el mapeo manual para evitar el error
                                val requestNuevo = com.example.mundopelota.network.PelotaRequest(
                                    nombre = nombre,
                                    precio = precioDouble,
                                    descripcion = descripcion,
                                    imageUrl = imageUrl,
                                    deporte = deporte,
                                    marca = marca,
                                    stock = stockInt
                                )
                                catalogViewModel.crearPelota(requestNuevo)
                                resultado = "Enviando solicitud de creación..."
                            }

                            showEditDialog = false
                            limpiarCampos()
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        limpiarCampos()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}









