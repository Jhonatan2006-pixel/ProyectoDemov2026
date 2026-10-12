package com.example.demov2026.model

data class CensoRequest(
    val nombre: String,
    val apellido: String,
    val total_miembros: Int,
    val cantidad_televisiones: Int,
    val latitud: Double,
    val longitud: Double
)

data class CensoResponse(
    val success: Boolean,
    val message: String,
    val data: CensoData?
)

data class CensoData(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val total_miembros: Int,
    val cantidad_televisiones: Int,
    val latitud: Double,
    val longitud: Double,
    val tipo_carga: String,
    val usuario_id: Int?,
    val created_at: String?
)

data class CensosResponse(
    val success: Boolean,
    val message: String,
    val data: List<CensoData>
)
