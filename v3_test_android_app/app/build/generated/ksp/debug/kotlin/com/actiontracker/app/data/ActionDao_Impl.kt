package com.actiontracker.app.`data`

import androidx.lifecycle.LiveData
import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.actiontracker.app.models.ActionEntity
import javax.`annotation`.processing.Generated
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
public class ActionDao_Impl(
  __db: RoomDatabase,
) : ActionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfActionEntity: EntityInsertAdapter<ActionEntity>

  private val __deleteAdapterOfActionEntity: EntityDeleteOrUpdateAdapter<ActionEntity>

  private val __updateAdapterOfActionEntity: EntityDeleteOrUpdateAdapter<ActionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfActionEntity = object : EntityInsertAdapter<ActionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `actions` (`actionId`,`actionName`,`creationTimestamp`,`backgroundColor`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ActionEntity) {
        statement.bindLong(1, entity.actionId.toLong())
        statement.bindText(2, entity.actionName)
        statement.bindLong(3, entity.creationTimestamp)
        statement.bindLong(4, entity.backgroundColor.toLong())
      }
    }
    this.__deleteAdapterOfActionEntity = object : EntityDeleteOrUpdateAdapter<ActionEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `actions` WHERE `actionId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ActionEntity) {
        statement.bindLong(1, entity.actionId.toLong())
      }
    }
    this.__updateAdapterOfActionEntity = object : EntityDeleteOrUpdateAdapter<ActionEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `actions` SET `actionId` = ?,`actionName` = ?,`creationTimestamp` = ?,`backgroundColor` = ? WHERE `actionId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ActionEntity) {
        statement.bindLong(1, entity.actionId.toLong())
        statement.bindText(2, entity.actionName)
        statement.bindLong(3, entity.creationTimestamp)
        statement.bindLong(4, entity.backgroundColor.toLong())
        statement.bindLong(5, entity.actionId.toLong())
      }
    }
  }

  public override suspend fun insertAction(actionEntity: ActionEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfActionEntity.insertAndReturnId(_connection, actionEntity)
    _result
  }

  public override suspend fun deleteAction(actionEntity: ActionEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfActionEntity.handle(_connection, actionEntity)
  }

  public override suspend fun updateAction(actionEntity: ActionEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfActionEntity.handle(_connection, actionEntity)
  }

  public override fun getAllActions(): LiveData<List<ActionEntity>> {
    val _sql: String = "SELECT * FROM actions ORDER BY creationTimestamp ASC"
    return __db.invalidationTracker.createLiveData(arrayOf("actions"), false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfActionId: Int = getColumnIndexOrThrow(_stmt, "actionId")
        val _columnIndexOfActionName: Int = getColumnIndexOrThrow(_stmt, "actionName")
        val _columnIndexOfCreationTimestamp: Int = getColumnIndexOrThrow(_stmt, "creationTimestamp")
        val _columnIndexOfBackgroundColor: Int = getColumnIndexOrThrow(_stmt, "backgroundColor")
        val _result: MutableList<ActionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ActionEntity
          val _tmpActionId: Int
          _tmpActionId = _stmt.getLong(_columnIndexOfActionId).toInt()
          val _tmpActionName: String
          _tmpActionName = _stmt.getText(_columnIndexOfActionName)
          val _tmpCreationTimestamp: Long
          _tmpCreationTimestamp = _stmt.getLong(_columnIndexOfCreationTimestamp)
          val _tmpBackgroundColor: Int
          _tmpBackgroundColor = _stmt.getLong(_columnIndexOfBackgroundColor).toInt()
          _item =
              ActionEntity(_tmpActionId,_tmpActionName,_tmpCreationTimestamp,_tmpBackgroundColor)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getActionById(actionId: Int): ActionEntity? {
    val _sql: String = "SELECT * FROM actions WHERE actionId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, actionId.toLong())
        val _columnIndexOfActionId: Int = getColumnIndexOrThrow(_stmt, "actionId")
        val _columnIndexOfActionName: Int = getColumnIndexOrThrow(_stmt, "actionName")
        val _columnIndexOfCreationTimestamp: Int = getColumnIndexOrThrow(_stmt, "creationTimestamp")
        val _columnIndexOfBackgroundColor: Int = getColumnIndexOrThrow(_stmt, "backgroundColor")
        val _result: ActionEntity?
        if (_stmt.step()) {
          val _tmpActionId: Int
          _tmpActionId = _stmt.getLong(_columnIndexOfActionId).toInt()
          val _tmpActionName: String
          _tmpActionName = _stmt.getText(_columnIndexOfActionName)
          val _tmpCreationTimestamp: Long
          _tmpCreationTimestamp = _stmt.getLong(_columnIndexOfCreationTimestamp)
          val _tmpBackgroundColor: Int
          _tmpBackgroundColor = _stmt.getLong(_columnIndexOfBackgroundColor).toInt()
          _result =
              ActionEntity(_tmpActionId,_tmpActionName,_tmpCreationTimestamp,_tmpBackgroundColor)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateActionColor(actionId: Int, color: Int) {
    val _sql: String = "UPDATE actions SET backgroundColor = ? WHERE actionId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, color.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, actionId.toLong())
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
