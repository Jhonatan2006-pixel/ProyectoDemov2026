package com.example.demov2026.data

import com.example.demov2026.data.local.UsuarioDao
import com.example.demov2026.data.local.UsuarioEntity
import com.example.demov2026.model.FotoRequest
import com.example.demov2026.model.LoginRequest
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

                Result.success(
                    Pair(
                        usuario,
                        response.token
                    )
                )

            } else {

                Result.failure(
                    Exception(response.message)
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        telefono: String
    ): Result<UsuarioResponse> {

        return try {

            val response = authApi.register(
                RegisterRequest(
                    nombre = nombre,
                    email = email,
                    telefono = telefono,
                    password = password
                )
            )

            if (response.success && response.data != null) {

                Result.success(response.data)

            } else {

                Result.failure(
                    Exception(response.message)
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
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
                    Exception(response.message)
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
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
                    Exception(response.message)
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun obtenerUsuarioLocal(): UsuarioEntity? {
        return usuarioDao?.obtenerUsuario()
    }

    suspend fun cerrarSesion() {
        usuarioDao?.eliminarUsuario()
    }
}