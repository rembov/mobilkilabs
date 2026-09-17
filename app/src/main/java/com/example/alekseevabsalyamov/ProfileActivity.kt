package com.example.alekseevabsalyamov

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        applySystemBarInsets(findViewById(R.id.main))

        val prefs = getSharedPreferences("registration", Context.MODE_PRIVATE)
        findViewById<TextView>(R.id.nameValue).text =
            prefs.getString("name", getString(R.string.no_data))
        findViewById<TextView>(R.id.surnameValue).text =
            prefs.getString("surname", getString(R.string.no_data))
        findViewById<TextView>(R.id.loginValue).text =
            prefs.getString("login", getString(R.string.no_data))
        findViewById<TextView>(R.id.emailValue).text =
            prefs.getString("email", getString(R.string.no_data))
    }
}