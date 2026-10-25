package com.example.demov2026.data

import androidx.annotation.StringRes
import com.example.demov2026.R
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

// Error de la app que ya trae el texto que se le muestra al usuario
// (un recurso de strings.xml, para que salga en el idioma del teléfono)
class ErrorApp(@StringRes val mensaje: Int) : Exception()

// El backend responde sus mensajes en español; aquí se traducen
// a los textos de la app para mostrarlos en el idioma del teléfono
private val mensajesDelServidor = mapOf(
    "Correo o contraseña incorrectos" to R.string.error_credentials,
    "Correo y contraseña son obligatorios" to R.string.error_email_password_required,
    "Nombre, correo y contraseña son obligatorios" to R.string.error_required_fields,
    "El correo ya está registrado" to R.string.error_email_registered,
    "Código de administrador incorrecto" to R.string.error_admin_code,
    "El rol seleccionado no es válido" to R.string.error_invalid_role,
    "Token no proporcionado" to R.string.error_session,
    "Formato de token inválido" to R.string.error_session,
    "Token inválido o expirado" to R.string.error_session,
    "Usuario no autenticado" to R.string.error_session,
    "Acceso permitido únicamente para censistas" to R.string.error_only_censistas,
    "Acceso permitido únicamente para administradores" to R.string.error_only_admins,
    "Todos los datos del censo son obligatorios" to R.string.error_census_data_required,
    "Las cantidades ingresadas no son válidas" to R.string.error_invalid_amounts,
    "No se recibió ninguna foto" to R.string.error_no_photo_received,
    "Usuario no encontrado" to R.string.error_user_not_found,
    "El rostro no coincide con la foto de perfil" to R.string.error_face_no_match,
    "El usuario no tiene foto de perfil" to R.string.error_face_no_profile_photo,
    "No se detectó un rostro en la foto" to R.string.error_face_not_detected,
    "El servicio de reconocimiento facial no está disponible" to R.string.error_face_service
)

// Texto de la app para un mensaje del backend; si no se conoce, el texto por defecto
@StringRes
fun mensajeDelServidor(
    mensaje: String?,
    @StringRes porDefecto: Int
): Int = mensajesDelServidor[mensaje?.trim()] ?: porDefecto

// Convierte un error de red en el texto que se le muestra al usuario
@StringRes
fun mensajeDeError(
    error: Exception,
    @StringRes porDefecto: Int
): Int {

    return when (error) {

        is ErrorApp -> error.mensaje

        // El backend respondió con un error (400, 401, 403, 409...):
        // se usa el mensaje que viene en la respuesta
        is HttpException -> {

            val mensaje = try {

                val cuerpo = error.response()?.errorBody()?.string() ?: ""

                val json = JSONObject(cuerpo)

                // Los controladores responden "message"
                // y el middleware del token responde "mensaje"
                json.optString("message").ifBlank {
                    json.optString("mensaje")
                }

            } catch (e: Exception) {
                ""
            }

            mensajesDelServidor[mensaje.trim()]
                ?: if (error.code() == 401) R.string.error_session else porDefecto
        }

        is IOException -> R.string.error_no_connection

        else -> porDefecto
    }
}

// Atajo para los repositorios: envuelve cualquier error en un ErrorApp
fun errorApp(
    error: Exception,
    @StringRes porDefecto: Int
): ErrorApp = ErrorApp(mensajeDeError(error, porDefecto))

// Atajo para los ViewModels: el texto del error de un Result fallido
@StringRes
fun Throwable?.textoDeError(@StringRes porDefecto: Int): Int =
    (this as? ErrorApp)?.mensaje ?: porDefecto
