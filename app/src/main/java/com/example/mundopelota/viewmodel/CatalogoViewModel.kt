package com.example.mundopelota.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf // IMPORTANTE: Para el dólar
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.network.ApiServiceCatalogo
import com.example.mundopelota.network.ApiServiceExternal // IMPORTANTE: Para el dólar
import com.example.mundopelota.network.PelotaRequest
import com.example.mundopelota.network.PelotaResponse
import com.example.mundopelota.network.RetrofitClient
import kotlinx.coroutines.launch

class CatalogoViewModel : ViewModel() {

    private val catalogoApi: ApiServiceCatalogo = RetrofitClient.getApiServiceCatalogo()
    private val externalApi: ApiServiceExternal = RetrofitClient.getApiServiceExternal()

    // ----------------------------------------------------------------
    // LISTA PRINCIPAL (Usada por UI Admin y Cliente)
    // ----------------------------------------------------------------
    val pelotas = mutableStateListOf<Pelota>()

    // VARIABLE DEL DÓLAR (Observable por UI)
    var valorDolar by mutableDoubleStateOf(0.0)
        private set

    // Respuesta cruda del servidor (útil para debug o datos extra)
    private val _pelotasServidor = MutableLiveData<List<PelotaResponse>?>()
    val pelotasServidor: LiveData<List<PelotaResponse>?> = _pelotasServidor

