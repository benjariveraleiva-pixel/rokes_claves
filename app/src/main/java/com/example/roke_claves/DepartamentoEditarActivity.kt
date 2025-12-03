package com.example.roke_claves

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DepartamentoEditarActivity : AppCompatActivity() {

    private lateinit var depaNumero: EditText
    private lateinit var depaTorre: EditText
    private lateinit var depaPiso: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private lateinit var session: SessionManager

    private var depaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departamento_editar)

        session = SessionManager(this)
        depaNumero = findViewById(R.id.depaNumeroEdit)
        depaTorre = findViewById(R.id.depaTorreEdit)
        depaPiso = findViewById(R.id.depaPisoEdit)
        btnGuardar = findViewById(R.id.btnGuardarEdit)
        btnEliminar = findViewById(R.id.btnEliminarDepa)

        depaId = intent.getIntExtra("id_depa", -1)

        if (depaId == -1) {
            Toast.makeText(this, "Error: ID inválido (ಠ_ಠ)", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        cargarDatos()

        btnGuardar.setOnClickListener {
            actualizarDepartamento()
        }

        btnEliminar.setOnClickListener {
            eliminarDepartamento()
        }
    }

    private fun cargarDatos() {
        val url = "http://100.103.19.56/api/departamentos/$depaId/"

        val request = object : JsonObjectRequest(
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
                    "Error cargando departamento: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val token = session.getToken() ?: ""
                return mutableMapOf("Authorization" to "Token $token")
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun actualizarDepartamento() {
        val url = "http://54.159.204.1/api/departamentos/$depaId/"

        val body = JSONObject().apply {
            put("numero", depaNumero.text.toString().trim())
            put("torre", depaTorre.text.toString().trim())
            put("piso", depaPiso.text.toString().trim())
        }

        val request = object : JsonObjectRequest(
            Request.Method.PUT,
            url,
            body,
            {
                Toast.makeText(this, "Departamento actualizado (ᐛ)", Toast.LENGTH_LONG).show()
                finish()
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error actualizando: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val token = session.getToken() ?: ""
                return mutableMapOf(
                    "Authorization" to "Token $token",
                    "Content-Type" to "application/json"
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun eliminarDepartamento() {
        val url = "http://100.103.19.56/api/departamentos/$depaId/"

        val request = object : StringRequest(
            Method.DELETE,
            url,
            {
                Toast.makeText(this, "Departamento eliminado correctamente", Toast.LENGTH_LONG).show()
                finish()
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error al eliminar: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val token = session.getToken() ?: ""
                return mutableMapOf("Authorization" to "Token $token")
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
