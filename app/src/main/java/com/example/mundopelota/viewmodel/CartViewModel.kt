package com.example.mundopelota.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mundopelota.model.Pelota
import com.example.mundopelota.network.*
import kotlinx.coroutines.launch
import android.util.Log

class CartViewModel : ViewModel() {

    private val carritoApi = RetrofitClient.getApiServiceCarrito()

    // Tu carrito local (para UI)
    var carrito = mutableStateListOf<Pelota>()
        private set

    // Carrito del servidor
    private val _carritoServidor = MutableLiveData<CarritoResponse?>()
    val carritoServidor: LiveData<CarritoResponse?> = _carritoServidor

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _usuarioId = MutableLiveData<Int>()

    // Agregar producto localmente (mantener compatibilidad)
    fun addPelotaAlCarrito(pelota: Pelota) {
        carrito.add(pelota)
    }

    // Agregar producto al servidor
    fun agregarAlCarritoServidor(usuarioId: Int, productoId: Int, cantidad: Int = 1) {
        _isLoading.value = true
        _usuarioId.value = usuarioId
        viewModelScope.launch {
            try {
                val response = carritoApi.agregarProducto(
                    usuarioId,
                    ItemCarritoRequest(productoId, cantidad)
                )
                if (response.isSuccessful && response.body() != null) {
                    _carritoServidor.value = response.body()
                    _error.value = null
                    Log.d("CartVM", "Producto agregado: ${response.body()?.items?.size} items")
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("CartVM", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("CartVM", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Obtener carrito del servidor
    fun obtenerCarritoServidor(usuarioId: Int) {
        _isLoading.value = true
        _usuarioId.value = usuarioId
        viewModelScope.launch {
            try {
                val response = carritoApi.obtenerCarrito(usuarioId)
                if (response.isSuccessful && response.body() != null) {
                    _carritoServidor.value = response.body()
                    _error.value = null
                    Log.d("CartVM", "Carrito obtenido: ${response.body()?.items?.size} items")
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("CartVM", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Eliminar producto del carrito
    fun eliminarDelCarritoServidor(productoId: Int) {
        val usuarioId = _usuarioId.value ?: return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = carritoApi.eliminarProducto(usuarioId, productoId)
                if (response.isSuccessful && response.body() != null) {
                    _carritoServidor.value = response.body()
                    _error.value = null
                    Log.d("CartVM", "Producto eliminado")
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Realizar checkout
    fun checkoutServidor(usuarioId: Int) {
        _usuarioId.value = usuarioId
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("CartVM", "🔄 Iniciando checkout para usuario: $usuarioId")
                val response = carritoApi.checkout(usuarioId)
                Log.d("CartVM", "✅ Respuesta: ${response.code()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    _carritoServidor.value = null
                    carrito.clear()
                    _error.value = null
                    Log.d("CartVM", "✅ Checkout realizado")
                } else {
                    _error.value = "Error: ${response.code()}"
                    Log.e("CartVM", "❌ Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("CartVM", "❌ Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Limpiar carrito local
    fun clearCart() {
        carrito.clear()
    }

    // Limpiar carrito completo
    fun limpiarTodo() {
        carrito.clear()
        _carritoServidor.value = null
    }
}




