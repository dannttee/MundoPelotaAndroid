package com.example.mundopelota.utils

object EmailValidator {
    private val ALLOWED_DOMAINS = listOf("@duoc.cl", "@gmail.com", "@duocprofesor.cl")

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() &&
                ALLOWED_DOMAINS.any { email.endsWith(it) } &&
                email.contains("@")
    }

    fun getErrorMessage(email: String): String {
        return when {
            email.isEmpty() -> "El email es requerido"
            !email.contains("@") -> "Email inválido"
            !ALLOWED_DOMAINS.any { email.endsWith(it) } ->
                "Email debe ser @duoc.cl, @gmail.com o @duocprofesor.cl"
            else -> ""
        }
    }
}
