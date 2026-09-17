package com.example.alekseevabsalyamov

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private val activityTag = "LoginActivity"
    private lateinit var loginField: EditText
    private lateinit var passwordField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        applySystemBarInsets(findViewById(R.id.main))
        Log.i(activityTag, "onCreate")

        loginField = findViewById(R.id.loginField)
        passwordField = findViewById(R.id.passwordField)

        if (savedInstanceState != null) {
            loginField.setText(savedInstanceState.getString("login"))
            passwordField.setText(savedInstanceState.getString("password"))
        }

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val login = loginField.text.toString().trim()
            val password = passwordField.text.toString()
            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_empty_fields), Toast.LENGTH_SHORT).show()
            } else {
                val openIntent = Intent(this, MenuActivity::class.java)
                openIntent.putExtra("login", login)
                startActivity(openIntent)
                Toast.makeText(this, getString(R.string.toast_login_success), Toast.LENGTH_LONG).show()
            }
        }

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val openIntent = Intent(this, RegisterActivity::class.java)
            startActivity(openIntent)
        }

        findViewById<Button>(R.id.exitButton).setOnClickListener {
            finishAffinity()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(activityTag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(activityTag, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(activityTag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(activityTag, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(activityTag, "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(activityTag, "onRestart")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("login", loginField.text.toString())
        outState.putString("password", passwordField.text.toString())
    }
}