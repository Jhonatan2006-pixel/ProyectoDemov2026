package com.example.demov2026.model

import androidx.annotation.StringRes

data class RegisterState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    @StringRes val mensaje: Int? = null,
    val cargando: Boolean = false,
    val registroExito: Boolean = false,

    // Rol elegido: CENSISTA o ADMIN
    val rol: String = "CENSISTA",

    // Código de administrador ya verificado por el backend
    val codigoAdmin: String = "",

    // Cuadro donde se escribe el código de administrador
    val mostrarDialogoCodigo: Boolean = false,
    val codigoIngresado: String = "",
    val verificandoCodigo: Boolean = false,
    @StringRes val mensajeCodigo: Int? = null
)
