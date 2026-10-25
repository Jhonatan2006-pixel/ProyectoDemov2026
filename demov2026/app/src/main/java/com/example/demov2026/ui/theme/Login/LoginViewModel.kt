package com.example.demov2026.ui.theme.Login

import android.graphics.Bitmap
import android.util.Base64
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.R
import com.example.demov2026.data.AuthRepository
import com.example.demov2026.data.TokenManager
import com.example.demov2026.data.local.UsuarioDao
import com.example.demov2026.data.textoDeError
import com.example.demov2026.model.LoginState
import com.example.demov2026.model.UsuarioResponse
import java.io.ByteArrayOutputStream
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

    // Deja el formulario vacío para el siguiente inicio de sesión
    fun limpiarEstado() {
        _state.value = LoginState()
    }

    fun onLoginClic() {

        viewModelScope.launch {

            _state.value = _state.value.copy(
                mensaje = null,
                cargando = true
            )

            val resultado = repository.login(
                _state.value.email,
                _state.value.password
            )

            procesarResultado(resultado, R.string.error_login)
        }
    }

    // Login con rostro: la selfie de la cámara se envía al backend,
    // que la compara con la foto de perfil del correo escrito
    fun onLoginFacial(selfie: Bitmap) {

        val email = _state.value.email.trim()

        if (email.isEmpty()) {
            avisarFaltaCorreo()
            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                mensaje = null,
                cargando = true
            )

            val resultado = repository.loginFacial(
                email,
                aBase64(selfie)
            )

            procesarResultado(resultado, R.string.error_face_login)
        }
    }

    // Cuando falta el correo no se abre la cámara
    fun avisarFaltaCorreo() {
        _state.value = _state.value.copy(
            mensaje = R.string.face_login_needs_email
        )
    }

    private fun procesarResultado(
        resultado: Result<Pair<UsuarioResponse, String>>,
        @StringRes errorPorDefecto: Int
    ) {

        if (resultado.isSuccess) {

            val usuarioYToken = resultado.getOrNull()

            val token = usuarioYToken?.second

            if (token != null) {
                tokenManager.guardarToken(token)
            }

            _state.value = _state.value.copy(
                mensaje = R.string.msg_login_success,
                cargando = false,
                loginExito = true,
                rol = usuarioYToken?.first?.rol ?: ""
            )

        } else {

            _state.value = _state.value.copy(
                mensaje = resultado.exceptionOrNull().textoDeError(errorPorDefecto),
                cargando = false,
                loginExito = false
            )
        }
    }

    // Convierte la selfie a JPEG en Base64 para enviarla al backend
    private fun aBase64(bitmap: Bitmap): String {

        val stream = ByteArrayOutputStream()

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            90,
            stream
        )

        return Base64.encodeToString(
            stream.toByteArray(),
            Base64.NO_WRAP
        )
    }
}