package com.example.demov2026.ui.theme.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.demov2026.data.TokenManager
import com.example.demov2026.data.local.UsuarioDao

class ProfileViewModelFactory(
    private val tokenManager: TokenManager,
    private val usuarioDao: UsuarioDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(
                tokenManager,
                usuarioDao
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}