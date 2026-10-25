package com.example.demov2026.ui.theme.Profile

import com.example.demov2026.data.textoDeError
import com.example.demov2026.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demov2026.data.AuthRepository
import com.example.demov2026.data.TokenManager
import com.example.demov2026.data.local.UsuarioDao
import com.example.demov2026.model.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

// Tamaño máximo, en píxeles, del lado más largo de la foto de perfil
private const val LADO_MAXIMO_FOTO = 800

class ProfileViewModel(
    private val tokenManager: TokenManager,
    usuarioDao: UsuarioDao
) : ViewModel() {

    private val repository = AuthRepository(usuarioDao)

    private val _state = MutableStateFlow(ProfileState())

    val state: StateFlow<ProfileState> = _state

    fun cargarPerfil() {

        viewModelScope.launch {

            // 1. Recuperar primero el usuario guardado localmente en Room
            val usuarioLocal = repository.obtenerUsuarioLocal()

            if (usuarioLocal != null) {

                _state.value = ProfileState(
                    nombre = usuarioLocal.nombre,
                    email = usuarioLocal.email,
                    telefono = usuarioLocal.telefono ?: "",
                    mensaje = null,
                    fotoPerfil = _state.value.fotoPerfil
                )
            }

            // 2. Obtener token para consultar el backend
            val token = tokenManager.obtenerToken()

            if (token == null) {

                if (usuarioLocal == null) {
                    _state.value = _state.value.copy(
                        mensaje = R.string.msg_no_user
                    )
                }

                return@launch
            }

            // 3. Consultar perfil en el backend
            val resultado = repository.obtenerPerfil(token)

            if (resultado.isSuccess) {

                val usuario = resultado.getOrNull()

                if (usuario != null) {

                    var fotoBitmap: Bitmap? = _state.value.fotoPerfil

                    // 4. Si el backend tiene una foto, convertirla
                    // de Base64 nuevamente a Bitmap
                    if (!usuario.foto.isNullOrEmpty()) {

                        try {

                            val fotoBytes = Base64.decode(
                                usuario.foto,
                                Base64.DEFAULT
                            )

                            fotoBitmap = BitmapFactory.decodeByteArray(
                                fotoBytes,
                                0,
                                fotoBytes.size
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // 5. Actualizar los datos y la fotografía
                    _state.value = ProfileState(
                        nombre = usuario.nombre,
                        email = usuario.email,
                        telefono = usuario.telefono ?: "",
                        mensaje = null,
                        fotoPerfil = fotoBitmap
                    )
                }

            } else {

                // Si ya existen datos locales, los dejamos visibles.
                // Solo mostramos error si tampoco tenemos información local.
                if (usuarioLocal == null) {

                    _state.value = _state.value.copy(
                        mensaje = resultado.exceptionOrNull().textoDeError(R.string.error_profile)
                    )
                }
            }
        }
    }

    // Recibe fotografía de cámara o galería
    fun onFotoTomada(bitmap: Bitmap) {

        _state.value = _state.value.copy(
            fotoPerfil = reducirFoto(bitmap)
        )
    }

    // Las fotos de la galería pueden pesar varios MB: se reducen
    // para que el envío y la carga del perfil sean rápidos
    private fun reducirFoto(bitmap: Bitmap): Bitmap {

        val ladoMayor = maxOf(bitmap.width, bitmap.height)

        if (ladoMayor <= LADO_MAXIMO_FOTO) {
            return bitmap
        }

        val escala = LADO_MAXIMO_FOTO.toFloat() / ladoMayor

        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * escala).toInt(),
            (bitmap.height * escala).toInt(),
            true
        )
    }

    // Convierte Bitmap a ByteArray
    fun obtenerFotoEnByte(): ByteArray? {

        val bitmap = _state.value.fotoPerfil ?: return null

        val stream = ByteArrayOutputStream()

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            80,
            stream
        )

        return stream.toByteArray()
    }

    // Guarda la fotografía en el backend
    fun guardarFoto() {

        viewModelScope.launch {

            val token = tokenManager.obtenerToken()

            if (token == null) {

                _state.value = _state.value.copy(
                    mensaje = R.string.msg_no_user
                )

                return@launch
            }

            val fotoBytes = obtenerFotoEnByte()

            if (fotoBytes == null) {

                _state.value = _state.value.copy(
                    mensaje = R.string.msg_no_photo
                )

                return@launch
            }

            // Convertir los bytes a Base64
            val fotoBase64 = Base64.encodeToString(
                fotoBytes,
                Base64.NO_WRAP
            )

            _state.value = _state.value.copy(
                mensaje = R.string.msg_saving_photo
            )

            val resultado = repository.guardarFoto(
                token = token,
                fotoBase64 = fotoBase64
            )

            if (resultado.isSuccess) {

                _state.value = _state.value.copy(
                    mensaje = R.string.msg_photo_saved
                )

            } else {

                _state.value = _state.value.copy(
                    mensaje = resultado.exceptionOrNull().textoDeError(R.string.error_save_photo)
                )
            }
        }
    }

    // Convierte ByteArray a Bitmap
    fun cargarFotoDesdeBytes(bytes: ByteArray) {

        val bitmap = BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )

        _state.value = _state.value.copy(
            fotoPerfil = bitmap
        )
    }

    fun cerrarSesion() {

        viewModelScope.launch {

            tokenManager.eliminarToken()

            repository.cerrarSesion()

            _state.value = ProfileState(
                mensaje = R.string.msg_logged_out
            )
        }
    }
}