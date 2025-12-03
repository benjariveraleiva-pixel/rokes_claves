package com.example.roke_claves

import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class EventoListarActivity : AppCompatActivity() {

    private lateinit var listaEventos: ListView
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evento_listar)

        session = SessionManager(this)
        listaEventos = findViewById(R.id.listViewEventos)

        cargarEventos()
    }

    private fun cargarEventos() {
        val url = "http://54.159.204.1/api/eventos/"

        val request = object : JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->

                val lista = ArrayList<Map<String, String>>()

                for (i in 0 until response.length()) {
                    val ev = response.getJSONObject(i)

                    val map = mapOf(
                        "line1" to "Tipo: ${ev.getString("tipo_evento")} | Resultado: ${ev.getString("resultado")}",
                        "line2" to "Fecha: ${ev.getString("fecha_hora")}"
                    )

                    lista.add(map)
                }

                val adapter = SimpleAdapter(
                    this,
                    lista,
                    android.R.layout.simple_list_item_2,
                    arrayOf("line1", "line2"),
                    intArrayOf(android.R.id.text1, android.R.id.text2)
                )

                listaEventos.adapter = adapter
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error al obtener eventos: ${error.networkResponse?.statusCode ?: "sin respuesta"}",
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
}
