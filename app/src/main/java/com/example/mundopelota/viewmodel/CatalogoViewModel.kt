package com.example.mundopelota.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.network.ApiServiceCatalogo
import com.example.mundopelota.network.PelotaRequest
import com.example.mundopelota.network.PelotaResponse
import com.example.mundopelota.network.RetrofitClient
import com.example.mundopelota.repository.PelotaRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import android.util.Log
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CatalogoViewModel : ViewModel() {

    private val catalogoApi: ApiServiceCatalogo = RetrofitClient.getApiServiceCatalogo()

    // Lista que usa tu CatalogoScreen y CatalogoAdminScreen
    val pelotas = mutableStateListOf<Pelota>()

    // Respuesta cruda del servidor (opcional)
    private val _pelotasServidor = MutableLiveData<List<PelotaResponse>?>()
    val pelotasServidor: LiveData<List<PelotaResponse>?> = _pelotasServidor

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        // Cargar pelotas del BACKEND al iniciar
        obtenerPelotasServidor()
    }

    private fun cargarPelotasIniciales() {
        pelotas.clear()
        pelotas.addAll(PelotaRepository.getPelotas())
        Log.d("CatalogoVM", "Pelotas iniciales (fallback) cargadas: ${pelotas.size}")
        pelotas.forEach { Log.d("CatalogoVM", "Local: ${it.nombre} - Stock: ${it.stock}") }
    }

    fun obtenerPelotasServidor() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                Log.d("CatalogoVM", "Llamando a /api/pelotas")
                val response = catalogoApi.obtenerPelotas()

                Log.d("CatalogoVM", "Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    _pelotasServidor.value = data

                    // 🔍 LOG DE DEBUG: Ver qué devuelve el servidor
                    Log.d("CatalogoVM", "Datos del servidor recibidos: ${data.size} pelotas")
                    data.forEach {
                        Log.d("CatalogoVM", "Servidor: ID=${it.id}, Nombre=${it.nombre}, Stock=${it.stock}")
                    }

                    // Mapear PelotaResponse → Pelota (modelo de la app)
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
                    Log.d("CatalogoVM", "Pelotas recibidas del servidor: ${pelotas.size}")
                    pelotas.forEach { Log.d("CatalogoVM", "Mapeada: ${it.nombre} - Stock: ${it.stock}") }
                } else {
                    _error.value = "Error ${response.code()}: ${response.message()}"
                    Log.e("CatalogoVM", "Error body: ${response.errorBody()?.string()}")
                    // Si falla, volver a cargar las iniciales como fallback
                    cargarPelotasIniciales()
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CatalogoVM", "Exception: ${e.stackTraceToString()}")
                // Si falla, volver a cargar las iniciales como fallback
                cargarPelotasIniciales()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 🆕 NUEVA FUNCIÓN: Decrementar stock al comprar
    fun decrementarStockAlComprar(pelotaId: Long, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                // Encontrar la pelota en la lista local
                val pelota = pelotas.find { it.id == pelotaId }

                if (pelota == null) {
                    Log.e("CatalogoVM", "❌ Pelota con ID $pelotaId no encontrada")
                    _error.value = "Producto no encontrado"
                    return@launch
                }

                // Validar que haya stock
                if (pelota.stock <= 0) {
                    Log.e("CatalogoVM", "❌ Sin stock para ${pelota.nombre}")
                    _error.value = "No hay stock disponible"
                    return@launch
                }

                Log.d("CatalogoVM", "📦 Decrementando stock de: ${pelota.nombre} (ID=$pelotaId)")
                Log.d("CatalogoVM", "   Stock actual: ${pelota.stock} → Nuevo: ${pelota.stock - 1}")

                // Crear request con stock decrementado
                val nuevoStock = pelota.stock - 1
                val request = PelotaRequest(
                    nombre = pelota.nombre,
                    precio = pelota.precio,
                    descripcion = pelota.descripcion,
                    imageUrl = pelota.imageUrl,
                    deporte = pelota.deporte,
                    marca = pelota.marca,
                    stock = nuevoStock  // ← STOCK DECREMENTADO
                )

                // Actualizar en el servidor usando PUT /api/pelotas/{id}
                val response = catalogoApi.actualizarPelota(pelotaId, request)

                if (response.isSuccessful && response.body() != null) {
                    val pelotaActualizada = response.body()!!

                    // Actualizar la lista local con los nuevos datos
                    val index = pelotas.indexOfFirst { it.id == pelotaId }
                    if (index != -1) {
                        pelotas[index] = Pelota(
                            id = pelotaActualizada.id,
                            nombre = pelotaActualizada.nombre,
                            precio = pelotaActualizada.precio,
                            descripcion = pelotaActualizada.descripcion,
                            imageUrl = pelotaActualizada.imageUrl,
                            deporte = pelotaActualizada.deporte,
                            marca = pelotaActualizada.marca,
                            stock = pelotaActualizada.stock
                        )
                    }

                    Log.d("CatalogoVM", "✅ Stock actualizado en servidor: ${pelotaActualizada.nombre}")
                    Log.d("CatalogoVM", "   Nuevo stock desde servidor: ${pelotaActualizada.stock}")
                    _error.value = null
                    onSuccess?.invoke()
                } else {
                    Log.e("CatalogoVM", "❌ Error ${response.code()}: ${response.message()}")
                    Log.e("CatalogoVM", "   Body: ${response.errorBody()?.string()}")
                    _error.value = "Error al actualizar stock: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("CatalogoVM", "❌ Exception al decrementar stock: ${e.message}")
                Log.e("CatalogoVM", e.stackTraceToString())
                _error.value = "Error de conexión: ${e.message}"
            }
        }
    }

    // 🆕 VERSIÓN SUSPENDIBLE: Restaurar stock (ESPERA hasta completar)
    suspend fun restaurarStockAlCancelarSuspend(pelotaId: Long): Result<Unit> {
        return try {
            // Encontrar la pelota
            val pelota = pelotas.find { it.id == pelotaId }

            if (pelota == null) {
                Log.e("CatalogoVM", "❌ Pelota ID $pelotaId no encontrada")
                return Result.failure(Exception("Producto no encontrado"))
            }

            Log.d("CatalogoVM", "🔄 Restaurando stock (suspend): ${pelota.nombre}")

            // Crear request con stock incrementado
            val nuevoStock = pelota.stock + 1
            val request = PelotaRequest(
                nombre = pelota.nombre,
                precio = pelota.precio,
                descripcion = pelota.descripcion,
                imageUrl = pelota.imageUrl,
                deporte = pelota.deporte,
                marca = pelota.marca,
                stock = nuevoStock
            )

            // Llamar API
            val response = catalogoApi.actualizarPelota(pelotaId, request)

            if (response.isSuccessful && response.body() != null) {
                val pelotaActualizada = response.body()!!

                // Actualizar lista local
                val index = pelotas.indexOfFirst { it.id == pelotaId }
                if (index != -1) {
                    pelotas[index] = Pelota(
                        id = pelotaActualizada.id,
                        nombre = pelotaActualizada.nombre,
                        precio = pelotaActualizada.precio,
                        descripcion = pelotaActualizada.descripcion,
                        imageUrl = pelotaActualizada.imageUrl,
                        deporte = pelotaActualizada.deporte,
                        marca = pelotaActualizada.marca,
                        stock = pelotaActualizada.stock
                    )
                }

                Log.d("CatalogoVM", "✅ Stock restaurado (suspend): ${pelotaActualizada.nombre} - Nuevo stock: ${pelotaActualizada.stock}")
                _error.value = null
                Result.success(Unit)
            } else {
                Log.e("CatalogoVM", "❌ Error al restaurar: ${response.code()}")
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("CatalogoVM", "❌ Exception: ${e.message}")
            Result.failure(e)
        }
    }

    // 🆕 VERSIÓN CALLBACK: Restaurar stock (por si aún usas callbacks)
    fun restaurarStockAlCancelar(pelotaId: Long, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                // Encontrar la pelota en la lista local
                val pelota = pelotas.find { it.id == pelotaId }

                if (pelota == null) {
                    Log.e("CatalogoVM", "❌ Pelota con ID $pelotaId no encontrada para restaurar")
                    _error.value = "Producto no encontrado"
                    return@launch
                }

                Log.d("CatalogoVM", "🔄 Restaurando stock de: ${pelota.nombre} (ID=$pelotaId)")
                Log.d("CatalogoVM", "   Stock actual: ${pelota.stock} → Nuevo: ${pelota.stock + 1}")

                // Crear request con stock incrementado
                val nuevoStock = pelota.stock + 1
                val request = PelotaRequest(
                    nombre = pelota.nombre,
                    precio = pelota.precio,
                    descripcion = pelota.descripcion,
                    imageUrl = pelota.imageUrl,
                    deporte = pelota.deporte,
                    marca = pelota.marca,
                    stock = nuevoStock  // ← STOCK INCREMENTADO
                )

                // Actualizar en el servidor usando PUT /api/pelotas/{id}
                val response = catalogoApi.actualizarPelota(pelotaId, request)

                if (response.isSuccessful && response.body() != null) {
                    val pelotaActualizada = response.body()!!

                    // Actualizar la lista local con los nuevos datos
                    val index = pelotas.indexOfFirst { it.id == pelotaId }
                    if (index != -1) {
                        pelotas[index] = Pelota(
                            id = pelotaActualizada.id,
                            nombre = pelotaActualizada.nombre,
                            precio = pelotaActualizada.precio,
                            descripcion = pelotaActualizada.descripcion,
                            imageUrl = pelotaActualizada.imageUrl,
                            deporte = pelotaActualizada.deporte,
                            marca = pelotaActualizada.marca,
                            stock = pelotaActualizada.stock
                        )
                    }

                    Log.d("CatalogoVM", "✅ Stock restaurado en servidor: ${pelotaActualizada.nombre}")
                    Log.d("CatalogoVM", "   Nuevo stock desde servidor: ${pelotaActualizada.stock}")
                    _error.value = null
                    onSuccess?.invoke()
                } else {
                    Log.e("CatalogoVM", "❌ Error al restaurar: ${response.code()}")
                    Log.e("CatalogoVM", "   Body: ${response.errorBody()?.string()}")
                    _error.value = "Error al restaurar stock: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("CatalogoVM", "❌ Exception al restaurar stock: ${e.message}")
                Log.e("CatalogoVM", e.stackTraceToString())
                _error.value = "Error de conexión: ${e.message}"
            }
        }
    }

    // Crear una pelota en el backend y agregarla a la lista local
    fun crearPelota(request: PelotaRequest, onSuccess: (() -> Unit)? = null) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
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

    // ALIAS: agregarPelota → para CatalogoAdminScreen
    fun agregarPelota(pelota: Pelota) {
        pelotas.add(pelota)
    }

    // Elimina una pelota por id en la lista local
    fun removePelota(id: Long) {
        pelotas.removeAll { it.id == id }
    }

    // ALIAS: eliminarPelota → para CatalogoAdminScreen
    fun eliminarPelota(id: Long) {
        removePelota(id)
    }

    // Actualiza una pelota en la lista local
    fun updatePelota(pelotaActualizada: Pelota) {
        val index = pelotas.indexOfFirst { it.id == pelotaActualizada.id }
        if (index != -1) {
            pelotas[index] = pelotaActualizada
        }
    }

    // ALIAS: actualizarPelota → para CatalogoAdminScreen
    fun actualizarPelota(pelotaActualizada: Pelota) {
        updatePelota(pelotaActualizada)
    }
}
