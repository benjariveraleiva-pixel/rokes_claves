package com.example.roke_claves

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SensorRegistrarActivity : AppCompatActivity() {

    private lateinit var codigo: EditText
    private lateinit var estado: Spinner
    private lateinit var tipo: Spinner
    private lateinit var fechaAlta: EditText
    private lateinit var fechaBaja: EditText
    private lateinit var departamentoId: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_registrar)

        codigo = findViewById(R.id.sensorCodigo)
        estado = findViewById(R.id.sensorEstado)
        tipo = findViewById(R.id.sensorTipo)
        fechaAlta = findViewById(R.id.sensorFechaAlta)
        fechaBaja = findViewById(R.id.sensorFechaBaja)
        departamentoId = findViewById(R.id.sensorDepartamento)
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

        btnGuardar.setOnClickListener { registrarSensor() }
    }

    private fun registrarSensor() {
        val url = "http://100.103.19.56/api/sensores/"

        val body = JSONObject().apply {
            put("codigo_sensor", codigo.text.toString())
            put("estado", estado.selectedItem.toString())
            put("tipo", tipo.selectedItem.toString())
            put("fecha_alta", fechaAlta.text.toString())
            put("fecha_baja", fechaBaja.text.toString())
            put("departamento", departamentoId.text.toString().toInt())
        }

        val req = JsonObjectRequest(
            Request.Method.POST,
            url,
            body,
            {
                Toast.makeText(this, "Sensor registrado", Toast.LENGTH_LONG).show()
            },
            { err ->
                Toast.makeText(this, "Error: ${err.networkResponse?.statusCode}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(req)
    }
}
