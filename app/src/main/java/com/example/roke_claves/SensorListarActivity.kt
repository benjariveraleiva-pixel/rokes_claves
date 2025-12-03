package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class SensorListarActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val listaSensores = mutableListOf<String>()
    private val listaIds = mutableListOf<Int>()
    private lateinit var session: SessionManager

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var refreshRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_listar)

        session = SessionManager(this)
        listView = findViewById(R.id.listViewSensores)
        val btnRegistrar: Button = findViewById(R.id.sensorRegistrarBoton)

        listView.setOnItemClickListener { _, _, position, _ ->
            val id = listaIds[position]
            val intent = Intent(this, SensorEditarActivity::class.java)
            intent.putExtra("sensor_id", id)
            startActivity(intent)
        }

        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, SensorRegistrarActivity::class.java))
        }

        refreshRunnable = Runnable {
            cargarSensores()
            handler.postDelayed(refreshRunnable, 1000) // Refresh every 1 second
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun cargarSensores() {
        val url = "http://54.159.204.1/api/sensores/"

        val request = object: JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                procesarLista(response)
            },
            { error ->
                if (!isFinishing) {
                    println("Error cargando sensores: ${error.message}")
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val token = session.getToken() ?: ""
                return mutableMapOf("Authorization" to "Token $token")
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun procesarLista(json: JSONArray) {
        val currentSelection = if (listView.adapter != null && !listaIds.isEmpty()) listaIds else null
        val previouslySelectedId = if (listView.checkedItemPosition != ListView.INVALID_POSITION) currentSelection?.get(listView.checkedItemPosition) else null

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

        if (previouslySelectedId != null) {
            val newPosition = listaIds.indexOf(previouslySelectedId)
            if (newPosition != -1) {
                listView.setItemChecked(newPosition, true)
            }
        }
    }
}
