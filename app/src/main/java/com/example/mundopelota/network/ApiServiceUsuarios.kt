package com.example.mundopelota.network

import retrofit2.Response
import retrofit2.http.*

// ===== DATA CLASSES =====

data class LoginRequest(
    val email: String,
    val password: String
)

data class UsuarioRequest(
    val email: String,
    val nombre: String,
    val password: String
)

data class UsuarioResponse(
    val id: Int,
    val email: String,
    val nombre: String,
    val rol: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

// ===== API SERVICE =====

interface ApiServiceUsuarios {

    @POST("api/usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ApiResponse<UsuarioResponse>>

    @POST("api/usuarios/register")
    suspend fun register(@Body usuario: UsuarioRequest): Response<ApiResponse<UsuarioResponse>>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Int): Response<UsuarioResponse>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Int,
        @Body usuario: UsuarioRequest
    ): Response<UsuarioResponse>
}

