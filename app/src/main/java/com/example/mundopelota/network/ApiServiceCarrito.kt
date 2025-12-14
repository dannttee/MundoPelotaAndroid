package com.example.mundopelota.network

import retrofit2.Response
import retrofit2.http.*

interface ApiServiceCarrito {

    @GET("/api/carrito/{usuarioId}")
    suspend fun obtenerCarrito(@Path("usuarioId") usuarioId: Int): Response<CarritoResponse>

    @POST("/api/carrito/{usuarioId}/items")
    suspend fun agregarProducto(
        @Path("usuarioId") usuarioId: Int,
        @Body item: ItemCarritoRequest
    ): Response<CarritoResponse>

    @DELETE("/api/carrito/{usuarioId}/items/{productoId}")
    suspend fun eliminarProducto(
        @Path("usuarioId") usuarioId: Int,
        @Path("productoId") productoId: Int
    ): Response<CarritoResponse>

    @POST("/api/carrito/{usuarioId}/checkout")
    suspend fun checkout(
        @Path("usuarioId") usuarioId: Int
    ): Response<ApiResponse<String>>
}

// Data Classes
data class CarritoResponse(
    val id: Int,
    val usuarioId: Int,
    val items: List<ItemCarritoResponse>
)

data class ItemCarritoRequest(
    val productoId: Int,
    val cantidad: Int
)

data class ItemCarritoResponse(
    val id: Int,
    val productoId: Int,
    val cantidad: Int,
    val precioUnitario: Double
)

data class OrdenResponse(
    val id: Int,
    val usuarioId: Int,
    val estado: String,
    val total: Double
)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)


