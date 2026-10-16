package com.example.composelab5.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY id")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE login = :login AND password = :password LIMIT 1")
    suspend fun findByLoginPassword(login: String, password: String): User?

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    suspend fun findByLogin(login: String): User?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Insert
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)
}
