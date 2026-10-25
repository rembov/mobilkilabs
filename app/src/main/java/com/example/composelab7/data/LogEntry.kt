package com.example.composelab7.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_logs")
data class LogEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "login")
    val login: String,

    @ColumnInfo(name = "role")
    val role: String,

    @ColumnInfo(name = "success")
    val success: Boolean
)
