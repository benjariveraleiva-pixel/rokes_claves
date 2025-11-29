package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class SensorListarActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val listaSensores = mutableListOf<String>()
    private val listaIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_listar)

        listView = findViewById(R.id.listViewSensores)
        val btnRegistrar: Button = findViewById(R.id.sensorRegistrarBoton)

        cargarSensores()

        listView.setOnItemClickListener { _, _, position, _ ->
            val id = listaIds[position]
            val intent = Intent(this, SensorEditarActivity::class.java)
            intent.putExtra("sensor_id", id)
            startActivity(intent)
        }

        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, SensorRegistrarActivity::class.java))
        }
    }

    private fun cargarSensores() {
        val url = "http://100.103.19.56/api/sensores/"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    procesarLista(JSONArray(response))
                } catch (e: JSONException) {
                    Toast.makeText(this, "JSON error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun procesarLista(json: JSONArray) {
        listaSensores.clear()
        listaIds.clear()

        for (i in 0 until json.length()) {
            val obj = json.getJSONObject(i)
            val id = obj.getInt("id")
            val codigo = obj.getString("codigo_sensor")
            val estado = obj.getString("estado")
            val tipo = obj.getString("tipo")

            listaSensores.add("Código: $codigo | Estado: $estado | Tipo: $tipo")
            listaIds.add(id)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaSensores)
        listView.adapter = adapter
    }
}
