package com.example.demov2026.ui.theme.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.demov2026.data.TokenManager
import com.example.demov2026.data.local.UsuarioDao

class LoginViewModelFactory(
    private val tokenManager: TokenManager,
    private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                tokenManager,
                usuarioDao
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}