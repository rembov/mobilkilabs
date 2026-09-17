package com.example.alekseevabsalyamov

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        applySystemBarInsets(findViewById(R.id.main))

        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val nightSwitch = findViewById<SwitchCompat>(R.id.nightSwitch)
        nightSwitch.isChecked = prefs.getBoolean("night_mode", false)

        nightSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("night_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}