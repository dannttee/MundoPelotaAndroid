package com.example.mundopelota.repository

import com.example.mundopelota.model.Pelota

object PelotaRepository {

    private val pelotasIniciales = listOf(
        Pelota(
            id = 1L,
            nombre = "Molten GG7X",
            precio = 19990.0,
            descripcion = "Pelota profesional de basketball, tamaño 7",
            imageUrl = "",
            deporte = "BASKETBALL",
            marca = "Molten",
            stock = 10
        ),
        Pelota(
            id = 2L,
            nombre = "Spalding NBA",
            precio = 23990.0,
            descripcion = "Pelota oficial de basketball NBA",
            imageUrl = "",
            deporte = "BASKETBALL",
            marca = "Spalding",
            stock = 8
        ),
        Pelota(
            id = 3L,
            nombre = "Adidas Tango",
            precio = 17990.0,
            descripcion = "Pelota de fútbol tradicional, resistente al agua",
            imageUrl = "",
            deporte = "FUTBOL",
            marca = "Adidas",
            stock = 12
        ),
        Pelota(
            id = 4L,
            nombre = "Nike Flight",
            precio = 24990.0,
            descripcion = "Pelota de fútbol alta competición",
            imageUrl = "",
            deporte = "FUTBOL",
            marca = "Nike",
            stock = 6
        ),
        Pelota(
            id = 5L,
            nombre = "Mikasa V200W",
            precio = 27990.0,
            descripcion = "Pelota oficial de volleyball olímpico",
            imageUrl = "",
            deporte = "VOLLEYBALL",
            marca = "Mikasa",
            stock = 7
        ),
        Pelota(
            id = 6L,
            nombre = "Wilson AVP",
            precio = 15990.0,
            descripcion = "Pelota de volleyball para juegos playeros",
            imageUrl = "",
            deporte = "VOLLEYBALL",
            marca = "Wilson",
            stock = 15
        )
    )

    fun getPelotas(): List<Pelota> = pelotasIniciales
}



