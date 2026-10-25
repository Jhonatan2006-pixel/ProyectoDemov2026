package com.example.demov2026.model

import androidx.annotation.StringRes
import android.graphics.Bitmap

data class ProfileState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    @StringRes val mensaje: Int? = null,
    val fotoPerfil: Bitmap? = null
)