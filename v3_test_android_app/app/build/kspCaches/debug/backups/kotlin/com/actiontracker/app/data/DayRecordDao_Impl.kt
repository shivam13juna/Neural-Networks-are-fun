package com.actiontracker.app.`data`

import androidx.lifecycle.LiveData
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.actiontracker.app.models.DayRecordEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class DayRecordDao_Impl(
  __db: RoomDatabase,
) : DayRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDayRecordEntity: EntityInsertAdapter<DayRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDayRecordEntity = object : EntityInsertAdapter<DayRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `day_records` (`date`,`actionId`,`count`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DayRecordEntity) {
        statement.bindText(1, entity.date)
        statement.bindLong(2, entity.actionId.toLong())
        statement.bindLong(3, entity.count.toLong())
      }
    }
  }

  public override suspend fun insertOrUpdateDayRecord(dayRecordEntity: DayRecordEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDayRecordEntity.insert(_connection, dayRecordEntity)
  }

  public override suspend fun incrementCount(date: String, actionId: Int): Unit =
      performInTransactionSuspending(__db) {
    super@DayRecordDao_Impl.incrementCount(date, actionId)
  }

  public override suspend fun decrementCount(date: String, actionId: Int): Unit =
      performInTransactionSuspending(__db) {
    super@DayRecordDao_Impl.decrementCount(date, actionId)
  }

  public override fun getDayRecordsForDate(date: String): LiveData<List<DayRecordEntity>> {
    val _sql: String = "SELECT * FROM day_records WHERE date = ?"
    return __db.invalidationTracker.createLiveData(arrayOf("day_records"), false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, date)
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfActionId: Int = getColumnIndexOrThrow(_stmt, "actionId")
        val _columnIndexOfCount: Int = getColumnIndexOrThrow(_stmt, "count")
        val _result: MutableList<DayRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DayRecordEntity
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpActionId: Int
          _tmpActionId = _stmt.getLong(_columnIndexOfActionId).toInt()
          val _tmpCount: Int
          _tmpCount = _stmt.getLong(_columnIndexOfCount).toInt()
          _item = DayRecordEntity(_tmpDate,_tmpActionId,_tmpCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDayRecordForDateAndAction(date: String, actionId: Int):
      DayRecordEntity? {
    val _sql: String = "SELECT * FROM day_records WHERE date = ? AND actionId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, date)
        _argIndex = 2
        _stmt.bindLong(_argIndex, actionId.toLong())
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfActionId: Int = getColumnIndexOrThrow(_stmt, "actionId")
        val _columnIndexOfCount: Int = getColumnIndexOrThrow(_stmt, "count")
        val _result: DayRecordEntity?
        if (_stmt.step()) {
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpActionId: Int
          _tmpActionId = _stmt.getLong(_columnIndexOfActionId).toInt()
          val _tmpCount: Int
          _tmpCount = _stmt.getLong(_columnIndexOfCount).toInt()
          _result = DayRecordEntity(_tmpDate,_tmpActionId,_tmpCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDayRecordsForRange(
    start: String,
    end: String,
    actionIds: List<Int>,
  ): LiveData<List<DayRecordEntity>> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM day_records WHERE date BETWEEN ")
    _stringBuilder.append("?")
    _stringBuilder.append(" AND ")
    _stringBuilder.append("?")
    _stringBuilder.append(" AND actionId IN(")
    val _inputSize: Int = actionIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return __db.invalidationTracker.createLiveData(arrayOf("day_records"), false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, start)
        _argIndex = 2
        _stmt.bindText(_argIndex, end)
        _argIndex = 3
        for (_item: Int in actionIds) {
          _stmt.bindLong(_argIndex, _item.toLong())
          _argIndex++
        }
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfActionId: Int = getColumnIndexOrThrow(_stmt, "actionId")
        val _columnIndexOfCount: Int = getColumnIndexOrThrow(_stmt, "count")
        val _result: MutableList<DayRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: DayRecordEntity
          val _tmpDate: String
          _tmpDate = _stmt.getText(_columnIndexOfDate)
          val _tmpActionId: Int
          _tmpActionId = _stmt.getLong(_columnIndexOfActionId).toInt()
          val _tmpCount: Int
          _tmpCount = _stmt.getLong(_columnIndexOfCount).toInt()
          _item_1 = DayRecordEntity(_tmpDate,_tmpActionId,_tmpCount)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllRecordsForAction(actionId: Int) {
    val _sql: String = "DELETE FROM day_records WHERE actionId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
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
