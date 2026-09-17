package com.example.alekseevabsalyamov

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private val activityTag = "RegisterActivity"
    private lateinit var nameField: EditText
    private lateinit var surnameField: EditText
    private lateinit var loginField: EditText
    private lateinit var passwordField: EditText
    private lateinit var emailField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        applySystemBarInsets(findViewById(R.id.main))
        Log.i(activityTag, "onCreate")

        nameField = findViewById(R.id.nameField)
        surnameField = findViewById(R.id.surnameField)
        loginField = findViewById(R.id.loginField)
        passwordField = findViewById(R.id.passwordField)
        emailField = findViewById(R.id.emailField)

        if (savedInstanceState != null) {
            nameField.setText(savedInstanceState.getString("name"))
            surnameField.setText(savedInstanceState.getString("surname"))
            loginField.setText(savedInstanceState.getString("login"))
            passwordField.setText(savedInstanceState.getString("password"))
            emailField.setText(savedInstanceState.getString("email"))
        }

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val name = nameField.text.toString().trim()
            val surname = surnameField.text.toString().trim()
            val login = loginField.text.toString().trim()
            val password = passwordField.text.toString()
            val email = emailField.text.toString().trim()
            if (name.isEmpty() || surname.isEmpty() || login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_empty_fields), Toast.LENGTH_SHORT).show()
            } else {
                saveRegistration(name, surname, login, password, email)
                Toast.makeText(this, getString(R.string.toast_reg_success), Toast.LENGTH_LONG).show()
                finish()
            }
        }

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            finish()
        }
    }

    private fun saveRegistration(name: String, surname: String, login: String, password: String, email: String) {
        getSharedPreferences("registration", Context.MODE_PRIVATE)
            .edit()
            .putString("name", name)
            .putString("surname", surname)
            .putString("login", login)
            .putString("password", password)
            .putString("email", email)
            .apply()
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
        outState.putString("name", nameField.text.toString())
        outState.putString("surname", surnameField.text.toString())
        outState.putString("login", loginField.text.toString())
        outState.putString("password", passwordField.text.toString())
        outState.putString("email", emailField.text.toString())
    }
}