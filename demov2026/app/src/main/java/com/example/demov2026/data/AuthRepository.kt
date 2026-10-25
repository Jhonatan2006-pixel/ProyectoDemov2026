package com.example.demov2026.data

import androidx.annotation.StringRes
import com.example.demov2026.R
import com.example.demov2026.data.local.UsuarioDao
import com.example.demov2026.data.local.UsuarioEntity
import com.example.demov2026.model.CodigoAdminRequest
import com.example.demov2026.model.FotoRequest
import com.example.demov2026.model.LoginFacialRequest
import com.example.demov2026.model.LoginRequest
import com.example.demov2026.model.LoginResponse
import com.example.demov2026.model.RegisterRequest
import com.example.demov2026.model.UsuarioResponse
import com.example.demov2026.network.RetrofitClient

class AuthRepository(
    private val usuarioDao: UsuarioDao? = null
) {

    private val authApi = RetrofitClient.authApi

    suspend fun login(
        email: String,
        password: String
    ): Result<Pair<UsuarioResponse, String>> {

        return try {

            val response = authApi.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )

            procesarLogin(response, R.string.error_login)

        } catch (e: Exception) {

            Result.failure(
                errorApp(e, R.string.error_login)
            )
        }
    }

    // Login con rostro: el backend compara la selfie con la foto de perfil
    // (Lambda compareFaces) y responde igual que el login normal
    suspend fun loginFacial(
        email: String,
        fotoBase64: String
    ): Result<Pair<UsuarioResponse, String>> {

        return try {

            val response = authApi.loginFacial(
                LoginFacialRequest(
                    email = email,
                    foto = fotoBase64
                )
            )

            procesarLogin(response, R.string.error_face_login)

        } catch (e: Exception) {

            Result.failure(
                errorApp(e, R.string.error_face_login)
            )
        }
    }

    private suspend fun procesarLogin(
        response: LoginResponse,
        @StringRes porDefecto: Int
    ): Result<Pair<UsuarioResponse, String>> {

        if (
            response.success &&
            response.data != null &&
            response.token != null
        ) {

            val usuario = response.data

            // Guardar el usuario localmente con Room
            usuarioDao?.guardarUsuario(
                UsuarioEntity(
                    id = usuario.id,
                    nombre = usuario.nombre,
                    email = usuario.email,
                    telefono = usuario.telefono,
                    rol = usuario.rol
                )
            )

            return Result.success(
                Pair(
                    usuario,
                    response.token
                )
            )
        }

        return Result.failure(
            ErrorApp(mensajeDelServidor(response.message, porDefecto))
        )
    }

    suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        rol: String = "CENSISTA",
        codigoAdmin: String? = null
    ): Result<UsuarioResponse> {

        return try {

            val response = authApi.register(
                RegisterRequest(
                    nombre = nombre,
                    email = email,
                    telefono = telefono,
                    password = password,
                    rol = rol,
                    codigo_admin = codigoAdmin
                )
            )

            if (response.success && response.data != null) {

                Result.success(response.data)

            } else {

                Result.failure(
                    ErrorApp(mensajeDelServidor(response.message, R.string.error_register))
                )
            }

        } catch (e: Exception) {

            Result.failure(
                errorApp(e, R.string.error_register)
            )
        }
    }

    // Comprobar en el backend el código para registrarse como administrador
    suspend fun verificarCodigoAdmin(
        codigo: String
    ): Result<String> {

        return try {

            val response = authApi.verificarCodigoAdmin(
                CodigoAdminRequest(
                    codigo = codigo
                )
            )

            if (response.success) {

                Result.success(response.message)

            } else {

                Result.failure(
                    ErrorApp(mensajeDelServidor(response.message, R.string.error_verify_code))
                )
            }

        } catch (e: Exception) {

            Result.failure(
                errorApp(e, R.string.error_verify_code)
            )
        }
    }

    suspend fun obtenerPerfil(
        token: String
    ): Result<UsuarioResponse> {

        return try {

            val response = authApi.profile(
                "Bearer $token"
            )

            if (response.success && response.data != null) {

                Result.success(response.data)

            } else {

                Result.failure(
                    ErrorApp(mensajeDelServidor(response.message, R.string.error_profile))
                )
            }

        } catch (e: Exception) {

            Result.failure(
                errorApp(e, R.string.error_profile)
            )
        }
    }

    // Enviar la foto de perfil al backend
    suspend fun guardarFoto(
        token: String,
        fotoBase64: String
    ): Result<String> {

        return try {

            val response = authApi.guardarFoto(
                token = "Bearer $token",
                request = FotoRequest(
                    foto = fotoBase64
                )
            )

            if (response.success) {

                Result.success(response.message)

            } else {

                Result.failure(
                    ErrorApp(mensajeDelServidor(response.message, R.string.error_save_photo))
                )
            }

        } catch (e: Exception) {

            Result.failure(
                errorApp(e, R.string.error_save_photo)
            )
        }
    }

    suspend fun obtenerUsuarioLocal(): UsuarioEntity? {
        return usuarioDao?.obtenerUsuario()
    }

    suspend fun cerrarSesion() {
        usuarioDao?.eliminarUsuario()
    }
}