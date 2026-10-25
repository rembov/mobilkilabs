package com.example.composelab7.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AuthLogDao {

    @Query("SELECT * FROM auth_logs ORDER BY id DESC")
    fun getAll(): List<LogEntry>

    @Insert
    fun insert(entry: LogEntry)

    @Query("DELETE FROM auth_logs")
    fun clear()
}
