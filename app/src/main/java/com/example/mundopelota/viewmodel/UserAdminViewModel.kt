package com.example.mundopelota.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mundopelota.network.LoginRequest
import com.example.mundopelota.network.RetrofitClient
import com.example.mundopelota.network.UsuarioResponse
// Asegúrate de importar tu modelo de dominio para la lista
import com.example.mundopelota.model.Usuario
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.util.Log

class UserAdminViewModel : ViewModel() {

    private val usuariosApi = RetrofitClient.getApiServiceUsuarios()

    // Estado para saber si el usuario logueado es Admin (para navegación)
    var isAdmin by mutableStateOf(false)

    // DATOS DEL USUARIO LOGUEADO (Desde la API)
    private val _usuario = MutableLiveData<UsuarioResponse?>()
    val usuario: LiveData<UsuarioResponse?> = _usuario

    private val _loginExitoso = MutableLiveData<Boolean>(false)
    val loginExitoso: LiveData<Boolean> = _loginExitoso

    // REGISTRO
    private val _registroExitoso = MutableLiveData<Boolean>(false)
    val registroExitoso: LiveData<Boolean> = _registroExitoso

    // LISTA DE USUARIOS (Para el panel de administración)
    private val _usuarios = MutableLiveData<List<Usuario>>(emptyList())
    val usuarios: LiveData<List<Usuario>> = _usuarios

    // ESTADOS GENERALES
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(email: String, password: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                // Llamada real a la API
                val response = usuariosApi.login(LoginRequest(email, password))

                if (response.isSuccessful && response.body() != null) {
                    val usuarioResponse = response.body()?.data
                    if (usuarioResponse != null) {
                        _usuario.value = usuarioResponse
                        // Determinamos si es admin basándonos en la respuesta del servidor
                        isAdmin = usuarioResponse.rol?.uppercase() == "ADMIN"
                        _loginExitoso.value = true
                    } else {
                        _error.value = "Respuesta vacía del servidor"
                    }
                } else {
                    _error.value = "Error login: ${response.message()}"
                    _loginExitoso.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                _loginExitoso.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(nombre: String, email: String, password: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                // TODO: Descomentar esto cuando la API de registro esté lista
                // val response = usuariosApi.register(RegisterRequest(nombre, email, password))

                // SIMULACIÓN DE ÉXITO (Para poder avanzar en la UI)
                delay(1000)
                _registroExitoso.value = true
                Log.d("Register", "Registro simulado exitoso para $email")

            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun obtenerUsuarios() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // SIMULACIÓN DE DATOS (Hardcoded)
                // Esto asegura que la lista en el Admin Panel no salga vacía mientras arreglas el backend
                delay(500) // Simula tiempo de red
                _usuarios.value = listOf(
                    Usuario(1, "Admin Principal", "admin@mundopelota.com", "ADMIN", "activo"),
                    Usuario(2, "Juan Perez", "juan@gmail.com", "CLIENTE", "activo"),
                    Usuario(3, "Maria Gomez", "maria@hotmail.com", "CLIENTE", "inactivo"),
                    Usuario(4, "Soporte Técnico", "soporte@mundopelota.com", "ADMIN", "activo")
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _usuario.value = null
        isAdmin = false
        _loginExitoso.value = false
        // Opcional: Limpiar lista de usuarios al salir
        _usuarios.value = emptyList()
    }

    fun resetLoginExitoso() {
        _loginExitoso.value = false
    }

    fun resetRegistroExitoso() {
        _registroExitoso.value = false
    }
}
