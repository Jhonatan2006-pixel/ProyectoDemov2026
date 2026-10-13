package com.example.demov2026.data

import com.example.demov2026.model.CensoData
import com.example.demov2026.model.CensoRequest
import com.example.demov2026.model.CensoResponse
import com.example.demov2026.network.RetrofitClient

class CensoRepository {

    suspend fun guardarCenso(
        token: String,
        request: CensoRequest
    ): Result<CensoResponse> {

        return try {

            val response = RetrofitClient.authApi.crearCenso(
                token = "Bearer $token",
                request = request
            )

            if (response.success) {

                Result.success(response)

            } else {

                Result.failure(
                    Exception(response.message)
                )
            }

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    mensajeDeError(e, "Error al registrar el censo")
                )
            )
        }
    }

    suspend fun obtenerCensos(
        token: String
    ): Result<List<CensoData>> {

        return try {

            val response = RetrofitClient.authApi.obtenerCensos(
                token = "Bearer $token"
            )

            if (response.success) {

                Result.success(response.data)

            } else {

                Result.failure(
                    Exception(response.message)
                )
            }

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    mensajeDeError(e, "Error al obtener los censos")
                )
            )
        }
    }

    suspend fun obtenerCensosMasivos(
        token: String
    ): Result<List<CensoData>> {

        return try {

            val response = RetrofitClient.authApi.obtenerCensosMasivos(
                token = "Bearer $token"
            )

            if (response.success) {

                Result.success(response.data)

            } else {

                Result.failure(
                    Exception(response.message)
                )
            }

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    mensajeDeError(e, "Error al obtener los censos masivos")
                )
            )
        }
    }
}