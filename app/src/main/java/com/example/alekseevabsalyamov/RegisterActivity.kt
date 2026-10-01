package com.example.alekseevabsalyamov

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alekseevabsalyamov.R

class RegisterActivity : AppCompatActivity() {

    private val activityTag = "RegisterActivity"
    private lateinit var loginField: EditText
    private lateinit var passwordField: EditText
    private lateinit var fioField: EditText
    private lateinit var birthField: EditText
    private lateinit var genderGroup: RadioGroup
    private lateinit var avatarView: ImageView

    private var avatarIndex = 0
    private val avatars = intArrayOf(
        R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        loginField = findViewById(R.id.loginField)
        passwordField = findViewById(R.id.passwordField)
        fioField = findViewById(R.id.fioField)
        birthField = findViewById(R.id.birthField)
        genderGroup = findViewById(R.id.genderGroup)
        avatarView = findViewById(R.id.avatarView)

        if (savedInstanceState != null) {
            loginField.setText(savedInstanceState.getString("login"))
            passwordField.setText(savedInstanceState.getString("password"))
            fioField.setText(savedInstanceState.getString("fio"))
            birthField.setText(savedInstanceState.getString("birth"))
            avatarIndex = savedInstanceState.getInt("avatarIndex", 0)
            val genderId = savedInstanceState.getInt("genderId", -1)
            if (genderId != -1) genderGroup.check(genderId)
        }
        avatarView.setImageResource(avatars[avatarIndex])

        avatarView.setOnClickListener {
            avatarIndex = (avatarIndex + 1) % avatars.size
            avatarView.setImageResource(avatars[avatarIndex])
            Log.i(activityTag, getString(R.string.log_avatar) + ": №" + (avatarIndex + 1) + " из " + avatars.size)
        }

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val login = loginField.text.toString().trim()
            val password = passwordField.text.toString()
            val fio = fioField.text.toString().trim()
            val birth = birthField.text.toString().trim()
            if (login.isEmpty() || password.isEmpty() || fio.isEmpty() || birth.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_empty_fields), Toast.LENGTH_SHORT).show()
            } else {
                val genderRadio =
                    findViewById<RadioButton>(genderGroup.checkedRadioButtonId)
                val genderText = genderRadio?.text?.toString() ?: "-"
                Users.list.add("$fio ($login)")
                Log.i(
                    activityTag,
                    getString(R.string.log_registered) + ": логин = " + login + ", ФИО = " + fio +
                            ", дата рождения = " + birth + ", пол = " + genderText +
                            ", аватар №" + (avatarIndex + 1)
                )
                Toast.makeText(this, getString(R.string.toast_reg_success), Toast.LENGTH_LONG).show()
                finish()
            }
        }

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            Log.i(activityTag, getString(R.string.log_cancel))
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("login", loginField.text.toString())
        outState.putString("password", passwordField.text.toString())
        outState.putString("fio", fioField.text.toString())
        outState.putString("birth", birthField.text.toString())
        outState.putInt("avatarIndex", avatarIndex)
        outState.putInt("genderId", genderGroup.checkedRadioButtonId)
    }
}