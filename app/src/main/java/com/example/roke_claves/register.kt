package com.example.roke_claves

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class register : AppCompatActivity() {

    private lateinit var nameUser: EditText
    private lateinit var emailUser: EditText
    private lateinit var passwordUser: EditText
    private lateinit var stateUser: EditText
    private lateinit var apartmentUser: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vincular elementos del layout
        nameUser = findViewById(R.id.name_user)
        emailUser = findViewById(R.id.email_user)
        passwordUser = findViewById(R.id.password_user)
        stateUser = findViewById(R.id.state_user)
        apartmentUser = findViewById(R.id.apartment_user)
        btnRegister = findViewById(R.id.btn_register1)

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = nameUser.text.toString().trim()
        val email = emailUser.text.toString().trim()
        val password = passwordUser.text.toString().trim()
        val state = stateUser.text.toString().trim()
        val apartment = apartmentUser.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || state.isEmpty() || apartment.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://100.103.19.56/api/users/register/"

        val jsonBody = JSONObject().apply {
            put("name", name)
            put("email", email)
            put("password", password)
            put("state", state)
            put("apartment", apartment)
        }

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            { response ->
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error al registrar: ${error.networkResponse?.statusCode ?: "sin respuesta"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}

