package com.example.alekseevabsalyamov

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        applySystemBarInsets(findViewById(R.id.main))

        val login = intent.getStringExtra("login")
        findViewById<TextView>(R.id.greetingText).text = getString(R.string.greeting_fmt, login ?: getString(R.string.no_data))

        findViewById<Button>(R.id.profileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.usersButton).setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }

        findViewById<Button>(R.id.exitMenuButton).setOnClickListener {
            finishAffinity()
        }
    }
}