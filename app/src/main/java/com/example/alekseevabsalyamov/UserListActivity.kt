package com.example.alekseevabsalyamov

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.alekseevabsalyamov.R

class UserListActivity : AppCompatActivity() {

    private val activityTag = "UserListActivity"
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        val textList = findViewById<ListView>(R.id.textList)
        adapter = ArrayAdapter(this, R.layout.item, R.id.itemContent, Users.list)
        textList.adapter = adapter

        findViewById<Button>(R.id.button1).setOnClickListener {
            Users.added++
            Users.list.add("Пользователь " + Users.added)
            adapter.notifyDataSetChanged()
            Log.i(activityTag, getString(R.string.log_added) + ": Пользователь " + Users.added)
        }
    }
}