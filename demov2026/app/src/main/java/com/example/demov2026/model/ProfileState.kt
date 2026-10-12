package com.example.demov2026.model

import android.graphics.Bitmap

data class ProfileState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val mensaje: String = "",
    val fotoPerfil: Bitmap? = null
)