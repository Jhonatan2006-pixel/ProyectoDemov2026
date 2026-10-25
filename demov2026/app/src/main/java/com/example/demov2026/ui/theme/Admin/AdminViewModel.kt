package com.example.demov2026.ui.theme.Admin

import androidx.annotation.StringRes
import com.example.demov2026.data.textoDeError
import com.example.demov2026.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.data.CensoRepository
import com.example.demov2026.data.TokenManager
import com.example.demov2026.model.CensoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminState(
    val cargando: Boolean = false,
    val censos: List<CensoData> = emptyList(),
    @StringRes val mensaje: Int? = null
)

class AdminViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val repository = CensoRepository()

    private val _state = MutableStateFlow(AdminState())
    val state: StateFlow<AdminState> = _state

    // Todos los censos: carga normal y carga masiva
    fun obtenerCensos() {
        cargarCensos { token ->
            repository.obtenerCensos(token)
        }
    }

    // Solamente los censos de carga masiva
    fun obtenerCensosMasivos() {
        cargarCensos { token ->
            repository.obtenerCensosMasivos(token)
        }
    }

    private fun cargarCensos(
        consulta: suspend (String) -> Result<List<CensoData>>
    ) {

        val token = tokenManager.obtenerToken()

        if (token.isNullOrBlank()) {
            _state.value = AdminState(
                mensaje = R.string.msg_no_session
            )
            return
        }

        viewModelScope.launch {

            _state.value = AdminState(
                cargando = true
            )

            val resultado = consulta(token)

            if (resultado.isSuccess) {

                val listaCensos = resultado.getOrNull() ?: emptyList()

                _state.value = AdminState(
                    cargando = false,
                    censos = listaCensos,
                    mensaje = if (listaCensos.isEmpty()) {
                        R.string.msg_no_censuses
                    } else {
                        null
                    }
                )

            } else {

                _state.value = AdminState(
                    cargando = false,
                    mensaje = resultado.exceptionOrNull().textoDeError(R.string.error_load_censuses)
                )
            }
        }
    }
}
