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

    fun obtenerCensos() {

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

            val resultado = repository.obtenerCensos(token)

            if (resultado.isSuccess) {

                val listaCensos = resultado.getOrNull() ?: emptyList()

                _state.value = AdminState(
                    cargando = false,
                    censos = listaCensos,
                    mensaje = "Censos obtenidos correctamente"
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