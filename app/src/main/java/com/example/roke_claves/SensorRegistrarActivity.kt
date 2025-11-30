package com.example.roke_claves

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class SensorRegistrarActivity : AppCompatActivity() {

    private lateinit var codigo: EditText
    private lateinit var estado: Spinner
    private lateinit var tipo: Spinner
    private lateinit var fechaAlta: EditText
    private lateinit var departamentoSpinner: Spinner
    private lateinit var btnGuardar: Button

    private lateinit var session: SessionManager
    private val departamentos = mutableListOf<DepartamentoDisplay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_registrar)

        session = SessionManager(this)

        codigo = findViewById(R.id.sensorCodigo)
        estado = findViewById(R.id.sensorEstado)
        tipo = findViewById(R.id.sensorTipo)
        fechaAlta = findViewById(R.id.sensorFechaAlta)
        departamentoSpinner = findViewById(R.id.sensorDepartamento)
        btnGuardar = findViewById(R.id.sensorGuardarBtn)

        estado.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("activo", "inactivo", "bloqueado")
        )

        tipo.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("tarjeta", "llavero")
        )

        fechaAlta.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))

        cargarDepartamentos()

        btnGuardar.setOnClickListener { registrarSensor() }
    }

    private fun cargarDepartamentos() {
        val url = "http://100.103.19.56/api/departamentos/"

        val req = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { arr ->
                departamentos.clear()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val id = o.getInt("id")
                    val piso = o.getString("piso")
                    val torre = o.getString("torre")
                    val numero = o.getString("numero")

                    departamentos.add(
                        DepartamentoDisplay(
                            id,
                            "Torre $torre • Piso $piso • Nº $numero"
                        )
                    )
                }

                departamentoSpinner.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    departamentos
                )
            },
            { Toast.makeText(this, "Error cargando departamentos", Toast.LENGTH_LONG).show() }
        ) {
            override fun getHeaders(): MutableMap<String, String> =
                mutableMapOf("Authorization" to "Token ${session.getToken()}")
        }

        Volley.newRequestQueue(this).add(req)
    }

    private fun registrarSensor() {

        val selected = departamentoSpinner.selectedItem as? DepartamentoDisplay
        if (selected == null) {
            Toast.makeText(this, "Seleccione un departamento", Toast.LENGTH_LONG).show()
            return
        }

        val url = "http://100.103.19.56/api/sensores/"

        val body = JSONObject().apply {
            put("codigo_sensor", codigo.text.toString())
            put("estado", estado.selectedItem.toString())
            put("tipo", tipo.selectedItem.toString())
            put("fecha_alta", fechaAlta.text.toString())
            put("departamento", selected.id)
        }

        val req = object : JsonObjectRequest(
            Request.Method.POST, url, body,
            {
                Toast.makeText(this, "Sensor registrado", Toast.LENGTH_LONG).show()
                finish()
            },
            { err -> Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_LONG).show() }
        ) {
            override fun getHeaders(): MutableMap<String, String> =
                mutableMapOf(
                    "Authorization" to "Token ${session.getToken()}",
                    "Content-Type" to "application/json"
                )
        }

        Volley.newRequestQueue(this).add(req)
    }
}
