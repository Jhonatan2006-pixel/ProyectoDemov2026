package com.example.demov2026.ui.theme.Censista

import androidx.annotation.StringRes
import com.example.demov2026.data.textoDeError
import com.example.demov2026.R
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
    @StringRes val mensaje: Int? = null,
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
                mensaje = R.string.msg_complete_fields
            )
            return
        }

        val miembros = totalMiembros.toIntOrNull()
        val televisiones = cantidadTelevisiones.toIntOrNull()

        if (miembros == null || miembros <= 0) {
            _state.value = CensoState(
                mensaje = R.string.msg_invalid_members
            )
            return
        }

        if (televisiones == null || televisiones < 0) {
            _state.value = CensoState(
                mensaje = R.string.msg_invalid_tvs
            )
            return
        }

        if (latitud == null || longitud == null) {
            _state.value = CensoState(
                mensaje = R.string.msg_get_location_first
            )
            return
        }

        val token = tokenManager.obtenerToken()

        if (token.isNullOrBlank()) {
            _state.value = CensoState(
                mensaje = R.string.msg_no_session
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
                    mensaje = R.string.msg_census_saved,
                    guardadoExitoso = true
                )

            } else {

                _state.value = CensoState(
                    mensaje = resultado.exceptionOrNull().textoDeError(R.string.error_save_census)
                )
            }
        }
    }

    fun limpiarEstado() {
        _state.value = CensoState()
    }
}