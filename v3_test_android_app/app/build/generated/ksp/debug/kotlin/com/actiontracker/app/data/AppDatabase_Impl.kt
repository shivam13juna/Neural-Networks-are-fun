package com.actiontracker.app.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _actionDao: Lazy<ActionDao> = lazy {
    ActionDao_Impl(this)
  }

  private val _dayRecordDao: Lazy<DayRecordDao> = lazy {
    DayRecordDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "f7191d9cd986a2239e708d241e55e32a", "1a6318cdf05c6692d1ed16051ac36ecf") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `actions` (`actionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `actionName` TEXT NOT NULL, `creationTimestamp` INTEGER NOT NULL, `backgroundColor` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `day_records` (`date` TEXT NOT NULL, `actionId` INTEGER NOT NULL, `count` INTEGER NOT NULL, PRIMARY KEY(`date`, `actionId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f7191d9cd986a2239e708d241e55e32a')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `actions`")
        connection.execSQL("DROP TABLE IF EXISTS `day_records`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsActions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsActions.put("actionId", TableInfo.Column("actionId", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsActions.put("actionName", TableInfo.Column("actionName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsActions.put("creationTimestamp", TableInfo.Column("creationTimestamp", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsActions.put("backgroundColor", TableInfo.Column("backgroundColor", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysActions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesActions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoActions: TableInfo = TableInfo("actions", _columnsActions, _foreignKeysActions,
            _indicesActions)
        val _existingActions: TableInfo = read(connection, "actions")
        if (!_infoActions.equals(_existingActions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |actions(com.actiontracker.app.models.ActionEntity).
              | Expected:
              |""".trimMargin() + _infoActions + """
              |
              | Found:
              |""".trimMargin() + _existingActions)
        }
        val _columnsDayRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDayRecords.put("date", TableInfo.Column("date", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDayRecords.put("actionId", TableInfo.Column("actionId", "INTEGER", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDayRecords.put("count", TableInfo.Column("count", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDayRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDayRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDayRecords: TableInfo = TableInfo("day_records", _columnsDayRecords,
            _foreignKeysDayRecords, _indicesDayRecords)
        val _existingDayRecords: TableInfo = read(connection, "day_records")
        if (!_infoDayRecords.equals(_existingDayRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |day_records(com.actiontracker.app.models.DayRecordEntity).
              | Expected:
              |""".trimMargin() + _infoDayRecords + """
              |
              | Found:
              |""".trimMargin() + _existingDayRecords)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "actions", "day_records")
  }

  public override fun clearAllTables() {
    super.performClear(false, "actions", "day_records")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ActionDao::class, ActionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DayRecordDao::class, DayRecordDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun actionDao(): ActionDao = _actionDao.value

  public override fun dayRecordDao(): DayRecordDao = _dayRecordDao.value
}
