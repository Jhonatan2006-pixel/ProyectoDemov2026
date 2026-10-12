package com.example.demov2026.model

data class LoginState(
    val email: String = "",
    val password: String = "",
    val mensaje: String = "",
    val loginExito: Boolean = false,
    val rol: String = ""
)