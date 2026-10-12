package com.example.demov2026.ui.theme.Register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.data.AuthRepository
import com.example.demov2026.model.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.demov2026.data.local.UsuarioDao
class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val _state = MutableStateFlow(RegisterState())

    val state: StateFlow<RegisterState> = _state

    fun onNombreChange(nuevoNombre: String) {

        _state.value = _state.value.copy(
            nombre = nuevoNombre
        )
    }

    fun onEmailChange(nuevoEmail: String) {

        _state.value = _state.value.copy(
            email = nuevoEmail
        )
    }

    fun onTelefonoChange(nuevoTelefono: String) {

        _state.value = _state.value.copy(
            telefono = nuevoTelefono
        )
    }

    fun onPasswordChange(nuevoPassword: String) {

        _state.value = _state.value.copy(
            password = nuevoPassword
        )
    }

    fun onConfirmarPasswordChange(nuevoPassword: String) {

        _state.value = _state.value.copy(
            confirmarPassword = nuevoPassword
        )
    }

    fun onRegisterClic() {

        val datos = _state.value

        if (
            datos.nombre.isBlank() ||
            datos.email.isBlank() ||
            datos.telefono.isBlank() ||
            datos.password.isBlank()
        ) {

            _state.value = datos.copy(
                mensaje = "Debe completar todos los campos",
                registroExito = false
            )

            return
        }

        if (datos.password != datos.confirmarPassword) {

            _state.value = datos.copy(
                mensaje = "Las contraseñas no coinciden",
                registroExito = false
            )

            return
        }

        viewModelScope.launch {

            _state.value = datos.copy(
                mensaje = "Registrando usuario...",
                registroExito = false
            )

            val resultado = repository.registrar(
                nombre = datos.nombre,
                email = datos.email,
                password = datos.password,
                telefono = datos.telefono
            )

            if (resultado.isSuccess) {

                _state.value = datos.copy(
                    mensaje = "Usuario registrado correctamente",
                    registroExito = true
                )

            } else {

                _state.value = datos.copy(
                    mensaje = resultado.exceptionOrNull()?.message
                        ?: "El correo ya está registrado",
                    registroExito = false
                )
            }
        }
    }
}