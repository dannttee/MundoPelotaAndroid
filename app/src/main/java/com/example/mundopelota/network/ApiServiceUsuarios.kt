package com.example.mundopelota.network

import retrofit2.Response
import retrofit2.http.*

// ===== DATA CLASSES =====

data class LoginRequest(
    val email: String,
    val password: String
)

data class UsuarioRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String = "CLIENTE" // Valor por defecto
)

data class UsuarioResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String?, // Nullable por seguridad
    val estado: String? = "activo", // Agregado para que no falle tu Admin Screen
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// Wrapper genérico para la respuesta del servidor
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

// ===== API SERVICE =====

interface ApiServiceUsuarios {

    // Login de usuario
    @POST("api/usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ApiResponse<UsuarioResponse>>

    // Registro de nuevo usuario
    @POST("api/usuarios/register")
    suspend fun register(@Body usuario: UsuarioRequest): Response<ApiResponse<UsuarioResponse>>

    // Obtener perfil de un usuario por ID
    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Int): Response<ApiResponse<UsuarioResponse>>

    // Obtener TODOS los usuarios (Para el Panel Admin)
    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): Response<ApiResponse<List<UsuarioResponse>>>

    // Actualizar usuario
    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Int,
        @Body usuario: UsuarioRequest
    ): Response<ApiResponse<UsuarioResponse>>

    // Eliminar usuario
    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Int): Response<ApiResponse<Unit>>
}
