package com.example.demov2026.ui.theme.Register

import com.example.demov2026.data.textoDeError
import com.example.demov2026.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.data.AuthRepository
import com.example.demov2026.model.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    // ============================
    // ROL
    // ============================

    fun onCensistaSeleccionado() {

        _state.value = _state.value.copy(
            rol = "CENSISTA",
            codigoAdmin = ""
        )
    }

    // El rol no cambia a ADMIN todavía: primero se pide el código
    fun onAdminSeleccionado() {

        if (_state.value.rol == "ADMIN") {
            return
        }

        _state.value = _state.value.copy(
            mostrarDialogoCodigo = true,
            codigoIngresado = "",
            mensajeCodigo = null
        )
    }

    fun onCodigoChange(nuevoCodigo: String) {

        _state.value = _state.value.copy(
            codigoIngresado = nuevoCodigo,
            mensajeCodigo = null
        )
    }

    // Cerrar el cuadro sin verificar: el rol se queda como estaba
    fun onCancelarCodigo() {

        _state.value = _state.value.copy(
            mostrarDialogoCodigo = false,
            codigoIngresado = "",
            verificandoCodigo = false,
            mensajeCodigo = null
        )
    }

    fun onVerificarCodigo() {

        val codigo = _state.value.codigoIngresado.trim()

        if (codigo.isEmpty()) {

            _state.value = _state.value.copy(
                mensajeCodigo = R.string.msg_enter_admin_code
            )

            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                verificandoCodigo = true,
                mensajeCodigo = null
            )

            val resultado = repository.verificarCodigoAdmin(codigo)

            // Si el usuario canceló mientras se verificaba, no se cambia nada
            if (!_state.value.mostrarDialogoCodigo) {
                return@launch
            }

            if (resultado.isSuccess) {

                _state.value = _state.value.copy(
                    rol = "ADMIN",
                    codigoAdmin = codigo,
                    mostrarDialogoCodigo = false,
                    codigoIngresado = "",
                    verificandoCodigo = false,
                    mensajeCodigo = null
                )

            } else {

                _state.value = _state.value.copy(
                    verificandoCodigo = false,
                    mensajeCodigo = resultado.exceptionOrNull().textoDeError(R.string.error_verify_code)
                )
            }
        }
    }

    // Deja el formulario vacío para el siguiente registro
    fun limpiarEstado() {
        _state.value = RegisterState()
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
                mensaje = R.string.msg_complete_fields,
                registroExito = false
            )

            return
        }

        if (datos.password != datos.confirmarPassword) {

            _state.value = datos.copy(
                mensaje = R.string.msg_passwords_mismatch,
                registroExito = false
            )

            return
        }

        viewModelScope.launch {

            _state.value = datos.copy(
                mensaje = null,
                cargando = true,
                registroExito = false
            )

            // El backend vuelve a comprobar el código al registrar
            val resultado = repository.registrar(
                nombre = datos.nombre,
                email = datos.email,
                password = datos.password,
                telefono = datos.telefono,
                rol = datos.rol,
                codigoAdmin = if (datos.rol == "ADMIN") {
                    datos.codigoAdmin
                } else {
                    null
                }
            )

            if (resultado.isSuccess) {

                _state.value = datos.copy(
                    mensaje = R.string.msg_user_registered,
                    registroExito = true
                )

            } else {

                _state.value = datos.copy(
                    mensaje = resultado.exceptionOrNull().textoDeError(R.string.error_register),
                    registroExito = false
                )
            }
        }
    }
}
