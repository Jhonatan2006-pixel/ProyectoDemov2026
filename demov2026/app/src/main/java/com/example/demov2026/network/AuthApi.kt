package com.example.demov2026.network

import com.example.demov2026.model.CodigoAdminRequest
import com.example.demov2026.model.CodigoAdminResponse
import com.example.demov2026.model.FotoRequest
import com.example.demov2026.model.FotoResponse
import com.example.demov2026.model.LoginFacialRequest
import com.example.demov2026.model.LoginRequest
import com.example.demov2026.model.LoginResponse
import com.example.demov2026.model.ProfileResponse
import com.example.demov2026.model.RegisterRequest
import com.example.demov2026.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import com.example.demov2026.model.CensoRequest
import com.example.demov2026.model.CensoResponse
import com.example.demov2026.model.CensosResponse

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("api/auth/verificar-codigo-admin")
    suspend fun verificarCodigoAdmin(
        @Body request: CodigoAdminRequest
    ): CodigoAdminResponse

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("api/auth/login-facial")
    suspend fun loginFacial(
        @Body request: LoginFacialRequest
    ): LoginResponse

    @GET("api/auth/profile")
    suspend fun profile(
        @Header("Authorization") token: String
    ): ProfileResponse

    @POST("api/auth/profile/foto")
    suspend fun guardarFoto(
        @Header("Authorization") token: String,
        @Body request: FotoRequest
    ): FotoResponse

    @POST("api/censos")
    suspend fun crearCenso(
        @Header("Authorization") token: String,
        @Body request: CensoRequest
    ): CensoResponse

    @GET("api/censos")
    suspend fun obtenerCensos(
        @Header("Authorization") token: String
    ): CensosResponse

    @GET("api/censos/masivos")
    suspend fun obtenerCensosMasivos(
        @Header("Authorization") token: String
    ): CensosResponse
}