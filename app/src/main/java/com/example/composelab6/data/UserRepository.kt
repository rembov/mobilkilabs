package com.example.composelab6.data

import android.content.Context

class UserRepository private constructor(
    private val userDao: UserDao,
    private val authLogger: AuthLogger
) {

    fun getAllUsers(): List<User> = userDao.getAll()

    fun register(
        login: String,
        password: String,
        fio: String,
        birth: String,
        gender: String
    ): User? {
        if (userDao.findByLogin(login) != null) return null
        val isFirst = userDao.count() == 0
        val user = User(
            login = login,
            password = password,
            fio = fio,
            birth = birth,
            gender = gender,
            isAdmin = isFirst
        )
        val id = userDao.insert(user)
        return user.copy(id = id.toInt())
    }

    fun authorize(login: String, password: String): User? {
        val user = userDao.findByLoginPassword(login.trim(), password)
        val role = if (user?.isAdmin == true) "администратор" else "пользователь"
        authLogger.log(
            if (user != null) "вход выполнен: $login ($role)" else "неудачный вход: $login"
        )
        return user
    }

    fun setAdmin(user: User, isAdmin: Boolean) {
        userDao.update(user.copy(isAdmin = isAdmin))
    }

    fun readLog(): String = authLogger.read()

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context): UserRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(
                    UserDatabase.getInstance(context).userDao(),
                    AuthLogger(context.applicationContext)
                ).also { INSTANCE = it }
            }
    }
}
