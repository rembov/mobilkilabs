package com.example.composelab6.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuthLogger(context: Context) {

    private val logFile = File(context.filesDir, "auth_log.txt")

    fun log(message: String) {
        val time = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        logFile.appendText("$time — $message\n")
    }

    fun read(): String =
        if (logFile.exists() && logFile.length() > 0) logFile.readText() else "(журнал пуст)"
}
