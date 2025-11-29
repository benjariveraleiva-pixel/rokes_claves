package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

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

        when (session.getTipoUsuario()) {
            0 -> startActivity(Intent(this, AdminDashboardActivity::class.java))     // admin
            1 -> startActivity(Intent(this, UserDashboardActivity::class.java)) // normal user
            else -> startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}
