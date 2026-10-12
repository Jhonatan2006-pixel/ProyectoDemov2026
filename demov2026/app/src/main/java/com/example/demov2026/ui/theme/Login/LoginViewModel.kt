package com.example.demov2026.ui.theme.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.data.AuthRepository
import com.example.demov2026.data.TokenManager
import com.example.demov2026.data.local.UsuarioDao
import com.example.demov2026.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val tokenManager: TokenManager,
    usuarioDao: UsuarioDao
) : ViewModel() {

    private val repository = AuthRepository(usuarioDao)

    private val _state = MutableStateFlow(LoginState())

    val state: StateFlow<LoginState> = _state

    fun onEmailChange(nuevoEmail: String) {
        _state.value = _state.value.copy(
            email = nuevoEmail
        )
    }

    fun onPasswordChange(nuevoPassword: String) {
        _state.value = _state.value.copy(
            password = nuevoPassword
        )
    }

    fun onLoginClic() {

        viewModelScope.launch {

            _state.value = _state.value.copy(
                mensaje = "Iniciando sesión..."
            )

            val resultado = repository.login(
                _state.value.email,
                _state.value.password
            )

            if (resultado.isSuccess) {

                val usuarioYToken = resultado.getOrNull()

                val token = usuarioYToken?.second

                if (token != null) {
                    tokenManager.guardarToken(token)
                }

                _state.value = _state.value.copy(
                    mensaje = "Bienvenido ${usuarioYToken?.first?.nombre}",
                    loginExito = true,
                    rol = usuarioYToken?.first?.rol ?: ""
                )

            } else {

                _state.value = _state.value.copy(
                    mensaje = resultado.exceptionOrNull()?.message
                        ?: "Credenciales incorrectas",
                    loginExito = false
                )
            }
        }
    }
}