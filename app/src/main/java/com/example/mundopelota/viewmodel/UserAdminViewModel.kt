package com.example.mundopelota.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mundopelota.network.*
import kotlinx.coroutines.launch
import android.util.Log

class UserAdminViewModel : ViewModel() {

    private val usuariosApi = RetrofitClient.getApiServiceUsuarios()

    var isAdmin by mutableStateOf(false)

    private val _usuario = MutableLiveData<UsuarioResponse?>()
    val usuario: LiveData<UsuarioResponse?> = _usuario

    private val _usuarioId = MutableLiveData<Int?>()
    val usuarioId: LiveData<Int?> = _usuarioId

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loginExitoso = MutableLiveData<Boolean>(false)
    val loginExitoso: LiveData<Boolean> = _loginExitoso

    fun login(email: String, password: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                Log.d("UserVM", "Intentando login con: $email")
                val response = usuariosApi.login(LoginRequest(email, password))

                Log.d("UserVM", "Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val usuarioResponse = response.body()?.data
                    if (usuarioResponse != null) {
                        _usuario.value = usuarioResponse
                        _usuarioId.value = usuarioResponse.id
                        isAdmin = usuarioResponse.rol.uppercase() == "ADMIN"
                        _loginExitoso.value = true
                        _error.value = null
                        Log.d("UserVM", "✅ Login exitoso: ${usuarioResponse.nombre}, Admin: $isAdmin")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Error ${response.code()}: ${response.message()}"
                    _loginExitoso.value = false
                    Log.e("UserVM", "❌ Error response: $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                _loginExitoso.value = false
                Log.e("UserVM", "❌ Exception: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, nombre: String, password: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                Log.d("UserVM", "Intentando registro con: $email")
                val response = usuariosApi.register(
                    UsuarioRequest(email, nombre, password)
                )
                if (response.isSuccessful && response.body() != null) {
                    val usuarioResponse = response.body()?.data
                    if (usuarioResponse != null) {
                        _usuario.value = usuarioResponse
                        _usuarioId.value = usuarioResponse.id
                        isAdmin = usuarioResponse.rol.uppercase() == "ADMIN"
                        _loginExitoso.value = true
                        _error.value = null
                        Log.d("UserVM", "✅ Registro exitoso")
                    }
                } else {
                    _error.value = "Error en registro: ${response.message()}"
                    _loginExitoso.value = false
                    Log.e("UserVM", "❌ Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _loginExitoso.value = false
                Log.e("UserVM", "❌ Exception: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _usuario.value = null
        _usuarioId.value = null
        isAdmin = false
        _loginExitoso.value = false
        _error.value = null
        Log.d("UserVM", "Logout realizado")
    }

    fun resetLoginExitoso() {
        _loginExitoso.value = false
    }
}



