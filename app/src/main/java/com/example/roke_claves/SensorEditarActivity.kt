package com.example.roke_claves

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.Calendar


class SensorEditarActivity : AppCompatActivity() {

    private lateinit var codigo: EditText
    private lateinit var estado: Spinner
    private lateinit var tipo: Spinner
    private lateinit var fechaAlta: EditText
    private lateinit var fechaBaja: EditText
    private lateinit var departamentoSpinner: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    private lateinit var session: SessionManager

    private var sensorId: Int = -1

    private val departamentos = mutableListOf<DepartamentoDisplay>()
    private var departamentoCargadoId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_editar)

        session = SessionManager(this)

        codigo = findViewById(R.id.sensorCodigoEdit)
        estado = findViewById(R.id.sensorEstadoEdit)
        tipo = findViewById(R.id.sensorTipoEdit)
        fechaAlta = findViewById(R.id.sensorFechaAltaEdit)
        fechaBaja = findViewById(R.id.sensorFechaBajaEdit)
        departamentoSpinner = findViewById(R.id.sensorDepartamentoEdit)

        btnGuardar = findViewById(R.id.sensorGuardarCambiosBtn)
        btnEliminar = findViewById(R.id.sensorEliminarBtn)

        sensorId = intent.getIntExtra("sensor_id", -1)

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

        setupDatePicker(fechaAlta)
        setupDatePicker(fechaBaja)

        cargarDepartamentos()
        cargarDatos()   // loads data, final selection will apply after spinner loads

        btnGuardar.setOnClickListener { guardarCambios() }
        btnEliminar.setOnClickListener { eliminarSensor() }
    }

    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val c = Calendar.getInstance()
            val dp = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val mm = (month + 1).toString().padStart(2, '0')
                    val dd = day.toString().padStart(2, '0')
                    editText.setText("$year-$mm-$dd")
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            dp.show()
        }
    }

    private fun cargarDepartamentos() {
        val url = "http://100.103.19.56/api/departamentos/"

        val req = object : JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
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

                // Now that the spinner has data, apply the previously loaded departamento
                departamentoCargadoId?.let { loaded ->
                    val idx = departamentos.indexOfFirst { it.id == loaded }
                    if (idx >= 0) departamentoSpinner.setSelection(idx)
                }
            },
            {
                Toast.makeText(this, "Error cargando departamentos", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> =
                mutableMapOf("Authorization" to "Token ${session.getToken()}")
        }

        Volley.newRequestQueue(this).add(req)
    }

    private fun cargarDatos() {
        val url = "http://54.159.204.1/api/sensores/$sensorId/"

        val req = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { obj ->
                codigo.setText(obj.getString("codigo_sensor"))
                fechaAlta.setText(obj.getString("fecha_alta"))
                fechaBaja.setText(obj.optString("fecha_baja", ""))

                val estadoVal = obj.getString("estado")
                estado.setSelection(listOf("activo", "inactivo", "bloqueado").indexOf(estadoVal))

                val tipoVal = obj.getString("tipo")
                tipo.setSelection(listOf("tarjeta", "llavero").indexOf(tipoVal))

                val deptoId = obj.getInt("departamento")

                // store it until spinner finishes loading
                departamentoCargadoId = deptoId

                // if spinner is already loaded, apply immediately
                if (departamentos.isNotEmpty()) {
                    val idx = departamentos.indexOfFirst { it.id == deptoId }
                    if (idx >= 0) departamentoSpinner.setSelection(idx)
                }
            },
            {
                Toast.makeText(this, "Error cargando datos", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> =
                mutableMapOf("Authorization" to "Token ${session.getToken()}")
        }

        Volley.newRequestQueue(this).add(req)
    }

    private fun guardarCambios() {
        val selected = departamentoSpinner.selectedItem as? DepartamentoDisplay
        if (selected == null) {
            Toast.makeText(this, "Departamento inválido (╯°□°）╯︵ ┻━┻", Toast.LENGTH_LONG).show()
            return
        }

        val url = "http://100.103.19.56/api/sensores/$sensorId/"

        val body = JSONObject().apply {
            put("codigo_sensor", codigo.text.toString())
            put("estado", estado.selectedItem.toString())
            put("tipo", tipo.selectedItem.toString())
            put("fecha_alta", fechaAlta.text.toString())
            put("fecha_baja", fechaBaja.text.toString())
            put("departamento", selected.id)   // FIXED: correct type (Int)
        }

        val req = object : JsonObjectRequest(
            Request.Method.PUT,
            url,
            body,
            {
                Toast.makeText(this, "Cambios guardados ( •_•)>⌐■-■", Toast.LENGTH_LONG).show()
                finish()
            },
            {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> =
                mutableMapOf(
                    "Authorization" to "Token ${session.getToken()}",
                    "Content-Type" to "application/json"
                )
        }

        Volley.newRequestQueue(this).add(req)
    }

    private fun eliminarSensor() {
        val url = "http://100.103.19.56/api/sensores/$sensorId/"

        val req = object : JsonObjectRequest(
            Request.Method.DELETE,
            url,
            null,
            {
                Toast.makeText(this, "Sensor eliminado", Toast.LENGTH_LONG).show()
                finish()
            },
            {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> =
                mutableMapOf("Authorization" to "Token ${session.getToken()}")
        }

        Volley.newRequestQueue(this).add(req)
    }
}
