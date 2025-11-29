package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val inputUsername = findViewById<TextInputEditText>(R.id.inputUsername)
        val inputPassword = findViewById<TextInputEditText>(R.id.inputPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val errorText = findViewById<TextView>(R.id.errorText)

        val queue = Volley.newRequestQueue(this)
        val session = SessionManager(this)
        val url = "http://100.103.19.56/api/login/"

        btnLogin.setOnClickListener {

            val body = JSONObject().apply {
                put("username", inputUsername.text.toString())
                put("password", inputPassword.text.toString())
            }

            val request = object : JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                { response ->
                    val token = response.getString("token")
                    val tipoUsuario = response.getInt("tipo_usuario")

                    session.saveSession(token, tipoUsuario)

                    if (tipoUsuario == 0) {
                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                    } else {
                        startActivity(Intent(this, UserDashboardActivity::class.java))
                    }

                    finish()
                },
                { error ->
                    errorText.text = "Credenciales inválidas (╯°□°）╯︵ ┻━┻"
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            queue.add(request)
        }
    }
}
