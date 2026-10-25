package com.example.composelab7.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AuthLogDao_Impl(
  __db: RoomDatabase,
) : AuthLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLogEntry: EntityInsertAdapter<LogEntry>
  init {
    this.__db = __db
    this.__insertAdapterOfLogEntry = object : EntityInsertAdapter<LogEntry>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `auth_logs` (`id`,`time`,`login`,`role`,`success`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LogEntry) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.time)
        statement.bindText(3, entity.login)
        statement.bindText(4, entity.role)
        val _tmp: Int = if (entity.success) 1 else 0
        statement.bindLong(5, _tmp.toLong())
      }
    }
  }

  public override fun insert(entry: LogEntry): Unit = performBlocking(__db, false, true) { _connection ->
    __insertAdapterOfLogEntry.insert(_connection, entry)
  }

  public override fun getAll(): List<LogEntry> {
    val _sql: String = "SELECT * FROM auth_logs ORDER BY id DESC"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTime: Int = getColumnIndexOrThrow(_stmt, "time")
        val _columnIndexOfLogin: Int = getColumnIndexOrThrow(_stmt, "login")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfSuccess: Int = getColumnIndexOrThrow(_stmt, "success")
        val _result: MutableList<LogEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: LogEntry
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTime: String
          _tmpTime = _stmt.getText(_columnIndexOfTime)
          val _tmpLogin: String
          _tmpLogin = _stmt.getText(_columnIndexOfLogin)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpSuccess: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSuccess).toInt()
          _tmpSuccess = _tmp != 0
          _item = LogEntry(_tmpId,_tmpTime,_tmpLogin,_tmpRole,_tmpSuccess)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun clear() {
    val _sql: String = "DELETE FROM auth_logs"
    return performBlocking(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
