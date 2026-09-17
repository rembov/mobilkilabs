package com.example.alekseevabsalyamov

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    private val activityTag = "MenuActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        applySystemBarInsets(findViewById(R.id.main))

        val login = intent.getStringExtra("login")
        findViewById<TextView>(R.id.greetingText).text = getString(R.string.greeting_fmt, login ?: getString(R.string.no_data))

        findViewById<Button>(R.id.startButton).setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_start), Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.profileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.reportButton).setOnClickListener {
            openReportOpportunity()
        }

        findViewById<Button>(R.id.exitMenuButton).setOnClickListener {
            finishAffinity()
        }
    }

    private fun openReportOpportunity() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
        }
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        } else {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+78000000000")
            }
            if (dialIntent.resolveActivity(packageManager) != null) {
                startActivity(dialIntent)
            } else {
                Log.e(activityTag, getString(R.string.toast_no_app))
                Toast.makeText(this, getString(R.string.toast_no_app), Toast.LENGTH_SHORT).show()
            }
        }
    }
}