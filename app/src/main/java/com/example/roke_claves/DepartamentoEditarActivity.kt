package com.example.roke_claves

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DepartamentoEditarActivity : AppCompatActivity() {

    private lateinit var depaNumero: EditText
    private lateinit var depaTorre: EditText
    private lateinit var depaPiso: EditText
    private lateinit var btnGuardar: Button

    private var depaId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departamento_editar)

        depaNumero = findViewById(R.id.depaNumeroEdit)
        depaTorre = findViewById(R.id.depaTorreEdit)
        depaPiso = findViewById(R.id.depaPisoEdit)
        btnGuardar = findViewById(R.id.btnGuardarEdit)

        depaId = intent.getIntExtra("id_depa", -1)

        if (depaId == -1) {
            Toast.makeText(this, "Error: ID inválido (ಠ_ಠ)", Toast.LENGTH_LONG).show()
            finish()
        }

        cargarDatos()

        btnGuardar.setOnClickListener {
            actualizarDepartamento()
        }
    }

    private fun cargarDatos() {
        val url = "http://100.103.19.56/api/departamentos/$depaId/"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                depaNumero.setText(response.getString("numero"))
                depaTorre.setText(response.getString("torre"))
                depaPiso.setText(response.getString("piso"))
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error cargando departamento: ${error.networkResponse?.statusCode}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun actualizarDepartamento() {
        val url = "http://100.103.19.56/api/departamentos/$depaId/"

        val body = JSONObject().apply {
            put("numero", depaNumero.text.toString().trim())
            put("torre", depaTorre.text.toString().trim())
            put("piso", depaPiso.text.toString().trim())
        }

        val request = JsonObjectRequest(
            Request.Method.PUT,
            url,
            body,
            {
                Toast.makeText(this, "Departamento actualizado (ᐛ)", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error actualizando: ${error.networkResponse?.statusCode}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}
