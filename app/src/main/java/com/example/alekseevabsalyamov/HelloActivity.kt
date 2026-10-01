package com.example.alekseevabsalyamov

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.alekseevabsalyamov.R

class HelloActivity : AppCompatActivity() {

    private val activityTag = "HelloActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helloact)

        val button1 = findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            button1.text = getString(R.string.btn_pressed)
            Log.i(activityTag, getString(R.string.log_press) + ": кнопка «" + getString(R.string.btn_press) + "», текст изменён на «" + getString(R.string.btn_pressed) + "»")
        }
    }
}