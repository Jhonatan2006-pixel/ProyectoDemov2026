package com.example.demov2026.ui.theme.Censista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.data.CensoRepository
import com.example.demov2026.data.TokenManager
import com.example.demov2026.model.CensoRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CensoState(
    val cargando: Boolean = false,
    val mensaje: String = "",
    val guardadoExitoso: Boolean = false
)

class CensoViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val repository = CensoRepository()

    private val _state = MutableStateFlow(CensoState())
    val state: StateFlow<CensoState> = _state

    fun guardarCenso(
        nombre: String,
        apellido: String,
        totalMiembros: String,
        cantidadTelevisiones: String,
        latitud: Double?,
        longitud: Double?
    ) {

        if (
            nombre.isBlank() ||
            apellido.isBlank() ||
            totalMiembros.isBlank() ||
            cantidadTelevisiones.isBlank()
        ) {
            _state.value = CensoState(
                mensaje = "Complete todos los campos"
            )
            return
        }

        val miembros = totalMiembros.toIntOrNull()
        val televisiones = cantidadTelevisiones.toIntOrNull()

        if (miembros == null || miembros <= 0) {
            _state.value = CensoState(
                mensaje = "El total de miembros no es válido"
            )
            return
        }

        if (televisiones == null || televisiones < 0) {
            _state.value = CensoState(
                mensaje = "La cantidad de televisiones no es válida"
            )
            return
        }

        if (latitud == null || longitud == null) {
            _state.value = CensoState(
                mensaje = "Primero debe obtener la ubicación"
            )
            return
        }

        val token = tokenManager.obtenerToken()

        if (token.isNullOrBlank()) {
            _state.value = CensoState(
                mensaje = "No se encontró una sesión activa"
            )
            return
        }

        val request = CensoRequest(
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            total_miembros = miembros,
            cantidad_televisiones = televisiones,
            latitud = latitud,
            longitud = longitud
        )

        viewModelScope.launch {

            _state.value = CensoState(
                cargando = true
            )

            val resultado = repository.guardarCenso(
                token = token,
                request = request
            )

            if (resultado.isSuccess) {

                _state.value = CensoState(
                    mensaje = "Censo registrado correctamente",
                    guardadoExitoso = true
                )

            } else {

                _state.value = CensoState(
                    mensaje = resultado.exceptionOrNull()?.message
                        ?: "Error al registrar el censo"
                )
            }
        }
    }

    fun limpiarEstado() {
        _state.value = CensoState()
    }
}