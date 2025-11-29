package com.example.roke_claves

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LogoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val session = SessionManager(this)
        session.clear()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
