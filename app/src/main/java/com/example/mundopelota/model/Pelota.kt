package com.example.mundopelota.model

data class Pelota(
    val id: Long,
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val imageUrl: String,
    val deporte: String,
    val marca: String,
    val stock: Int
)


enum class CategoriaPelota { BASKETBALL, FUTBOL, VOLLEYBALL }

fun categoriaDesdeDeporte(deporte: String): CategoriaPelota? =
    when (deporte.uppercase()) {
        "BASKETBALL" -> CategoriaPelota.BASKETBALL
        "FUTBOL"     -> CategoriaPelota.FUTBOL
        "VOLLEYBALL" -> CategoriaPelota.VOLLEYBALL
        else         -> null
    }



