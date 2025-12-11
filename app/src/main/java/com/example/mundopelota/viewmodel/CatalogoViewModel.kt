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
import kotlinx.coroutines.launch
import android.util.Log

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
                    Log.d("CatalogoVM", "Pelotas recibidas: ${pelotas.size}")
                } else {
                    _error.value = "Error ${response.code()}: ${response.message()}"
                    Log.e("CatalogoVM", "Error body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                Log.e("CatalogoVM", "Exception: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
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

    // Elimina una pelota por id en la lista local
    fun removePelota(id: Long) {
        pelotas.removeAll { it.id == id }
    }

    // Actualiza una pelota en la lista local
    fun updatePelota(pelotaActualizada: Pelota) {
        val index = pelotas.indexOfFirst { it.id == pelotaActualizada.id }
        if (index != -1) {
            pelotas[index] = pelotaActualizada
        }
    }
}






