package com.example.demov2026.data

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

// Convierte un error de red en el mensaje que se le muestra al usuario
fun mensajeDeError(
    error: Exception,
    porDefecto: String
): String {

    return when (error) {

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

            mensaje.ifBlank { porDefecto }
        }

        is IOException -> "No se pudo conectar con el servidor"

        else -> error.message ?: porDefecto
    }
}
