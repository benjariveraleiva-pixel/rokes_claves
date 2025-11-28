package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class DepartamentoListarActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val listaDepartamentos = mutableListOf<String>()
    private val listaIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departamento_listar)

        listView = findViewById(R.id.listViewDepartamentos)

        cargarDepartamentos()

        listView.setOnItemClickListener { _, _, position, _ ->
            val id = listaIds[position]
            val intent = Intent(this, DepartamentoEditarActivity::class.java)
            intent.putExtra("id_depa", id)
            startActivity(intent)
        }
    }

    private fun cargarDepartamentos() {
        val url = "http://100.103.19.56/api/departamentos/"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    procesarLista(jsonArray)
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error parsing JSON: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error de red: ${error.toString()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

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
