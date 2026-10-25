package com.example.composelab7.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class, LogEntry::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun authLogDao(): AuthLogDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        // РњРёРіСЂР°С†РёСЏ 1 -> 2: РґРѕР±Р°РІР»РµРЅРёРµ РѕС‚РґРµР»СЊРЅРѕР№ С‚Р°Р±Р»РёС†С‹ Р¶СѓСЂРЅР°Р»Р° Р°РІС‚РѕСЂРёР·Р°С†РёР№.
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `auth_logs` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`time` TEXT NOT NULL, " +
                        "`login` TEXT NOT NULL, " +
                        "`role` TEXT NOT NULL, " +
                        "`success` INTEGER NOT NULL)"
                )
            }
        }

        fun getInstance(context: Context): UserDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "lab5_users.db"
                ).addMigrations(MIGRATION_1_2)
                    .build().also { INSTANCE = it }
            }
    }
}
