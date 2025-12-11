package com.example.mundopelota.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceCatalogo {

    @GET("api/pelotas")
    suspend fun obtenerPelotas(): Response<List<PelotaResponse>>

    @GET("api/pelotas/{id}")
    suspend fun obtenerPelota(@Path("id") id: Long): Response<PelotaResponse>

    @POST("api/pelotas")
    suspend fun crearPelota(@Body pelota: PelotaRequest): Response<PelotaResponse>

    @PUT("api/pelotas/{id}")
    suspend fun actualizarPelota(
        @Path("id") id: Long,
        @Body pelota: PelotaRequest
    ): Response<PelotaResponse>

    @DELETE("api/pelotas/{id}")
    suspend fun eliminarPelota(@Path("id") id: Long): Response<Void>
}

// Data classes alineadas con Pelota de Spring
data class PelotaResponse(
    val id: Long,
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val imageUrl: String,
    val deporte: String,
    val marca: String,
    val stock: Int
)

data class PelotaRequest(
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val imageUrl: String,
    val deporte: String,
    val marca: String,
    val stock: Int
)

