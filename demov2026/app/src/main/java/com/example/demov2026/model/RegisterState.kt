package com.example.demov2026.model

data class RegisterState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val mensaje: String = "",
    val registroExito: Boolean = false
)