package com.example.demov2026.model

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val telefono: String?,
    val password: String,
    val rol: String = "CENSISTA",

    // Solo se envía al registrarse como administrador
    val codigo_admin: String? = null
)

// Código que autoriza registrarse como administrador
data class CodigoAdminRequest(
    val codigo: String
)

data class CodigoAdminResponse(
    val success: Boolean,
    val message: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UsuarioResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String?,
    val foto: String? = null,
    val rol: String = "CENSISTA"
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: UsuarioResponse?
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UsuarioResponse?,
    val token: String?
)

data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: UsuarioResponse?
)

// Datos que Android enviará al backend para guardar la foto
data class FotoRequest(
    val foto: String
)

// Respuesta del backend después de guardar la foto
data class FotoResponse(
    val success: Boolean,
    val message: String,
    val data: Any?
)