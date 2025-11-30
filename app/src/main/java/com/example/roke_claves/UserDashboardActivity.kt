package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserDashboardActivity : AppCompatActivity() {

    private lateinit var btnEventos: Button
    private lateinit var btnAcceso: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Enlazar botones con IDs reales
        btnEventos = findViewById(R.id.btn_leventos)
        btnAcceso = findViewById(R.id.btn_pacceso)

        // Ir a Lista de Eventos
        btnEventos.setOnClickListener {
            val intent = Intent(this, user_evento_acceso::class.java)
            startActivity(intent)
        }

        // Ir a Llave de Acceso
        btnAcceso.setOnClickListener {
            val intent = Intent(this, llave_acceso::class.java)
            startActivity(intent)
        }
    }
}
