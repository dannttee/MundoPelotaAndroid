package com.example.mundopelota.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

// 1. Modelos para leer la respuesta de mindicador.cl
data class DolarResponse(
    @SerializedName("serie") val serie: List<SerieData>
)

data class SerieData(
    @SerializedName("valor") val valor: Double,
    @SerializedName("fecha") val fecha: String
)

// 2. La Interfaz
interface ApiServiceExternal {
    @GET("api/dolar")
    suspend fun obtenerValorDolar(): Response<DolarResponse>
}

