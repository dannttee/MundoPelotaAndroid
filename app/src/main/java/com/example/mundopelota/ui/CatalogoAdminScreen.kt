package com.example.mundopelota.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.mundopelota.viewmodel.CatalogoViewModel
import com.example.mundopelota.model.Pelota

@Composable
fun CatalogoAdminScreen(
    navController: NavController,
    catalogViewModel: CatalogoViewModel
) {
    var resultado by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editPelota by remember { mutableStateOf<Pelota?>(null) }

    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var deporte by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    fun limpiarCampos() {
        nombre = ""
        precio = ""
        descripcion = ""
        deporte = ""
        marca = ""
        stock = ""
        editPelota = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Catálogo de productos (ADMIN)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        catalogViewModel.pelotas.forEach { pelota ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${pelota.nombre} (ID: ${pelota.id})", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Precio: ${pelota.precio} | ${pelota.deporte} | ${pelota.marca}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("Stock: ${pelota.stock}", style = MaterialTheme.typography.bodySmall)
                    Text(pelota.descripcion, style = MaterialTheme.typography.bodySmall)
                }
                Column(
                    modifier = Modifier.widthIn(min = 120.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            editPelota = pelota
                            nombre = pelota.nombre
                            precio = pelota.precio.toString()
                            descripcion = pelota.descripcion
                            deporte = pelota.deporte
                            marca = pelota.marca
                            stock = pelota.stock.toString()
                            showEditDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Modificar") }

                    Button(
                        onClick = {
                            catalogViewModel.removePelota(pelota.id)
                            resultado = "Producto eliminado"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Eliminar") }
                }
            }
            Divider()
        }

        if (resultado.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(resultado, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                navController.navigate("admin") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Volver") }
    }

    if (showEditDialog && editPelota != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false; limpiarCampos() },
            confirmButton = {
                Button(onClick = {
                    val precioDouble = precio.toDoubleOrNull()
                    val stockInt = stock.toIntOrNull()

                    val nombreValido = nombre.isNotBlank()
                    val descripcionValida = descripcion.isNotBlank()
                    val precioValido = precioDouble != null && precioDouble > 0
                    val stockValido = stockInt != null && stockInt >= 0

                    val existe = catalogViewModel.pelotas.any {
                        it.id != editPelota!!.id && it.nombre.equals(nombre, ignoreCase = true)
                    }

                    if (!nombreValido || !descripcionValida || !precioValido || !stockValido) {
                        resultado = "Campos obligatorios faltantes o inválidos"
                    } else if (existe) {
                        resultado = "Ya existe otro producto con ese nombre"
                    } else {
                        catalogViewModel.updatePelota(
                            Pelota(
                                id = editPelota!!.id,
                                nombre = nombre,
                                precio = precioDouble!!,
                                descripcion = descripcion,
                                imageUrl = editPelota!!.imageUrl, // o campo editado si lo agregas
                                deporte = deporte,
                                marca = marca,
                                stock = stockInt!!
                            )
                        )
                        resultado = "Producto modificado"
                        showEditDialog = false
                        limpiarCampos()
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false; limpiarCampos() }) { Text("Cancelar") }
            },
            title = { Text("Modificar producto") },
            text = {
                Column {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Precio") }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = deporte, onValueChange = { deporte = it }, label = { Text("Deporte") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it.filter { c -> c.isDigit() } },
                        label = { Text("Stock") }
                    )
                }
            }
        )
    }
}






