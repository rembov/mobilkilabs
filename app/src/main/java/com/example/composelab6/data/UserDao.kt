package com.example.composelab6.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY id")
    fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE login = :login AND password = :password LIMIT 1")
    fun findByLoginPassword(login: String, password: String): User?

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    fun findByLogin(login: String): User?

    @Query("SELECT COUNT(*) FROM users")
    fun count(): Int

    @Insert
    fun insert(user: User): Long

    @Update
    fun update(user: User)
}
