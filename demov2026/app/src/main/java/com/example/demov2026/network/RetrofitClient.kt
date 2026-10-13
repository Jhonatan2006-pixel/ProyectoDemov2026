package com.example.demov2026.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Backend publicado en AWS (IP fija del servidor, puerto 3001).
    // Para usar el backend de esta computadora desde el emulador,
    // cambiar por "http://10.0.2.2:3000/"
    private const val BASE_URL = "http://77.113.138.104:3001/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}