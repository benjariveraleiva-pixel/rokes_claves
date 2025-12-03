package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnDepartamentos = findViewById<Button>(R.id.btnDepartamentos)
        val btnUsuarios = findViewById<Button>(R.id.btnUsuarios)
        val btnEventos = findViewById<Button>(R.id.btnEventos)
        val btnSensores = findViewById<Button>(R.id.btnSensores)
        val btnEventosPendientes = findViewById<Button>(R.id.btnEventosPendientes)


        btnDepartamentos.setOnClickListener {
            startActivity(Intent(this, DepartamentoListarActivity::class.java))
        }

        btnUsuarios.setOnClickListener {
            startActivity(Intent(this, UsuarioListarActivity::class.java))
        }

        btnEventos.setOnClickListener {
            startActivity(Intent(this, EventoListarActivity::class.java))
        }
        btnEventosPendientes.setOnClickListener {
            startActivity(Intent(this, EventoPendienteListarActivity::class.java))
        }

        btnSensores.setOnClickListener {
            startActivity(Intent(this, SensorListarActivity::class.java))
        }
    }
}
