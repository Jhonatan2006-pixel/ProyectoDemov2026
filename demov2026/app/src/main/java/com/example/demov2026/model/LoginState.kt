package com.example.demov2026.model

import androidx.annotation.StringRes

data class LoginState(
    val email: String = "",
    val password: String = "",
    @StringRes val mensaje: Int? = null,
    val cargando: Boolean = false,
    val loginExito: Boolean = false,
    val rol: String = ""
)