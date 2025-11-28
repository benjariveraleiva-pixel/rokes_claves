package com.example.roke_claves

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class registro_departamento : AppCompatActivity() {
    private lateinit var depaNumero: EditText
    private lateinit var depaTorre: EditText
    private lateinit var depaPiso: EditText
    private lateinit var depaBoton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_registro_departamento)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        depaNumero = findViewById(R.id.depaNumero)
        depaTorre = findViewById(R.id.depaTorre)
        depaPiso = findViewById(R.id.depaPiso)
        depaBoton = findViewById(R.id.depaBoton)

        depaBoton.setOnClickListener {
            registrarDepartamento()
        }
    }

    private fun registrarDepartamento() {
        val torre = depaTorre.text.toString().trim()
        val numero = depaNumero.text.toString().trim()
        val piso = depaPiso.text.toString().trim()

        val url = "http://100.103.19.56/api/departamentos/"

        val jsonBody = JSONObject().apply {
            put("numero", numero)
            put("torre", torre)
            put("piso", piso)
        }

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            { response ->
                Toast.makeText(this, "Departamento registrado correctamente", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error al registrar: ${error.networkResponse?.statusCode ?: "sin respuesta"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        // Ejecutar request con Volley
        Volley.newRequestQueue(this).add(request)
    }
}
