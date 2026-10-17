package com.example.composelab6.data

import android.os.Handler

/**
 * Выполняет запросы к базе данных в отдельном потоке (java.lang.Thread).
 * Результат работы потока передаётся в UI-поток через механизм сообщений:
 * Handler -> MessageQueue -> Looper -> Message (метод handleMessage в UI-потоке).
 */
class DbWorker(
    private val repository: UserRepository,
    private val callback: Handler
) {

    // Имитация задержки при обращении к БД (в миллисекундах).
    @Volatile
    var delayMs: Long = 1500L

    /** Загрузка списка пользователей из БД в отдельном потоке. */
    fun loadUsers() {
        runInThread(MSG_USERS) { repository.getAllUsers() }
    }

    /** Авторизация: запрос к БД в отдельном потоке. */
    fun signIn(login: String, password: String) {
        runInThread(MSG_SIGN_IN) { repository.authorize(login, password) }
    }

    /** Регистрация: запрос к БД в отдельном потоке. */
    fun signUp(login: String, password: String, fio: String, birth: String, gender: String) {
        runInThread(MSG_SIGN_UP) { repository.register(login, password, fio, birth, gender) }
    }

    /** Изменение роли пользователя: запись в БД в отдельном потоке. */
    fun setAdmin(user: User, isAdmin: Boolean) {
        runInThread(MSG_SET_ADMIN) {
            repository.setAdmin(user, isAdmin)
            user
        }
    }

    /** Чтение журнала авторизаций в отдельном потоке. */
    fun loadLog() {
        runInThread(MSG_LOG) { repository.readLog() }
    }

    private fun runInThread(what: Int, block: () -> Any?) {
        Thread {
            simulateDbDelay()
            val result = try {
                block()
            } catch (e: Exception) {
                null
            }
            val message = callback.obtainMessage(what)
            message.obj = result
            callback.sendMessage(message)
        }.start()
    }

    private fun simulateDbDelay() {
        val delay = delayMs
        if (delay > 0) {
            try {
                Thread.sleep(delay)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    companion object {
        const val MSG_USERS = 1
        const val MSG_SIGN_IN = 2
        const val MSG_SIGN_UP = 3
        const val MSG_SET_ADMIN = 4
        const val MSG_LOG = 5
    }
}
