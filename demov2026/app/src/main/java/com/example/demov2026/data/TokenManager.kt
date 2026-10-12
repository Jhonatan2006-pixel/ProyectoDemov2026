package com.example.demov2026.data

import android.content.Context
import androidx.core.content.edit

class TokenManager(context: Context) {

    private val preferences =
        context.getSharedPreferences("taskflow_session", Context.MODE_PRIVATE)

    fun guardarToken(token: String) {
        preferences.edit {
            putString("token", token)
        }
    }

    fun obtenerToken(): String? {
        return preferences.getString("token", null)
    }

    fun eliminarToken() {
        preferences.edit {
            remove("token")
        }
    }
}