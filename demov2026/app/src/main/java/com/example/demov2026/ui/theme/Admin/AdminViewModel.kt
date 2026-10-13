package com.example.demov2026.ui.theme.Admin

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
    val mensaje: String = ""
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
                mensaje = "No se encontró una sesión activa"
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
                        "No hay censos para mostrar"
                    } else {
                        "Censos obtenidos correctamente"
                    }
                )

            } else {

                _state.value = AdminState(
                    cargando = false,
                    mensaje = resultado.exceptionOrNull()?.message
                        ?: "Error al obtener los censos"
                )
            }
        }
    }
}
