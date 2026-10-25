package com.example.composelab7.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "login")
    val login: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "fio")
    val fio: String,

    @ColumnInfo(name = "birth")
    val birth: String,

    @ColumnInfo(name = "gender")
    val gender: String,

    @ColumnInfo(name = "is_admin")
    val isAdmin: Boolean = false
)
