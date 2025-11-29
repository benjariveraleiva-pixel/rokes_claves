package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class DepartamentoListarActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var btnRegistrar: Button
    private val listaDepartamentos = mutableListOf<String>()
    private val listaIds = mutableListOf<Int>()
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departamento_listar)

        session = SessionManager(this)
        listView = findViewById(R.id.listViewDepartamentos)
        btnRegistrar = findViewById(R.id.depaRegistrarBoton)

        cargarDepartamentos()

        listView.setOnItemClickListener { _, _, position, _ ->
            val id = listaIds[position]
            val intent = Intent(this, DepartamentoEditarActivity::class.java)
            intent.putExtra("id_depa", id)
            startActivity(intent)
        }

        btnRegistrar.setOnClickListener {
            startActivity(Intent(this, DepartamentoRegistrarActivity::class.java))
        }
    }

    private fun cargarDepartamentos() {
        val url = "http://100.103.19.56/api/departamentos/"

        val request = object: JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                procesarLista(response)
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error cargando departamentos: ${error.networkResponse?.statusCode}",
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

    private fun procesarLista(json: JSONArray) {
        listaDepartamentos.clear()
        listaIds.clear()

        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            val id = item.getInt("id")
            val numero = item.getString("numero")
            val torre = item.getString("torre")
            val piso = item.getString("piso")

            listaDepartamentos.add("Depto $numero - Torre $torre - Piso $piso")
            listaIds.add(id)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDepartamentos)
        listView.adapter = adapter
    }
}
