package com.example.demov2026.data

import com.example.demov2026.R
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
                    ErrorApp(mensajeDelServidor(response.message, R.string.error_save_census))
                )
            }

        } catch (e: Exception) {

            Result.failure(
                errorApp(e, R.string.error_save_census)
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
                    ErrorApp(mensajeDelServidor(response.message, R.string.error_load_censuses))
                )
            }

        } catch (e: Exception) {

            Result.failure(
                errorApp(e, R.string.error_load_censuses)
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
                    ErrorApp(mensajeDelServidor(response.message, R.string.error_load_bulk))
                )
            }

        } catch (e: Exception) {

            Result.failure(
                errorApp(e, R.string.error_load_bulk)
            )
        }
    }
}