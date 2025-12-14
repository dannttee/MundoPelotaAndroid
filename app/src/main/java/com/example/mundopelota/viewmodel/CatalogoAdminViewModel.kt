package com.example.mundopelota.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.network.PelotaRequest
import com.example.mundopelota.network.RetrofitClient
import kotlinx.coroutines.launch

class CatalogoAdminViewModel : ViewModel() {
    var pelotas = mutableStateListOf<Pelota>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    var mensajeExito by mutableStateOf<String?>(null)
        private set

    init {
        cargarPelotas()
    }

    fun cargarPelotas() {
        viewModelScope.launch {
            isLoading = true
            try {
                // Llamamos a tu endpoint GET "api/pelotas" que devuelve List<PelotaResponse>
                val response = RetrofitClient.getApiServiceCatalogo().obtenerPelotas()

                if (response.isSuccessful && response.body() != null) {
                    pelotas.clear()
                    // Mapeamos de PelotaResponse (Backend) a Pelota (UI)
                    val listaConvertida = response.body()!!.map { responseItem ->
                        Pelota(
                            id = responseItem.id, // Ya es Long
                            nombre = responseItem.nombre,
                            precio = responseItem.precio,
                            descripcion = responseItem.descripcion,
                            imageUrl = responseItem.imageUrl,
                            deporte = responseItem.deporte,
                            marca = responseItem.marca,
                            stock = responseItem.stock
                        )
                    }
                    pelotas.addAll(listaConvertida)
                    mensajeError = null
                } else {
                    mensajeError = "Error al cargar: ${response.code()}"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
                Log.e("CatalogoAdminViewModel", "Error cargarPelotas", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun agregarPelota(pelota: Pelota) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Creamos el REQUEST especifico que pide tu API
                val request = PelotaRequest(
                    nombre = pelota.nombre,
                    precio = pelota.precio,
                    descripcion = pelota.descripcion,
                    imageUrl = pelota.imageUrl,
                    deporte = pelota.deporte,
                    marca = pelota.marca,
                    stock = pelota.stock
                )

                // Usamos 'crearPelota' en lugar de 'agregarPelota'
                val response = RetrofitClient.getApiServiceCatalogo().crearPelota(request)

                if (response.isSuccessful) {
                    mensajeExito = "Producto creado con éxito"
                    cargarPelotas()
                } else {
                    mensajeError = "Error al crear: ${response.code()}"
                }
            } catch (e: Exception) {
                mensajeError = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun editarPelota(pelota: Pelota) {
        viewModelScope.launch {
            isLoading = true
            try {
                val request = PelotaRequest(
                    nombre = pelota.nombre,
                    precio = pelota.precio,
                    descripcion = pelota.descripcion,
                    imageUrl = pelota.imageUrl,
                    deporte = pelota.deporte,
                    marca = pelota.marca,
                    stock = pelota.stock
                )

                // Usamos 'actualizarPelota' en lugar de 'editarPelota'
                val response = RetrofitClient.getApiServiceCatalogo().actualizarPelota(pelota.id, request)

                if (response.isSuccessful) {
                    mensajeExito = "Producto actualizado"
                    cargarPelotas()
                } else {
                    mensajeError = "Error al editar: ${response.code()}"
                }
            } catch (e: Exception) {
                mensajeError = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun eliminarPelota(id: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Usamos 'eliminarPelota' que ya recibe Long
                val response = RetrofitClient.getApiServiceCatalogo().eliminarPelota(id)

                if (response.isSuccessful) {
                    mensajeExito = "Producto eliminado"
                    cargarPelotas()
                } else {
                    mensajeError = "Error al eliminar: ${response.code()}"
                }
            } catch (e: Exception) {
                mensajeError = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun limpiarMensajes() {
        mensajeError = null
        mensajeExito = null
    }
}
