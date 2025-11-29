package com.example.roke_claves

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SensorEditarActivity : AppCompatActivity() {

    private var sensorId: Int = -1

    private lateinit var codigo: EditText
    private lateinit var estado: Spinner
    private lateinit var tipo: Spinner
    private lateinit var fechaAlta: EditText
    private lateinit var fechaBaja: EditText
    private lateinit var departamento: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_editar)

        sensorId = intent.getIntExtra("sensor_id", -1)

        codigo = findViewById(R.id.sensorCodigoEdit)
        estado = findViewById(R.id.sensorEstadoEdit)
        tipo = findViewById(R.id.sensorTipoEdit)
        fechaAlta = findViewById(R.id.sensorFechaAltaEdit)
        fechaBaja = findViewById(R.id.sensorFechaBajaEdit)
        departamento = findViewById(R.id.sensorDepartamentoEdit)
        btnGuardar = findViewById(R.id.sensorGuardarCambiosBtn)

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

        cargarDatos()

        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarDatos() {
        val url = "http://100.103.19.56/api/sensores/$sensorId/"

        val req = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { res ->
                codigo.setText(res.getString("codigo_sensor"))
                fechaAlta.setText(res.getString("fecha_alta"))
                fechaBaja.setText(res.getString("fecha_baja"))
                departamento.setText(res.getInt("departamento").toString())

                estado.setSelection(listOf("activo","inactivo","bloqueado").indexOf(res.getString("estado")))
                tipo.setSelection(listOf("tarjeta","llavero").indexOf(res.getString("tipo")))
            },
            { err ->
                Toast.makeText(this, "Error al cargar: $err", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(req)
    }

    private fun guardarCambios() {
        val url = "http://100.103.19.56/api/sensores/$sensorId/"

        val body = JSONObject().apply {
            put("codigo_sensor", codigo.text.toString())
            put("estado", estado.selectedItem.toString())
            put("tipo", tipo.selectedItem.toString())
            put("fecha_alta", fechaAlta.text.toString())
            put("fecha_baja", fechaBaja.text.toString())
            put("departamento", departamento.text.toString().toInt())
        }

        val req = JsonObjectRequest(
            Request.Method.PUT,
            url,
            body,
            {
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_LONG).show()
            },
            { err ->
                Toast.makeText(this, "Error: $err", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(req)
    }
}
