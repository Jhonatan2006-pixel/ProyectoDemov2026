package com.example.demov2026.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String?,
    val rol: String = "CENSISTA"
)