    // Estados de UI
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        // Cargar datos al iniciar
        obtenerPelotasServidor()
        obtenerValorDolarDia() // <-- CARGAR DÓLAR
    }

    private fun cargarPelotasIniciales() {
        Log.w("CatalogoVM", "⚠️ Cargando fallback local (vacío por ahora)")
    }

    // ----------------------------------------------------------------
    // OPERACIONES DE LECTURA (GET)
    // ----------------------------------------------------------------

    fun obtenerPelotasServidor() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                Log.d("CatalogoVM", "📡 Llamando a GET /api/pelotas")
                val response = catalogoApi.obtenerPelotas()

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    _pelotasServidor.value = data

                    Log.d("CatalogoVM", "✅ Recibidas ${data.size} pelotas")

                    pelotas.clear()
                    pelotas.addAll(
                        data.map {
                            Pelota(
                                id = it.id,
                                nombre = it.nombre,
                                precio = it.precio,
                                descripcion = it.descripcion,
                                imageUrl = it.imageUrl,
                                deporte = it.deporte,
                                marca = it.marca,
                                stock = it.stock
                            )
                        }
                    )
                } else {
                    _error.value = "Error ${response.code()}: ${response.message()}"
                    Log.e("CatalogoVM", "❌ Error servidor: ${response.code()}")
                    cargarPelotasIniciales()
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CatalogoVM", "❌ Excepción: ${e.message}")
                cargarPelotasIniciales()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ----------------------------------------------------------------
    // API EXTERNA (DÓLAR)
    // ----------------------------------------------------------------
    fun obtenerValorDolarDia() {
        viewModelScope.launch {
            try {
                Log.d("DolarAPI", "💵 Obteniendo valor del dólar...")
                val response = externalApi.obtenerValorDolar()
                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!.serie
                    if (lista.isNotEmpty()) {
                        valorDolar = lista[0].valor
                        Log.d("DolarAPI", "✅ Dólar hoy: $$valorDolar")
                    }
                } else {
                    Log.e("DolarAPI", "⚠️ Error al obtener dólar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("DolarAPI", "❌ Excepción dólar: ${e.message}")
            }
        }
    }

    // ----------------------------------------------------------------
    // OPERACIONES DE ESCRITURA (ADMIN & COMPRA)
    // ----------------------------------------------------------------

    // 1. Crear Pelota (Admin)
    fun crearPelota(request: PelotaRequest, onSuccess: (() -> Unit)? = null) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Aquí se envía la request al backend
                // Si la imageUrl es una URI local (file://...), el backend la guardará como string.
                // En una app real, aquí se debería subir la imagen a un servidor (S3/Cloudinary)
                // y enviar esa URL pública. Para la tarea, enviar la URI local es válido funcionalmente.

                val response = catalogoApi.crearPelota(request)
                if (response.isSuccessful && response.body() != null) {
                    val nueva = response.body()!!
                    pelotas.add(
                        Pelota(
                            id = nueva.id,
                            nombre = nueva.nombre,
                            precio = nueva.precio,
                            descripcion = nueva.descripcion,
                            imageUrl = nueva.imageUrl,
                            deporte = nueva.deporte,
                            marca = nueva.marca,
                            stock = nueva.stock
                        )
                    )
                    Log.d("CatalogoVM", "✅ Pelota creada: ${nueva.nombre}")
                    onSuccess?.invoke()
                } else {
                    _error.value = "Error al crear: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 2. Decrementar Stock (Al agregar al carrito)
    fun decrementarStockAlComprar(pelotaId: Long, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val pelota = pelotas.find { it.id == pelotaId } ?: return@launch
                if (pelota.stock <= 0) {
                    _error.value = "Sin stock"
                    return@launch
                }

                val nuevoStock = pelota.stock - 1
                val request = mapToRequest(pelota, nuevoStock)

                val response = catalogoApi.actualizarPelota(pelotaId, request)

                if (response.isSuccessful && response.body() != null) {
                    actualizarListaLocal(response.body()!!)
                    Log.d("CatalogoVM", "✅ Stock bajado a $nuevoStock")
                    onSuccess?.invoke()
                } else {
                    _error.value = "Error stock: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error red: ${e.message}"
            }
        }
    }

    // 3. Restaurar Stock (Al cancelar compra)
    suspend fun restaurarStockAlCancelarSuspend(pelotaId: Long): Result<Unit> {
        return try {
            val pelota = pelotas.find { it.id == pelotaId }
                ?: return Result.failure(Exception("Producto no encontrado"))

            val nuevoStock = pelota.stock + 1
            val request = mapToRequest(pelota, nuevoStock)

            val response = catalogoApi.actualizarPelota(pelotaId, request)

            if (response.isSuccessful && response.body() != null) {
                actualizarListaLocal(response.body()!!)
                Log.d("CatalogoVM", "✅ Stock restaurado a $nuevoStock")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Versión callback (legacy)
    fun restaurarStockAlCancelar(pelotaId: Long, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            val result = restaurarStockAlCancelarSuspend(pelotaId)
            if (result.isSuccess) onSuccess?.invoke()
            else _error.value = result.exceptionOrNull()?.message
        }
    }

    // ----------------------------------------------------------------
    // ALIAS & UTILIDADES
    // ----------------------------------------------------------------

    fun agregarPelota(pelota: Pelota) {
        // Alias para crearPelota localmente si fuera necesario,
        // pero idealmente usar crearPelota(request)
        pelotas.add(pelota)
    }

    // ACTUALIZAR (PUT)
    // Se usa tanto para editar productos desde el panel Admin como para cambiar stock
    fun actualizarPelota(pelotaActualizada: Pelota) {
        viewModelScope.launch {
            try {
                val request = PelotaRequest(
                    nombre = pelotaActualizada.nombre,
                    precio = pelotaActualizada.precio,
                    descripcion = pelotaActualizada.descripcion,
                    imageUrl = pelotaActualizada.imageUrl,
                    deporte = pelotaActualizada.deporte,
                    marca = pelotaActualizada.marca,
                    stock = pelotaActualizada.stock
                )

                val response = catalogoApi.actualizarPelota(pelotaActualizada.id, request)

                if (response.isSuccessful && response.body() != null) {
                    actualizarListaLocal(response.body()!!)
                    Log.d("CatalogoVM", "✅ Pelota actualizada: ${pelotaActualizada.nombre}")
                } else {
                    _error.value = "Error al actualizar: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión al actualizar"
            }
        }
    }

    fun eliminarPelota(id: Long) {
        // Elimina localmente y del servidor
        viewModelScope.launch {
            try {
                val response = catalogoApi.eliminarPelota(id)
                if (response.isSuccessful) {
                    pelotas.removeAll { it.id == id }
                    Log.d("CatalogoVM", "✅ Pelota eliminada ID: $id")
                } else {
                    _error.value = "Error al eliminar: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar: ${e.message}"
                // Fallback local por si el server falla pero queremos que la UI responda
                pelotas.removeAll { it.id == id }
            }
        }
    }

    private fun actualizarListaLocal(response: PelotaResponse) {
        val index = pelotas.indexOfFirst { it.id == response.id }
        if (index != -1) {
            pelotas[index] = Pelota(
                id = response.id,
                nombre = response.nombre,
                precio = response.precio,
                descripcion = response.descripcion,
                imageUrl = response.imageUrl,
                deporte = response.deporte,
                marca = response.marca,
                stock = response.stock
            )
        }
    }

    private fun mapToRequest(pelota: Pelota, nuevoStock: Int): PelotaRequest {
        return PelotaRequest(
            nombre = pelota.nombre,
            precio = pelota.precio,
            descripcion = pelota.descripcion,
            imageUrl = pelota.imageUrl,
            deporte = pelota.deporte,
            marca = pelota.marca,
            stock = nuevoStock
        )
    }
}


