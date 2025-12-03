package com.example.roke_claves

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class EventoPendienteListarActivity : AppCompatActivity() {

    private lateinit var listaEventos: ListView
    private lateinit var emptyTextView: TextView
    private lateinit var session: SessionManager
    private val handler = Handler(Looper.getMainLooper())
    private var isUpdating = false
    private val updateInterval = 1000L
    private val eventosPendientes = mutableListOf<EventoPendiente>()
    private lateinit var adapter: EventoPendienteAdapter

    companion object {
        private const val TAG = "EventoPendienteActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            enableEdgeToEdge()
            setContentView(R.layout.activity_evento_pendiente_listar)

            Log.d(TAG, "Activity created")

            // Initialize SessionManager
            session = SessionManager(this)

            // Find views
            listaEventos = findViewById(R.id.listViewEventos)
            emptyTextView = findViewById(R.id.emptyTextView)

            // Initialize adapter
            adapter = EventoPendienteAdapter(eventosPendientes)
            listaEventos.adapter = adapter
            listaEventos.emptyView = emptyTextView

            setupListViewClickListener()

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error al inicializar: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            startAutoUpdate()
            cargarEventosPendientes() // Initial load
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume: ${e.message}", e)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            stopAutoUpdate()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause: ${e.message}", e)
        }
    }

    private fun startAutoUpdate() {
        if (!isUpdating) {
            isUpdating = true
            handler.post(updateRunnable)
        }
    }

    private fun stopAutoUpdate() {
        isUpdating = false
        handler.removeCallbacks(updateRunnable)
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            try {
                if (isUpdating) {
                    cargarEventosPendientes()
                    handler.postDelayed(this, updateInterval)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in updateRunnable: ${e.message}", e)
            }
        }
    }

    private fun setupListViewClickListener() {
        Log.d(TAG, "Setting up click listener")

        listaEventos.setOnItemClickListener { parent, view, position, id ->
            try {
                Log.d(TAG, "Item clicked at position: $position")
                Log.d(TAG, "List size: ${eventosPendientes.size}")

                if (position < eventosPendientes.size) {
                    val evento = eventosPendientes[position]
                    Log.d(TAG, "Evento clicked: ID=${evento.id}, Sensor=${evento.sensor}")
                    mostrarDialogoConfirmacion(evento)
                } else {
                    Log.e(TAG, "Position $position out of bounds")
                    Toast.makeText(this, "Error: Ítem no válido", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in click listener: ${e.message}", e)
            }
        }
    }

    private fun cargarEventosPendientes() {
        try {
            val url = "http://54.159.204.1/api/eventos/"
            val token = session.getToken() ?: ""

            Log.d(TAG, "Loading events from: $url")

            val request = object : JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    try {
                        Log.d(TAG, "Response received, length: ${response.length()}")

                        val newEvents = mutableListOf<EventoPendiente>()

                        for (i in 0 until response.length()) {
                            try {
                                val ev = response.getJSONObject(i)
                                val resultado = ev.getString("resultado")
                                val id = ev.getString("id")

                                Log.d(TAG, "Event $id - Resultado: $resultado")

                                if (resultado == "PENDIENTE") {
                                    val sensor = ev.optJSONObject("sensor")?.getString("codigo_sensor") ?: "Desconocido"
                                    val usuario = ev.optJSONObject("usuario")?.getString("user") ?: "Sin usuario"
                                    val fecha = ev.getString("fecha_hora")

                                    Log.d(TAG, "Found pending event: $id - $sensor")

                                    val evento = EventoPendiente(id, sensor, usuario, fecha)
                                    newEvents.add(evento)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing event $i: ${e.message}")
                            }
                        }

                        Log.d(TAG, "Total pending events found: ${newEvents.size}")

                        // Update the list
                        eventosPendientes.clear()
                        eventosPendientes.addAll(newEvents)
                        adapter.notifyDataSetChanged()

                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing response: ${e.message}", e)
                    }
                },
                { error ->
                    Log.e(TAG, "Error loading events: ${error.message}")
                    if (eventosPendientes.isEmpty()) {
                        emptyTextView.text = "Error al cargar eventos"
                    }
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf(
                        "Authorization" to "Token $token",
                        "Content-Type" to "application/json"
                    )
                }
            }

            Volley.newRequestQueue(this).add(request)

        } catch (e: Exception) {
            Log.e(TAG, "Error in cargarEventosPendientes: ${e.message}", e)
        }
    }

    private fun mostrarDialogoConfirmacion(evento: EventoPendiente) {
        try {
            Log.d(TAG, "Showing dialog for event: ${evento.id}")

            AlertDialog.Builder(this)
                .setTitle("Autorizar Acceso")
                .setMessage("¿Permitir acceso para el sensor ${evento.sensor}?\nUsuario: ${evento.usuario}\nID: ${evento.id}")
                .setPositiveButton("Sí") { dialog, which ->
                    try {
                        Log.d(TAG, "User clicked Sí for event ${evento.id}")
                        cambiarEstadoEvento(evento.id, "ABRIENDO")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in Sí button: ${e.message}", e)
                    }
                }
                .setNegativeButton("No") { dialog, which ->
                    try {
                        Log.d(TAG, "User clicked No for event ${evento.id}")
                        Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in No button: ${e.message}", e)
                    }
                }
                .setNeutralButton("Cancelar") { dialog, which ->
                    Log.d(TAG, "User clicked Cancelar")
                }
                .setOnCancelListener {
                    Log.d(TAG, "Dialog cancelled")
                }
                .show()

        } catch (e: Exception) {
            Log.e(TAG, "Error showing dialog: ${e.message}", e)
        }
    }

    private fun cambiarEstadoEvento(eventoId: String, nuevoEstado: String) {
        try {
            val url = "http://54.159.204.1/api/eventos/$eventoId/"
            val token = session.getToken() ?: ""

            val body = mapOf("resultado" to nuevoEstado)

            val request = object : JsonObjectRequest(
                Request.Method.PATCH,
                url,
                org.json.JSONObject(body),
                { response ->
                    try {
                        Toast.makeText(this, "Acceso autorizado", Toast.LENGTH_SHORT).show()
                        // Refresh immediately
                        cargarEventosPendientes()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing PATCH response: ${e.message}", e)
                    }
                },
                { error ->
                    try {
                        val errorMsg = if (error.networkResponse != null) {
                            String(error.networkResponse.data, Charsets.UTF_8)
                        } else {
                            error.message ?: "Error desconocido"
                        }
                        Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in error handler: ${e.message}", e)
                    }
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf(
                        "Authorization" to "Token $token",
                        "Content-Type" to "application/json"
                    )
                }
            }

            Volley.newRequestQueue(this).add(request)

        } catch (e: Exception) {
            Log.e(TAG, "Error in cambiarEstadoEvento: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    data class EventoPendiente(
        val id: String,
        val sensor: String,
        val usuario: String,
        val fecha: String
    )
}