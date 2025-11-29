package com.example.roke_claves

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveSession(token: String, tipoUsuario: Int) {
        prefs.edit {
            putString("token", token)
                .putInt("tipo_usuario", tipoUsuario)
        }
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun getTipoUsuario(): Int = prefs.getInt("tipo_usuario", -1)

    fun clear() {
        prefs.edit { clear() }
    }
}