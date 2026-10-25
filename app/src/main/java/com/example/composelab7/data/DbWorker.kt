package com.example.composelab7.data

import android.os.Handler

class DbWorker(
    private val repository: UserRepository,
    private val callback: Handler
) {

    @Volatile
    var delayMs: Long = 1500L

    fun loadUsers() {
        runInThread(MSG_USERS) { repository.getAllUsers() }
    }
    fun signIn(login: String, password: String) {
        runInThread(MSG_SIGN_IN) { repository.authorize(login, password) }
    }

    fun signUp(login: String, password: String, fio: String, birth: String, gender: String) {
        runInThread(MSG_SIGN_UP) { repository.register(login, password, fio, birth, gender) }
    }
    fun setAdmin(user: User, isAdmin: Boolean) {
        runInThread(MSG_SET_ADMIN) {
            repository.setAdmin(user, isAdmin)
            user
        }
    }
    fun loadLog() {
        runInThread(MSG_LOG) { repository.getAllLogs() }
    }
    fun clearLog() {
        runInThread(MSG_LOG) {
            repository.clearLog()
            repository.getAllLogs()
        }
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
