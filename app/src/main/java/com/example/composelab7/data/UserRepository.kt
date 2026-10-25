package com.example.composelab7.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRepository private constructor(
    private val userDao: UserDao,
    private val logDao: AuthLogDao
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

    // Р—Р°РїРёСЃСЊ РІ Р¶СѓСЂРЅР°Р» РІС‹РїРѕР»РЅСЏРµС‚СЃСЏ РІ С‚РѕРј Р¶Рµ С„РѕРЅРѕРІРѕРј РїРѕС‚РѕРєРµ, С‡С‚Рѕ Рё Р·Р°РїСЂРѕСЃ Рє Р‘Р”.
    fun authorize(login: String, password: String): User? {
        val user = userDao.findByLoginPassword(login.trim(), password)
        val role = when {
            user == null -> "вЂ”"
            user.isAdmin -> "Р°РґРјРёРЅРёСЃС‚СЂР°С‚РѕСЂ"
            else -> "РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ"
        }
        logDao.insert(
            LogEntry(
                time = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
                login = login.trim(),
                role = role,
                success = user != null
            )
        )
        return user
    }

    fun setAdmin(user: User, isAdmin: Boolean) {
        userDao.update(user.copy(isAdmin = isAdmin))
    }

    fun getAllLogs(): List<LogEntry> = logDao.getAll()

    fun clearLog() {
        logDao.clear()
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context): UserRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserDatabase.getInstance(context).let { db ->
                    UserRepository(db.userDao(), db.authLogDao())
                }.also { INSTANCE = it }
            }
    }
}
