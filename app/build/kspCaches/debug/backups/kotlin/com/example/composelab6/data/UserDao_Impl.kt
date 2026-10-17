package com.example.composelab6.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performBlocking
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class UserDao_Impl(
  __db: RoomDatabase,
) : UserDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUser: EntityInsertAdapter<User>

  private val __updateAdapterOfUser: EntityDeleteOrUpdateAdapter<User>
  init {
    this.__db = __db
    this.__insertAdapterOfUser = object : EntityInsertAdapter<User>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `users` (`id`,`login`,`password`,`fio`,`birth`,`gender`,`is_admin`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: User) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.login)
        statement.bindText(3, entity.password)
        statement.bindText(4, entity.fio)
        statement.bindText(5, entity.birth)
        statement.bindText(6, entity.gender)
        val _tmp: Int = if (entity.isAdmin) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }
    this.__updateAdapterOfUser = object : EntityDeleteOrUpdateAdapter<User>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `users` SET `id` = ?,`login` = ?,`password` = ?,`fio` = ?,`birth` = ?,`gender` = ?,`is_admin` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: User) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.login)
        statement.bindText(3, entity.password)
        statement.bindText(4, entity.fio)
        statement.bindText(5, entity.birth)
        statement.bindText(6, entity.gender)
        val _tmp: Int = if (entity.isAdmin) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.id.toLong())
      }
    }
  }

  public override fun insert(user: User): Long = performBlocking(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfUser.insertAndReturnId(_connection, user)
    _result
  }

  public override fun update(user: User): Unit = performBlocking(__db, false, true) { _connection ->
    __updateAdapterOfUser.handle(_connection, user)
  }

  public override fun getAll(): List<User> {
    val _sql: String = "SELECT * FROM users ORDER BY id"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLogin: Int = getColumnIndexOrThrow(_stmt, "login")
        val _columnIndexOfPassword: Int = getColumnIndexOrThrow(_stmt, "password")
        val _columnIndexOfFio: Int = getColumnIndexOrThrow(_stmt, "fio")
        val _columnIndexOfBirth: Int = getColumnIndexOrThrow(_stmt, "birth")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfIsAdmin: Int = getColumnIndexOrThrow(_stmt, "is_admin")
        val _result: MutableList<User> = mutableListOf()
        while (_stmt.step()) {
          val _item: User
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpLogin: String
          _tmpLogin = _stmt.getText(_columnIndexOfLogin)
          val _tmpPassword: String
          _tmpPassword = _stmt.getText(_columnIndexOfPassword)
          val _tmpFio: String
          _tmpFio = _stmt.getText(_columnIndexOfFio)
          val _tmpBirth: String
          _tmpBirth = _stmt.getText(_columnIndexOfBirth)
          val _tmpGender: String
          _tmpGender = _stmt.getText(_columnIndexOfGender)
          val _tmpIsAdmin: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsAdmin).toInt()
          _tmpIsAdmin = _tmp != 0
          _item = User(_tmpId,_tmpLogin,_tmpPassword,_tmpFio,_tmpBirth,_tmpGender,_tmpIsAdmin)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun findByLoginPassword(login: String, password: String): User? {
    val _sql: String = "SELECT * FROM users WHERE login = ? AND password = ? LIMIT 1"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, login)
        _argIndex = 2
        _stmt.bindText(_argIndex, password)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLogin: Int = getColumnIndexOrThrow(_stmt, "login")
        val _columnIndexOfPassword: Int = getColumnIndexOrThrow(_stmt, "password")
        val _columnIndexOfFio: Int = getColumnIndexOrThrow(_stmt, "fio")
        val _columnIndexOfBirth: Int = getColumnIndexOrThrow(_stmt, "birth")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfIsAdmin: Int = getColumnIndexOrThrow(_stmt, "is_admin")
        val _result: User?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpLogin: String
          _tmpLogin = _stmt.getText(_columnIndexOfLogin)
          val _tmpPassword: String
          _tmpPassword = _stmt.getText(_columnIndexOfPassword)
          val _tmpFio: String
          _tmpFio = _stmt.getText(_columnIndexOfFio)
          val _tmpBirth: String
          _tmpBirth = _stmt.getText(_columnIndexOfBirth)
          val _tmpGender: String
          _tmpGender = _stmt.getText(_columnIndexOfGender)
          val _tmpIsAdmin: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsAdmin).toInt()
          _tmpIsAdmin = _tmp != 0
          _result = User(_tmpId,_tmpLogin,_tmpPassword,_tmpFio,_tmpBirth,_tmpGender,_tmpIsAdmin)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun findByLogin(login: String): User? {
    val _sql: String = "SELECT * FROM users WHERE login = ? LIMIT 1"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, login)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLogin: Int = getColumnIndexOrThrow(_stmt, "login")
        val _columnIndexOfPassword: Int = getColumnIndexOrThrow(_stmt, "password")
        val _columnIndexOfFio: Int = getColumnIndexOrThrow(_stmt, "fio")
        val _columnIndexOfBirth: Int = getColumnIndexOrThrow(_stmt, "birth")
        val _columnIndexOfGender: Int = getColumnIndexOrThrow(_stmt, "gender")
        val _columnIndexOfIsAdmin: Int = getColumnIndexOrThrow(_stmt, "is_admin")
        val _result: User?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpLogin: String
          _tmpLogin = _stmt.getText(_columnIndexOfLogin)
          val _tmpPassword: String
          _tmpPassword = _stmt.getText(_columnIndexOfPassword)
          val _tmpFio: String
          _tmpFio = _stmt.getText(_columnIndexOfFio)
          val _tmpBirth: String
          _tmpBirth = _stmt.getText(_columnIndexOfBirth)
          val _tmpGender: String
          _tmpGender = _stmt.getText(_columnIndexOfGender)
          val _tmpIsAdmin: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsAdmin).toInt()
          _tmpIsAdmin = _tmp != 0
          _result = User(_tmpId,_tmpLogin,_tmpPassword,_tmpFio,_tmpBirth,_tmpGender,_tmpIsAdmin)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM users"
    return performBlocking(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          _result = _stmt.getLong(0).toInt()
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
