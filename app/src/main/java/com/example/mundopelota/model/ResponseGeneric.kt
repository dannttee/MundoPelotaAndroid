package com.example.mundopelota.model

data class ResponseGeneric<T>(
    val status: Int,
    val message: String,
    val data: T?
)
