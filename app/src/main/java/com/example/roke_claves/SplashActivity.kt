package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.security.Principal

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)
        val token = session.getToken()

        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            // Cargamos el Activity principal
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            // Cerramos el Activity Principal que en este caso es el MainActivity
            finish()
        }, 7000) // Tiempo de espera en milisegundos


        when (session.getTipoUsuario()) {
            0 -> startActivity(Intent(this, AdminDashboardActivity::class.java))     // admin
            1 -> startActivity(Intent(this, UserDashboardActivity::class.java)) // normal user
            else -> startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}
